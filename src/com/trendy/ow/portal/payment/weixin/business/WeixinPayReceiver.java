package com.trendy.ow.portal.payment.weixin.business;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.crypto.MD5;
import com.trendy.fw.common.util.DateKit;
import com.trendy.fw.common.util.StringKit;
import com.trendy.fw.common.web.HttpResponseKit;
import com.trendy.fw.common.web.ParamKit;
import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.ow.portal.payment.bean.ChannelNotifyResultBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.business.PayInfoBusiness;
import com.trendy.ow.portal.payment.business.PayItemBusiness;
import com.trendy.ow.portal.payment.business.PayLogBusiness;
import com.trendy.ow.portal.payment.business.PayReceiver;
import com.trendy.ow.portal.payment.business.PayUtil;
import com.trendy.ow.portal.payment.cache.PayChannelCache;
import com.trendy.ow.portal.payment.cache.StoreInfoCache;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.config.PayErrorCode;
import com.trendy.ow.portal.payment.weixin.bean.WeixinConfigBean;
import com.trendy.ow.portal.payment.weixin.cache.WeixinCache;
import com.trendy.ow.portal.payment.weixin.config.WeixinConfig;

public class WeixinPayReceiver extends PayReceiver {
	private static Logger log = LoggerFactory.getLogger(WeixinPayReceiver.class);

	@Override
	public void processNotify(HttpServletRequest request, HttpServletResponse response) throws PortalServletException {
		String returnCode = PayConfig.SUCCESS.toUpperCase();
		String returnMsg = PayConfig.OK.toUpperCase();
		PayItemBean payItem = new PayItemBean();
		try {
			// 获取payItemBean 通知消息处理 异常直接抛出
			payItem = processNotifyRequest(request);
		} catch (Exception e) {
			returnCode = "FAIL";
			returnMsg = e.getMessage();
		}
		StringBuilder sb = new StringBuilder("<xml>");
		sb.append("<return_code><![CDATA[" + returnCode + "]]></return_code>");
		sb.append("<return_msg><![CDATA[" + returnMsg + "]]></return_msg>");
		sb.append("</xml>");

		// 返回支付平台success
		HttpResponseKit.printMessage(response, sb.toString());

		// 业务系统通知
		if (returnCode.equalsIgnoreCase(PayConfig.SUCCESS)) {
			notifyApplication(payItem);
		}
	}

