package com.trendy.ow.portal.payment.alipay.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.ow.portal.payment.alipay.business.AliPayWapPayReceiver;
import com.trendy.ow.portal.payment.business.PayReceiver;

@WebServlet(urlPatterns = "/alipay/AlipayWapCallback.do")
public class AlipayWapCallback extends HttpServlet {
	private static final long serialVersionUID = -1394658698362013785L;
	private static Logger log = LoggerFactory.getLogger(AlipayWapCallback.class);
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PayReceiver receiver=new AliPayWapPayReceiver();
		try {
			receiver.processCallback(request, response);
		} catch (PortalServletException e) {
			log.error("AlipayWapCallback 处理异常", e);
			throw e;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}
	
}

