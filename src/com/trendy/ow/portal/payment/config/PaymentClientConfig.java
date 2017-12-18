package com.trendy.ow.portal.payment.config;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.crypto.RSA;
import com.trendy.fw.common.util.PropertiesKit;

public class PaymentClientConfig {
	// 配置文件路径
	public static final String PAYMENT_CLIENT_PATH = Constants.PROP_FILE_PATH + "/payment_client";
	private static Map<String, String> MD5_KEY_MAP=new HashMap<String, String>();
	private static Map<String, PublicKey> RSA_PUBLIC_KEY_MAP=new HashMap<String, PublicKey>();
	private static Map<String, String> PAY_URL_MAP=new HashMap<String, String>();
	private static Map<String, String> OFFLINE_PAY_URL_MAP=new HashMap<String, String>();
	private static String MD5_KEY="_MD5_KEY";
	private static String RSA_PUBLIC_KEY="_RSA_PUBLIC_KEY";
	private static String PAY_URL="_PAY_URL";
	private static String OFFLINE_PAY_URL="_OFFLINE_PAY_URL";
	
	
	public static String getMd5Key(String appId){
		String md5Str=MD5_KEY_MAP.get(appId);
		if(md5Str==null){
			String key=(appId+MD5_KEY).toUpperCase();
			md5Str=PropertiesKit.getBundleProperties(PAYMENT_CLIENT_PATH, key);
			MD5_KEY_MAP.put(appId, md5Str);
		}
		return md5Str;
	}
	public static PublicKey getRSAPublicKey(String appId){
		PublicKey publicKey=RSA_PUBLIC_KEY_MAP.get(appId);
		if(publicKey==null){
			String key=(appId+RSA_PUBLIC_KEY).toUpperCase();
			String keyStr= PropertiesKit.getBundleProperties(PAYMENT_CLIENT_PATH, key);
			if(keyStr!=null){
				publicKey=RSA.getPublicKey(keyStr);
				RSA_PUBLIC_KEY_MAP.put(appId, publicKey);
			}
		}
		return publicKey;
	}
	
	public static String getPayUrl(String appId){
		String payUrl=PAY_URL_MAP.get(appId);
		if(payUrl==null){
			String key=(appId+PAY_URL).toUpperCase();
			payUrl=PropertiesKit.getBundleProperties(PAYMENT_CLIENT_PATH, key);
			PAY_URL_MAP.put(appId, payUrl);
		}
		return payUrl;
	}
	
	public static String getOfflinePayUrl(String appId) {
		String offlinePayUrl=OFFLINE_PAY_URL_MAP.get(appId);
		if(offlinePayUrl==null){
			String key=(appId+OFFLINE_PAY_URL).toUpperCase();
			offlinePayUrl=PropertiesKit.getBundleProperties(PAYMENT_CLIENT_PATH, key);
			OFFLINE_PAY_URL_MAP.put(appId, offlinePayUrl);
		}
		return offlinePayUrl;
	}

}