	private PayItemBean processNotifyRequest(HttpServletRequest request) throws PortalServletException {
		String postXml = getRequestPostXml(request);
		log.info("weixin-receiver-is:" + postXml);
		Map<String, String> notifyMap = new HashMap<String, String>();
		try {
			notifyMap = PayUtil.xml2Map(postXml);
		} catch (DocumentException e) {
			log.error("WeixinNotifyReceiver 对notify信息xml解析异常  xmlResult:" + postXml, e);
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"WeixinNotifyReceiver parse xmlResult-xml exception:" + e.getMessage());
		}
		ChannelNotifyResultBean bean = parseNotifyInfo(notifyMap);
		PayItemBusiness itemBusiness = new PayItemBusiness();
		int payItemId = bean.getPayItemId();
		PayItemBean itemBean = itemBusiness.getPayItemByKey(payItemId);
		if (itemBean == null) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"WeixinNotifyReceiver Received invalid notifyInfo:hava no itemId=" + payItemId + " record.");
		}
		if (PayConfig.PAYS_PAYED.equalsIgnoreCase(itemBean.getPayStatus())) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"WeixinNotifyReceiver Received repeated notifyInfo:itemId=" + payItemId + ",payStatus="
							+ PayConfig.PAYS_PAYED);
		}
		String payStatus = bean.getPayStatus();
		Timestamp payTime = bean.getPayTime();

		int payId = itemBean.getPayId();
		PayInfoBusiness infoBusiness = new PayInfoBusiness();
		PayInfoBean infoBean = infoBusiness.getPayInfoByKey(payId);

		int storeId = infoBean.getStoreId();
		StoreInfoCache storeInfoCache = new StoreInfoCache();
		String storeCode = storeInfoCache.getStoreCodeByIdMap().get(storeId);

		// 信息验证
		TreeMap<String, String> paramMap = new TreeMap<String, String>(notifyMap);
		if (!checkNotifyInfo(paramMap, storeCode)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"WeixinNotifyReceiver Received invalid notice:sign fail");
		}
		PayLogBusiness logBusiness = new PayLogBusiness();
		logBusiness.savePayLog(payId, payStatus, "收到支付通知:" + paramMap.toString());

		// 更新支付信息payItem payInfo
		int result = savePayResult(infoBean, itemBean, payStatus, payTime, bean.getPayNumber(), bean.getPayAmount());
		if (result <= 0) {
			log.error("更新支付信息记录payItem|payInfo失败,payId=" + payId + ",itemId=" + itemBean.getPayItemId());
		}
		return itemBean;

	}

	public String getRequestPostXml(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		InputStream is;
		try {
			is = request.getInputStream();
			byte[] buf = new byte[1024];
			while (is.read(buf) != -1) {
				String temp = new String(buf);
				sb.append(temp);
			}
			is.close();
		} catch (IOException e) {
			log.error("getRequestPostXml-exception:" + e.getMessage());
		}
		return sb.toString().trim();
	}

	public ChannelNotifyResultBean parseNotifyInfo(Map<String, String> paramMap) throws PortalServletException {
		log.info("WeixinNotifyReceiver 收到通知信息:" + paramMap.toString());
		String resultCode = paramMap.get("result_code");
		if (!PayConfig.SUCCESS.equalsIgnoreCase(resultCode)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"WeixinNotifyReceiver received no pay success notice:resultCode=" + resultCode);
		}

		String totalFee = paramMap.get("cash_fee");
		String transactionId = paramMap.get("transaction_id");
		String sign = paramMap.get("sign");
		String outTradeNo = paramMap.get("out_trade_no");
		String timeEnd = paramMap.get("time_end");
		if (!StringKit.isValid(totalFee) || !StringKit.isValid(sign) || !StringKit.isValid(outTradeNo)) {
			throw new PortalServletException(
					PayConfig.APP_ID,
					PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"WeixinNotifyReceiver received invalid notice:notifyId||totalFee||transactionId||payResult||sign||out_trade_no is empty.");
		}
		int itemId = 0;
		try {
			itemId = Integer.parseInt(outTradeNo);
		} catch (Exception e) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"WeixinNotifyReceiver received invalid notice:parseInt out_trade_no fail。outTradeNo=" + outTradeNo);
		}

		ChannelNotifyResultBean bean = new ChannelNotifyResultBean();
		bean.setPayItemId(itemId);
		bean.setPayStatus(PayConfig.PAYS_PAYED);
		bean.setPayNumber(transactionId);
		bean.setPayAmount(new BigDecimal(totalFee).divide(new BigDecimal(100)).doubleValue());
		bean.setPayTime(DateKit.str2Timestamp(timeEnd, "yyyyMMddhhmmss"));
		return bean;
	}

	public boolean checkNotifyInfo(TreeMap<String, String> paramMap, String storeCode) throws PortalServletException {
		WeixinCache cache = new WeixinCache();
		WeixinConfigBean configBean = cache.getWeixinConfig(storeCode);
		String markedSign = paramMap.get("sign");
		paramMap.remove("sign");
		String urlParam = PayUtil.buildDecodeQueryString(paramMap);
		String signValue = MD5.getMD5(urlParam + "&key=", configBean.getKey()).toUpperCase();
		return signValue.equals(markedSign);
	}

	@Override
	public void processCallback(HttpServletRequest request, HttpServletResponse response) throws PortalServletException {
		// 获取payItem 有异常直接抛出
		PayItemBean itemBean = processCallbackRequest(request);

		// 统一跳转到业务系统页面
		callbackApplication(itemBean, response);
	}

	private PayItemBean processCallbackRequest(HttpServletRequest request) throws PortalServletException {
		int payItemId = ParamKit.getIntParameter(request, "itemId", 0);
		PayItemBusiness itemBusiness = new PayItemBusiness();
		PayItemBean itemBean = itemBusiness.getPayItemByKey(payItemId);
		if (itemBean == null) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"WeixinJsApiCallback received invalid callbackInfo:hava no itemId=" + payItemId + " record.");
		}
		if (!PayConfig.PAYS_PAYED.equals(itemBean.getPayStatus())) {
			log.info("WeixinJsApiCallback received invalid callbackInfo:noPayedStatusNotifyRequest item.paystatus:"
					+ itemBean.getPayStatus());
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"noPayedStatusNotifyRequest");
		}
		PayChannelCache channelCache = new PayChannelCache();
		String channelCode = channelCache.getPayChannelInfo(itemBean.getChannelId()).getChannelCode();
		if (!WeixinConfig.CHANNEL_CODE_SCAN_CODE.equals(channelCode)) {
			log.info("WeixinScanCallback received invalid callbackInfo:error channel " + channelCode);
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION, "InvaildRequest");
		}
		PayLogBusiness logBusiness = new PayLogBusiness();
		logBusiness.savePayLog(itemBean.getPayId(), itemBean.getPayStatus(), "微信扫码支付跳转信息:payItemId=" + payItemId);
		return itemBean;
	}

}
