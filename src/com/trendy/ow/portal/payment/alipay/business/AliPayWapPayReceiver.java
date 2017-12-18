package com.trendy.ow.portal.payment.alipay.business;

import java.sql.Timestamp;
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
import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.fw.tools.order.config.SyncStatusConfig;
import com.trendy.ow.portal.payment.alipay.bean.AliPayWapConfigBean;
import com.trendy.ow.portal.payment.alipay.cache.AliPayWapCache;
import com.trendy.ow.portal.payment.bean.ChannelCallbackResultBean;
import com.trendy.ow.portal.payment.bean.ChannelNotifyResultBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.business.PayInfoBusiness;
import com.trendy.ow.portal.payment.business.PayItemBusiness;
import com.trendy.ow.portal.payment.business.PayLogBusiness;
import com.trendy.ow.portal.payment.business.PayReceiver;
import com.trendy.ow.portal.payment.business.PayUtil;
import com.trendy.ow.portal.payment.cache.StoreInfoCache;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.config.PayErrorCode;

public class AliPayWapPayReceiver extends PayReceiver {
	private static Logger log = LoggerFactory.getLogger(AliPayWapPayReceiver.class);

	@Override
	public void processNotify(HttpServletRequest request, HttpServletResponse response) throws PortalServletException {
		// 获取payItemBean 通知消息处理 异常直接抛出
		PayItemBean payItem = processNotifyRequest(request);

		// 返回支付平台success
		HttpResponseKit.printMessage(response, PayConfig.SUCCESS);

		// 业务系统通知
		notifyApplication(payItem);
	}

	@Override
	public void processCallback(HttpServletRequest request, HttpServletResponse response) throws PortalServletException {
		// 获取payItem 有异常直接抛出
		PayItemBean itemBean = processCallbackRequest(request);

		// 统一跳转到业务系统页面
		callbackApplication(itemBean, response);
	}

