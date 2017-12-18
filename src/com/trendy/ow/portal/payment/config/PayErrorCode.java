package com.trendy.ow.portal.payment.config;



public class PayErrorCode {
	

	//业务系统到支付系统的请求处理异常
	public static final int PAY_REQUEST_EXCEPTION=1;
	
	//支付跳转处理异常
	public static final int PAY_REDIRECT_EXCEPTION=2;
	
	//接受支付平台回调信息处理异常
	public static final int RECIVE_CALLBACK_EXCEPTION=3;
	
	//发送支付系统回调信息处理异常
	public static final int SEND_CALLBACK_EXCEPTION=4;
	
	//接受支付平台通知息处理异常
	public static final int RECIVE_NOTIFY_EXCEPTION=5;
	
	//发送支付系统通知信息处理异常
	public static final int SEND_NOTIFY_EXCEPTION=6;
	
	
	
}
