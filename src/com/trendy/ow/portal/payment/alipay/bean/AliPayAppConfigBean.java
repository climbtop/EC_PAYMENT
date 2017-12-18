package com.trendy.ow.portal.payment.alipay.bean;



public class AliPayAppConfigBean {
	private String storeCode = "";
	//taobaoRSApublickey
	private String tbRsaPublicKey = "";

	private String notifyVerifyService = "";
	
	private String authenticationBaseUrl = "";
	private String partner = "";

	
	public String getNotifyVerifyService() {
		return notifyVerifyService;
	}

	public void setNotifyVerifyService(String notifyVerifyService) {
		this.notifyVerifyService = notifyVerifyService;
	}

	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	public String getTbRsaPublicKey() {
		return tbRsaPublicKey;
	}

	public void setTbRsaPublicKey(String tbRsaPublicKey) {
		this.tbRsaPublicKey = tbRsaPublicKey;
	}

	public String getAuthenticationBaseUrl() {
		return authenticationBaseUrl;
	}

	public void setAuthenticationBaseUrl(String authenticationBaseUrl) {
		this.authenticationBaseUrl = authenticationBaseUrl;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}


}
