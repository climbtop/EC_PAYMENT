package com.trendy.ow.portal.payment.alipay.business;

import java.util.HashMap;

import com.trendy.fw.common.util.BeanKit;
import com.trendy.ow.portal.payment.alipay.bean.AliPayWapConfigBean;
import com.trendy.ow.portal.payment.alipay.config.AliPayWapConfig;
import com.trendy.ow.portal.payment.cache.PayConfigCache;

public class AliPayWapConfigBusiness {
	public AliPayWapConfigBean getAliPayWapConfig(String storeCode) {
		AliPayWapConfigBean configBean = new AliPayWapConfigBean();
		PayConfigCache cache=new PayConfigCache();
		HashMap<String, Object> configMap = new HashMap<String, Object>();
		configMap.putAll(cache.getCompanyConfigValueMap(AliPayWapConfig.COMPANY_CODE));
		configMap.putAll(cache.getStoreConfigValueMap(AliPayWapConfig.COMPANY_CODE, storeCode));
		configMap.put("storeCode", storeCode);

		try {
			configBean = BeanKit.map2Bean(configMap, AliPayWapConfigBean.class);
		} catch (Exception e) {
		}

		return configBean;
	}

}
