package com.trendy.ow.portal.payment.business;

import com.trendy.ow.portal.payment.bean.AppNotifyConfigBean;
import com.trendy.ow.portal.payment.config.AppNotifyConfig;

public class AppNotifyConfigBusiness {
	public AppNotifyConfigBean getAppNotifyConfig(String storeCode) {
		PayConfigInfoBusiness business = new PayConfigInfoBusiness();
		AppNotifyConfigBean bean = new AppNotifyConfigBean();
		bean.setStoreCode(storeCode);
		bean.setAppCallBackUrl(business.getPayConfigValue(AppNotifyConfig.getAppCallbackUrl(storeCode)));
		bean.setAppNotifyUrl(business.getPayConfigValue(AppNotifyConfig.getAppNotifyUrl(storeCode)));
		return bean;

	}
}
