<%@page import="com.trendy.fw.common.util.StringKit"%>
<%@page import="com.trendy.fw.common.web.ParamKit"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>clientChooseTest</title>
</head>
<body>
	<form method="get" action="TestPayClient.do">
		
		appId:<input type="text" name="appId" value="<%=ParamKit.getParameter(request, "appId", "5")%>">
		infoId:<input type="text" name="infoId" value="<%=ParamKit.getParameter(request, "infoId", "1")%>">
		referType:<input type="text" name="referType" value="<%=ParamKit.getParameter(request, "referType", "FULL")%>">
		storeId:<input type="text" name="storeId" value="<%=ParamKit.getParameter(request, "storeId", "2")%>">
		userId:<input type="text" name="userId" value="<%=ParamKit.getParameter(request, "userId", "1")%>">
		companyCode:<input type="text" name="companyCode" value="<%=ParamKit.getParameter(request, "companyCode", "")%>">
		channelCode:<input type="text" name="channelCode" value="<%=ParamKit.getParameter(request, "channelCode", "")%>">
		currency:<input type="text" name="currency" value="<%=ParamKit.getParameter(request, "storeId", "CNY")%>">
		requestAmount:<input type="text" name="requestAmount" value="<%=ParamKit.getParameter(request, "requestAmount", "0.01")%>">
		timestamp:<input type="text" name="timestamp" value="<%=ParamKit.getParameter(request, "timestamp", "sd1timestamp2")%>">
		randomValue:<input type="text" name="randomValue" value="<%=ParamKit.getParameter(request, "randomValue", "randomValue")%>">
		<br/>
		获得加密字符串：<input type="checkbox" name="option"  value="1" />
		<input type="submit" value="提交"/>
	</form>
	data:
	<textarea style="width:100%;height:200px;"><%=StringKit.validStr(ParamKit.getAttribute(request, "data")) %></textarea>
</body>
</html>