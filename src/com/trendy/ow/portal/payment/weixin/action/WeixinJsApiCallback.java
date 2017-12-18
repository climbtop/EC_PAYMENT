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
import com.trendy.ow.portal.payment.weixin.business.WeixinJsApiPayReceiver;

@WebServlet(urlPatterns = "/weixin/WeixinJsApiCallback.do")
public class WeixinJsApiCallback extends HttpServlet {
	private static final long serialVersionUID = -4637561456117139726L;
	private static Logger log = LoggerFactory.getLogger(WeixinJsApiCallback.class);

	public WeixinJsApiCallback() {
		// TODO Auto-generated constructor stub
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PayReceiver processor = new WeixinJsApiPayReceiver();
		try {
			processor.processCallback(request, response);
		} catch (PortalServletException e) {
			log.error("WeixinJsApiCallback 处理异常:" + e.getMessage(), e);
			throw e;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

}
