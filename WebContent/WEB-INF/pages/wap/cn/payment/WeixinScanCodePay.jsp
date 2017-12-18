<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="com.trendy.ow.portal.payment.bean.PayItemBean"%>
<%@page import="com.trendy.ow.portal.payment.bean.PayInfoBean"%>
<%@page import="com.trendy.ow.portal.payment.weixin.bean.WeixinPreOrderResponseBean"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="com.trendy.fw.common.web.ParamKit"%>

<%
WeixinPreOrderResponseBean wxResponse=(WeixinPreOrderResponseBean)request.getAttribute("responseBean");
PayInfoBean infoBean=(PayInfoBean)request.getAttribute("payInfo");
PayItemBean itemBean=(PayItemBean)request.getAttribute("payItem");
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<title>微信扫码支付</title>
	<%@ include file="/module/static/ad/ec/wap_my_css_js.html" %>
</head>
<body>
	<div class="fp_container">
		<div class="top_nav" id="topNav">
			<span>微信扫码支付</span>
			<a href="javascript:;" onclick="window.history.go(-1);">返回</a>
		</div>

		<div class="checkout_step align_center">
			<div class="pay_wechar_qrcode" id="payWecharQrcode">
				<h2>请用微信扫描下面的二维码或长按图片识别</h2>
				<p><em>订单号：</em><strong><%=infoBean.getInfoId()%></strong></p>
				<p><em>订单金额：</em><strong>￥<%=String.format("%.2f", infoBean.getRequestAmount())%></strong></p>
				<img src="/weixin/WeixinCreatScanCode.do?codeUrl=<%=ParamKit.getParameter(request, "codeUrl") %>" />
				<p><a href="javascript:;" onclick="window.history.go(-1)">选择其他支付方式</a></p>
			</div>
		</div>

		<script>
			te$.mobile.pay.waitWecharQrcodeScan({'itemId':'<%=itemBean.getPayItemId()%>', 'payId':'<%=infoBean.getPayId()%>'});
		</script>
	</div>
</body>
</html>