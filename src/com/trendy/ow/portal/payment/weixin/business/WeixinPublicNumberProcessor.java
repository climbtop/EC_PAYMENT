package com.trendy.ow.portal.payment.weixin.business;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.util.JsonKit;
import com.trendy.fw.common.web.BrowserKit;
import com.trendy.fw.common.web.ParamKit;
import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.fw.tools.language.util.LanguageKit;
import com.trendy.fw.tools.portal.config.PortalConfig;
import com.trendy.ow.passport.sso.business.SsoAccountHelper;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayGatewayRequestBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.business.PayLogBusiness;
import com.trendy.ow.portal.payment.business.PayProcessor;
import com.trendy.ow.portal.payment.business.PayUtil;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.config.PayErrorCode;
import com.trendy.ow.portal.payment.weixin.bean.WeixinConfigBean;
import com.trendy.ow.portal.payment.weixin.bean.WeixinPreOrderResponseBean;
import com.trendy.ow.portal.payment.weixin.cache.WeixinCache;
import com.trendy.ow.portal.payment.weixin.config.WeiXinJsApiConfig;
import com.trendy.ow.portal.payment.weixin.config.WeixinConfig;

public class WeixinPublicNumberProcessor extends PayProcessor {
	private static Logger log = LoggerFactory.getLogger(WeixinPublicNumberProcessor.class);
	final String MUST_VALUE_PARAMS = "appid,mch_id,nonce_str,body,out_trade_no,total_fee,spbill_create_ip,notify_url,trade_type,openid";
	final String TRADE_TYPE = "JSAPI";

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response, PayInfoBean payInfo,
			PayItemBean payItem) throws PortalServletException {
		PayLogBusiness logBusiness = new PayLogBusiness();
		SsoAccountHelper accountHelper = new SsoAccountHelper(request, response);
		LinkedHashMap<String,Object> accountAuthMap = accountHelper.getAccountAuthMap(payInfo.getStoreId());
		String weixinOpenid = String.valueOf(accountAuthMap.get("openId")); 
		
		// 获取支付链接
		String storeCode = getStoreCode(payInfo);
		WeixinConfigBean configBean = new WeixinCache().getWeixinConfig(storeCode);
		String basePath = PayUtil.getBasePath(request);
		String openId = ParamKit.getParameter(request, "openId", weixinOpenid);
		TreeMap<String, String> packageInfoMap = getPackageInfoMap(configBean, basePath, payInfo, payItem, openId);
		logBusiness.savePayLog(payInfo.getPayId(), PayConfig.PAYS_WAIT_PAY,
				"增加支付明细记录,生成微信支付package:" + JsonKit.toJson(packageInfoMap));
		String callbackUrl = basePath + WeixinConfig.WX_CALLBACK_URL;
		packageInfoMap.put("callbackUrl", callbackUrl);
		request.setAttribute("callbackUrl", callbackUrl);
		request.setAttribute("configBean", configBean);
		request.setAttribute("packageInfoMap", packageInfoMap);
		request.setAttribute("payItem", payItem);
		request.setAttribute("payInfo", payInfo);
		log.info("WeixinPublicNumber processRequest Param packageInfoMap:{}, configBean:{}, callbackUrl:{}", 
				new Object[]{JsonKit.toJson(packageInfoMap), JsonKit.toJson(configBean), callbackUrl});
		
		String pagePath = PayConfig.PAGE_PATH + "PublicNumberPay.jsp";
		String path = MessageFormat.format(pagePath, getDevicePath(request), LanguageKit.getLanguage(request)
				.toLowerCase());
		path = "/WEB-INF/pages/weixin/cn/payment/" + "PublicNumberPay.jsp";
		RequestDispatcher rd = request.getRequestDispatcher(path);
		try {
			rd.forward(request, response);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REDIRECT_EXCEPTION, e.getMessage());
		}

	}

	public String getDevicePath(HttpServletRequest request) {
		if (BrowserKit.isMobileBrowser(request)) {
			return PortalConfig.WAP_PATH;
		}
		return PortalConfig.WEB_PATH;
	}

	public TreeMap<String, String> getPackageInfoMap(WeixinConfigBean configBean, String basePath, PayInfoBean payInfo,
			PayItemBean payItem, String openId) throws PortalServletException {
		WeixinPayProcessor processor = new WeixinPayProcessor();
		String notifyUrl = basePath + WeixinConfig.WX_NOTIFY_URL;
		WeixinPreOrderResponseBean responseBean = processor.preJsApiOrder(configBean, TRADE_TYPE, notifyUrl, openId,
				MUST_VALUE_PARAMS, payInfo, payItem);
		if (responseBean.getReturnCode().equalsIgnoreCase(PayConfig.FAIL)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION,
					responseBean.getReturnMsg());
		}
		if (responseBean.getResultCode().equalsIgnoreCase(PayConfig.FAIL)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION,
					responseBean.getErrCodeDes());
		}
		return processor.getBrandWCPayRequest(configBean.getAppId(), responseBean.getPrepayId(), configBean.getKey());
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
