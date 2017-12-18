package com.trendy.ow.portal.payment.alipay.business;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.ow.portal.payment.alipay.config.AliPayAppConfig;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.bean.PayGatewayRequestBean;
import com.trendy.ow.portal.payment.business.PayLogBusiness;
import com.trendy.ow.portal.payment.business.PayProcessor;
import com.trendy.ow.portal.payment.config.PayConfig;

public class AliPayAppPayProcessor extends PayProcessor {
	private static Logger log = LoggerFactory.getLogger(AliPayAppPayProcessor.class);

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response, PayInfoBean payInfo,
			PayItemBean payItem) throws PortalServletException {
		// TODO Auto-generated method stub

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
				context.put("notifyUrl", basePath + AliPayAppConfig.ALIPAY_APP_NOTIFY_URL);
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
