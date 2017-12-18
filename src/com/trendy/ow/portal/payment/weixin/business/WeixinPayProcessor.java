package com.trendy.ow.portal.payment.weixin.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.crypto.MD5;
import com.trendy.fw.common.transfer.HttpClientKit;
import com.trendy.fw.common.transfer.HttpClientResultBean;
import com.trendy.fw.common.util.ListKit;
import com.trendy.fw.common.util.StringKit;
import com.trendy.fw.tools.criphertext.CiphertextKit;
import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.business.PayUtil;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.config.PayErrorCode;
import com.trendy.ow.portal.payment.weixin.bean.WeixinConfigBean;
import com.trendy.ow.portal.payment.weixin.bean.WeixinPreOrderRequestBean;
import com.trendy.ow.portal.payment.weixin.bean.WeixinPreOrderResponseBean;
import com.trendy.ow.portal.payment.weixin.bean.WeixinMicropayRequestBean;
import com.trendy.ow.portal.payment.weixin.bean.WeixinMicropayResponseBean;

public class WeixinPayProcessor {
	private static Logger log = LoggerFactory.getLogger(WeixinPayProcessor.class);

	public WeixinPreOrderRequestBean getWeixinPreOrderRequestBean(WeixinConfigBean configBean, String tradeType,
			String notifyUrl, String spbillCreateIp, String totalFee, String outTradeNo) {
		WeixinPreOrderRequestBean preOrderBean = new WeixinPreOrderRequestBean();
		preOrderBean.setAppId(configBean.getAppId());
		preOrderBean.setAttach(configBean.getAttach());
		preOrderBean.setBody(configBean.getBody());
		preOrderBean.setDetail(configBean.getDetail());
		preOrderBean.setMchId(configBean.getMchId());
		preOrderBean.setAttach(configBean.getAttach());
		preOrderBean.setNonceStr(CiphertextKit.getRandomValue(16));
		preOrderBean.setOutTradeNo(outTradeNo);
		preOrderBean.setTotalFee(totalFee);
		preOrderBean.setSpbillCreateIp(spbillCreateIp);
		preOrderBean.setNotifyUrl(notifyUrl);
		preOrderBean.setTradeType(tradeType);
		return preOrderBean;
	}

	public TreeMap<String, String> getPreOrderParamTreeMap(WeixinPreOrderRequestBean preOrderRequestBean,
			String appKey, String mustValueParams) throws Exception {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("appid", preOrderRequestBean.getAppId());
		map.put("mch_id", preOrderRequestBean.getMchId());
		map.put("device_info", preOrderRequestBean.getDeviceInfo());
		map.put("nonce_str", preOrderRequestBean.getNonceStr());
		map.put("body", preOrderRequestBean.getBody());
		map.put("detail", preOrderRequestBean.getDetail());
		map.put("attach", preOrderRequestBean.getAttach());
		map.put("out_trade_no", preOrderRequestBean.getOutTradeNo());
		map.put("fee_type", preOrderRequestBean.getFeeType());
		map.put("total_fee", preOrderRequestBean.getTotalFee());
		map.put("spbill_create_ip", preOrderRequestBean.getSpbillCreateIp());
		map.put("time_start", preOrderRequestBean.getTimeStart());
		map.put("time_expire", preOrderRequestBean.getTimeExpire());
		map.put("goods_tag", preOrderRequestBean.getGoodsTag());
		map.put("notify_url", preOrderRequestBean.getNotifyUrl());
		map.put("trade_type", preOrderRequestBean.getTradeType());
		map.put("product_id", preOrderRequestBean.getProductId());
		map.put("openid", preOrderRequestBean.getOpenId());
		String sign = signParam(map, mustValueParams, appKey);
		map.put("sign", sign);
		return map;
	}

	public WeixinPreOrderResponseBean preOrder(WeixinConfigBean configBean, TreeMap<String, String> paramTreeMap)
			throws Exception {
		WeixinPreOrderResponseBean responseBean = new WeixinPreOrderResponseBean();
		String payApiUrl = configBean.getPayApiUrl();
		log.info("preOrder-payApiUrl:" + payApiUrl);
		String postXml = getPostXml(paramTreeMap);
		HttpClientKit httpClientKit = new HttpClientKit();
		log.info("preOrder-postXml:" + postXml);
		HttpClientResultBean resultBean = httpClientKit.postContent(payApiUrl, postXml, Constants.CODE_UNICODE);
		String returnXml = resultBean.getResultContent();
		log.info("preOrder-reponse:" + returnXml);
		Map<String, String> returnMap = PayUtil.xml2Map(returnXml);
		String returnCode = returnMap.get("return_code");
		responseBean.setReturnCode(returnCode);
		if (PayConfig.SUCCESS.equalsIgnoreCase(returnCode)) {
			String resultCode = returnMap.get("result_code");
			responseBean.setResultCode(resultCode);
			responseBean.setAppid(returnMap.get("appid"));
			responseBean.setMchId(returnMap.get("mch_id"));
			responseBean.setDeviceInfo(StringKit.validStr(returnMap.get("device_info")));
			responseBean.setNonceStr(returnMap.get("nonce_str"));
			responseBean.setSign(returnMap.get("sign"));
			responseBean.setErrCode(StringKit.validStr(returnMap.get("err_code")));
			responseBean.setErrCodeDes(StringKit.validStr(returnMap.get("err_code_des")));
			if (PayConfig.SUCCESS.equalsIgnoreCase(resultCode)) {
				responseBean.setTradeType(returnMap.get("trade_type"));
				responseBean.setPrepayId(returnMap.get("prepay_id"));
				responseBean.setCodeUrl(StringKit.validStr(returnMap.get("code_url")));
			}
		} else {
			responseBean.setReturnMsg(returnMap.get("return_msg"));
		}
		return responseBean;
	}

