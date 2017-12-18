package com.trendy.ow.portal.payment.alipay.config;

import java.util.ArrayList;
import java.util.List;

public class AliPayAppConfig {
	public static String COMPANY_CODE = "AliPayApp";
	public static String CHANNEL_CODE_WALLET = "AliPayApp_Wallet";
	// 配置的后缀名
	private static String TB_RSA_PUBLIC_KEY = "TbRsaPublicKey";

	public static final String ALIPAY_APP_NOTIFY_URL = "alipay/AlipayAppNotifyReceiver.do";

	public static List<String> COMPANY_CONFIG_KEY_LIST = null;
	public static List<String> STORE_CONFIG_KEY_LIST = null;
	private static String PARTNER = "Partner";
	private static String NOTIFY_VERIFY_SERVICE = "NotifyVerifyService";
	private static String AUTHENTICATION_BASE_URL = "AuthenticationBaseUrl";

	static {
		new AliPayAppConfig().init();
	}

	private void init() {
		initCompanyConfigKeyList();
		initStoreConfigKeyList();
	}

	private void initCompanyConfigKeyList() {
		COMPANY_CONFIG_KEY_LIST = new ArrayList<String>();
		COMPANY_CONFIG_KEY_LIST.add(NOTIFY_VERIFY_SERVICE);
		COMPANY_CONFIG_KEY_LIST.add(AUTHENTICATION_BASE_URL);
	}

	private void initStoreConfigKeyList() {
		STORE_CONFIG_KEY_LIST = new ArrayList<String>();
		STORE_CONFIG_KEY_LIST.add(TB_RSA_PUBLIC_KEY);
		STORE_CONFIG_KEY_LIST.add(PARTNER);
	}

	public static String getStoreConfigCode(String storeCode, String configKey) {
		return storeCode + "_" + COMPANY_CODE + "_" + configKey;
	}

	public static String getCompanyConfigCode(String configKey) {
		return COMPANY_CODE + "_" + configKey;
	}
}