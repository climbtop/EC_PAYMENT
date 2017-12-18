package com.trendy.ow.portal.payment.alipay.config;

import java.util.ArrayList;
import java.util.List;

public class AliPayWebConfig {
	public static String COMPANY_CODE = "AliPay";
	// 配置的后缀名
	private static String SERVICE = "Service";
	private static String INPUT_CHARSET = "InputCharset";
	private static String SIGN_TYPE = "SignType";
	private static String PAYMENT_TYPE = "PaymentType";
	private static String NOTIFY_VERIFY_SERVICE = "NotifyVerifyService";
	private static String AUTHENTICATION_BASE_URL = "AuthenticationBaseUrl";
	private static String SELLER_EMAIL = "SellerEmail";
	private static String SUBJECT = "Subject";
	private static String BODY = "Body";
	private static String SHOWURL = "ShowUrl";
	private static String PARTNER = "Partner";
	private static String KEY = "Key";
	private static String PAY_METHOD = "PayMethod";
	private static String DEFAULT_BANK = "DefaultBank";

	public static final String ALIPAY_WEB_NOTIFY_URL = "alipay/AlipayWebNotifyReceiver.do";
	public static final String ALIPAY_WEB_CALL_BACK_URL = "alipay/AlipayWebCallback.do";

	public static List<String> COMPANY_CONFIG_KEY_LIST = null;
	public static List<String> STORE_CONFIG_KEY_LIST = null;
	public static List<String> CHANNEL_CONFIG_KEY_LIST = null;

	static {
		new AliPayWebConfig().init();
	}

	private void init() {
		initCompanyConfigKeyList();
		initStoreConfigKeyList();
		initChannelConfigKeyList();
	}

	private void initCompanyConfigKeyList() {
		COMPANY_CONFIG_KEY_LIST = new ArrayList<String>();
		COMPANY_CONFIG_KEY_LIST.add(INPUT_CHARSET);
		COMPANY_CONFIG_KEY_LIST.add(SIGN_TYPE);
		COMPANY_CONFIG_KEY_LIST.add(SERVICE);
		COMPANY_CONFIG_KEY_LIST.add(PAYMENT_TYPE);
		COMPANY_CONFIG_KEY_LIST.add(NOTIFY_VERIFY_SERVICE);
		COMPANY_CONFIG_KEY_LIST.add(AUTHENTICATION_BASE_URL);
	}

	private void initStoreConfigKeyList() {
		STORE_CONFIG_KEY_LIST = new ArrayList<String>();
		STORE_CONFIG_KEY_LIST.add(SUBJECT);
		STORE_CONFIG_KEY_LIST.add(SELLER_EMAIL);
		STORE_CONFIG_KEY_LIST.add(BODY);
		STORE_CONFIG_KEY_LIST.add(KEY);
		STORE_CONFIG_KEY_LIST.add(SHOWURL);
		STORE_CONFIG_KEY_LIST.add(PARTNER);
	}

	private void initChannelConfigKeyList() {
		CHANNEL_CONFIG_KEY_LIST = new ArrayList<String>();
		CHANNEL_CONFIG_KEY_LIST.add(DEFAULT_BANK);
		CHANNEL_CONFIG_KEY_LIST.add(PAY_METHOD);
	}

	public static String getStoreConfigCode(String storeCode, String configKey) {
		return storeCode + "_" + COMPANY_CODE + "_" + configKey;
	}

	public static String getCompanyConfigCode(String configKey) {
		return COMPANY_CODE + "_" + configKey;
	}

	public static String getChannelConfigCode(String channelCode, String configKey) {
		return COMPANY_CODE + "_" + channelCode + "_" + configKey;
	}

}
