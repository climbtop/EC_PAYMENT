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

@WebServlet(urlPatterns = "/alipay/AlipayWebNotifyReceiver.do")
public class AlipayWebNotifyReceiver extends HttpServlet {
	private static final long serialVersionUID = 2407317780896933429L;
	private static Logger log = LoggerFactory.getLogger(AlipayWebNotifyReceiver.class);

	public AlipayWebNotifyReceiver() {
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
		PayReceiver receiver = new AliPayWebPayReceiver();
		try {
			receiver.processNotify(request, response);
		} catch (Exception e) {
			if (e  instanceof PortalServletException) {
				log.info(e.getMessage());
			}else {
				log.error("AlipayWebNotifyReceiver 处理异常", e);
			}
			return;
		}

	}

}
