package com.trendy.ow.portal.payment.alipay.business;

import java.util.HashMap;

import com.trendy.fw.common.util.BeanKit;
import com.trendy.ow.portal.payment.alipay.bean.AliPayAppConfigBean;
import com.trendy.ow.portal.payment.alipay.config.AliPayAppConfig;
import com.trendy.ow.portal.payment.cache.PayConfigCache;

public class AliPayAppConfigBusiness {
	
	public AliPayAppConfigBean getAliPayAppConfig(String storeCode) {
		AliPayAppConfigBean configBean = new AliPayAppConfigBean();
		PayConfigCache cache=new PayConfigCache();
		HashMap<String, Object> configMap = new HashMap<String, Object>();
		configMap.putAll(cache.getCompanyConfigValueMap(AliPayAppConfig.COMPANY_CODE));
		configMap.putAll(cache.getStoreConfigValueMap(AliPayAppConfig.COMPANY_CODE, storeCode));
		configMap.put("storeCode", storeCode);
		configBean.setStoreCode(storeCode);
		try {
			configBean = BeanKit.map2Bean(configMap, AliPayAppConfigBean.class);
		} catch (Exception e) {
		}

		return configBean;
	}
}
