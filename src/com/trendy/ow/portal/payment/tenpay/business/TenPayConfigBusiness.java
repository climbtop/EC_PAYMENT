package com.trendy.ow.portal.payment.tenpay.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.trendy.fw.common.util.BeanKit;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayCompanyInfoBean;
import com.trendy.ow.portal.payment.cache.PayChannelCache;
import com.trendy.ow.portal.payment.cache.PayCompanyCache;
import com.trendy.ow.portal.payment.cache.PayConfigCache;
import com.trendy.ow.portal.payment.tenpay.bean.TenPayConfigBean;
import com.trendy.ow.portal.payment.tenpay.config.TenPayConfig;

public class TenPayConfigBusiness {
	public TenPayConfigBean getTenPayConfig(String storeCode){
		TenPayConfigBean configBean=new TenPayConfigBean();
		
		PayConfigCache cache=new PayConfigCache();
		HashMap<String, Object> configMap = new HashMap<String, Object>();
		
		configMap.putAll(cache.getCompanyConfigValueMap(TenPayConfig.COMPANY_CODE));
		configMap.putAll(cache.getStoreConfigValueMap(TenPayConfig.COMPANY_CODE, storeCode));
		
		PayCompanyCache companyCache = new PayCompanyCache();
		PayCompanyInfoBean company = companyCache.getPayCompanyInfo(TenPayConfig.COMPANY_CODE);
		int companyId=company.getCompanyId();
		PayChannelCache channelCache = new PayChannelCache();
		List<PayChannelInfoBean> list =channelCache.getPayChannelListByCompanyId(companyId);
		Map<String, String> bankTypes = new HashMap<String, String>();
		for(PayChannelInfoBean channel:list){
			String channelCode=channel.getChannelCode();
			HashMap<String, String> dataMap=cache.getChannelConfigValueMap(TenPayConfig.COMPANY_CODE, channelCode);
			bankTypes.put(channelCode, dataMap.get("bankType"));
		}
		
		configMap.put("bankTypes", bankTypes);
		configMap.put("storeCode", storeCode);
		try {
			configBean = BeanKit.map2Bean(configMap, TenPayConfigBean.class);
		} catch (Exception e) {
		}

		
		return configBean;
	}
}
