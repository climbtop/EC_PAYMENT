package com.trendy.ow.portal.payment.weixin.bean;

public class WeixinPreOrderResponseBean extends WeixinBaseResponseBean{

	private String prepayId = "";

	private String codeUrl = "";

	public String getPrepayId() {
		return prepayId;
	}

	public void setPrepayId(String prepayId) {
		this.prepayId = prepayId;
	}

	public String getCodeUrl() {
		return codeUrl;
	}

	public void setCodeUrl(String codeUrl) {
		this.codeUrl = codeUrl;
	}

}
