package com.trendy.ow.portal.payment.weixin.business;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.util.DateKit;
import com.trendy.fw.common.util.StringKit;
import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayGatewayRequestBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.business.PayLogBusiness;
import com.trendy.ow.portal.payment.business.PayProcessor;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.config.PayErrorCode;
import com.trendy.ow.portal.payment.weixin.bean.WeixinConfigBean;
import com.trendy.ow.portal.payment.weixin.bean.WeixinMicropayResponseBean;
import com.trendy.ow.portal.payment.weixin.cache.WeixinCache;
import com.trendy.ow.portal.payment.weixin.config.WeixinConfig;

public class WeixinMicropayProcessor extends PayProcessor {
	private static Logger log = LoggerFactory.getLogger(WeixinMicropayProcessor.class);
	final String MUST_VALUE_PARAMS = "appid,mch_id,nonce_str,body,out_trade_no,total_fee,spbill_create_ip,auto_code";

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response, PayInfoBean payInfo,
			PayItemBean payItem) throws PortalServletException {
		throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION,
				"ThisPayChannelnotSupportThisRequest");
	}

	@Override
	public Map<String, String> processPayGatewayRequest(PayInfoBean payInfo, PayChannelInfoBean channel,
			String basePath, PayGatewayRequestBean requestBean) throws Exception {
		Map<String, String> context = new HashMap<String, String>();
		try {
			String authCode =requestBean.getAuthCode();
			log.info("authCode:"+authCode);
			if (!StringKit.isValid(authCode)) {
				throw new Exception("AuthCodeIsEmpty");
			}
			final PayItemBean payItem = savePayItem(payInfo, channel);
			if (payItem.getPayItemId() <= 0) {
				PayLogBusiness logBusiness = new PayLogBusiness();
				logBusiness.savePayLog(payInfo.getPayId(), PayConfig.PAYS_WAIT_PAY, "增加支付明细记录失败");
				log.info("save payItem record fail。");
				throw new Exception("InternalError");
			}

			WeixinCache cache = new WeixinCache();
			String storeCode = getStoreCode(payInfo);
			WeixinConfigBean config = cache.getWeixinConfig(storeCode);
			String shopNumber = requestBean.getShopNumber();
			String secret = WeixinConfig.getShopWeixinConfigSecret();
			if (StringKit.isValid(shopNumber)) {
				WeixinConfigBusiness configBusiness = new WeixinConfigBusiness();
				config = configBusiness.getWeixinConfig(shopNumber, secret, storeCode);
			}
			WeixinMicropayResponseBean responseBean = micropay(authCode, payInfo, payItem, config);
			context.put("outTradeNo", responseBean.getOutTradeNo());
			context.put("transactionId", responseBean.getTransactionId());
			context.put("tradeType", responseBean.getTradeType());
			context.put("totalFee", responseBean.getTotalFee());
			context.put("payTime", responseBean.getTimeEnd());
			processNotify(payItem);
		} catch (Exception e) {
			if (e instanceof PortalServletException) {
				log.info("processPayGatewayRequestFail:" + e.getMessage());
				e = new Exception(e.getMessage());
			}
			throw e;
		}
		return context;
	}

	public void processNotify(final PayItemBean itemBean) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					notifyApplication(itemBean);
				} catch (PortalServletException e) {
					log.info("WeixinMicroPayProcessor.processNotify[itemId=" + itemBean.getPayItemId() + "]fail:"
							+ e.getMessage());
				}

			}
		}).start();
	}

	public WeixinMicropayResponseBean micropay(String authCode, PayInfoBean payInfo, PayItemBean payItem,
			WeixinConfigBean configBean) throws PortalServletException {
		PayLogBusiness logBusiness = new PayLogBusiness();
		WeixinPayProcessor processor = new WeixinPayProcessor();
		WeixinMicropayResponseBean responseBean = processor.microPay(configBean, authCode, MUST_VALUE_PARAMS, payInfo,
				payItem);
		if (responseBean.getReturnCode().equalsIgnoreCase(PayConfig.FAIL)) {
			logBusiness
					.savePayLog(payInfo.getPayId(), PayConfig.PAYS_WAIT_PAY, "刷卡支付失败:" + responseBean.getReturnMsg());
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION,
					responseBean.getReturnMsg());
		}
		if (responseBean.getResultCode().equalsIgnoreCase(PayConfig.FAIL)) {
			logBusiness.savePayLog(payInfo.getPayId(), PayConfig.PAYS_WAIT_PAY,
					"刷卡支付失败:" + responseBean.getErrCodeDes());
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION,
					responseBean.getErrCodeDes());
		}
		// 支付成功，更新记录
		double payAmount = new BigDecimal(responseBean.getTotalFee()).divide(new BigDecimal(100)).doubleValue();
		String payNumber = responseBean.getTransactionId();
		Timestamp payTime = DateKit.str2Timestamp(responseBean.getTimeEnd(), "yyyyMMddhhmmss");
		int result = savePayResult(payInfo, payItem, PayConfig.PAYS_PAYED, payTime, payNumber, payAmount);
		if (result <= 0) {
			log.error("更新支付信息记录payItem|payInfo失败,payId=" + payInfo.getPayId() + ",itemId=" + payItem.getPayItemId());
		}
		logBusiness.savePayLog(payInfo.getPayId(), PayConfig.PAYS_PAYED, "刷卡支付成功:支付总金额" + responseBean.getTotalFee());
		return responseBean;
	}

}
