package com.trendy.ow.portal.payment.business;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trendy.fw.tools.exception.PortalServletException;

public abstract class PayReceiver extends PayBaseProcessor {

	public abstract void processNotify(HttpServletRequest request, HttpServletResponse response)
			throws PortalServletException;

	public abstract void processCallback(HttpServletRequest request, HttpServletResponse response)
			throws PortalServletException;


}
