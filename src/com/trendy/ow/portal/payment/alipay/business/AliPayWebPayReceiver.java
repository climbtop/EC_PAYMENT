package com.trendy.ow.portal.payment.alipay.business;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
import com.trendy.ow.portal.payment.alipay.bean.AliPayWebConfigBean;
import com.trendy.ow.portal.payment.alipay.cache.AliPayWebCache;
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

public class AliPayWebPayReceiver extends PayReceiver {
	private static Logger log = LoggerFactory.getLogger(AliPayWebPayReceiver.class);

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
					"AlipayWebCallback received invalid callbackInfo:hava no itemId=" + payItemId + " record.");
		}
		String payStatus = resultBean.getPayStatus();
		String tradeNo = resultBean.getPayNumber();

		PayInfoBean infoBean = getPayInfo(itemBean);
		String storeCode = getStoreCode(infoBean);
		if (!checkCallbackInfo(paramMap, storeCode)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"AlipayWebCallback received invalid callbackInfo:sign fail.");
		}

		itemBean.setPayStatus(payStatus);
		itemBean.setPayNumber(tradeNo);
		itemBusiness.updatePayItem(itemBean);

		PayLogBusiness logBusiness = new PayLogBusiness();
		logBusiness.savePayLog(itemBean.getPayId(), payStatus, "收到支付跳转信息" + paramMap.toString());
		return itemBean;
	}

	public ChannelCallbackResultBean parseCallbackInfo(Map<String, String> paramMap) throws PortalServletException {
		log.info("AlipayWebCallback 收到支付信息：" + paramMap);
		String tradeStatus = paramMap.get("trade_status");
		if (!("TRADE_FINISHED".equals(tradeStatus) || "TRADE_SUCCESS".equals(tradeStatus))) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"AlipayWebCallback received invalid callBackInfo,tradeStatus:" + tradeStatus);
		}
		String outTradeNo = paramMap.get("out_trade_no");
		String tradeNo = paramMap.get("trade_no");
		String sign = paramMap.get("sign");
		if (!StringKit.isValid(outTradeNo) || !StringKit.isValid(tradeStatus) || !StringKit.isValid(tradeNo)
				|| !StringKit.isValid(sign)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"AlipayWebCallback received invalid callBackInfo:outTradeNo||tradeStatus||tradeNo||sign is empty.");
		}
		int itemId = 0;
		try {
			itemId = Integer.parseInt(outTradeNo);
		} catch (Exception e) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"AlipayWebCallback received invalid callBackInfo:parseInt outTradeNo fail.outTradeNo=" + outTradeNo);
		}

		ChannelCallbackResultBean bean = new ChannelCallbackResultBean();
		bean.setPayItemId(itemId);
		bean.setPayStatus(PayConfig.PAYS_PAYED);
		bean.setPayNumber(tradeNo);
		return bean;
	}

	public boolean checkCallbackInfo(TreeMap<String, String> paramMap, String storeCode) throws PortalServletException {
		AliPayWebCache cache = new AliPayWebCache();
		AliPayWebConfigBean configBean = cache.getAliPayWebConfig(storeCode);
		return this.checkMsg(paramMap, configBean);
	}

	private boolean checkMsg(TreeMap<String, String> paramMap, AliPayWebConfigBean configBean) {
		String notify_id = paramMap.get("notify_id");
		// 获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求
		String veryfy_url = configBean.getAuthenticationBaseUrl() + "?service=" + configBean.getNotifyVerifyService()
				+ "&partner=" + configBean.getPartner() + "&notify_id=" + notify_id;
		try {
			URL url = new URL(veryfy_url);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String responseTxt = in.readLine().toString();
			if (!"true".equalsIgnoreCase(responseTxt)) {
				return false;
			}
		} catch (Exception e) {
			log.error("AlipayWeb checkMsg 获取远程服务器ATN结果异常，veryfy_url=" + veryfy_url + "。", e);
			return false;
		}

		String markedSign = paramMap.get("sign");
		String preSignStr = AlipayUtil.getSignString(paramMap);

		String sign = MD5.getMD5(preSignStr, configBean.getKey());
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
					"AlipayWebNotify received invalid callbackInfo:hava no itemId=" + payItemId + " record.");
		}
		String payStatus = bean.getPayStatus();
		Timestamp payTime = bean.getPayTime();

		int payId = itemBean.getPayId();
		PayInfoBusiness infoBusiness = new PayInfoBusiness();
		PayInfoBean infoBean = infoBusiness.getPayInfoByKey(payId);
		if (PayConfig.PAYS_PAYED.equalsIgnoreCase(itemBean.getPayStatus())
				&& !SyncStatusConfig.SYNCS_NONE.equalsIgnoreCase(infoBean.getSyncStatus())) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"AlipayWebNotify Received repeated notifyInfo:itemId=" + payItemId + ",payStatus="
							+ PayConfig.PAYS_PAYED);
		}

		int storeId = infoBean.getStoreId();
		StoreInfoCache storeInfoCache = new StoreInfoCache();
		String storeCode = storeInfoCache.getStoreCodeByIdMap().get(storeId);

		// 信息验证
		if (!checkNotifyInfo(paramMap, storeCode)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"AlipayWebNotify Received invalid notice:sign fail");
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
		log.info("AlipayWebNotifyReceiver 收到通知信息:" + paramMap.toString());
		String tradeStatus = paramMap.get("trade_status");
		if (!("TRADE_FINISHED".equals(tradeStatus) || "TRADE_SUCCESS".equals(tradeStatus))) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"AlipayWebNotifyReceiver received not success payInfo.tradeStatus=" + tradeStatus);
		}
		String outTradeNo = paramMap.get("out_trade_no");
		String tradeNo = paramMap.get("trade_no");
		String sign = paramMap.get("sign");
		String gmtPayment = paramMap.get("gmt_payment");
		String totalFee = paramMap.get("total_fee");
		String notifyId = paramMap.get("notify_id");
		String signType = paramMap.get("sign_type");
		if (!StringKit.isValid(outTradeNo) || !StringKit.isValid(tradeNo) || !StringKit.isValid(sign)
				|| !StringKit.isValid(notifyId) || !StringKit.isValid(gmtPayment) || !StringKit.isValid(totalFee)
				|| !StringKit.isValid(signType)) {
			throw new PortalServletException(
					PayConfig.APP_ID,
					PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"AlipayWebNotifyReceiver received invalid notice:outTradeNo||tradeStatus||tradeNo||sign||notifyId||gmtPayment||totalFee is empty.");
		}
		int itemId = 0;
		try {
			itemId = Integer.parseInt(outTradeNo);
		} catch (Exception e) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"AlipayWebNotifyReceiver received invalid notice:parseInt outTradeNo fail.outTradeNo=" + outTradeNo);
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
		AliPayWebCache cache = new AliPayWebCache();
		AliPayWebConfigBean configBean = cache.getAliPayWebConfig(storeCode);
		return checkMsg(paramMap, configBean);
	}

}
