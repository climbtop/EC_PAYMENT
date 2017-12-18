package com.trendy.ow.portal.payment.weixin.bean;

public class WeixinMicropayResponseBean extends WeixinBaseResponseBean {
	// 用户标识
	private String openid = "";
	// 是否关注公众账号
	private String isSubscribe = "";
	// 付款银行
	private String bankType = "";
	// 货币类型
	private String feeType = "";
	// 总金额
	private String totalFee = "";
	// 现金支付金额
	private String cashFee = "";
	// 代金券或立减优惠金额
	private String couponFee = "";
	// 微信支付订单号
	private String transactionId = "";
	// 商户订单号
	private String outTradeNo = "";
	// 商家数据包
	private String attach = "";
	// 支付完成时间
	private String timeEnd = "";
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getIsSubscribe() {
		return isSubscribe;
	}
	public void setIsSubscribe(String isSubscribe) {
		this.isSubscribe = isSubscribe;
	}
	public String getBankType() {
		return bankType;
	}
	public void setBankType(String bankType) {
		this.bankType = bankType;
	}
	public String getFeeType() {
		return feeType;
	}
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	public String getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}
	public String getCashFee() {
		return cashFee;
	}
	public void setCashFee(String cashFee) {
		this.cashFee = cashFee;
	}
	public String getCouponFee() {
		return couponFee;
	}
	public void setCouponFee(String couponFee) {
		this.couponFee = couponFee;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public String getAttach() {
		return attach;
	}
	public void setAttach(String attach) {
		this.attach = attach;
	}
	public String getTimeEnd() {
		return timeEnd;
	}
	public void setTimeEnd(String timeEnd) {
		this.timeEnd = timeEnd;
	}

}
