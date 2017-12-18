package com.trendy.ow.portal.payment.alipay.business;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.crypto.MD5;
import com.trendy.fw.common.transfer.HttpClientKit;
import com.trendy.fw.common.transfer.HttpClientResultBean;
import com.trendy.fw.common.util.JsonKit;
import com.trendy.fw.common.util.SetKit;
import com.trendy.fw.common.util.StringKit;
import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.ow.portal.payment.alipay.bean.AliPayWapConfigBean;
import com.trendy.ow.portal.payment.alipay.cache.AliPayWapCache;
import com.trendy.ow.portal.payment.alipay.config.AliPayWapConfig;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.bean.PayGatewayRequestBean;
import com.trendy.ow.portal.payment.business.PayLogBusiness;
import com.trendy.ow.portal.payment.business.PayProcessor;
import com.trendy.ow.portal.payment.business.PayUtil;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.config.PayErrorCode;

public class AliPayWapPayProcessor extends PayProcessor {
	private static Logger log = LoggerFactory.getLogger(AliPayWapPayProcessor.class);

	private static final String encodeFields = "call_back_url,notify_url,req_data,merchant_url";
	private static Set<String> encodeFieldSet = new HashSet<String>();

	static {
		encodeFieldSet = SetKit.string2Set(encodeFields, ",");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response, PayInfoBean payInfo,
			PayItemBean payItem) throws PortalServletException {
		PayLogBusiness logBusiness = new PayLogBusiness();
		// 获取支付链接
		String returnPath = PayUtil.getBasePath(request);
		String url = getPayUrl(payInfo, payItem.getPayItemId(), returnPath);
		logBusiness.savePayLog(payInfo.getPayId(), PayConfig.PAYS_WAIT_PAY, "增加支付明细记录,生成支付链接成功，url:" + url);

		// 转发
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REDIRECT_EXCEPTION, e.getMessage());
		}
	}

	public String getPayUrl(PayInfoBean payInfo, int itemId, String returnPath) throws PortalServletException {
		String storeCode = getStoreCode(payInfo);

		AliPayWapCache cache = new AliPayWapCache();
		AliPayWapConfigBean configBean = cache.getAliPayWapConfig(storeCode);
		log.info("AliPayWapConfigBean:" + JsonKit.toJson(configBean));
		String token = getToken(configBean, returnPath, payInfo.getRequestAmount(),
				String.valueOf(payInfo.getUserId()), String.valueOf(itemId));

		return buildUrl(configBean, token);
	}

	public String getToken(AliPayWapConfigBean configBean, String basePath, double requestAmount, String outUser,
			String outTradeNo) throws PortalServletException {
		TreeMap<String, String> requestMap = new TreeMap<String, String>();
		String notifyUrl = basePath + AliPayWapConfig.ALIPAY_WAP_NOTIFY_URL;
		String callbackUrl = basePath + AliPayWapConfig.ALIPAY_WAP_CALL_BACK_URL;
		String totalFee = new BigDecimal(String.valueOf(requestAmount)).setScale(2).toString();
		String subject = configBean.getSubject();
		String sellerAccountName = configBean.getSellerAccountName();
		String merchantUrl = configBean.getMerchantUrl();
		String payExpire = configBean.getPayExpire();
		StringBuilder reqData = new StringBuilder();
		reqData.append("<direct_trade_create_req>");
		reqData.append("<subject>").append(subject).append("</subject>");
		reqData.append("<out_trade_no>").append(outTradeNo).append("</out_trade_no>");
		reqData.append("<total_fee>").append(totalFee).append("</total_fee>");
		reqData.append("<seller_account_name>").append(sellerAccountName).append("</seller_account_name>");
		reqData.append("<call_back_url>").append(callbackUrl).append("</call_back_url>");
		reqData.append("<notify_url>").append(notifyUrl).append("</notify_url>");
		reqData.append("<out_user>").append(outUser).append("</out_user>");
		reqData.append("<pay_expire>").append(payExpire).append("</pay_expire>");
		reqData.append("<merchant_url>").append(merchantUrl).append("</merchant_url>");
		reqData.append("</direct_trade_create_req>");

		requestMap.put("req_data", reqData.toString());
		requestMap.put("req_id", String.valueOf(System.currentTimeMillis()));
		requestMap.put("sec_id", configBean.getSecId());
		requestMap.put("partner", configBean.getPartner());
		requestMap.put("call_back_url", callbackUrl);
		requestMap.put("format", configBean.getFormat());
		requestMap.put("v", configBean.getV());
		requestMap.put("service", configBean.getCreateService());
		// 获取token
		try {
			String sign = MD5.getMD5(PayUtil.map2String(requestMap), configBean.getKey());
			requestMap.put("sign", sign);
			String result = "";
			HttpClientKit kit = new HttpClientKit();
			HttpClientResultBean bean = kit.postContent(configBean.getAuthenticationBaseUrl(), requestMap,
					Constants.CODE_UNICODE);
			log.info("-----------create wap alipay return:" + JsonKit.toJson(bean));
			if (bean.getResult()) {
				result = java.net.URLDecoder.decode(bean.getResultContent(), Constants.CODE_UNICODE);
				int begin = result.indexOf("<request_token>") + 15;
				int end = result.indexOf("</request_token>");
				return result.substring(begin, end);
			} else {
				throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REDIRECT_EXCEPTION,
						"AliPayWapPlugin get token fail:" + bean.getResultContent());
			}
		} catch (Exception e) {
			if (e instanceof PortalServletException) {
				log.info(e.getMessage());
				throw (PortalServletException) e;
			} else {
				log.error(e.getMessage());
				throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REDIRECT_EXCEPTION,
						"AliPayWapPlugin get token exception:" + e.getMessage());
			}
		}

	}

	public String buildUrl(AliPayWapConfigBean configBean, String token) throws PortalServletException {
		String reqData = "<auth_and_execute_req><request_token>" + token + "</request_token></auth_and_execute_req>";
		Map<String, String> map = new TreeMap<String, String>();
		map.put("service", configBean.getService());
		map.put("sec_id", configBean.getSecId());
		map.put("partner", configBean.getPartner());
		map.put("format", configBean.getFormat());
		map.put("v", configBean.getV());
		map.put("req_data", reqData);

		String sign = MD5.getMD5(PayUtil.map2String(map), configBean.getKey());
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (StringKit.isValid(value)) {
				if (encodeFieldSet.contains(key)) {
					value = PayUtil.urlEncode(value, Constants.CODE_UNICODE);
				}
				sb.append("&");
				sb.append(key);
				sb.append("=");
				sb.append(value);
			}
		}
		sb.insert(0, "sign=" + sign);

		String authenticationBaseUrl = configBean.getAuthenticationBaseUrl();
		log.info("AliPayWap生成的支付链接：" + authenticationBaseUrl + "?" + sb);
		return authenticationBaseUrl + "?" + sb.toString();
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
				context.put("notifyUrl", basePath + AliPayWapConfig.ALIPAY_WAP_NOTIFY_URL);
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
