package com.trendy.ow.portal.payment.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.web.HttpRequestKit;
import com.trendy.fw.common.web.ParamKit;
import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.ow.passport.sso.business.SsoClientHelper;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayCompanyInfoBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.business.PayInfoBusiness;
import com.trendy.ow.portal.payment.business.PayItemBusiness;
import com.trendy.ow.portal.payment.business.PayLogBusiness;
import com.trendy.ow.portal.payment.business.PayProcessor;
import com.trendy.ow.portal.payment.cache.PayChannelCache;
import com.trendy.ow.portal.payment.cache.PayCompanyCache;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.config.PayErrorCode;
import com.trendy.ow.portal.payment.factory.PayProcessorFactory;

@WebServlet(urlPatterns = "/pay/PayRedirect.do")
public class PayRedirect extends HttpServlet {
	private static final long serialVersionUID = 2077731014675268162L;
	private static Logger log = LoggerFactory.getLogger(PayRedirect.class);

	public PayRedirect() {
		// TODO Auto-generated constructor stub
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			int payId = ParamKit.getIntParameter(request, "payId", 0);
			int channelId = ParamKit.getIntParameter(request, "channelId", 0);
			log.info("payRedirect Param payId:{}, channelId:{}", new Object[]{payId, channelId});
			
			PayInfoBusiness business = new PayInfoBusiness();
			PayInfoBean payInfo = business.getPayInfoByKey(payId);
			if (payInfo == null) {
				throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REDIRECT_EXCEPTION,
						"to pay redirect,but can't find this payInfo record。payId=" + payId);

			}
			SsoClientHelper clientHelper = new SsoClientHelper(request, response);
			int userId = clientHelper.getUserId();
			if(payInfo.getUserId()!=userId){
				throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REDIRECT_EXCEPTION,
						"errorUserRequest");
			}
			PayChannelCache channelCache = new PayChannelCache();
			PayChannelInfoBean channel = channelCache.getPayChannelInfo(channelId);
			if (channel == null) {
				throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REDIRECT_EXCEPTION,
						"PayProcessorFactory can't find pay channel record。storeId=" + payInfo.getStoreId()
								+ ",channelId=" + channelId);
			}

			PayCompanyCache companyCache = new PayCompanyCache();
			PayCompanyInfoBean company = companyCache.getPayCompanyInfo(channel.getCompanyId());
			if (company == null) {
				throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REDIRECT_EXCEPTION,
						"PayProcessorFactory can't find pay company record。channelId=" + channelId + ",companyId="
								+ channel.getCompanyId());
			}

			// 插入支付明细表
			PayLogBusiness logBusiness = new PayLogBusiness();
			PayItemBusiness itemBusiness=new PayItemBusiness();
			String ipAddress = HttpRequestKit.getIpAddress(request);
			PayItemBean payItem = itemBusiness.savePayItem(payId, channelId, channel.getCompanyId(),
					payInfo.getCurrency(), ipAddress, PayConfig.PAYS_WAIT_PAY, Constants.STATUS_VALID);
			if (payItem.getPayItemId() <= 0) {
				logBusiness.savePayLog(payId, PayConfig.PAYS_WAIT_PAY, "增加支付明细记录失败");
				throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REDIRECT_EXCEPTION,
						"save payItem record fail.");
			}

			// 寻找渠道 各自处理
			PayProcessorFactory factory = new PayProcessorFactory();
			PayProcessor payProcessor = factory.getPayProcessor(company.getCompanyCode(), channel.getChannelCode());
			payProcessor.processRequest(request, response, payInfo, payItem);

		} catch (PortalServletException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

}
