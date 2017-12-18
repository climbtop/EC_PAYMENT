package com.trendy.ow.portal.payment.weixin.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.ow.portal.payment.business.PayReceiver;
import com.trendy.ow.portal.payment.weixin.business.WeixinPayReceiver;

@WebServlet(urlPatterns = "/weixin/WeixinCallback.do")
public class WeixinCallback extends HttpServlet {
	private static final long serialVersionUID = -2127780462398971288L;
	private static Logger log = LoggerFactory.getLogger(WeixinCallback.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PayReceiver receiver = new WeixinPayReceiver();
		try {
			receiver.processCallback(request, response);
		} catch (PortalServletException e) {
			log.error("WeixinCallback 处理异常:" + e.getMessage(), e);
			throw e;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

}
