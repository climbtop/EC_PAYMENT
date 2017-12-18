package com.trendy.ow.portal.payment.config;

public class AppNotifyConfig {
	// 业务系统绑定客户端的配置
	private static final String APP_CALLBACK_URL = "_CallbackUrl";
	private static final String APP_NOTIFY_URL = "_NotifyUrl";
	
	
	public static String getAppCallbackUrl(String storeCode) {
		return storeCode + APP_CALLBACK_URL;
	}

	public static String getAppNotifyUrl(String storeCode) {
		return storeCode + APP_NOTIFY_URL;
	}

}
