package com.trendy.ow.portal.payment.service;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.crypto.RSA;
import com.trendy.fw.common.util.JsonKit;
import com.trendy.fw.common.util.StringKit;
import com.trendy.fw.common.web.HttpRequestKit;
import com.trendy.fw.common.web.HttpResponseKit;
import com.trendy.fw.common.web.ParamKit;
import com.trendy.fw.common.web.ReturnMessageBean;
import com.trendy.fw.tools.criphertext.CiphertextKit;
import com.trendy.fw.tools.order.config.SyncStatusConfig;
import com.trendy.ow.portal.payment.bean.AppConfigBean;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.bean.PayRequestBean;
import com.trendy.ow.portal.payment.bean.PayStoreChannelBean;
import com.trendy.ow.portal.payment.business.PayInfoBusiness;
import com.trendy.ow.portal.payment.business.PayItemBusiness;
import com.trendy.ow.portal.payment.business.PayLogBusiness;
import com.trendy.ow.portal.payment.business.PayUtil;
import com.trendy.ow.portal.payment.cache.PayChannelCache;
import com.trendy.ow.portal.payment.cache.PayConfigCache;
import com.trendy.ow.portal.payment.cache.PayStoreChannelCache;
import com.trendy.ow.portal.payment.cache.StoreInfoCache;
import com.trendy.ow.portal.payment.cache.SysApplicationInfoCache;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.weixin.bean.WeixinPreOrderResponseBean;
import com.trendy.ow.portal.payment.weixin.business.WeixinScanCodeProcessor;
import com.trendy.ow.portal.payment.weixin.config.WeixinConfig;

@WebServlet(urlPatterns = "/m/WeixinPayOrder.action")
public class WeixinPayOrderService extends HttpServlet {
	private static final long serialVersionUID = 1548652811782300992L;
	private static Logger log = LoggerFactory.getLogger(PayedAddService.class);

