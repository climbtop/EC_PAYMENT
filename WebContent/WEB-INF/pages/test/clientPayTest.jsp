<%@page import="com.trendy.fw.common.util.StringKit"%>
<%@page import="com.trendy.fw.common.web.ParamKit"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>clientNotifyTest</title>
<script type="text/javascript">
	
</script>
</head>
<body>
	<h3>。。。.</h3>
	<form method="post" action="/pay/PayChannelList.do" id="form">
		appId:<input type="text" name="appId" value="<%=request.getAttribute("appId")%>">
		data:<input type="text" name="data" value="<%=request.getAttribute("data")%>" >
		openId:<input type="text" name="openId" value="<%=request.getAttribute("openId")==null?"":request.getAttribute("openId")%>" >
		<input type="submit" value="提交" >
	</form>
</body>
</html>