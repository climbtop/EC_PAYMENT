package com.trendy.ow.portal.payment.weixin.bean;

public class WeixinMicropayRequestBean extends WeixinBaseRequestBean{
	// 授权码
	private String authCode = "";

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	
}
