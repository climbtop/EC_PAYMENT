package com.trendy.ow.portal.payment.weixin.business;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.util.StringKit;
import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.bean.PayGatewayRequestBean;
import com.trendy.ow.portal.payment.business.PayLogBusiness;
import com.trendy.ow.portal.payment.business.PayProcessor;
import com.trendy.ow.portal.payment.cache.StoreInfoCache;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.config.PayErrorCode;
import com.trendy.ow.portal.payment.weixin.bean.WeiXinJsApiConfigBean;
import com.trendy.ow.portal.payment.weixin.config.WeiXinJsApiConfig;

public class WeixinJsApiPayProcessor extends PayProcessor {
	private static Logger log = LoggerFactory.getLogger(WeixinJsApiPayProcessor.class);

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response, PayInfoBean payInfo,
			PayItemBean payItem) throws PortalServletException {
		String storeCode = new StoreInfoCache().getStoreCodeByIdMap().get(payInfo.getStoreId());
		WeiXinJsApiConfigBean configBean = WeiXinJsApiConfig.getJsApiConfig(storeCode);
		if (!StringKit.isValid(configBean.getAppId())) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REDIRECT_EXCEPTION,
					"your request invalid: hava no" + storeCode);
		}
		request.setAttribute("configBean", configBean);
		request.setAttribute("payInfo", payInfo);
		request.setAttribute("payItem", payItem);
		String pagePath = Constants.PAGE_PATH + "common/wp.jsp";
		RequestDispatcher rd = request.getRequestDispatcher(pagePath);
		try {
			rd.forward(request, response);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REDIRECT_EXCEPTION, e.getMessage());
		}
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
				context.put("notifyUrl", basePath + WeiXinJsApiConfig.WX_NOTIFY_URL);
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
