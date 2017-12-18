package com.trendy.ow.portal.payment.alipay.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.trendy.fw.common.util.BeanKit;
import com.trendy.ow.portal.payment.alipay.bean.AliPayWebConfigBean;
import com.trendy.ow.portal.payment.alipay.config.AliPayWebConfig;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayCompanyInfoBean;
import com.trendy.ow.portal.payment.cache.PayChannelCache;
import com.trendy.ow.portal.payment.cache.PayCompanyCache;
import com.trendy.ow.portal.payment.cache.PayConfigCache;

public class AliPayWebConfigBusiness {
	
	public AliPayWebConfigBean getAliPayWebConfig(String storeCode) {
		AliPayWebConfigBean configBean = new AliPayWebConfigBean();
		PayConfigCache cache=new PayConfigCache();
		HashMap<String, Object> configMap = new HashMap<String, Object>();
		configMap.putAll(cache.getCompanyConfigValueMap(AliPayWebConfig.COMPANY_CODE));
		configMap.putAll(cache.getStoreConfigValueMap(AliPayWebConfig.COMPANY_CODE, storeCode));
		
		PayCompanyCache companyCache = new PayCompanyCache();
		PayCompanyInfoBean company = companyCache.getPayCompanyInfo(AliPayWebConfig.COMPANY_CODE);
		int companyId=company.getCompanyId();
		PayChannelCache channelCache = new PayChannelCache();
		List<PayChannelInfoBean> list =channelCache.getPayChannelListByCompanyId(companyId);
		Map<String, String> defaultBank = new HashMap<String, String>();
		Map<String, String> payMethod = new HashMap<String, String>();
		for(PayChannelInfoBean channel:list){
			String channelCode=channel.getChannelCode();
			HashMap<String, String> dataMap=cache.getChannelConfigValueMap(AliPayWebConfig.COMPANY_CODE, channelCode);
			defaultBank.put(channelCode, dataMap.get("defaultBank"));
			payMethod.put(channelCode, dataMap.get("payMethod"));
		}
		configMap.put("defaultBank", defaultBank);
		configMap.put("payMethod", payMethod);
		configMap.put("storeCode", storeCode);
		configBean.setStoreCode(storeCode);
		try {
			configBean = BeanKit.map2Bean(configMap, AliPayWebConfigBean.class);
		} catch (Exception e) {
		}

		return configBean;
	}
}
