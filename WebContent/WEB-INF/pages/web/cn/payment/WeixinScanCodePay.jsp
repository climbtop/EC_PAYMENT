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
<html>
<head>
	<meta charset="utf-8">
	<title>微信扫码支付</title>

	<%@ include file="/module/static/3c/ed/my_css_js.html" %>	
</head>
<body>
<%@ include file="/module/static/da/89/och_header_v1.html" %>

	<div class="cart_container">
		<div class="edge">
			<div class="checkout_step align_center">
				<div class="pay_wechar_qrcode" id="payWecharQrcode">
					<h2>请用微信扫描下面的二维码完成支付</h2>
					<div>
						<p class="col_2_1 row">
							<span class="col_2_1 cell align_right"><em>订单号：</em><strong><%=infoBean.getInfoId()%></strong></span>
							<span class="col_2_1 cell align_left"><em>订单金额：</em><strong>￥<%=String.format("%.2f", infoBean.getRequestAmount())%></strong></span>
						</p>
					</div>
					<img src="/weixin/WeixinCreatScanCode.do?codeUrl=<%=ParamKit.getParameter(request, "codeUrl") %>" />
					<p><a href="javascript:;" onclick="window.history.go(-1)">选择其他支付方式</a></p>
				</div>
			</div>
		</div>
	</div>
	<script>
		te$.business.pay.waitWecharQrcodeScan({'itemId':'<%=itemBean.getPayItemId()%>', 'payId':'<%=infoBean.getPayId()%>'});
	</script>

<%@ include file="/module/static/b4/69/och_footer_v1.html" %>
</body>
</html>