package com.trendy.ow.portal.payment.business;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.util.StringKit;
import com.trendy.fw.common.web.BrowserKit;
import com.trendy.fw.tools.portal.config.PortalConfig;
import com.trendy.ow.portal.payment.config.PayConfig;

public class PayUtil {
	public static int getLocalState(HttpServletRequest request) {
		if (BrowserKit.isMobileBrowser(request)) {
			String ua = StringKit.validStr(request.getHeader("user-agent")).toLowerCase();
			String hua=StringKit.validStr(request.getHeader("HTTP_USER_AGENT")).toLowerCase();
			if (ua.indexOf("micromessenger") > 0||hua.indexOf("micromessenger") > 0) {// 是微信浏览器
				return PayConfig.PL_WEIXIN;
			}else {
				return PayConfig.PL_WAP;
			}
		} else {
			return PayConfig.PL_WEB;
		}

	}

	public static String getDevicePath(HttpServletRequest request) {
		if (BrowserKit.isMobileBrowser(request)) {
			return PortalConfig.WAP_PATH;
		}
		return PortalConfig.WEB_PATH;
	}

	/**
	 * map转url格式
	 * 
	 * @param parameters
	 * @return
	 */
	public static String map2String(Map<String, String> map) {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (StringKit.isValid(value)) {
				sb.append("&" + key + "=" + value);
			}
		}
		if (sb.length() > 1) {
			return sb.substring(1, sb.length());
		}
		return sb.toString();
	}

	public static String buildEncodeQueryString(Map<String, String> map) {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (StringKit.isValid(value)) {
				sb.append("&" + key + "=" + urlEncode(value, Constants.CODE_UNICODE));
			}
		}
		if (sb.length() > 1) {
			return sb.substring(1, sb.length());
		}
		return sb.toString();
	}

	public static String buildDecodeQueryString(Map<String, String> map) {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (StringKit.isValid(value)) {
				sb.append("&" + key + "=" + urlDecode(value, Constants.CODE_UNICODE));
			}
		}
		if (sb.length() > 1) {
			return sb.substring(1, sb.length());
		}
		return sb.toString();
	}

	/**
	 * 回调request参数转换为map
	 * 
	 * @param request
	 * @return
	 */
	public static TreeMap<String, String> request2TreeMap(HttpServletRequest request) {
		TreeMap<String, String> parameters = new TreeMap<String, String>();

		Iterator<String> iter = request.getParameterMap().keySet().iterator();
		while (iter.hasNext()) {
			String name = iter.next();
			parameters.put(name, request.getParameter(name));
		}

		return parameters;
	}

	/**
	 * 对TenPay和aliwap 两层xml字符串解析
	 * 
	 * @param xml
	 * @param tag
	 * @return
	 * @throws DocumentException
	 */
	public static Map<String, String> xml2Map(String xml) throws DocumentException {
		Map<String, String> map = new HashMap<String, String>();
		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();
		List<Element> list = root.elements();
		for (Element e : list) {
			map.put(e.getName(), e.getTextTrim());
		}
		return map;
	}

	/**
	 * url encode
	 * 
	 * @param content
	 * @param charset
	 * @return
	 */
	public static String urlEncode(String content, String charset) {
		String result = null;
		try {
			result = URLEncoder.encode(content, charset);
		} catch (Exception e) {
			result = content;
		}
		return result;
	}

	public static String urlDecode(String content, String charset) {
		String result = null;
		try {
			result = URLDecoder.decode(content, charset);
		} catch (Exception e) {
			result = content;
		}
		return result;
	}

	/**
	 * 获取basePath
	 * 
	 * @param request
	 * @return
	 */
	public static String getBasePath(HttpServletRequest request) {
		String path = request.getContextPath();
		String port = PayConfig.SERVER_PORT;
		if (StringKit.isValid(port)) {
			port = ":" + port;
		}
		return request.getScheme() + "://" + request.getServerName() + port + path + "/";
	}

}
