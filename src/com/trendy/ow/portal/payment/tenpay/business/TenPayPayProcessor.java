package com.trendy.ow.portal.payment.tenpay.business;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.crypto.MD5;
import com.trendy.fw.common.util.JsonKit;
import com.trendy.fw.common.util.SetKit;
import com.trendy.fw.tools.exception.PortalServletException;
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
import com.trendy.ow.portal.payment.tenpay.bean.TenPayConfigBean;
import com.trendy.ow.portal.payment.tenpay.cache.TenPayCache;
import com.trendy.ow.portal.payment.tenpay.config.TenPayConfig;

public class TenPayPayProcessor extends PayProcessor {
	private static Logger log = LoggerFactory.getLogger(TenPayPayProcessor.class);

	private static final String encodeFields = "body,return_url,notify_url";
	private static Set<String> encodeFieldSet = new HashSet<String>();

	static {
		encodeFieldSet = SetKit.string2Set(encodeFields, ",");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response, PayInfoBean payInfo,
			PayItemBean payItem) throws PortalServletException {
		PayLogBusiness logBusiness = new PayLogBusiness();
		// 获取支付链接
		String basePath = PayUtil.getBasePath(request);
		String storeCode = getStoreCode(payInfo);
		PayChannelInfoBean channel = new PayChannelCache().getPayChannelInfo(payItem.getChannelId());

		String url = getPayURL(payInfo, payItem.getPayItemId(), channel, storeCode, basePath, payItem.getIpAddress());
		logBusiness.savePayLog(payInfo.getPayId(), PayConfig.PAYS_WAIT_PAY, "增加支付明细记录,生成支付链接成功，url:" + url);

		// 转发
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REDIRECT_EXCEPTION, e.getMessage());
		}

	}

	public String getPayURL(PayInfoBean payInfo, int itemId, PayChannelInfoBean channel, String storeCode,
			String basePath, String ipAddress) throws PortalServletException {
		TenPayCache cache = new TenPayCache();
		TenPayConfigBean configBean = cache.getTenPayConfig(storeCode);
		log.info("TenPayConfigBean:" + JsonKit.toJson(configBean));
		String notifyUrl = basePath + TenPayConfig.TENPAY_NOTIFY_URL;
		String returnUrl = basePath + TenPayConfig.TENPAY_CALL_BACK_URL;
		String totalFee = String.valueOf((new BigDecimal(String.valueOf(payInfo.getRequestAmount()))
				.multiply(new BigDecimal(100))).intValue());
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("input_charset", configBean.getInputCharset());
		parameters.put("bank_type", configBean.getBankType(channel.getChannelCode()));
		parameters.put("body", configBean.getBody());
		parameters.put("return_url", returnUrl);
		parameters.put("notify_url", notifyUrl);
		parameters.put("partner", configBean.getPartner());
		parameters.put("input_charset", configBean.getInputCharset());
		parameters.put("out_trade_no", String.valueOf(itemId));
		parameters.put("total_fee", totalFee);
		parameters.put("fee_type", configBean.getFeeType());
		parameters.put("spbill_create_ip", ipAddress);
		String sign = MD5.getMD5(PayUtil.map2String(parameters) + "&key=", configBean.getKey()).toUpperCase();
		StringBuffer sb = new StringBuffer();
		for (Iterator<String> iter = parameters.keySet().iterator(); iter.hasNext();) {
			String key = iter.next();
			String value = (String) parameters.get(key);
			if (value != null && !"".equals(value)) {
				if (encodeFieldSet.contains(key))
					value = PayUtil.urlEncode(value, Constants.CODE_UNICODE);
				sb.append("&");
				sb.append(key);
				sb.append("=");
				sb.append(value);
			}
		}
		sb.append("&").append("sign").append("=").append(sign);
		String url = configBean.getAuthenticationBaseUrl() + "?" + sb.substring(1, sb.length()).toString();
		log.info("TenPayPlugin-生成链接:" + url);
		return url;
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
				context.put("notifyUrl", basePath + TenPayConfig.TENPAY_NOTIFY_URL);
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
