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
import com.trendy.ow.portal.payment.alipay.business.AliPayAppPayReceiver;

@WebServlet(urlPatterns = "/alipay/AlipayAppNotifyReceiver.do")
public class AlipayAppNotifyReceiver extends HttpServlet {
	private static final long serialVersionUID = -1394658698362013785L;
	private static Logger log = LoggerFactory.getLogger(AlipayAppNotifyReceiver.class);

	public AlipayAppNotifyReceiver() {
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
		AliPayAppPayReceiver receiver = new AliPayAppPayReceiver();
		try {
			receiver.processNotify(request, response);
		} catch (Exception e) {
			if (e  instanceof PortalServletException) {
				log.info(e.getMessage());
			}else {
				log.error("AlipayWalletNotifyReceiver 处理异常", e);
			}
			return;
		}

	}

}
