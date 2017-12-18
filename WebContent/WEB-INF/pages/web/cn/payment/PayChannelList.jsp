<%@ page trimDirectiveWhitespaces="true" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
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
<%@ include file="../../../include/Common.jsp"%>
<%
	PayRequestBean detailBean=(PayRequestBean)request.getAttribute("payRequestBean");
	 //  PayInfoBean payInfoBean=(PayInfoBean)request.getAttribute("PayInfoBean");
	   Map<Integer, List<PayChannelInfoBean>> map=( Map<Integer, List<PayChannelInfoBean>>)request.getAttribute("channelListGroupByType");
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<title>选择支付方式</title>

	<%@ include file="/module/static/3c/ed/my_css_js.html" %>	
</head>
<body>
<%@ include file="/module/static/da/89/och_header_v1.html" %>
	<script>
		var _mvq = _mvq || [];
		var _gaSkuItem = _gaSkuItem || [];
		_mvq.push(['$setGeneral', 'ordercreate', '', '', '']);
		_mvq.push(['$addOrder','<%=detailBean.getInfoId()%>', '<%=String.format("%.2f", detailBean.getRequestAmount())%>']);
	</script>	
	<div class="cart_container">
		<div class="edge">
			<div class="checkout_step">
				<div class="pay_tip col_6">
					<h2>订单已生成，请继续完成支付！</h2>
					<p><span>订单编号：<%=detailBean.getInfoId()%></span> <span>支付金额：<b>￥<%=String.format("%.2f", detailBean.getRequestAmount())%></b></span></p>
				</div>
			</div>

			<div class="checkout row">
				<!--支付信息|支付Id: <%=request.getAttribute("payId")%> ;appId:<%=detailBean.getAppId() %>-->
				<!-- <div>显示渠道|渠道公司数目：<%=map.keySet().size() %>  </div> -->
				<p class="pay_tip_fallow">你需要支付：<b>￥<%=String.format("%.2f", detailBean.getRequestAmount())%>元</b>，请选择：</p>
				<form action="PayRedirect.do" id="payForm">
					<input type="hidden" name="payId" value="<%=request.getAttribute("payId")%>" />
					<input type="hidden" name="channelId" value="" />
				</form>
				<div class="pay_choose col_13">
					<div class="pay_choose_content" id="payChannelChoose">
						<ul class="tab order_list_tab" id="orderListTab" data-config="{'target':'payWayList', 'tab':'li', 'content':'div', 'activeTabClass':'active', 'activeContentClass':''}">
							<li class="active col_4_1 cell"><span>网上银行支付</span></li>
							<li class="col_4_1 cell"><span>网上支付平台</span></li>
						</ul>
						<div id="payWayList">
							<%
							Set<Entry<Integer,List<PayChannelInfoBean>>> entrys=map.entrySet();
							for(Entry<Integer,List<PayChannelInfoBean>> entry:entrys){
							%>
							<div class="pay_bank_list">

								<!--<%=MapKit.getValueFromMap(entry.getKey()+"", StatusKit.toMap(PayConfig.PAY_CHANNEL_TYPE_LIST))%>-->
								<%
								List<PayChannelInfoBean> list=entry.getValue();
								for(PayChannelInfoBean channel:list){	
								%>
								<p>
									<label for="raBank<%=channel.getChannelId() %>">
										<input type="radio" name="payChannel" value="<%=channel.getChannelId() %>" id="raBank<%=channel.getChannelId() %>" />
										<img src="http:<%=_sName.get("img2")%><%=channel.getChannelLogo() %>" />
										<span>*</span>
									</label>
								</p>
								<%
								}
								%>
							</div>
							<%
							}
							%>
						</div>
					</div>	
				</div>
				<script>
					te$.ui.switching.init('orderListTab');
				</script>
				<div class="pay_total col_13">
					<div>
						<em>
							使用
							<span>
								<img id="imgPayChannel" src="" />
								<i>*</i>
							</span>
						</em>
						支付 <b>￥<%=String.format("%.2f", detailBean.getRequestAmount())%></b>
					</div>
					<p>
						<button class="col_3" type="button" id="btnGotoPay">支付</button>
					</p>
				</div>
				
			</div>
		</div>
	</div>
	<script>
		var _gaOrderInfo = {
			'shop' : te$.getCurrBrand(),
			'total': <%=String.format("%.2f", detailBean.getRequestAmount())%>,
			'ship': 0.00000,
			'id' : 5585244
		}
		var _gaViewPoint1 = 'beforePay';
		te$.business.pay.initPayChannel();
	</script>

<%@ include file="/module/static/b4/69/och_footer_v1.html" %>
</body>
</html>