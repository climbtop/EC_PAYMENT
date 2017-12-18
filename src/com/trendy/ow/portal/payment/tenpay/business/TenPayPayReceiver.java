package com.trendy.ow.portal.payment.tenpay.business;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.crypto.MD5;
import com.trendy.fw.common.transfer.HttpClientKit;
import com.trendy.fw.common.transfer.HttpClientResultBean;
import com.trendy.fw.common.util.DateKit;
import com.trendy.fw.common.util.JsonKit;
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
import com.trendy.ow.portal.payment.cache.StoreInfoCache;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.config.PayErrorCode;
import com.trendy.ow.portal.payment.tenpay.bean.TenPayConfigBean;
import com.trendy.ow.portal.payment.tenpay.cache.TenPayCache;

public class TenPayPayReceiver extends PayReceiver {
	private static Logger log = LoggerFactory.getLogger(TenPayPayReceiver.class);
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
					"TenpayCallback received invalid callbackInfo:hava no itemId=" + payItemId + " record.");
		}
		String payStatus = resultBean.getPayStatus();
		String tradeNo = resultBean.getPayNumber();	
		
		PayInfoBean infoBean = getPayInfo(itemBean);
		String storeCode = getStoreCode(infoBean);
		if (!checkCallbackInfo(paramMap, storeCode)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"TenpayCallback received invalid callbackInfo:sign fail.");
		}

		itemBean.setPayStatus(payStatus);
		itemBean.setPayNumber(tradeNo);
		itemBusiness.updatePayItem(itemBean);
		PayLogBusiness logBusiness = new PayLogBusiness();
		logBusiness.savePayLog(itemBean.getPayId(), payStatus, "收到支付跳转信息" + paramMap.toString());
		return itemBean;
	}
	
	
	
	public ChannelCallbackResultBean parseCallbackInfo(Map<String, String> paramMap) throws PortalServletException {
		log.info("TenpayCallback 收到支付信息:" + paramMap.toString());
		String tradeState = paramMap.get("trade_state");
		if (!PayConfig.ZERO.equals(tradeState)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"TenpayCallback received not success callBackInfo.tradeStatus:" + tradeState);
		} 
		String sign = paramMap.get("sign");
		String outTradeNo = paramMap.get("out_trade_no");
		String transactionId = paramMap.get("transaction_id");

		if (!StringKit.isValid(tradeState) || !StringKit.isValid(transactionId) || !StringKit.isValid(sign)
				|| !StringKit.isValid(outTradeNo)) {
			throw new PortalServletException(PayConfig.APP_ID,PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"TenpayCallback received invalid callBackInfo:tradeState||sign||out_trade_no is empty.");
		}
		int itemId = 0;
		try {
			itemId = Integer.parseInt(outTradeNo);
		} catch (Exception e) {
			throw new PortalServletException(PayConfig.APP_ID,PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"TenpayCallback received invalid callBackInfo:parseInt outTradeNo fail.outTradeNo=" + outTradeNo);
		}
		ChannelCallbackResultBean bean = new ChannelCallbackResultBean();
		bean.setPayItemId(itemId);
		bean.setPayStatus(PayConfig.PAYS_PAYED);
		bean.setPayNumber(transactionId);
		return bean;
	}


	
	public boolean checkCallbackInfo(TreeMap<String, String> paramMap, String storeCode) throws PortalServletException {
		if("true".equals(PayConfig.IS_TEST)){
			return true;
		}
		TenPayCache cache = new TenPayCache();
		TenPayConfigBean configBean = cache.getTenPayConfig(storeCode);
		return this.checkSignature(paramMap, configBean.getKey());
	}


	protected boolean checkSignature(TreeMap<String, String> parameters, String securityCode) {
		String sign = null;
		try {
			String markedSign = parameters.get("sign");
			parameters.remove("sign");
			String signData = PayUtil.buildDecodeQueryString(parameters);
			sign = MD5.getMD5(signData + "&key=", securityCode).toUpperCase();
			return sign.equals(markedSign);
		} catch (Exception e) {
			log.error("支付回调信息 md5验证sign异常", e);
		}
		return false;
	}
	
	
	private PayItemBean processNotifyRequest(HttpServletRequest request) throws PortalServletException {
		TreeMap<String, String> paramMap = PayUtil.request2TreeMap(request);
		ChannelNotifyResultBean bean = parseNotifyInfo(paramMap);
		PayItemBusiness itemBusiness = new PayItemBusiness();
		int payItemId = bean.getPayItemId();
		PayItemBean itemBean = itemBusiness.getPayItemByKey(payItemId);
		if (itemBean == null) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_CALLBACK_EXCEPTION,
					"TenPayNotifyReceiver Received invalid callbackInfo:hava no itemId=" + payItemId + " record.");
		}
		String payStatus = bean.getPayStatus();
		Timestamp payTime = bean.getPayTime();

		int payId = itemBean.getPayId();
		PayInfoBusiness infoBusiness = new PayInfoBusiness();
		PayInfoBean infoBean = infoBusiness.getPayInfoByKey(payId);
		if (PayConfig.PAYS_PAYED.equalsIgnoreCase(itemBean.getPayStatus())&& !SyncStatusConfig.SYNCS_NONE.equalsIgnoreCase(infoBean.getSyncStatus())) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"TenPayNotifyReceiver Received repeated notifyInfo:itemId=" + payItemId + ",payStatus="+PayConfig.PAYS_PAYED);
		}

		int storeId = infoBean.getStoreId();
		StoreInfoCache storeInfoCache = new StoreInfoCache();
		String storeCode = storeInfoCache.getStoreCodeByIdMap().get(storeId);

		// 信息验证
		if (!checkNotifyInfo(paramMap, storeCode)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"TenPayNotifyReceiver Received invalid notice:sign fail");
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
		log.info("TenPayNotifyReceiver 收到通知信息:" + paramMap.toString());
		String tradeState = paramMap.get("trade_state");
		if (!PayConfig.ZERO.equals(tradeState)) {
			throw new PortalServletException(PayConfig.APP_ID,PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"TenPayNotifyReceiver received no success notice:tradeState=" + tradeState);
		} 
		String notifyId = paramMap.get("notify_id");
		String totalFee = paramMap.get("total_fee");
		String transactionId = paramMap.get("transaction_id");
		String sign = paramMap.get("sign");
		String outTradeNo = paramMap.get("out_trade_no");
		String timeEnd = paramMap.get("time_end");
		if (!StringKit.isValid(notifyId) || !StringKit.isValid(totalFee) || !StringKit.isValid(tradeState)
				|| !StringKit.isValid(sign) || !StringKit.isValid(outTradeNo)) {
			throw new PortalServletException(PayConfig.APP_ID,
					PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"TenPayNotifyReceiver received invalid notice:notifyId||totalFee||transactionId||tradeState||sign||out_trade_no is empty.");
		}
		int itemId = 0;
		try {
			itemId = Integer.parseInt(outTradeNo);
		} catch (Exception e) {
			throw new PortalServletException(PayConfig.APP_ID,PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"TenPayNotifyReceiver received invalid notice:parseInt out_trade_no fail。outTradeNo=" + outTradeNo);
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
		if("true".equals(PayConfig.IS_TEST)){
			return true;
		}
		TenPayCache cache = new TenPayCache();
		TenPayConfigBean configBean = cache.getTenPayConfig(storeCode);
		if(!this.checkSignature(paramMap, configBean.getKey())){
			return false;
		}
		String verifyNotifyUrl=configBean.getVerifyNotifyUrl();
		
		TreeMap<String, String> reqMap=new TreeMap<String, String>();
		reqMap.put("notify_id", paramMap.get("notify_id"));
		reqMap.put("partner", paramMap.get("partner"));
		reqMap.put("sign_type", paramMap.get("sign_type"));
		reqMap.put("input_charset", paramMap.get("input_charset"));
		String reqUri=PayUtil.map2String(reqMap);
		String sign = MD5.getMD5(reqUri + "&key=", configBean.getKey());
		reqMap.put("sign", sign);
	
		log.info("TenPayNotifyReceiver 调用查询接口查询信息 url=" +verifyNotifyUrl+":POST-params="+ JsonKit.toJson(reqMap));
		String xmlResult = "";
		try {
			HttpClientKit httpClientKit = new HttpClientKit();
			HttpClientResultBean resultBean = httpClientKit.postContent(verifyNotifyUrl, reqMap, Constants.CODE_UNICODE);
			xmlResult=resultBean.getResultContent();
			log.info("TenPayNotifyReceiver 查询notify信息 返回:" + xmlResult);
		} catch (Exception e) {
			log.error("TenPayNotifyReceiver 查询notify信息 接口出现异常:" +e.getMessage(), e);
			throw new PortalServletException(PayConfig.APP_ID,PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"TenPayNotifyReceiver query notify_api exception:" + e.getMessage());
		}
		Map<String, String> notifyMap = new HashMap<String, String>();
		try {
			notifyMap = PayUtil.xml2Map(xmlResult);
		} catch (Exception e) {
			log.error("TenPayNotifyReceiver 对notify信息xml解析异常  xmlResult:" + xmlResult, e);
			throw new PortalServletException(PayConfig.APP_ID,PayErrorCode.RECIVE_NOTIFY_EXCEPTION,
					"TenPayNotifyReceiver parse xmlResult-xml exception:" + e.getMessage());
		}
		String retcode = notifyMap.get("retcode");
		String totalFee = notifyMap.get("total_fee");
		totalFee = new BigDecimal(totalFee).divide(new BigDecimal(100)).toString();
		if(!PayConfig.ZERO.equals(retcode)) {
			log.info("TenPayNotifyReceiver verifyNotifyId retmsg:"+notifyMap.get("retmsg"));
			return false;
		}
		return true;
	}
	
	
}
