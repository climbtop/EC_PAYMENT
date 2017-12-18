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

@WebServlet(urlPatterns = "/weixin/WeixinNotifyReceiver.do")
public class WeixinNotifyReceiver extends HttpServlet {
	private static final long serialVersionUID = 7816525111206361113L;
	private static Logger log = LoggerFactory.getLogger(WeixinNotifyReceiver.class);

	public WeixinNotifyReceiver() {
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
		PayReceiver processor = new WeixinPayReceiver();
		try {
			processor.processNotify(request, response);
		} catch (Exception e) {
			if (e  instanceof PortalServletException) {
				log.info(e.getMessage());
			}else {
				log.error("WeixinNotifyReceiver 处理异常", e);
			}
			return;
		}

	}
}
