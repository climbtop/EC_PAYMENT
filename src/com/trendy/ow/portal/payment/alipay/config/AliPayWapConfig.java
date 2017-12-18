package com.trendy.ow.portal.payment.alipay.config;

import java.util.ArrayList;
import java.util.List;

public class AliPayWapConfig {
	public static String COMPANY_CODE = "AliPayWap";
	// 配置的后缀名
	private static String SECID = "SecId";
	private static String CREATE_SERVICE = "CreateService";
	private static String SERVICE = "Service";
	private static String FORMAT = "Format";
	private static String V = "V";
	private static String AUTHENTICATION_BASE_URL = "AuthenticationBaseUrl";
	private static String PAY_EXPIRE = "PayExpire";
	private static String SUBJECT = "Subject";
	private static String MERCHANT_URL = "MerchantUrl";
	private static String PARTNER = "Partner";
	private static String KEY = "Key";
	private static String SELLER_ACCOUNT_NAME = "SellerAccountName";

	public static final String ALIPAY_WAP_NOTIFY_URL = "alipay/AlipayWapNotifyReceiver.do";
	public static final String ALIPAY_WAP_CALL_BACK_URL = "alipay/AlipayWapCallback.do";

	public static List<String> COMPANY_CONFIG_KEY_LIST = null;
	public static List<String> STORE_CONFIG_KEY_LIST = null;

	static {
		new AliPayWapConfig().init();
	}

	private void init() {
		initCompanyConfigKeyList();
		initStoreConfigKeyList();
	}

	private void initCompanyConfigKeyList() {
		COMPANY_CONFIG_KEY_LIST = new ArrayList<String>();
		COMPANY_CONFIG_KEY_LIST.add(SECID);
		COMPANY_CONFIG_KEY_LIST.add(CREATE_SERVICE);
		COMPANY_CONFIG_KEY_LIST.add(SERVICE);
		COMPANY_CONFIG_KEY_LIST.add(FORMAT);
		COMPANY_CONFIG_KEY_LIST.add(V);
		COMPANY_CONFIG_KEY_LIST.add(AUTHENTICATION_BASE_URL);
		COMPANY_CONFIG_KEY_LIST.add(PAY_EXPIRE);
	}

	private void initStoreConfigKeyList() {
		STORE_CONFIG_KEY_LIST = new ArrayList<String>();
		STORE_CONFIG_KEY_LIST.add(SUBJECT);
		STORE_CONFIG_KEY_LIST.add(MERCHANT_URL);
		STORE_CONFIG_KEY_LIST.add(PARTNER);
		STORE_CONFIG_KEY_LIST.add(KEY);
		STORE_CONFIG_KEY_LIST.add(SELLER_ACCOUNT_NAME);
	}

	public static String getStoreConfigCode(String storeCode, String configKey) {
		return storeCode + "_" + COMPANY_CODE + "_" + configKey;
	}

	public static String getCompanyConfigCode(String configKey) {
		return COMPANY_CODE + "_" + configKey;
	}
}