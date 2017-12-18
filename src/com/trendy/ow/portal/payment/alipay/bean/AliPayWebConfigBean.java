package com.trendy.ow.portal.payment.alipay.bean;

import java.util.HashMap;
import java.util.Map;


public class AliPayWebConfigBean {
	private String storeCode = "";
	private String service = "";
	private String inputCharset = "";
	private String signType = "";
	private String paymentType = "";
	private String authenticationBaseUrl = "";
	private String sellerEmail = "";
	private String subject = "";
	private String body = "";
	private String showUrl = "";
	private String partner = "";
	private String key = "";
	private String notifyVerifyService = "";
	//当设置paymethod（默认支付方式）为directPay（余额支付）时，请求参数defaultbank（默认网银）不要设置或不要传递。 这里渠道-改属性值
	private Map<String, String> defaultBank = new HashMap<String, String>();
	private Map<String, String> payMethod = new HashMap<String, String>();

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getInputCharset() {
		return inputCharset;
	}

	public void setInputCharset(String inputCharset) {
		this.inputCharset = inputCharset;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}


	public String getAuthenticationBaseUrl() {
		return authenticationBaseUrl;
	}

	public void setAuthenticationBaseUrl(String authenticationBaseUrl) {
		this.authenticationBaseUrl = authenticationBaseUrl;
	}

	public String getSellerEmail() {
		return sellerEmail;
	}

	public void setSellerEmail(String sellerEmail) {
		this.sellerEmail = sellerEmail;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getShowUrl() {
		return showUrl;
	}

	public void setShowUrl(String showUrl) {
		this.showUrl = showUrl;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

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

	public Map<String, String> getDefaultBank() {
		return defaultBank;
	}

	public void setDefaultBank(Map<String, String> defaultBank) {
		this.defaultBank = defaultBank;
	}

	public Map<String, String> getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(Map<String, String> payMethod) {
		this.payMethod = payMethod;
	}


}
