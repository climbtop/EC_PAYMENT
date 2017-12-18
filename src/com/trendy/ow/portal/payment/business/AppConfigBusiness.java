package com.trendy.ow.portal.payment.business;

import com.trendy.ow.portal.payment.bean.AppConfigBean;
import com.trendy.ow.portal.payment.config.AppConfig;

public class AppConfigBusiness {
	public AppConfigBean getAppConfig(String appCode) {
		PayConfigInfoBusiness business = new PayConfigInfoBusiness();
		AppConfigBean bean = new AppConfigBean();
		bean.setAppCode(appCode);
		bean.setMd5Key(business.getPayConfigValue(AppConfig.getMD5Key(appCode)));
		bean.setRsaPrivateKey(business.getPayConfigValue(AppConfig.getRsaPrivateKey(appCode)));
		return bean;

	}
}
