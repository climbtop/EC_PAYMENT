package com.trendy.ow.portal.payment.weixin.business;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.crypto.MD5;
import com.trendy.fw.common.util.DateKit;
import com.trendy.fw.common.util.StringKit;
import com.trendy.fw.common.web.HttpResponseKit;
import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.fw.tools.order.config.SyncStatusConfig;
import com.trendy.ow.portal.payment.bean.ChannelCallbackResultBean;
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
import com.trendy.ow.portal.payment.weixin.bean.WeiXinJsApiConfigBean;
import com.trendy.ow.portal.payment.weixin.config.WeiXinJsApiConfig;
import com.trendy.ow.portal.payment.weixin.config.WeixinConfig;

public class WeixinJsApiPayReceiver extends PayReceiver {
	private static Logger log = LoggerFactory.getLogger(WeixinJsApiPayReceiver.class);

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
		if (!WeixinConfig.CHANNEL_CODE_JS_API.equals(channelCode)) {
			log.info("WeixinJsApiCallback received invalid callbackInfo:error channel " + channelCode);
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION, "InvaildRequest");
		}

		PayInfoBean infoBean = getPayInfo(itemBean);
		String storeCode = getStoreCode(infoBean);
		if (!checkCallbackInfo(paramMap, storeCode)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"WeixinJsApiCallback received invalid callbackInfo:sign fail.");
		}

		PayLogBusiness logBusiness = new PayLogBusiness();
		logBusiness.savePayLog(itemBean.getPayId(), resultBean.getPayStatus(), "收到支付跳转信息" + paramMap.toString());
		return itemBean;
	}

	public ChannelCallbackResultBean parseCallbackInfo(Map<String, String> paramMap) throws PortalServletException {
		log.info("WeixinJsApiCallback 收到支付信息:" + paramMap.toString());
		String itemIdStr = paramMap.get("itemId");
		String errMsg = paramMap.get("err_msg");
		// String errCode=paramMap.get("err_code");
		// String errDesc=paramMap.get("err_desc");

		if (!StringKit.isValid(itemIdStr) || !StringKit.isValid(errMsg)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"WeixinJsApiCallback received invalid callBackInfo:itemId||errMsg is empty.");
		}
		int itemId = 0;
		try {
			itemId = Integer.parseInt(itemIdStr);
		} catch (Exception e) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"WeixinJsApiCallback received invalid callBackInfo:parseInt itemId fail.itemId=" + itemIdStr);
		}
		String payStatus = PayConfig.PAYS_WAIT_PAY;
		if (errMsg.equals(PayConfig.OK)) {
			payStatus = PayConfig.PAYS_PAYED;
		} else if (errMsg.equals(PayConfig.CANCEL)) {
			payStatus = PayConfig.PAYS_CANCELLED;
		}
		ChannelCallbackResultBean bean = new ChannelCallbackResultBean();
		bean.setPayItemId(itemId);
		bean.setPayStatus(payStatus);
		bean.setPayNumber("");
		return bean;
	}

	public boolean checkCallbackInfo(TreeMap<String, String> paramMap, String storeCode) throws PortalServletException {
		return true;
	}

	private PayItemBean processNotifyRequest(HttpServletRequest request) throws PortalServletException {
		TreeMap<String, String> paramMap = PayUtil.request2TreeMap(request);
		ChannelNotifyResultBean bean = parseNotifyInfo(paramMap);
		PayItemBusiness itemBusiness = new PayItemBusiness();
		int payItemId = bean.getPayItemId();
		PayItemBean itemBean = itemBusiness.getPayItemByKey(payItemId);
		if (itemBean == null) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"WeixinJsApiNotifyReceiver Received invalid callbackInfo:hava no itemId=" + payItemId + " record.");
		}
		String payStatus = bean.getPayStatus();
		Timestamp payTime = bean.getPayTime();

		int payId = itemBean.getPayId();
		PayInfoBusiness infoBusiness = new PayInfoBusiness();
		PayInfoBean infoBean = infoBusiness.getPayInfoByKey(payId);
		if (PayConfig.PAYS_PAYED.equalsIgnoreCase(itemBean.getPayStatus())
				&& !SyncStatusConfig.SYNCS_NONE.equalsIgnoreCase(infoBean.getSyncStatus())) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"WeixinJsApiPayReceiver Received repeated notifyInfo:itemId=" + payItemId + ",payStatus="
							+ PayConfig.PAYS_PAYED);
		}

		int storeId = infoBean.getStoreId();
		StoreInfoCache storeInfoCache = new StoreInfoCache();
		String storeCode = storeInfoCache.getStoreCodeByIdMap().get(storeId);

		// 信息验证
		if (!checkNotifyInfo(paramMap, storeCode)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"WeixinJsApiNotifyReceiver Received invalid notice:sign fail");
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
		log.info("WeixinJsApiNotifyReceiver 收到通知信息:" + paramMap.toString());
		String payResult = paramMap.get("pay_result");
		String tradeState = paramMap.get("trade_state");
		if (!(PayConfig.ZERO.equals(tradeState) || PayConfig.ZERO.equals(payResult))) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"WeixinJsApiNotifyReceiver received no pay success notice:payStatus=" + payResult + "|"
							+ tradeState);
		}
		String notifyId = paramMap.get("notify_id");
		String totalFee = paramMap.get("total_fee");
		String transactionId = paramMap.get("transaction_id");
		String sign = paramMap.get("sign");
		String outTradeNo = paramMap.get("out_trade_no");
		String timeEnd = paramMap.get("time_end");
		if (!StringKit.isValid(notifyId) || !StringKit.isValid(totalFee) || !StringKit.isValid(sign)
				|| !StringKit.isValid(outTradeNo)) {
			throw new PortalServletException(
					PayConfig.APP_ID,
					PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"WeixinJsApiNotifyReceiver received invalid notice:notifyId||totalFee||transactionId||payResult||sign||out_trade_no is empty.");
		}
		int itemId = 0;
		try {
			itemId = Integer.parseInt(outTradeNo);
		} catch (Exception e) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"WeixinJsApiNotifyReceiver received invalid notice:parseInt out_trade_no fail。outTradeNo="
							+ outTradeNo);
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
		WeiXinJsApiConfigBean configBean = WeiXinJsApiConfig.getJsApiConfig(storeCode);
		String markedSign = paramMap.get("sign");
		paramMap.remove("sign");
		String urlParam = PayUtil.buildDecodeQueryString(paramMap);
		String signValue = MD5.getMD5(urlParam + "&key=", configBean.getPartnerKey()).toUpperCase();
		return signValue.equals(markedSign);
	}

}
