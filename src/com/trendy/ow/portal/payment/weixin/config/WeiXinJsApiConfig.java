package com.trendy.ow.portal.payment.weixin.config;

import com.trendy.ow.portal.payment.weixin.bean.WeiXinJsApiConfigBean;

public class WeiXinJsApiConfig {
	
	public static final String WX_NOTIFY_URL = "weixin/WeixinJsApiNotifyReceiver.do";
	public static final String WX_CALL_BACK_URL = "weixin/WeixinJsApiCallback.do";
	public static WeiXinJsApiConfigBean getJsApiConfig(String storeCode){
		WeiXinJsApiConfigBean bean=new WeiXinJsApiConfigBean();
		bean.setStoreCode(storeCode);
		if ("OchirlyOfficial".equals(storeCode)) {
			bean.setAppId("wx31254a520b5e437b");
			bean.setAppKey("1NRDJ8jwteoLQ7ujeQyIdWOGRrzbGub5FoAfngCcSml5QGLqpF3uZewd2th6ljxmBuZm9XUTH0QTIsdZ1BPmGJsA4AxLNXvKkRXQcK6ghjvo8BJu4WcC4v5sYu78N1f2");
			bean.setBody("欧时力官网购物");
			bean.setPartner("1217916901");
			bean.setPartnerKey("c6a1d1fc22a6f232d64aae645523eb51");
		}else if ("FivePlusOfficial".equals(storeCode)) {
			bean.setAppId("wxbcdf0d58922caa13");
			bean.setAppKey("Y1YPRbqLvVXNYE8FiFquyexdnZ1VOHm2vc7IPsGmQZbXZcFbXQcCwxb1eyCQND2sz6UQm3H4mED8PKYlNpqKOVzBzDTWmc1x8DWj3DAsGW807BhRxxnM9raTc9HKSvnK");
			bean.setBody("Fiveplus官网购物");
			bean.setPartner("1219700701");
			bean.setPartnerKey("23c880e2c5160afdcb7401407209466f");
		}
		bean.setBankType("WX");
		bean.setFeeType("1");
		bean.setInputCharset("GBK");
		bean.setSignType("SHA1");
		return bean;
	}
	
}
