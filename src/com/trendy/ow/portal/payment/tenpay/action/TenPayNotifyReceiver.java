package com.trendy.ow.portal.payment.tenpay.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.ow.portal.payment.tenpay.business.TenPayPayReceiver;

@WebServlet(urlPatterns = "/tenpay/TenpayNotifyReceiver.do")
public class TenPayNotifyReceiver extends HttpServlet {
	private static final long serialVersionUID = 7919813866030997430L;
	private static Logger log = LoggerFactory.getLogger(TenPayNotifyReceiver.class);

	public TenPayNotifyReceiver() {
		// TODO Auto-generated constructor stub
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
		TenPayPayReceiver processor = new TenPayPayReceiver();
		try {
			processor.processNotify(request, response);
		} catch (Exception e) {
			if (e  instanceof PortalServletException) {
				log.info(e.getMessage());
			}else {
				log.error("TenpayNotifyReceiver 处理异常", e);
			}
			return;
		}

	}

}
