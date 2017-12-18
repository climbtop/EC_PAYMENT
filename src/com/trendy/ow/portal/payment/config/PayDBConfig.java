package com.trendy.ow.portal.payment.config;

import com.trendy.fw.common.util.PropertiesKit;

public class PayDBConfig {

	public static String DB_LINK_ORDER_W = PropertiesKit.getBundleProperties(PayConfig.PAYMENT_PROP_FILE_NAME,
			"DB_LINK_PAYMENT_W");

	public static String DB_LINK_ORDER_R = PropertiesKit.getBundleProperties(PayConfig.PAYMENT_PROP_FILE_NAME,
			"DB_LINK_PAYMENT_R");
}
