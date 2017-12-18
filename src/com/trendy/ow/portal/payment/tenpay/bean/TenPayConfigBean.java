package com.trendy.ow.portal.payment.tenpay.bean;

import java.util.HashMap;
import java.util.Map;


public class TenPayConfigBean{
	private String storeCode="";
	private Map<String, String> bankTypes = new HashMap<String, String>();
	private String feeType = "";
	private String inputCharset = "";
	private String authenticationBaseUrl = "";
	private String body = "";
	private String partner = "";
	private String key = "";
	private String verifyNotifyUrl="";
	
	public String getStoreCode() {
		return storeCode;
	}
	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}
	public String getBankType(String channelCode){
		return bankTypes.get(channelCode);
	}
	public Map<String, String> getBankTypes() {
		return bankTypes;
	}
	public void setBankTypes(Map<String, String> bankTypes) {
		this.bankTypes = bankTypes;
	}
	public String getFeeType() {
		return feeType;
	}
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	public String getInputCharset() {
		return inputCharset;
	}
	public void setInputCharset(String inputCharset) {
		this.inputCharset = inputCharset;
	}
	public String getAuthenticationBaseUrl() {
		return authenticationBaseUrl;
	}
	public void setAuthenticationBaseUrl(String authenticationBaseUrl) {
		this.authenticationBaseUrl = authenticationBaseUrl;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
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
	public String getVerifyNotifyUrl() {
		return verifyNotifyUrl;
	}
	public void setVerifyNotifyUrl(String verifyNotifyUrl) {
		this.verifyNotifyUrl = verifyNotifyUrl;
	}
	
}
