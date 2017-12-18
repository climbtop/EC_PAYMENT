<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="com.trendy.fw.common.crypto.*"%>
<%@page import="com.trendy.fw.common.util.*"%>
<%@page import="java.util.*"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="com.trendy.ow.portal.payment.weixin.bean.WeixinConfigBean"%>
<%@page import="com.trendy.ow.portal.payment.weixin.business.WeixinPayProcessor"%>
<%@page import="com.trendy.ow.portal.payment.bean.*"%>
<%@ include file="../../../include/Common.jsp"%>
  <%
  TreeMap<String,String> packageInfoMap=(TreeMap<String,String>)request.getAttribute("packageInfoMap");
  WeixinConfigBean configBean = (WeixinConfigBean)request.getAttribute("configBean");
  PayInfoBean payInfo = (PayInfoBean)request.getAttribute("payInfo");
  %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>微信支付</title>
<script>
	function json2Str(res){
		var str=""
		for(var i in res){
			str+=i+"="+res[i]+"&"
		}
		return str.substring(0,str.length-1);
	}
	window.onload=function(){
		   function onBridgeReady(){
			   WeixinJSBridge.invoke(
			       'getBrandWCPayRequest', {
			           "appId" : "<%=packageInfoMap.get("appId")%>",       //公众号名称，由商户传入     
			           "timeStamp":"<%=packageInfoMap.get("timeStamp")%>", //时间戳，自1970年以来的秒数     
			           "nonceStr" : "<%=packageInfoMap.get("nonceStr")%>", //随机串     
			           "package" : "<%=packageInfoMap.get("package")%>",   //订单详情扩展字符串 
			           "signType" : "<%=packageInfoMap.get("signType")%>", //微信签名方式:     
			           "paySign" : "<%=signParam(configBean,packageInfoMap)%>"        //微信签名 
			       },
			       function(res){     
			           if(res.err_msg == "get_brand_wcpay_request:ok" ) {
			        	   window.location.href="<%=_sName.get("my")%>/order/payThanks.do?orderId=<%=payInfo.getInfoId()%>&referType=<%=payInfo.getReferType()%>";
			           }else{
			        	   //alert("支付未完成!");
			        	   window.location.href="<%=_sName.get("my")%>/order/list.do?status=0";
			           }
			       }
			   ); 
			}
			if (typeof WeixinJSBridge == "undefined"){
			   if( document.addEventListener ){
			       document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
			   }else if (document.attachEvent){
			       document.attachEvent('WeixinJSBridgeReady', onBridgeReady); 
			       document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
			   }
			}else{
			   onBridgeReady();
			}
	}
	
</script>
</head>
<body class="body_bg" style="background-color:#888888;">
</body>
</html>

<%!
public String signParam(WeixinConfigBean configBean, TreeMap<String,String> packageInfoMap) throws Exception {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("appId", packageInfoMap.get("appId"));
		map.put("timeStamp", packageInfoMap.get("timeStamp"));
		map.put("nonceStr", packageInfoMap.get("nonceStr"));
		map.put("package", packageInfoMap.get("package"));
		map.put("signType", packageInfoMap.get("signType"));
		String md5key = configBean.getKey();
		String paySign = new WeixinPayProcessor().signParam(map, "", md5key);
		return paySign;
}
%>