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
<html lang="en">
    <head>
        <%@ include file="/module/static/30/ca/lp_ec_meta.html"%>
        <title>选择支付方式</title>
        <%@ include file="/module/static/cc/12/lp_ec_common.html"%>
        <script src="/lpmas/ec/common/js/te.js"></script>
		<script src="/lpmas/ec/common/js/te.business.js"></script>
    </head>
<body class="body_bg">
		<header class="box-s">
			<a href ="<%=_sName.get("my")%>/order/list.do?status=0"><img src="/lpmas/ec/wap/v0/images/point.png" class="fl point_img"></a>
		    <span class="head_title">选择支付方式</span>
		    <a href="<%=_sName.get("my")%>/user/Home.do"><img src="/lpmas/ec/wap/v0/images/user.png" class="fr  shop_car right1"></a>
		    <a href="<%=_sName.get("my")%>/order/cart.do"><img src="/lpmas/ec/wap/v0/images/key.png" class="fr"></a>
		    <div class="shop_tnum">
		        <span class="red_point border-r1"></span>
		    </div>
		</header>
        
        <script>
			var _mvq = _mvq || [];
			var _gaSkuItem = _gaSkuItem || [];
			_mvq.push(['$setGeneral', 'ordercreate', '', '', '']);
			_mvq.push(['$addOrder','<%=detailBean.getInfoId()%>', '<%=String.format("%.2f", detailBean.getRequestAmount())%>']);
		</script>
		

		<div class="back_c10 pa1">
		    <div class="c_3 f_w s_fs1 pa11">
		            <p class="dis">
		                <span>订单编号：</span><span><%=detailBean.getInfoId()%></span>
		            </p>
		            <p class="dis fr">
		                <span class="c_2">￥<%=String.format("%.2f", detailBean.getRequestAmount())%></span>
		            </p>
		    </div>
			<!--支付信息|支付Id: <%=request.getAttribute("payId")%> ;appId:<%=detailBean.getAppId() %>-->
			<!-- <div>显示渠道|渠道公司数目：<%=map.keySet().size() %>  </div> -->
			<form action="PayRedirect.do" id="payForm">
				<input type="hidden" name="payId" value="<%=request.getAttribute("payId")%>" />
				<input type="hidden" name="channelId" value="" />
			</form>
			
		    <p class="clear"></p>
		    <div id="payChannelChoose">
		        <p class="c_3 s_fs pa11 bot">选择支付方式</p>
		        
						<%
						Set<Entry<Integer,List<PayChannelInfoBean>>> entrys=map.entrySet();
						for(Entry<Integer,List<PayChannelInfoBean>> entry:entrys){
						%>
							<!--<%=MapKit.getValueFromMap(entry.getKey()+"", StatusKit.toMap(PayConfig.PAY_CHANNEL_TYPE_LIST))%>-->
							<%
							List<PayChannelInfoBean> list=entry.getValue();
							for(PayChannelInfoBean channel:list){	
							%>
					        <p class="bot pa12 po_r" >
					            <img src="http:<%=_sName.get("img2")%><%=channel.getChannelLogo() %>" class="pay_img">
					            <span class="chose_img <%=map.keySet().size()==1?"choose":"no_choose"%>" for="raBank<%=channel.getChannelId() %>"></span>
					            <input type="radio" class="radio_pay" name="payChannel" value="<%=channel.getChannelId() %>" id="raBank<%=channel.getChannelId() %>" <%=map.keySet().size()==1?"checked":""%> />
					            <%if(map.keySet().size()==1){%><script>t$('payForm').channelId.value = '<%=channel.getChannelId() %>';</script><%}%>
					        </p>
							<%
							}
						}
						%>
		    </div>
		</div>
		
		<div class="pay_btn"><p class="u_btn go_pay border-r w9" id="btnGotoPay">支付</p></div>

	<script>
		var _gaOrderInfo = {
			'shop' : te$.getCurrBrand(),
			'total': <%=String.format("%.2f", detailBean.getRequestAmount())%>,
			'ship': 0.00000,
			'id' : 5585244
		};
		$(document).ready(function(){
			var	payForm = t$('payForm'),
				channelChoose = t$('payChannelChoose'),
				check = null;

			if (!payForm) return;

			check = function() {
				if (payForm.payId.value && payForm.channelId.value && payForm.channelId.value!='') return true;
				else {
					alert('请选择一种支付方式');
					return false;	
				}
			};

			$('#payChannelChoose input').click(function(){
				payForm.channelId.value = this.value;
				$(channelChoose).find('span').each(function(){
					$(this).removeClass("choose").addClass("no_choose");
				});
				$(this).prev("span").removeClass("no_choose").addClass("choose");
			});

			$('#btnGotoPay').click(function(){
				if (check()) payForm.submit();
			});
		});
	</script>
	
</body>
</html>