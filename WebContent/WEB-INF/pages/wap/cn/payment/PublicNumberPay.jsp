<%@page import="java.util.TreeMap"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
  <%
  TreeMap<String,String> packageInfoMap=(TreeMap<String,String>)request.getAttribute("packageInfoMap");
  String callBackUrl=(String)request.getAttribute("callbackUrl");
  %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>微信支付</title>
<script>
function onBridgeReady(){
	try {
	   WeixinJSBridge.invoke(
	       'getBrandWCPayRequest', {
	    	   "appId" : '<%=packageInfoMap.get("appId")%>',
	           "timeStamp":'<%=packageInfoMap.get("timeStamp")%>',
	           "nonceStr" : '<%=packageInfoMap.get("nonceStr")%>',    
	           "package" : '<%=packageInfoMap.get("package")%>',     
	           "signType" : '<%=packageInfoMap.get("signType")%>', 
	           "paySign" : '<%=packageInfoMap.get("paySign")%>'
	       },
	       function(res){   
	           if(res.err_msg == "get_brand_wcpay_request:ok" ) {
	        	   window.location.href="<%=callBackUrl%>";
	        	}else if(res.err_msg == "get_brand_wcpay_request:cancel"){ 
	        		alert("支付取消");
	        		window.history.back(-1);
	        	}else{
	        		alert(res.err_desc);
	        		window.history.back(-1);
	        	}
	       }
	   ); 
	} catch (e) {
		alert("exception:"+e);
	}
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
</script>
</head>
<body>

</body>
</html>