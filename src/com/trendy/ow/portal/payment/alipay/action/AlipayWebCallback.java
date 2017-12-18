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
import com.trendy.ow.portal.payment.alipay.business.AliPayWebPayReceiver;
import com.trendy.ow.portal.payment.business.PayReceiver;

@WebServlet(urlPatterns = "/alipay/AlipayWebCallback.do")
public class AlipayWebCallback extends HttpServlet {
	private static final long serialVersionUID = -9137903053409073458L;
	private static Logger log = LoggerFactory.getLogger(AlipayWebCallback.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PayReceiver receiver = new AliPayWebPayReceiver();
		try {
			receiver.processCallback(request, response);
		} catch (PortalServletException e) {
			log.error("AlipayWebCallback 处理异常:" + e.getMessage(), e);
			throw e;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

}
