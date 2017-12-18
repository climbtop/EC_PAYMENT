package com.trendy.ow.portal.payment.service;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.crypto.BASE64;
import com.trendy.fw.common.crypto.MD5;
import com.trendy.fw.common.crypto.RSA;
import com.trendy.fw.common.util.JsonKit;
import com.trendy.fw.common.util.StringKit;
import com.trendy.fw.common.web.HttpRequestKit;
import com.trendy.fw.common.web.HttpResponseKit;
import com.trendy.fw.common.web.ParamKit;
import com.trendy.fw.common.web.ReturnMessageBean;
import com.trendy.fw.tools.criphertext.CiphertextBean;
import com.trendy.fw.tools.criphertext.CiphertextFormatter;
import com.trendy.fw.tools.criphertext.CiphertextKit;
import com.trendy.fw.tools.order.config.SyncStatusConfig;
import com.trendy.ow.portal.payment.bean.AppConfigBean;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayCompanyInfoBean;
import com.trendy.ow.portal.payment.bean.PayGatewayRequestBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayStoreChannelBean;
import com.trendy.ow.portal.payment.business.PayInfoBusiness;
import com.trendy.ow.portal.payment.business.PayProcessor;
import com.trendy.ow.portal.payment.business.PayUtil;
import com.trendy.ow.portal.payment.cache.PayChannelCache;
import com.trendy.ow.portal.payment.cache.PayCompanyCache;
import com.trendy.ow.portal.payment.cache.PayConfigCache;
import com.trendy.ow.portal.payment.cache.PayStoreChannelCache;
import com.trendy.ow.portal.payment.cache.StoreInfoCache;
import com.trendy.ow.portal.payment.cache.SysApplicationInfoCache;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.factory.PayProcessorFactory;

@WebServlet(urlPatterns = "/m/PayGateway.action")
public class PayGatewayService extends HttpServlet {
	private static final long serialVersionUID = 1548652811782300992L;
	private static Logger log = LoggerFactory.getLogger(PayedAddService.class);

	public PayGatewayService() {
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
		ReturnMessageBean resultBean = new ReturnMessageBean();
		try {
			String appCode = ParamKit.getParameter(request, "appCode", "");
			String data = ParamKit.getParameter(request, "data", "");
			log.info("PayGatewayService-reciveMsg:appCode:" + appCode + ",data:" + data);

			PayGatewayRequestBean requestBean = parseRequestData(appCode, data);
			String msg = checkPayOrderRequest(requestBean);
			if (msg != null) {
				log.info("pay request params[" + msg + "] is not valid or has not default value.");
				throw new Exception("InvalidArgument:[" + msg + "]");
			}

			PayInfoBusiness infoBusiness = new PayInfoBusiness();
			PayInfoBean payInfo = infoBusiness.getPayInfoByKey(requestBean.getInfoId(), requestBean.getReferType(),
					requestBean.getAppId());
			if (payInfo == null) {
				payInfo = savePayInfo(infoBusiness, requestBean, HttpRequestKit.getIpAddress(request));
				int payId = payInfo.getPayId();
				if (payId <= 0) {
					log.error("save payinfo exception。");
					throw new Exception("InternalError");
				}
			}

			PayChannelCache channelCache = new PayChannelCache();
			PayChannelInfoBean channel = channelCache.getPayChannelInfo(requestBean.getChannelCode());
			if (channel.getChannelId()==0) {
				log.info("InvaildChannelCode:" + requestBean.getChannelCode());
				throw new Exception("InvaildChannelCode");
			}
			
			if (channel.getPayLocale()!=requestBean.getPayLocale()) {
				log.info("PayLocale:"+channel.getPayLocale());
				throw new Exception("ChannelNotSupportSuchPayLocale");
			}

			PayCompanyCache companyCache = new PayCompanyCache();
			PayCompanyInfoBean company = companyCache.getPayCompanyInfo(channel.getCompanyId());
			if (!requestBean.getCompanyCode().equals(company.getCompanyCode())) {
				log.info("requestBean.getCompanyCode()[" + requestBean.getCompanyCode()
						+ "] != company.getCompanyCode[" + company.getCompanyCode() + "]");
				throw new Exception("InvaildCompanyCode");
			}

			PayStoreChannelCache storeChannelCache = new PayStoreChannelCache();
			PayStoreChannelBean bean = storeChannelCache.getPayStoreChannel(requestBean.getStoreId(),
					channel.getChannelId());
			if (bean.getStoreId() <= 0 || bean.getChannelId() <= 0 || bean.getStatus() == Constants.STATUS_NOT_VALID
					|| bean.getUseStatus() == Constants.STATUS_NOT_VALID ) {
				log.info("NoSuchStoreChannelConfig:storeId=" + requestBean.getStoreId());
				throw new Exception("NoSuchStoreChannelConfig");
			}

			PayProcessor payProcessor = new PayProcessorFactory().getPayProcessor(requestBean.getCompanyCode(),
					requestBean.getChannelCode());
			String basePath = PayUtil.getBasePath(request);
			Map<String, String> dataMap = payProcessor.processPayGatewayRequest(payInfo, channel, basePath, requestBean);

			resultBean.setCode(Constants.STATUS_VALID);
			resultBean.setContent(dataMap);
			resultBean.setMessage(msg);
		} catch (Exception e) {
			String msg = e.getMessage();
			if (!StringKit.isValid(msg)) {
				msg = "InternalError";
				log.error(msg, e);
			} else {
				log.info(msg);
			}
			resultBean.setCode(Constants.STATUS_NOT_VALID);
			resultBean.setMessage(msg);
		}
		HttpResponseKit.printJson(request, response, resultBean, "callback");
	}

	private PayGatewayRequestBean parseRequestData(String appCode, String data) throws Exception {
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
			String decryptStr = new String(RSA.decryptByPrivateKey(BASE64.decodeBase64(data), privateKey));
			CiphertextBean bean = (CiphertextBean)JsonKit.toBean(decryptStr, PayGatewayRequestBean.class);
			String sign = CiphertextFormatter.format(bean);
			System.out.println("加密前："+sign);
			System.out.println(bean.getSign()+";md5key:"+md5Key+";"+MD5.getMD5(sign, md5Key)+";bol:"+bean.getSign().equals(MD5.getMD5(sign, md5Key)));
			result = CiphertextKit.decrypt(data, privateKey, md5Key, PayGatewayRequestBean.class);
		} catch (Exception e) {
			throw new Exception("RSADecryptFail");
		}
		if (result.getCode() == 0) {
			throw new Exception("SignFail");
		}
		PayGatewayRequestBean detailBean = (PayGatewayRequestBean) result.getContent();
		log.info("PayOrder-jsonData:" + JsonKit.toJson(detailBean));
		return detailBean;
	}

	private PayInfoBean savePayInfo(PayInfoBusiness infoBusiness,PayGatewayRequestBean requestBean,String idAddress){
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
	public String checkPayOrderRequest(PayGatewayRequestBean bean) {
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
		if (!StringKit.isValid(bean.getChannelCode())) {
			sb.append(",channelCode");
		}
		if (!StringKit.isValid(bean.getCompanyCode())) {
			sb.append(",companyCode");
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