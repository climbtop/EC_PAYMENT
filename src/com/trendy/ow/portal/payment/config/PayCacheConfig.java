package com.trendy.ow.portal.payment.config;

public class PayCacheConfig {
	
	public final static String PAY_COMPANY_INFO_ALL_MAP_KEY = "PAY_COMPANY_INFO_ALL_MAP_KEY";
	
	private final static String PAY_COMPANY_INFO_KEY = "PAY_COMPANY_INFO_";

	private final static String PAY_CHANNEL_INFO_KEY = "PAY_CHANNEL_INFO_";

	private final static String PAY_CHANNEL_INFO_LIST_BY_COMPANY_ID_KEY = "PAY_CHANNEL_INFO_LIST_BY_COMPANY_ID_";

	private final static String PAY_CHANNEL_INFO_LIST_GROUP_BY_COMPANY_MAP_KEY = "PAY_CHANNEL_INFO_LIST_GROUP_BY_COMPANY_MAP_";

	private final static String PAY_STORE_CHANNEL_ALL_LIST_KEY = "PAY_STORE_CHANNEL_ALL_LIST_";
	
	private final static String PAY_STORE_CHANNEL_KEY = "PAY_STORE_CHANNEL_";

	public final static String PAY_CONFIG_ALL_MAP_KEY = "PAY_CONFIG_ALL_MAP_KEY";

	public final static String PAY_STORE_INFO_CODE_ALL_MAP_KEY = "PAY_STORE_INFO_CODE_ALL_MAP_KEY";

	public final static String SYS_APPLICATION_CODE_ALL_MAP_KEY = "SYS_APPLICATION_CODE_ALL_MAP_KEY";

	private final static String APP_CONFIG_KEY = "APP_CONFIG_";
	
	private final static String CLIENT_CONFIG_KEY = "STORE_CLIENT_CONFIG_";

	private final static String ALI_PAY_WAP_CONFIG_KEY = "ALI_PAY_WAP_CONFIG_";

	private final static String TEN_PAY_CONFIG_KEY = "TEN_PAY_CONFIG_";

	private final static String WEI_XIN_JS_API_CONFIG_KEY = "WEI_XIN_JS_API_CONFIG_";
	
	private final static String WEIXIN_CONFIG_KEY = "WEIXIN_CONFIG_";
	private final static String SHOP_WEIXIN_CONFIG_KEY = "SHOP_WEIXIN_CONFIG_";
	
	private final static String ALI_PAY_WEB_CONFIG_KEY = "ALI_PAY_WEB_CONFIG_";
	
	private final static String ALI_PAY_APP_CONFIG_KEY = "ALI_PAY_APP_CONFIG_";
	
	private final static String COMPANY_CONFIG_VALUE_MAP_KEY = "COMPANY_CONFIG_VALUE_MAP_";
	
	private final static String STORE_CONFIG_VALUE_MAP_KEY = "STORE_CONFIG_VALUE_MAP_";
	
	private final static String CHANNEL_CONFIG_VALUE_MAP_KEY = "CHANNEL_CONFIG_VALUE_MAP_";

	public static String getPayChannelInfoListByCompanyIdKey(int companyId) {
		return PAY_CHANNEL_INFO_LIST_BY_COMPANY_ID_KEY + companyId+"_KEY";
	}
	

	public static String getPayChannelInfoListGroupByCompanyMapKey(int store, int payLocal, String companyCode,
			String channelCode) {
		return PAY_CHANNEL_INFO_LIST_GROUP_BY_COMPANY_MAP_KEY + store+"_"+payLocal+"_"+companyCode+"_"+channelCode;
	}

	public static String getAliPayWapConfigKey(String storeCode) {
		return ALI_PAY_WAP_CONFIG_KEY + storeCode;
	}

	public static String getTenPayConfigKey(String storeCode) {
		return TEN_PAY_CONFIG_KEY + storeCode;
	}

	public static String getWeiXinJsApiConfigKey(String storeCode) {
		return WEI_XIN_JS_API_CONFIG_KEY + storeCode;
	}
	
	public static String getWeixinConfigKey(String storeCode) {
		return WEIXIN_CONFIG_KEY + storeCode;
	}
	
	public static String getShopWeixinConfigKey(String ShopNumber,String storeCode) {
		return SHOP_WEIXIN_CONFIG_KEY + ShopNumber+"_"+storeCode+"_KEY";
	}
	
	public static String getAliPayWebConfigKey(String storeCode) {
		return ALI_PAY_WEB_CONFIG_KEY + storeCode;
	}

	public static String getAliPayAppConfigKey(String storeCode) {
		return ALI_PAY_APP_CONFIG_KEY + storeCode;
	}

	
	public static String getAppConfigKey(String appCode) {
		return APP_CONFIG_KEY+appCode;
	}

	public static String getClientConfigKey(String storeCode) {
		return CLIENT_CONFIG_KEY+storeCode;
	}

	public static String getPayCompanyInfoKey(int companyId) {
		return PAY_COMPANY_INFO_KEY+companyId;
	}

	public static String getPayCompanyInfoKey(String companyCode) {
		return PAY_COMPANY_INFO_KEY+companyCode;
	}
	
	public static String getPayChannelInfoKey(int channelId) {
		return PAY_CHANNEL_INFO_KEY+channelId;
	}
	
	public static String getPayChannelInfoKey(String channelCode) {
		return PAY_CHANNEL_INFO_KEY+channelCode;
	}

	public static String getPayStoreChannelKey(int storeCode,int channelId) {
		return PAY_STORE_CHANNEL_KEY+"STORE_"+storeCode+"_CHANNEL_"+channelId+"_KEY";
	}

	public static String getPayStoreChannelAllListKey(int storeId) {
		return PAY_STORE_CHANNEL_ALL_LIST_KEY+storeId+"_KEY";
	}

	public static String getCompanyConfigValueMapKey(String companyCode) {
		return COMPANY_CONFIG_VALUE_MAP_KEY+companyCode+"_KEY";
	}

	public static String getStoreConfigValueMapKey(String companyCode,String storeCode) {
		return STORE_CONFIG_VALUE_MAP_KEY+companyCode+"_"+storeCode+"_KEY";
	}

	public static String getChannelConfigValueMapKey(String companyCode,String channelCode) {
		return CHANNEL_CONFIG_VALUE_MAP_KEY+companyCode+"_"+channelCode+"_KEY";
	}

}
