package com.trendy.ow.portal.payment.weixin.action;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.util.StringKit;
import com.trendy.fw.common.web.ParamKit;
import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.fw.tools.language.util.LanguageKit;
import com.trendy.ow.passport.sso.business.SsoClientHelper;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.business.PayInfoBusiness;
import com.trendy.ow.portal.payment.business.PayItemBusiness;
import com.trendy.ow.portal.payment.business.PayUtil;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.config.PayErrorCode;
import com.trendy.ow.portal.payment.weixin.config.WeixinConfig;

@WebServlet(urlPatterns = "/weixin/WeixinScanCodePay.do")
public class WeixinScanCodePay extends HttpServlet {
	private static final long serialVersionUID = 8680348107146677258L;
	private static Logger log = LoggerFactory.getLogger(WeixinScanCodePay.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PayItemBusiness itemBusiness=new PayItemBusiness();
		PayInfoBusiness infoBusiness=new PayInfoBusiness();
		try {
			int itemId=ParamKit.getIntParameter(request, "itemId", 0);
			String codeUrl = ParamKit.getParameter(request, "codeUrl", "");
			log.info("itemId="+itemId+",codeUrl="+codeUrl);
			PayItemBean itemBean=itemBusiness.getPayItemByKey(itemId);
			if (itemBean==null || !StringKit.isValid(codeUrl)) {
				throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REDIRECT_EXCEPTION,
						"InvalidRequestParam");
			}
			PayInfoBean infoBean=infoBusiness.getPayInfoByKey(itemBean.getPayId());
			SsoClientHelper clientHelper = new SsoClientHelper(request, response);
			int userId = clientHelper.getUserId();
			if(infoBean.getUserId()!=userId){
				throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REDIRECT_EXCEPTION,
						"errorUserRequest");
			}
			if (PayConfig.PAYS_PAYED.equals(itemBean.getPayStatus())) {
				response.sendRedirect("/"+ WeixinConfig.WX_CALLBACK_URL+"?itemId="+itemBean.getPayItemId());
				return;
			}else {
				request.setAttribute("codeUrl", codeUrl);
				request.setAttribute("payItem", itemBean);
				request.setAttribute("payInfo", infoBean);
				String pagePath = PayConfig.PAGE_PATH + "WeixinScanCodePay.jsp";
				String path = MessageFormat.format(pagePath, PayUtil.getDevicePath(request),
						LanguageKit.getLanguage(request).toLowerCase());
				RequestDispatcher rd = request.getRequestDispatcher(path);
				rd.forward(request, response);
			}
		} catch (Exception e) {
			if (e instanceof PortalServletException) {
				log.info(e.getMessage());
				throw (PortalServletException)e;
			}else {
				log.error("InternalError",e);
			}
			//throw e;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

}
