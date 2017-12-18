package com.trendy.ow.portal.payment.bean;

public class PayGatewayRequestBean extends PayRequestBean {
	private int payLocale = 0;
	// 0 一般下单 1
	private int requestType = 0;
	
	private String shopNumber = null;

	private String authCode = null;

	public String getShopNumber() {
		return shopNumber;
	}

	public void setShopNumber(String shopNumber) {
		this.shopNumber = shopNumber;
	}

	public int getRequestType() {
		return requestType;
	}

	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}

	public int getPayLocale() {
		return payLocale;
	}

	public void setPayLocale(int payLocale) {
		this.payLocale = payLocale;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

}
