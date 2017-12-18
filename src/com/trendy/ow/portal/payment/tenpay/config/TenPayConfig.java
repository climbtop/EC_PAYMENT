package com.trendy.ow.portal.payment.tenpay.config;

import java.util.ArrayList;
import java.util.List;

public class TenPayConfig {

	public static String COMPANY_CODE = "TenPay";
	// 配置的后缀名
	private static String FEE_TYPE = "FeeType";
	private static String VERIFY_NOTIFY_URL = "VerifyNotifyUrl";
	private static String INPUT_CHARSET = "InputCharset";
	private static String AUTHENTICATION_BASE_URL = "AuthenticationBaseUrl";
	private static String KEY = "Key";
	private static String PARTNER = "Partner";
	private static String BODY = "Body";
	private static String BANK_TYPE = "BankType";
	
	public static final String TENPAY_NOTIFY_URL = "tenpay/TenpayNotifyReceiver.do";
	public static final String TENPAY_CALL_BACK_URL = "tenpay/TenpayCallback.do";
	
	
	public static List<String> COMPANY_CONFIG_KEY_LIST = null;
	public static List<String> STORE_CONFIG_KEY_LIST = null;
	public static List<String> CHANNEL_CONFIG_KEY_LIST = null;
	

	static {
		new TenPayConfig().init();
	}

	private void init() {
		initCompanyConfigKeyList();
		initStoreConfigKeyList();
		initChannelConfigKeyList();
	}

	private void initCompanyConfigKeyList() {
		COMPANY_CONFIG_KEY_LIST = new ArrayList<String>();
		COMPANY_CONFIG_KEY_LIST.add(FEE_TYPE);
		COMPANY_CONFIG_KEY_LIST.add(VERIFY_NOTIFY_URL);
		COMPANY_CONFIG_KEY_LIST.add(INPUT_CHARSET);
		COMPANY_CONFIG_KEY_LIST.add(AUTHENTICATION_BASE_URL);
	}

	private void initStoreConfigKeyList() {
		STORE_CONFIG_KEY_LIST = new ArrayList<String>();
		STORE_CONFIG_KEY_LIST.add(KEY);
		STORE_CONFIG_KEY_LIST.add(PARTNER);
		STORE_CONFIG_KEY_LIST.add(BODY);
	}
	
	private void initChannelConfigKeyList() {
		CHANNEL_CONFIG_KEY_LIST = new ArrayList<String>();
		CHANNEL_CONFIG_KEY_LIST.add(BANK_TYPE);
	}

	public static String getStoreConfigCode(String storeCode, String configKey) {
		return storeCode + "_" + COMPANY_CODE + "_" + configKey;
	}

	public static String getCompanyConfigCode(String configKey) {
		return COMPANY_CODE + "_" + configKey;
	}

	public static String  getChannelConfigCode(String channelCode, String configKey){
		return COMPANY_CODE + "_" + channelCode + "_" + configKey;
	}

}