	public WeixinPreOrderResponseBean preJsApiOrder(WeixinConfigBean configBean, String tradeType, String notifyUrl,
			String openId, String mustValueParam, PayInfoBean payInfo, PayItemBean payItem)
			throws PortalServletException {
		try {
			String spbillCreateIp = payItem.getIpAddress();
			String totalFee = String.valueOf((new BigDecimal(String.valueOf(payInfo.getRequestAmount()))
					.multiply(new BigDecimal(100))).intValue());
			String outTradeNo = String.valueOf(payItem.getPayItemId());
			WeixinPreOrderRequestBean requestBean = getWeixinPreOrderRequestBean(configBean, tradeType, notifyUrl,
					spbillCreateIp, totalFee, outTradeNo);
			requestBean.setOpenId(openId);
			TreeMap<String, String> paramTreeMap = getPreOrderParamTreeMap(requestBean, configBean.getKey(),
					mustValueParam);
			WeixinPreOrderResponseBean responseBean = preOrder(configBean, paramTreeMap);
			return responseBean;
		} catch (Exception e) {
			log.error("preOrder-exception:" + e.getMessage(), e);
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION, e.getMessage());
		}
	}

	public WeixinPreOrderResponseBean preNativeOrder(WeixinConfigBean configBean, String tradeType, String notifyUrl,
			String productId, String mustValueParam, PayInfoBean payInfo, PayItemBean payItem)
			throws PortalServletException {
		try {
			String spbillCreateIp = payItem.getIpAddress();
			String totalFee = String.valueOf((new BigDecimal(String.valueOf(payInfo.getRequestAmount()))
					.multiply(new BigDecimal(100))).intValue());
			String outTradeNo = String.valueOf(payItem.getPayItemId());
			WeixinPreOrderRequestBean requestBean = getWeixinPreOrderRequestBean(configBean, tradeType, notifyUrl,
					spbillCreateIp, totalFee, outTradeNo);
			requestBean.setProductId(productId);
			TreeMap<String, String> paramTreeMap = getPreOrderParamTreeMap(requestBean, configBean.getKey(),
					mustValueParam);
			WeixinPreOrderResponseBean responseBean = preOrder(configBean, paramTreeMap);
			return responseBean;
		} catch (Exception e) {
			log.error("preOrder-exception:" + e.getMessage(), e);
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION, e.getMessage());
		}
	}

	public TreeMap<String, String> getBrandWCPayRequest(String appId, String prepayId, String md5key)
			throws PortalServletException {
		try {
			TreeMap<String, String> map = new TreeMap<String, String>();
			map.put("appId", appId);
			map.put("timeStamp", getWxTimeStamp());
			map.put("nonceStr", CiphertextKit.getRandomValue(16));
			map.put("package", "prepay_id=" + prepayId);
			String sign = signParam(map, "", md5key);
			map.put("signType", "MD5");
			map.put("paySign", sign);
			return map;
		} catch (Exception e) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION,
					"getBrandWCPayRequestInfo exception:" + e.getMessage());
		}
	}

	public String getWxTimeStamp() {
		return Long.toString(new Date().getTime() / 1000);
	}

	public String signParam(TreeMap<String, String> map, String mustValueParams, String md5key) throws Exception {
		List<String> list = ListKit.string2List(mustValueParams, ",");
		StringBuffer sb = new StringBuffer();
		Set<Entry<String, String>> entries = map.entrySet();
		for (Entry<String, String> entry : entries) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (key.equals("sign")) {
				continue;
			}
			if (StringKit.isValid(value)) {
				sb.append(key + "=" + value + "&");
			} else if (list.contains(key)) {
				throw new Exception(key + " must value");
			}
		}

		return MD5.getMD5(sb.toString() + "key=", md5key).toUpperCase();
	}

	public String getPostXml(TreeMap<String, String> map) {
		StringBuffer sb = new StringBuffer("<xml>");
		Set<Entry<String, String>> entries = map.entrySet();
		for (Entry<String, String> entry : entries) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (StringKit.isValid(value)) {
				sb.append("<" + key + ">").append(value).append("</" + key + ">");
			}
		}
		sb.append("</xml>");
		return sb.toString();
	}

	public WeixinMicropayRequestBean getWeixinSwipingCardPayRequestBean(WeixinConfigBean configBean, String authCode,
			String spbillCreateIp, String totalFee, String outTradeNo) {
		WeixinMicropayRequestBean requestBean = new WeixinMicropayRequestBean();
		requestBean.setAppId(configBean.getAppId());
		requestBean.setAttach(configBean.getAttach());
		requestBean.setDetail(configBean.getDetail());
		requestBean.setBody(configBean.getBody());
		requestBean.setMchId(configBean.getMchId());
		requestBean.setAttach(configBean.getAttach());
		requestBean.setNonceStr(CiphertextKit.getRandomValue(16));
		requestBean.setOutTradeNo(outTradeNo);
		requestBean.setTotalFee(totalFee);
		requestBean.setSpbillCreateIp(spbillCreateIp);
		requestBean.setAuthCode(authCode);
		return requestBean;
	}

	public TreeMap<String, String> getWeixinSwipingCardPayRquestParamTreeMap(WeixinMicropayRequestBean requestBean,
			String appKey, String mustValueParams) throws Exception {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("appid", requestBean.getAppId());
		map.put("attach", requestBean.getAttach());
		map.put("auth_code", requestBean.getAuthCode());
		map.put("body", requestBean.getBody());
		map.put("device_info", requestBean.getDeviceInfo());
		map.put("goods_tag", requestBean.getGoodsTag());
		map.put("mch_id", requestBean.getMchId());
		map.put("nonce_str", requestBean.getNonceStr());
		map.put("out_trade_no", requestBean.getOutTradeNo());
		map.put("spbill_create_ip", requestBean.getSpbillCreateIp());
		map.put("total_fee", requestBean.getTotalFee());
		map.put("detail", requestBean.getDetail());
		map.put("fee_type", requestBean.getFeeType());
		String sign = signParam(map, mustValueParams, appKey);
		map.put("sign", sign);
		return map;
	}

	public WeixinMicropayResponseBean microPay(WeixinConfigBean configBean, String authCode, String mustValueParam,
			PayInfoBean payInfo, PayItemBean payItem) throws PortalServletException {
		try {
			String spbillCreateIp = payItem.getIpAddress();
			String totalFee = String.valueOf((new BigDecimal(String.valueOf(payInfo.getRequestAmount()))
					.multiply(new BigDecimal(100))).intValue());
			String outTradeNo = String.valueOf(payItem.getPayItemId());
			WeixinMicropayRequestBean requestBean = getWeixinSwipingCardPayRequestBean(configBean, authCode,
					spbillCreateIp, totalFee, outTradeNo);
			TreeMap<String, String> paramTreeMap = getWeixinSwipingCardPayRquestParamTreeMap(requestBean,
					configBean.getKey(), mustValueParam);
			WeixinMicropayResponseBean responseBean = microPay(configBean, paramTreeMap);
			return responseBean;
		} catch (Exception e) {
			log.error("microPay-exception:" + e.getMessage(), e);
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION, e.getMessage());
		}
	}

	public WeixinMicropayResponseBean microPay(WeixinConfigBean configBean, TreeMap<String, String> paramTreeMap)
			throws Exception {
		WeixinMicropayResponseBean responseBean = new WeixinMicropayResponseBean();
		String payApiUrl = configBean.getMicroPayUrl();
		log.info("microPay-payApiUrl:" + payApiUrl);

		String postXml = getPostXml(paramTreeMap);
		HttpClientKit httpClientKit = new HttpClientKit();
		log.info("microPay-postXml:" + postXml);

		HttpClientResultBean resultBean = httpClientKit.postContent(payApiUrl, postXml, Constants.CODE_UNICODE);
		String returnXml = resultBean.getResultContent();
		log.info("microPay-response:" + returnXml);

		Map<String, String> returnMap = PayUtil.xml2Map(returnXml);
		String returnCode = returnMap.get("return_code");
		responseBean.setReturnCode(returnCode);
		if (PayConfig.SUCCESS.equalsIgnoreCase(returnCode)) {
			String resultCode = returnMap.get("result_code");
			responseBean.setResultCode(resultCode);
			responseBean.setAppid(returnMap.get("appid"));
			responseBean.setMchId(returnMap.get("mch_id"));
			responseBean.setDeviceInfo(StringKit.validStr(returnMap.get("device_info")));
			responseBean.setNonceStr(returnMap.get("nonce_str"));
			responseBean.setSign(returnMap.get("sign"));
			responseBean.setErrCode(StringKit.validStr(returnMap.get("err_code")));
			responseBean.setErrCodeDes(StringKit.validStr(returnMap.get("err_code_des")));
			if (PayConfig.SUCCESS.equalsIgnoreCase(resultCode)) {
				responseBean.setTradeType(returnMap.get("trade_type"));
				responseBean.setTotalFee(returnMap.get("total_fee"));
				responseBean.setOutTradeNo(returnMap.get("out_trade_no"));
				responseBean.setTransactionId(returnMap.get("transaction_id"));
				responseBean.setTimeEnd(returnMap.get("time_end"));
			}
		} else {
			responseBean.setReturnMsg(returnMap.get("return_msg"));
		}
		return responseBean;
	}
}
