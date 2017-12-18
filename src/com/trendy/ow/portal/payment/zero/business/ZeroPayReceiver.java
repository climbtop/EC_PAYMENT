package com.trendy.ow.portal.payment.zero.business;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.business.PayReceiver;

public class ZeroPayReceiver extends PayReceiver {
	private static Logger log = LoggerFactory.getLogger(ZeroPayReceiver.class);

	public void processNotify(final PayItemBean payItem) {
		// 业务系统通知
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					notifyApplication(payItem);
				} catch (PortalServletException e) {
					log.error("ZeroPayReceiverNotify 处理异常:" + e.getMessage(), e);
				}
			}
		}).start();
		
	}

	public void processCallback(PayItemBean payItem, HttpServletResponse response) throws PortalServletException {
		// 统一跳转到业务系统页面
		try {
			callbackApplication(payItem, response);
		} catch (PortalServletException e) {
			log.error("ZeroPayReceiverCallback 处理异常:" + e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public void processNotify(HttpServletRequest request, HttpServletResponse response) throws PortalServletException {
	}

	@Override
	public void processCallback(HttpServletRequest request, HttpServletResponse response) throws PortalServletException {

	}
}