	private PayItemBean processCallbackRequest(HttpServletRequest request) throws PortalServletException {
		TreeMap<String, String> paramMap = PayUtil.request2TreeMap(request);
		ChannelCallbackResultBean resultBean = parseCallbackInfo(paramMap);
		PayItemBusiness itemBusiness = new PayItemBusiness();
		int payItemId = resultBean.getPayItemId();
		PayItemBean itemBean = itemBusiness.getPayItemByKey(payItemId);
		if (itemBean == null) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"AlipayWapCallback received invalid callbackInfo:hava no itemId=" + payItemId + " record.");
		}
		String payStatus = resultBean.getPayStatus();
		String tradeNo = resultBean.getPayNumber();

		PayInfoBean infoBean = getPayInfo(itemBean);
		String storeCode = getStoreCode(infoBean);
		if (!checkCallbackInfo(paramMap, storeCode)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"AlipayWapCallback received invalid callbackInfo:sign fail.");
		}

		itemBean.setPayStatus(payStatus);
		itemBean.setPayNumber(tradeNo);
		itemBusiness.updatePayItem(itemBean);

		PayLogBusiness logBusiness = new PayLogBusiness();
		logBusiness.savePayLog(itemBean.getPayId(), payStatus, "收到支付跳转信息" + paramMap.toString());
		return itemBean;
	}

	public ChannelCallbackResultBean parseCallbackInfo(Map<String, String> paramMap) throws PortalServletException {
		log.info("AlipayWapCallback 收到支付信息：" + paramMap);
		String result = paramMap.get("result");
		if (!result.equals(PayConfig.SUCCESS)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"AlipayWapCallback received invalid callBackInfo,result:" + result);
		}
		String outTradeNo = paramMap.get("out_trade_no");
		String tradeNo = paramMap.get("trade_no");
		String sign = paramMap.get("sign");
		if (!StringKit.isValid(outTradeNo) || !StringKit.isValid(tradeNo) || !StringKit.isValid(sign)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"AlipayWapCallback received invalid callBackInfo:outTradeNo||result||tradeNo||sign is empty.");
		}
		int itemId = 0;
		try {
			itemId = Integer.parseInt(outTradeNo);
		} catch (Exception e) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"AlipayWapCallback received invalid callBackInfo:parseInt outTradeNo fail.outTradeNo=" + outTradeNo);
		}

		ChannelCallbackResultBean bean = new ChannelCallbackResultBean();
		bean.setPayItemId(itemId);
		bean.setPayStatus(PayConfig.PAYS_PAYED);
		bean.setPayNumber(tradeNo);
		return bean;
	}

	public boolean checkCallbackInfo(TreeMap<String, String> paramMap, String storeCode) throws PortalServletException {
		AliPayWapCache cache = new AliPayWapCache();
		AliPayWapConfigBean bean = cache.getAliPayWapConfig(storeCode);
		String markedSign = paramMap.get("sign");
		String securityCode = bean.getKey();
		String result = paramMap.get("result");
		String requestToken = paramMap.get("request_token");
		String outTradeNo = paramMap.get("out_trade_no");
		String tradeNo = paramMap.get("trade_no");

		TreeMap<String, String> resultMap = new TreeMap<String, String>();
		resultMap.put("result", result);
		resultMap.put("request_token", requestToken);
		resultMap.put("out_trade_no", outTradeNo);
		resultMap.put("trade_no", tradeNo);
		String signString = AlipayUtil.getSignString(resultMap);
		String sign = MD5.getMD5(signString, securityCode);
		return sign.equals(markedSign);
	}

	private PayItemBean processNotifyRequest(HttpServletRequest request) throws PortalServletException {
		TreeMap<String, String> paramMap = PayUtil.request2TreeMap(request);
		ChannelNotifyResultBean bean = parseNotifyInfo(paramMap);
		PayItemBusiness itemBusiness = new PayItemBusiness();
		int payItemId = bean.getPayItemId();
		PayItemBean itemBean = itemBusiness.getPayItemByKey(payItemId);
		if (itemBean == null) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"AlipayWapNotify Received invalid notifyInfo:hava no itemId=" + payItemId + " record.");
		}
		String payStatus = bean.getPayStatus();
		Timestamp payTime = bean.getPayTime();

		int payId = itemBean.getPayId();
		PayInfoBusiness infoBusiness = new PayInfoBusiness();
		PayInfoBean infoBean = infoBusiness.getPayInfoByKey(payId);
		if (PayConfig.PAYS_PAYED.equalsIgnoreCase(itemBean.getPayStatus())
				&& !SyncStatusConfig.SYNCS_NONE.equalsIgnoreCase(infoBean.getSyncStatus())) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"AlipayWapNotify Received repeated notifyInfo:itemId=" + payItemId + ",payStatus="
							+ PayConfig.PAYS_PAYED);
		}

		int storeId = infoBean.getStoreId();
		StoreInfoCache storeInfoCache = new StoreInfoCache();
		String storeCode = storeInfoCache.getStoreCodeByIdMap().get(storeId);

		// 信息验证
		if (!checkNotifyInfo(paramMap, storeCode)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"AlipayWapNotify Received invalid notice:sign fail");
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

	public ChannelNotifyResultBean parseNotifyInfo(Map<String, String> paramMap) throws PortalServletException {
		log.info("AlipayWapNotifyReceiver 收到通知信息:" + paramMap.toString());
		String notifyData = paramMap.get("notify_data");
		if (!StringKit.isValid(notifyData)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"AlipayWapNotify received invalid notice:notify_data is empty.");
		}
		notifyData = notifyData.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		Map<String, String> notifyMap;
		try {
			notifyMap = PayUtil.xml2Map(notifyData);
		} catch (DocumentException e2) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"AlipayWapNotify parse notifyData-xml exception:notifyData:" + notifyData);
		}
		String tradeStatus = notifyMap.get("trade_status");
		if (!("TRADE_FINISHED".equals(tradeStatus) || "TRADE_SUCCESS".equals(tradeStatus))) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"AlipayWapNotify received invalid notice:not understand trade_status。trade_status=" + tradeStatus);
		}
		String outTradeNo = notifyMap.get("out_trade_no");
		String totalFee = notifyMap.get("total_fee");
		String tradeNo = notifyMap.get("trade_no");
		String gmtPayment = notifyMap.get("gmt_payment");
		int itemId = 0;
		try {
			itemId = Integer.parseInt(outTradeNo);
		} catch (Exception e) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"AlipayWapNotify received invalid notice:parseInt out_trade_no fail。outTradeNo=" + outTradeNo);
		}
		ChannelNotifyResultBean bean = new ChannelNotifyResultBean();
		bean.setPayItemId(itemId);
		bean.setPayStatus(PayConfig.PAYS_PAYED);
		bean.setPayNumber(tradeNo);
		bean.setPayAmount(Double.valueOf(totalFee));
		bean.setPayTime(DateKit.str2Timestamp(gmtPayment, "yyyy-MM-dd HH:mm:ss"));

		return bean;
	}

	public boolean checkNotifyInfo(TreeMap<String, String> paramMap, String storeCode) throws PortalServletException {
		AliPayWapCache cache = new AliPayWapCache();
		AliPayWapConfigBean bean = cache.getAliPayWapConfig(storeCode);
		try {
			String markedSign = paramMap.get("sign");
			String securityCode = bean.getKey();
			String service = paramMap.get("service");
			String v = paramMap.get("v");
			String secId = paramMap.get("sec_id");
			String notifyData = paramMap.get("notify_data");
			log.info("notifyReceiverMsgCheck 通知参数为：" + "service=" + service + "&v=" + v + "&sec_id=" + secId
					+ "&notify_data=" + notifyData);
			String signData = "service=" + service + "&v=" + v + "&sec_id=" + secId + "&notify_data=" + notifyData;
			String sign = MD5.getMD5(signData, securityCode);
			return sign.equals(markedSign);
		} catch (Exception e) {
			log.error("notifyReceiverMsgCheck 支付回调信息 md5验证sign异常", e);
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"AlipayWapNotify notifyReceiverMsgCheck md5-sign exception:" + e.getMessage());
		}
	}

}
