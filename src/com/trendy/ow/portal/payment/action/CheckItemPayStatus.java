package com.trendy.ow.portal.payment.action;

import java.io.IOException;
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
import com.trendy.fw.common.util.StringKit;
import com.trendy.fw.common.web.HttpResponseKit;
import com.trendy.fw.common.web.ParamKit;
import com.trendy.fw.common.web.ReturnMessageBean;
import com.trendy.ow.passport.sso.business.SsoClientHelper;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.business.PayInfoBusiness;
import com.trendy.ow.portal.payment.business.PayItemBusiness;
import com.trendy.ow.portal.payment.cache.PayChannelCache;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.weixin.config.WeixinConfig;

@WebServlet(urlPatterns = "/pay/CheckItemPayStatus.do")
public class CheckItemPayStatus extends HttpServlet {
	private static final long serialVersionUID = -6650290345277632221L;
	private static Logger log = LoggerFactory.getLogger(CheckItemPayStatus.class);

	public CheckItemPayStatus() {
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
		int itemId=ParamKit.getIntParameter(request, "itemId",0);
		int payId=ParamKit.getIntParameter(request, "payId",0);
		try {
			PayInfoBusiness infoBusiness=new PayInfoBusiness();
			PayItemBusiness itemBusiness=new PayItemBusiness();
			PayInfoBean infoBean=infoBusiness.getPayInfoByKey(payId);
			PayItemBean itemBean=itemBusiness.getPayItemByKey(itemId);
			if (infoBean==null || itemBean == null) {
				throw new Exception("InvalidRequestParam");
			}
			SsoClientHelper clientHelper = new SsoClientHelper(request, response);
			int userId = clientHelper.getUserId();
			if(infoBean.getUserId()!=userId){
				throw new Exception("errorUserRequest");
			}
			PayChannelCache channelCache=new PayChannelCache();
			PayChannelInfoBean channelBean=channelCache.getPayChannelInfo(itemBean.getChannelId());
			String channelCode=channelBean.getChannelCode();
			if (WeixinConfig.CHANNEL_CODE_SCAN_CODE.equals(channelCode)) {
				Map<String, String> dataMap = new HashMap<String, String>();
				dataMap.put("payStatus", itemBean.getPayStatus());
				if (PayConfig.PAYS_PAYED.equals(itemBean.getPayStatus())) {
					dataMap.put("callBackUrl", "/"+ WeixinConfig.WX_CALLBACK_URL+"?itemId="+itemBean.getPayItemId());
				}
				resultBean.setCode(Constants.STATUS_VALID);
				resultBean.setContent(dataMap);
			}else {
				throw new Exception("NotSupportThisItem");
			}
			
		} catch (Exception e) {
			String msg=e.getMessage();
			if (!StringKit.isValid(msg)) {
				msg="InternalError";
				log.error("CheckItemPayStatus:[itemId="+itemId+",payId="+payId+"]"+msg,e);
			}else {
				log.info("CheckItemPayStatus:[itemId="+itemId+",payId="+payId+"]"+msg);
			}
			resultBean.setCode(Constants.STATUS_NOT_VALID);
			resultBean.setMessage(msg);
		}finally{
			HttpResponseKit.printJson(request, response, resultBean, "callback");
		}
	}
	

}
