package com.trendy.ow.portal.payment.config;

public class AppConfig {
	private static String MD5_KEY = "_Md5Key"; 
	private static String RSA_PRIVATE_KEY = "_RsaPrivateKey";
	public static String getMD5Key(String appCode) {
		return appCode+MD5_KEY;
	}
	public static String getRsaPrivateKey(String appCode) {
		return appCode+RSA_PRIVATE_KEY;
	}
}
