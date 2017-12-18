package com.trendy.ow.portal.payment.business;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayGatewayRequestBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;

public abstract class PayProcessor extends PayBaseProcessor{

	//支持网页可选调用
	public abstract void processRequest(HttpServletRequest request, HttpServletResponse response, PayInfoBean payInfo,
			PayItemBean payItem) throws PortalServletException;
	
	//支持app调用
	public abstract Map<String, String> processPayGatewayRequest(PayInfoBean payInfo, PayChannelInfoBean channel,
			String basePath, PayGatewayRequestBean requestBean) throws Exception;


}
