package com.trendy.ow.portal.payment.action;

import java.io.IOException;
import java.security.PrivateKey;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
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
import com.trendy.fw.common.web.ParamKit;
import com.trendy.fw.common.web.ReturnMessageBean;
import com.trendy.fw.tools.criphertext.CiphertextKit;
import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.fw.tools.language.util.LanguageKit;
import com.trendy.ow.passport.sso.business.SsoClientHelper;
import com.trendy.ow.portal.payment.bean.AppConfigBean;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayRequestBean;
import com.trendy.ow.portal.payment.bean.PayStoreChannelBean;
import com.trendy.ow.portal.payment.business.PayInfoBusiness;
import com.trendy.ow.portal.payment.business.PayUtil;
import com.trendy.ow.portal.payment.cache.PayChannelCache;
import com.trendy.ow.portal.payment.cache.PayConfigCache;
import com.trendy.ow.portal.payment.cache.PayStoreChannelCache;
import com.trendy.ow.portal.payment.cache.StoreInfoCache;
import com.trendy.ow.portal.payment.cache.SysApplicationInfoCache;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.config.PayErrorCode;
import com.trendy.ow.portal.payment.zero.config.ZeroPayConfig;

@WebServlet(urlPatterns = "/pay/PayChannelList.do")
public class PayChannelList extends HttpServlet {
	private static final long serialVersionUID = -7698735162351888123L;
	private static Logger log = LoggerFactory.getLogger(PayChannelList.class);

	public PayChannelList() {
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
			log.info("PayChannelList-reciveMsg:appCode:" + appCode + ",data:" + data);

			PayRequestBean requestBean = parseRequestData(appCode, data);
			String msg = checkPayRequestBean(requestBean);
			if (msg != null) {
				throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION,
						"pay request params[" + msg + "] is not valid or has not default value.");
			}
		
			SsoClientHelper clientHelper = new SsoClientHelper(request, response);
			int userId = clientHelper.getUserId();
			if (requestBean.getUserId() != userId) {
				throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION,
						"errorUserRequest");
			}
			PayInfoBusiness infoBusiness = new PayInfoBusiness();
			PayInfoBean payInfo = infoBusiness.getPayInfoByKey(requestBean.getInfoId(), requestBean.getReferType(),
					requestBean.getAppId());
			int payId = 0;
			if (payInfo != null) {
				if (PayConfig.PAYS_PAYED.equals(payInfo.getPayStatus())
						|| PayConfig.PAYS_CANCELLED.equals(payInfo.getPayStatus())) {
					throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION,
							"payInfo status is" + payInfo.getPayStatus());
				} else {
					payId = payInfo.getPayId();
				}
			} else {
				payId = infoBusiness.savePayInfo(requestBean, HttpRequestKit.getIpAddress(request));
				if (payId <= 0) {
					throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION,
							"save payInfo exception");
				}
			}

			PayChannelCache channelCache = new PayChannelCache();
			if (StringKit.isValid(requestBean.getChannelCode())) {
				PayChannelInfoBean channel = channelCache.getPayChannelInfo(requestBean.getChannelCode());
				if (channel.getChannelId() == 0 || channel.getStatus() == Constants.STATUS_NOT_VALID
						|| channel.getUseStatus() == Constants.STATUS_NOT_VALID
						|| channel.getPayLocale() == PayConfig.PL_CREDIT_CARD
						|| channel.getPayLocale() == PayConfig.PL_CASH || channel.getPayLocale() == PayConfig.PL_SHOP) {
					throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION,
							"invalidChannelCode");
				}
				PayStoreChannelCache storeChannelCache = new PayStoreChannelCache();
				PayStoreChannelBean bean = storeChannelCache.getPayStoreChannel(requestBean.getStoreId(),
						channel.getChannelId());
				if (bean.getStoreId() > 0 && bean.getChannelId() > 0 && bean.getStatus() == Constants.STATUS_VALID
						&& bean.getUseStatus() == Constants.STATUS_VALID) {
					RequestDispatcher rd = request.getRequestDispatcher("PayRedirect.do?channelId="
							+ channel.getChannelId() + "&payId=" + payId);
					rd.forward(request, response);
				} else {
					throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION,
							"noSuchStoreChannel");
				}
			}

			int localState = PayUtil.getLocalState(request);
			Map<Integer, List<PayChannelInfoBean>> channelListGroupByType = channelCache.getChannelInfoListMap(
					requestBean.getStoreId(), localState, requestBean.getCompanyCode(), requestBean.getChannelCode());

			log.info("payChannel Param storeId:{}, localState:{}, companyCode:{}, channelCode:{}, channelSize:{},", 
					new Object[]{requestBean.getStoreId(), localState, requestBean.getCompanyCode(), requestBean.getChannelCode(), channelListGroupByType.size()});
			
			request.setAttribute("payRequestBean", requestBean);
			request.setAttribute("payId", payId);
			request.setAttribute("channelListGroupByType", channelListGroupByType);

			String pagePath = PayConfig.PAGE_PATH + "PayChannelList.jsp";
			String path = MessageFormat.format(pagePath, PayUtil.getDevicePath(request),
					LanguageKit.getLanguage(request).toLowerCase());
			RequestDispatcher rd = request.getRequestDispatcher(path);
			rd.forward(request, response);

		} catch (PortalServletException e) {
			log.error(e.getMessage());
			throw e;
		}
	}


	private PayRequestBean parseRequestData(String appCode, String data) throws PortalServletException {
		if (!StringKit.isValid(appCode)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION, "appCode=" + appCode
					+ " is not valid value。");
		}
		PayConfigCache configCache = new PayConfigCache();
		AppConfigBean appConfigBean = configCache.getAppConfigBean(appCode);
		String rsaPrivateKey = appConfigBean.getRsaPrivateKey();
		String md5Key = appConfigBean.getMd5Key();
		if (!StringKit.isValid(rsaPrivateKey) || !StringKit.isValid(md5Key)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION, "appCode=" + appCode
					+ " has no md5key or rsaPrivateKey config record.");
		}
		ReturnMessageBean result = null;
		try {
			PrivateKey privateKey = RSA.getPrivateKey(rsaPrivateKey);
			result = CiphertextKit.decrypt(data, privateKey, md5Key, PayRequestBean.class);
		} catch (Exception e) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION, "appCode=" + appCode
					+ " ,decrypt fail.");
		}
		if (result.getCode() == 0) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION, "appCode=" + appCode
					+ " ,sign fail.");
		}
		PayRequestBean detailBean = (PayRequestBean) result.getContent();
		log.info("PayChannelList-jsonData:" + JsonKit.toJson(detailBean));
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
		if (!StringKit.isValid(bean.getCurrency())) {
			sb.append(",currency");
		}
		String channelCode = bean.getChannelCode();
		if (ZeroPayConfig.CHANNEL_CODE_DEFAULT.equals(channelCode)) {
			if (Double.compare(bean.getRequestAmount(), 0) != 0) {
				sb.append(",requestAmount");
			}
		} else if (bean.getRequestAmount() <= 0) {
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
