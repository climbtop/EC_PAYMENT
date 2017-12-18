package com.trendy.ow.portal.payment.alipay.business;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.crypto.MD5;
import com.trendy.fw.common.util.JsonKit;
import com.trendy.fw.common.util.StringKit;
import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.ow.portal.payment.alipay.bean.AliPayWebConfigBean;
import com.trendy.ow.portal.payment.alipay.cache.AliPayWebCache;
import com.trendy.ow.portal.payment.alipay.config.AliPayWebConfig;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.bean.PayGatewayRequestBean;
import com.trendy.ow.portal.payment.business.PayLogBusiness;
import com.trendy.ow.portal.payment.business.PayProcessor;
import com.trendy.ow.portal.payment.business.PayUtil;
import com.trendy.ow.portal.payment.cache.PayChannelCache;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.config.PayErrorCode;

public class AliPayWebPayProcessor extends PayProcessor {
	private static Logger log = LoggerFactory.getLogger(AliPayWebPayProcessor.class);

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response, PayInfoBean payInfo,
			PayItemBean payItem) throws PortalServletException {
		// 获取支付链接
		PayLogBusiness logBusiness = new PayLogBusiness();
		String basePath = PayUtil.getBasePath(request);
		String storeCode = getStoreCode(payInfo);
		PayChannelCache channelCache = new PayChannelCache();
		PayChannelInfoBean channel = channelCache.getPayChannelInfo(payItem.getChannelId());
		String url = getPayUrl(payInfo.getRequestAmount(), payItem.getPayItemId(), storeCode, basePath,
				payItem.getIpAddress(), channel.getChannelCode());
		log.info("payurl:" + url);
		logBusiness.savePayLog(payInfo.getPayId(), PayConfig.PAYS_WAIT_PAY, "增加支付明细记录,生成支付链接成功，url:" + url);

		// 转发
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REDIRECT_EXCEPTION, e.getMessage());
		}

	}

	public String getPayUrl(double requestAmount, int itemId, String storeCode, String basePath, String ipAddress,
			String channelCode) throws PortalServletException {
		AliPayWebCache cache = new AliPayWebCache();
		AliPayWebConfigBean bean = cache.getAliPayWebConfig(storeCode);
		log.info("AliPayWebConfigBean:" + JsonKit.toJson(bean));

		String notifyUrl = basePath + AliPayWebConfig.ALIPAY_WEB_NOTIFY_URL;
		String returnUrl = basePath + AliPayWebConfig.ALIPAY_WEB_CALL_BACK_URL;
		String totalFee = new BigDecimal(String.valueOf(requestAmount)).setScale(2).toString();

		TreeMap<String, String> paramMap = new TreeMap<String, String>();
		paramMap.put("service", bean.getService());
		paramMap.put("partner", bean.getPartner());
		paramMap.put("_input_charset", bean.getInputCharset());
		paramMap.put("payment_type", bean.getPaymentType());
		paramMap.put("notify_url", notifyUrl);
		paramMap.put("return_url", returnUrl);
		paramMap.put("seller_email", bean.getSellerEmail());
		paramMap.put("out_trade_no", String.valueOf(itemId));
		paramMap.put("subject", bean.getSubject());
		paramMap.put("total_fee", totalFee);
		paramMap.put("body", bean.getBody());
		paramMap.put("show_url", bean.getShowUrl());
		String payMethod = bean.getPayMethod().get(channelCode);
		if (StringKit.isValid(payMethod)) {
			paramMap.put("paymethod", payMethod);
		}
		String defaultBank = bean.getDefaultBank().get(channelCode);
		if (StringKit.isValid(defaultBank)) {
			paramMap.put("defaultbank", defaultBank);
		}
		try {
			paramMap.put("anti_phishing_key",
					queryTimestamp(bean.getAuthenticationBaseUrl(), bean.getPartner(), bean.getInputCharset()));
		} catch (Exception e) {
			log.error("AliPayWeb anti_phishing_key获取失败，" + e.getMessage());
		}
		paramMap.put("exter_invoke_ip", ipAddress);

		// 待请求参数数组
		TreeMap<String, String> signParamMap = getSignDataMap(paramMap, bean.getKey(), bean.getSignType());
		return bean.getAuthenticationBaseUrl() + "?" + PayUtil.buildEncodeQueryString(signParamMap);
	}

	/**
	 * 用于防钓鱼，调用接口query_timestamp来获取时间戳的处理函数 注意：远程解析XML出错，与服务器是否支持SSL等配置有关
	 * 
	 * @return 时间戳字符串
	 * @throws IOException
	 * @throws DocumentException
	 * @throws MalformedURLException
	 */
	@SuppressWarnings("unchecked")
	private static String queryTimestamp(String authenticationBaseURL, String partner, String inputCharset)
			throws MalformedURLException, DocumentException, IOException {

		// 构造访问query_timestamp接口的URL串
		String strUrl = authenticationBaseURL + "?service=query_timestamp&partner=" + partner + "&_input_charset="
				+ inputCharset;
		StringBuffer result = new StringBuffer();
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new URL(strUrl).openStream());
		List<Node> nodeList = doc.selectNodes("//alipay/*");
		for (Node node : nodeList) {
			// 截取部分不需要解析的信息
			if (node.getName().equals("is_success") && node.getText().equals("T")) {
				// 判断是否有成功标示
				List<Node> nodeList1 = doc.selectNodes("//response/timestamp/*");
				for (Node node1 : nodeList1) {
					result.append(node1.getText());
				}
			}
		}
		return result.toString();
	}

	public static TreeMap<String, String> getSignDataMap(TreeMap<String, String> paramMap, String md5Key,
			String signType) {
		TreeMap<String, String> resultMap = new TreeMap<String, String>();
		StringBuffer sb = new StringBuffer();
		for (Iterator<String> iter = paramMap.keySet().iterator(); iter.hasNext();) {
			String key = iter.next();
			String value = paramMap.get(key);
			if (!StringKit.isValid(value) || key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("sign_type")) {
				continue;
			}
			resultMap.put(key, value);
			sb.append("&").append(key).append("=").append(value);
		}
		String uri = "";
		if (sb.length() > 1) {
			uri = sb.substring(1, sb.length());
		}

		// 生成签名结果
		String mysign = "";
		if (signType.equals("MD5")) {
			mysign = MD5.getMD5(uri, md5Key);
		}
		// 签名结果与签名方式加入请求提交参数组中
		resultMap.put("sign", mysign);
		resultMap.put("sign_type", signType);
		return resultMap;
	}

	@Override
	public Map<String, String> processPayGatewayRequest(PayInfoBean payInfo, PayChannelInfoBean channel, String basePath,
			PayGatewayRequestBean requestBean) throws Exception {
		Map<String, String> context = new HashMap<String, String>();
		try {
			PayItemBean payItem = savePayItem(payInfo, channel);
			if (payItem.getPayItemId() <= 0) {
				PayLogBusiness logBusiness = new PayLogBusiness();
				logBusiness.savePayLog(payInfo.getPayId(), PayConfig.PAYS_WAIT_PAY, "增加支付明细记录失败");
				log.info("save payItem record fail。");
				throw new Exception("InternalError");
			}
			if (requestBean.getRequestType() == PayConfig.REQUEST_TYPE_ZERO) {
				context.put("tradeNo", String.valueOf(payItem.getPayItemId()));
				context.put("notifyUrl", basePath + AliPayWebConfig.ALIPAY_WEB_NOTIFY_URL);
			} else {
				throw new Exception("nowThisPayChannelNotSupportThisRequestType");
			}
		} catch (Exception e) {
			if (e instanceof PortalServletException) {
				log.info("processPayOrderFail:" + e.getMessage());
				e = new Exception("processPayOrderFail:" + e.getMessage());
			}
			throw e;
		}
		return context;
	}

}
