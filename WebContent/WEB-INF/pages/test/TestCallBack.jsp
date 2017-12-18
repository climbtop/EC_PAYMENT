<%@page import="com.trendy.ow.portal.payment.bean.PayChannelInfoBean"%>
<%@page import="com.trendy.ow.portal.payment.bean.PayCompanyInfoBean"%>
<%@page import="com.trendy.ow.portal.payment.bean.PayInfoBean"%>
<%@page import="com.trendy.ow.portal.payment.bean.PayRequestBean"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
	<%
		   PayInfoBean infoBean=(PayInfoBean)request.getAttribute("infoBean");
	%>		
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>TestCallBack</title>
<style type="text/css">
	.ul_bank li img.active {
 	 border-color: #a70432;
	}
	
	.ul_bank li img {
	  display: inline-block;
	  width: 123px;
	  height: 30px;
	  vertical-align: top;
	  border: 3px solid #eeeeee;
	}

</style>
<script>

</script>
</head>
<body>
	<h3>TenPayCallBack</h3>
	<form  method="post"  action="TestCallBack.do" id="form">
		<input type="hidden" name="callBackType" value="TenPayCallBack"/>
		trade_state:<input type="text" name="trade_state" value="0"/>
		appId:<input type="text" name="appId" value="5"/>
		infoId:<input type="text" name="infoId" value="<%=infoBean.getInfoId()%>"/>
		return_url:<input type="text" name="return_url" value="http://test03.teadmin.net:3080/tenpay/TenpayCallback.do" />
		<input type="submit" value="提交"/>
	</form>
<img src="TestScanMa.do" />
	<!-- 
	<h3>TestNotify</h3>
	<form  method="post"  action="TestCallBack.do" id="form">
		<input type="hidden" name="callBackType" value="TenPayNotify"/>
		infoId:<input type="text" name="infoId" value="15"/>
		itemId:<input type="text" name="itemId" value="19"/>
		storeCode:<input type="text" name="storeCode" value="OchirlyOfficial"/>
		appCode:<input type="text" name="appCode" value="Official"/>
		<input type="submit" value="提交"/>
	</form>
	<span>
	例子：
	</span>
	 -->
	
</body>
</html>