	public WeixinPayOrderService() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
		return;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		WeixinScanCodeProcessor processor = new WeixinScanCodeProcessor();
		ReturnMessageBean resultBean = new ReturnMessageBean();
		try {
			String appCode = ParamKit.getParameter(request, "appCode", "");
			String data = ParamKit.getParameter(request, "data", "");
			log.info("PayInfoAddService-reciveMsg:appCode" + appCode + ",data:" + data);

			PayRequestBean requestBean = parseRequestData(appCode, data);
			String msg = checkPayRequestBean(requestBean);
			if (msg != null) {
				log.info("pay request params[" + msg + "] is not valid or has not default value.");
				throw new Exception("InvalidArgument");
			}
			
			SysApplicationInfoCache cache=new SysApplicationInfoCache();
			Map<Integer, String> appMap =cache.getSysApplicationInfoCodeAllMap();
			if (!StringKit.isValid(appMap.get(requestBean.getAppId()))) {
				throw new Exception("errorAppIdRequest");
			}
			
			PayInfoBusiness infoBusiness = new PayInfoBusiness();
			PayInfoBean payInfo = infoBusiness.getPayInfoByKey(requestBean.getInfoId(), requestBean.getReferType(),
					requestBean.getAppId());
			if (payInfo == null) {
				payInfo=savePayInfo(infoBusiness, requestBean, HttpRequestKit.getIpAddress(request));
				int payId = payInfo.getPayId();
				if (payId <= 0) {
					log.error("save payinfo exception。");
					throw new Exception("InternalError");
				}
			}
			PayChannelCache channelCache = new PayChannelCache();
			PayChannelInfoBean channel = channelCache.getPayChannelInfo(WeixinConfig.CHANNEL_CODE_SCAN_CODE);
			if (channel == null) {
				log.info("can't find pay channel record。channelCode=" + WeixinConfig.CHANNEL_CODE_SCAN_CODE);
				throw new Exception("InternalError");
			}
			if (!hasChannel(requestBean.getStoreId(), WeixinConfig.CHANNEL_CODE_SCAN_CODE, channelCache)) {
				log.info("NoSuchStoreChannelConfig:storeId=" + requestBean.getStoreId());
				throw new Exception("NoSuchStoreChannelConfig");
			}

			// 插入支付明细表
			PayLogBusiness logBusiness = new PayLogBusiness();
			PayItemBusiness itemBusiness = new PayItemBusiness();
			String ipAddress = HttpRequestKit.getIpAddress(request);
			PayItemBean payItem = itemBusiness.savePayItem(payInfo.getPayId(), channel.getChannelId(),
					channel.getCompanyId(), payInfo.getCurrency(), ipAddress, PayConfig.PAYS_WAIT_PAY,
					Constants.STATUS_VALID);
			if (payItem.getPayItemId() <= 0) {
				logBusiness.savePayLog(payInfo.getPayId(), PayConfig.PAYS_WAIT_PAY, "增加支付明细记录失败");
				log.error("save payItem record fail。");
				throw new Exception("InternalError");
			}

			String basePath = PayUtil.getBasePath(request);
			WeixinPreOrderResponseBean resposeBean = processor.getWeixinPreOrderResponse(basePath, payInfo, payItem);
			Map<String, String> dataMap = new HashMap<String, String>();
			dataMap.put("codeUrl", resposeBean.getCodeUrl());
			dataMap.put("prepayId", resposeBean.getPrepayId());
			dataMap.put("scanUrl", basePath + WeixinConfig.WX_CREATE_SCAN_CODE_URL);
			resultBean.setCode(Constants.STATUS_VALID);
			resultBean.setContent(dataMap);
			resultBean.setMessage(msg);
		} catch (Exception e) {
			String msg = e.getMessage();
			if (!StringKit.isValid(msg)) {
				msg = "InternalError";
				log.error(msg, e);
			}else {
				log.info(msg);
			}
			resultBean.setCode(Constants.STATUS_NOT_VALID);
			resultBean.setMessage(msg);
		}
		HttpResponseKit.printJson(request, response, resultBean, "callback");
	}

	private boolean hasChannel(int storeId, String channelCode, PayChannelCache channelCache) {
		PayStoreChannelCache cache = new PayStoreChannelCache();
		PayChannelInfoBean channel = channelCache.getPayChannelInfo(channelCode);
		PayStoreChannelBean bean = cache.getPayStoreChannel(storeId, channel.getChannelId());
		if (bean.getStoreId() > 0 && bean.getChannelId() > 0 && bean.getStatus() == Constants.STATUS_VALID
				&& bean.getUseStatus() == Constants.STATUS_VALID) {
			return true;
		}
		return false;
	}

	private PayRequestBean parseRequestData(String appCode, String data) throws Exception {
		if (!StringKit.isValid(appCode)) {
			throw new Exception("InvalidAppCode");
		}
		PayConfigCache configCache = new PayConfigCache();
		AppConfigBean appConfigBean = configCache.getAppConfigBean(appCode);
		String rsaPrivateKey = appConfigBean.getRsaPrivateKey();
		String md5Key = appConfigBean.getMd5Key();
		if (!StringKit.isValid(rsaPrivateKey) || !StringKit.isValid(md5Key)) {
			throw new Exception("NoSuchAppIdConfig");
		}
		ReturnMessageBean result = null;
		try {
			PrivateKey privateKey = RSA.getPrivateKey(rsaPrivateKey);
			result = CiphertextKit.decrypt(data, privateKey, md5Key, PayRequestBean.class);
		} catch (Exception e) {
			throw new Exception("RSADecryptFail");
		}
		if (result.getCode() == 0) {
			throw new Exception("SignFail");
		}
		PayRequestBean detailBean = (PayRequestBean) result.getContent();
		log.info("PayInfoAddService-jsonData:" + JsonKit.toJson(detailBean));
		return detailBean;
	}
	
	private PayInfoBean savePayInfo(PayInfoBusiness infoBusiness,PayRequestBean requestBean,String idAddress){
		PayInfoBean payInfo = new PayInfoBean();
		payInfo.setAppId(requestBean.getAppId());
		payInfo.setCurrency(requestBean.getCurrency());
		payInfo.setRequestAmount(Double.valueOf(requestBean.getRequestAmount()));
		payInfo.setInfoId(requestBean.getInfoId());
		payInfo.setReferType(requestBean.getReferType());
		payInfo.setIpAddress(idAddress);
		payInfo.setStoreId(requestBean.getStoreId());
		payInfo.setUserId(requestBean.getUserId());
		payInfo.setStatus(Constants.STATUS_VALID);
		payInfo.setPayStatus(PayConfig.PAYS_WAIT_PAY);
		payInfo.setSyncStatus(SyncStatusConfig.SYNCS_NONE);
		int payId = infoBusiness.addPayInfo(payInfo);
		payInfo.setPayId(payId);
		return payInfo;
	}

	/**
	 * 检查参数是否正确
	 * 
	 * @param bean
	 * @return
	 */
	public String checkPayRequestBean(PayRequestBean bean) {
		StringBuilder sb = new StringBuilder();
		SysApplicationInfoCache appCache=new SysApplicationInfoCache();
		if (!appCache.getSysApplicationInfoCodeAllMap().containsKey(bean.getAppId())) {
			sb.append(",appId"); 
		}
		if (bean.getInfoId() <= 0) {
			sb.append(",infoId");
		}
		if (!StringKit.isValid(bean.getReferType())) {
			sb.append(",referType");
		}
		StoreInfoCache storeCache=new StoreInfoCache();
		if (!storeCache.getStoreCodeByIdMap().containsKey(bean.getStoreId())) {
			sb.append(",storeId");
		}
		if (bean.getUserId() <= 0) {
			sb.append(",userId");
		}
		if (bean.getRequestAmount() <= 0) {
			sb.append(",requestAmount");
		}
		if (!StringKit.isValid(bean.getTimestamp())) {
			sb.append(",timestamp");
		}
		if (!StringKit.isValid(bean.getRandomValue())) {
			sb.append(",randomValue");
		}
		if (!StringKit.isValid(bean.getSign())) {
			sb.append(",sign");
		}
		if (sb.length() > 0) {
			return sb.substring(1);
		}
		return null;
	}
}