package com.trendy.ow.portal.payment.service;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.List;

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
import com.trendy.ow.portal.payment.bean.AppConfigBean;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayCompanyInfoBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayRequestBean;
import com.trendy.ow.portal.payment.bean.PayStoreChannelBean;
import com.trendy.ow.portal.payment.bean.PayedAddRequestBean;
import com.trendy.ow.portal.payment.business.PayInfoBusiness;
import com.trendy.ow.portal.payment.cache.PayChannelCache;
import com.trendy.ow.portal.payment.cache.PayCompanyCache;
import com.trendy.ow.portal.payment.cache.PayConfigCache;
import com.trendy.ow.portal.payment.cache.PayStoreChannelCache;
import com.trendy.ow.portal.payment.cache.StoreInfoCache;
import com.trendy.ow.portal.payment.cache.SysApplicationInfoCache;
import com.trendy.ow.portal.payment.offline.business.OfflinePayProcessor;
import com.trendy.ow.portal.payment.offline.config.OfflinePayConfig;

@WebServlet(urlPatterns = "/m/PayedAdd.action")
public class PayedAddService extends HttpServlet {
	private static final long serialVersionUID = -7698735162351888123L;
	private static Logger log = LoggerFactory.getLogger(PayedAddService.class);

	public PayedAddService() {
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
		try {
			String appCode = ParamKit.getParameter(request, "appCode", "");
			String data = ParamKit.getParameter(request, "data", "");
			log.info("PayInfoAddService-reciveMsg:appCode:" + appCode + ",data:" + data);

			PayedAddRequestBean requestBean = parseRequestData(appCode, data);
			String msg = checkPayRequestBean(requestBean);
			if (msg != null) {
				log.info("pay request params[" + msg + "] is not valid or has not default value.");
				throw new Exception("InvalidArgument["+msg+"]");
			}

			PayInfoBusiness infoBusiness = new PayInfoBusiness();
			PayInfoBean payInfo = infoBusiness.getPayInfoByKey(requestBean.getInfoId(), requestBean.getReferType(),
					requestBean.getAppId());
			if (payInfo == null) {
				int payId = infoBusiness.savePayInfo(requestBean, HttpRequestKit.getIpAddress(request));
				if (payId <= 0) {
					log.error("save payinfo exception。");
					throw new Exception("InternalError");
				}
				payInfo = infoBusiness.getPayInfoByKey(payId);
			} else {
				payInfo.setRequestAmount(requestBean.getRequestAmount());
				infoBusiness.updatePayInfo(payInfo);
			}
			
			if (hasOfflinePayChannel(requestBean.getStoreId())) {
				OfflinePayProcessor payProcessor=new OfflinePayProcessor();
				payProcessor.processRequest(request, response, payInfo, requestBean);
				payProcessor.processNotify(payInfo);
			} else {
				throw new Exception("NoSuchStoreChannelConfig");
			}

		} catch (Exception e) {
			log.info("Exception e:" + e.getMessage());
			String msg = e.getMessage();
			if (!StringKit.isValid(msg)) {
				msg = "InternalError";
				log.error(msg, e);
			} else {
				log.info(msg);
			}
			ReturnMessageBean resultBean = new ReturnMessageBean();
			resultBean.setCode(Constants.STATUS_NOT_VALID);
			resultBean.setMessage(msg);
			HttpResponseKit.printJson(request, response, resultBean, "callback");
		}
	}
	
	private boolean hasOfflinePayChannel(int storeId) {
		PayStoreChannelCache cache = new PayStoreChannelCache();
		PayChannelCache channelCache = new PayChannelCache();
		PayCompanyCache companyCache = new PayCompanyCache();
		PayCompanyInfoBean company = companyCache.getPayCompanyInfo(OfflinePayConfig.COMPANY_CODE);
		List<PayChannelInfoBean> list = channelCache.getPayChannelListByCompanyId(company.getCompanyId());
		for (PayChannelInfoBean channel : list) {
			PayStoreChannelBean bean = cache.getPayStoreChannel(storeId, channel.getChannelId());
			if (bean.getChannelId() > 0 && bean.getStoreId() > 0) {
				return true;
			}
		}
		return false;
	}

	private PayedAddRequestBean parseRequestData(String appCode, String data) throws Exception {
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
			result = CiphertextKit.decrypt(data, privateKey, md5Key, PayedAddRequestBean.class);
		} catch (Exception e) {
			throw new Exception("RSADecryptFail");
		}
		if (result.getCode() == 0) {
			throw new Exception("SignFail");
		}
		PayedAddRequestBean detailBean = (PayedAddRequestBean) result.getContent();
		log.info("PayInfoAddService-jsonData:" + JsonKit.toJson(detailBean));
		return detailBean;
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
		if (bean.getRequestAmount() < 0) {
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
