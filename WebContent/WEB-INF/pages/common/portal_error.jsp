<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ page import="java.util.*"  %>
<%@ page import="com.trendy.fw.tools.exception.*"  %>
<%@ page import="com.trendy.fw.common.web.*"  %>
<%@ page import="com.trendy.fw.tools.web.util.*"  %>
<%
String ERROR_PAGE_URL = "http://www." + DomainKit.getTopDomain(request) + "/ErrorPage.do";
String appId = "";
int errorCode = 0;
String message = "";

if(exception instanceof PortalServletException){
    PortalServletException pse = (PortalServletException)exception;
    
    errorCode = pse.getErrorCode();
    message = pse.getMessage();
}else{
	message = exception.getMessage();
}

HashMap<String, String> map = new HashMap<String, String>();
map.put("appId", appId);
map.put("errorCode", errorCode + "");
map.put("message", message);
HttpResponseKit.printForm(response, ERROR_PAGE_URL, map);
%>