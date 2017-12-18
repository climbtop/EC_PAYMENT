package com.trendy.ow.portal.payment.alipay.business;

import java.util.Map.Entry;
import java.util.TreeMap;

import com.trendy.fw.common.util.StringKit;

public class AlipayUtil {
	public static String getSignString(TreeMap<String, String> map) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (!StringKit.isValid(value) || key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("sign_type")) {
				continue;
			}
			sb.append("&").append(key + "=" + value);
		}

		String result = "";
		if (sb.length() > 1) {
			result = sb.substring(1, sb.length());
		}
		return result;
	}
}
