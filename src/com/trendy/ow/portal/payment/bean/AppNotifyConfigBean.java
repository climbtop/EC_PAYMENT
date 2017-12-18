package com.trendy.ow.portal.payment.bean;

public class AppNotifyConfigBean {
	private String storeCode="";
	private String appCallBackUrl = "";
	private String appNotifyUrl = "";

	public String getAppCallBackUrl() {
		return appCallBackUrl;
	}

	public void setAppCallBackUrl(String appCallBackUrl) {
		this.appCallBackUrl = appCallBackUrl;
	}

	public String getAppNotifyUrl() {
		return appNotifyUrl;
	}

	public void setAppNotifyUrl(String appNotifyUrl) {
		this.appNotifyUrl = appNotifyUrl;
	}

	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

}
