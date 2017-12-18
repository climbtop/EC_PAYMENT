package com.trendy.ow.portal.payment.weixin.config;

import java.util.ArrayList;
import java.util.List;

import com.trendy.fw.common.util.PropertiesKit;
import com.trendy.ow.portal.payment.config.PayConfig;
public class WeixinConfig {
	public static String COMPANY_CODE="WeixinPay";
	public static String CHANNEL_CODE_PUBLICN_NUMBER="WeixinPay_PublicNumber";
	public static String CHANNEL_CODE_JS_API="WeixinPay_JsApi";
	public static String CHANNEL_CODE_SCAN_CODE="WeixinPay_ScanCode";
	public static String CHANNEL_CODE_MICROPAY="WeixinPay_Micropay";
	
	public static int SCAN_CODE_IMG_WIDTH=150;
	public static int SCAN_CODE_IMG_HEIGHT=150;
	//配置的后缀名
	
	private static String KEY="Key";
	private static String APP_ID="AppId";
	private static String MCH_ID="MchId";
	private static String BODY="Body";
	private static String DETAIL="Detail";
	private static String ATTACH="Attach";
	private static String PAY_API_URL = "PayApiUrl";
	private static String MICRO_PAY_URL = "MicroPayUrl";
	// 通知地址
	public static final String WX_NOTIFY_URL = "weixin/WeixinNotifyReceiver.do";
	public static final String WX_CALLBACK_URL = "weixin/WeixinCallback.do";
	public static final String WX_CREATE_SCAN_CODE_URL = "weixin/WeixinCreatScanCode.do";
	
	public static List<String> COMPANY_CONFIG_KEY_LIST = null;
	public static List<String> STORE_CONFIG_KEY_LIST = null;
	
	private static String SHOP_WEIXIN_CONFIG_URL =null;
	private static String SHOP_WEIXIN_CONFIG_SECRET =null;
	
	static {
		new WeixinConfig().init();
	}

	private void init() {
		initCompanyConfigKeyList();
		initStoreConfigKeyList();
		initScanCodeImgSize();
	}

	private void initCompanyConfigKeyList() {
		COMPANY_CONFIG_KEY_LIST = new ArrayList<String>();
		COMPANY_CONFIG_KEY_LIST.add(PAY_API_URL);
		COMPANY_CONFIG_KEY_LIST.add(MICRO_PAY_URL);
	}

	private void initStoreConfigKeyList() {
		STORE_CONFIG_KEY_LIST = new ArrayList<String>();
		STORE_CONFIG_KEY_LIST.add(KEY);
		STORE_CONFIG_KEY_LIST.add(APP_ID);
		STORE_CONFIG_KEY_LIST.add(MCH_ID);
		STORE_CONFIG_KEY_LIST.add(DETAIL);
		STORE_CONFIG_KEY_LIST.add(BODY);
		STORE_CONFIG_KEY_LIST.add(ATTACH);
	}
	
	public static String getStoreConfigCode(String storeCode, String configKey) {
		return storeCode + "_" + COMPANY_CODE + "_" + configKey;
	}

	public static String getCompanyConfigCode(String configKey) {
		return COMPANY_CODE + "_" + configKey;
	}
	
	private void initScanCodeImgSize(){
		try {
			String width = PropertiesKit.getBundleProperties(PayConfig.PAYMENT_PROP_FILE_NAME, "WX_PAY_SCAN_CODE_IMG_WIDTH");
			String height = PropertiesKit.getBundleProperties(PayConfig.PAYMENT_PROP_FILE_NAME, "WX_PAY_SCAN_CODE_IMG_HEIGHT");
			SCAN_CODE_IMG_WIDTH=Integer.parseInt(width);
			SCAN_CODE_IMG_HEIGHT=Integer.parseInt(height);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	public static String getShopWeixinConfigUrl() {
		if (SHOP_WEIXIN_CONFIG_URL==null) {
			SHOP_WEIXIN_CONFIG_URL=PropertiesKit.getBundleProperties(PayConfig.PAYMENT_PROP_FILE_NAME, "SHOP_WEIXIN_CONFIG_URL");
		}
		return SHOP_WEIXIN_CONFIG_URL;
	}
	
	public static String getShopWeixinConfigSecret() {
		if (SHOP_WEIXIN_CONFIG_SECRET==null) {
			SHOP_WEIXIN_CONFIG_SECRET=PropertiesKit.getBundleProperties(PayConfig.PAYMENT_PROP_FILE_NAME, "SHOP_WEIXIN_CONFIG_SECRET");
		}
		return SHOP_WEIXIN_CONFIG_SECRET;
	}
}
