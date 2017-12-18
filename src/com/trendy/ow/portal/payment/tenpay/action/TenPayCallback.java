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


@WebServlet(urlPatterns = "/tenpay/TenpayCallback.do")
public class TenPayCallback extends HttpServlet {
	private static final long serialVersionUID = -5777514421555035028L;
	private static Logger log = LoggerFactory.getLogger(TenPayCallback.class);

	public TenPayCallback() {
		// TODO Auto-generated constructor stub
		super();
	}
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		TenPayPayReceiver processor = new TenPayPayReceiver();
		try {
			processor.processCallback(request, response);
		} catch (PortalServletException e) {
			log.error("TenpayCallback 处理异常:"+e.getMessage(),e);
			throw e;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
