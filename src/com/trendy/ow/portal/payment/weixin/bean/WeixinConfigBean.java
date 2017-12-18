package com.trendy.ow.portal.payment.weixin.bean;

public class WeixinConfigBean {

	private String storeCode = "";
	// key MD5 key
	private String key = "";
	// 公众账号ID
	private String appId = "";
	// 商户号
	private String mchId = "";
	// 商品描述
	private String body = "";
	// 商品详情
	private String detail = "";
	// 附加数据
	private String attach = "";
	// 1）被扫支付API
	private String payApiUrl = "";
	//提交刷卡支付API
	private String microPayUrl = "";

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getPayApiUrl() {
		return payApiUrl;
	}

	public void setPayApiUrl(String payApiUrl) {
		this.payApiUrl = payApiUrl;
	}

	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	public String getMicroPayUrl() {
		return microPayUrl;
	}

	public void setMicroPayUrl(String microPayUrl) {
		this.microPayUrl = microPayUrl;
	}


}
