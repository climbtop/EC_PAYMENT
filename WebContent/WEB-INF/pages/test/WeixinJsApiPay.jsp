<%@page import="com.trendy.fw.common.util.StatusKit"%>
<%@page import="com.trendy.fw.common.util.MapKit"%>
<%@page import="com.trendy.ow.portal.payment.config.PayConfig"%>
<%@page import="com.trendy.ow.portal.payment.bean.PayCompanyInfoBean"%>
<%@page import="com.trendy.ow.portal.payment.bean.PayChannelInfoBean"%>
<%@page import="com.trendy.ow.portal.payment.bean.PayInfoBean"%>
<%@page import="com.trendy.ow.portal.payment.bean.PayRequestBean"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
	<%
		PayRequestBean detailBean=(PayRequestBean)request.getAttribute("PayRequestDetailBean");
		 //  PayInfoBean payInfoBean=(PayInfoBean)request.getAttribute("PayInfoBean");
		   Map<Integer, List<PayChannelInfoBean>> map=( Map<Integer, List<PayChannelInfoBean>>)request.getAttribute("channelListGroupByType");
	%>		
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>weixinjsApipay-test</title>
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
	window.onload=function(){
		eval('var bizPackage=<%=request.getAttribute("bizPackage")%>');
		if(WeixinJSBridge){
			WeixinJSBridge.invoke('getBrandWCPayRequest',bizPackage,function(res){
				WeixinJSBridge.log(res.err_msg);
				getDom("err_msg").value=res.err_msg;
				getDom("err_code").value=res.err_code;
				getDom("err_desc").value=res.err_desc;
				alert(res.err_code+res.err_desc);
			});
		}else{
			alert('浏览器不支持');
		}
	}
	function getDom(id){
		return document.getElementById(id)
	}
</script>
</head>
<body>
	<span id="bizPackage"><%=request.getAttribute("bizPackage")%></span>
	<form method="get" id="form" action="<%=request.getAttribute("callBackUrl")%>">
		itemId:<input name="itemId" id="itemId" value="<%=request.getAttribute("itemId")%>">
		err_msg:<input name="err_msg" id="err_msg">
		err_code:<input name="err_code" id="err_code">
		err_desc:<input name="err_desc" id="err_desc">
		<input type="submit" value="提交"/>
	</form>
</body>
</html>