package com.trendy.ow.portal.payment.business;

import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.dao.PayItemDao;

public class PayItemBusiness {
	public int addPayItem(PayItemBean bean) {
		PayItemDao dao = new PayItemDao();
		return dao.insertPayItem(bean);
	}

	public int updatePayItem(PayItemBean bean) {
		PayItemDao dao = new PayItemDao();
		return dao.updatePayItem(bean);
	}

	public PayItemBean getPayItemByKey(int itemId) {
		PayItemDao dao = new PayItemDao();
		return dao.getPayItemByKey(itemId);
	}
	
	public int updatePayItemAndInfo(PayItemBean itemBean,PayInfoBean infoBean) {
		PayItemDao dao = new PayItemDao();
		return dao.updatePayItemAndInfo(itemBean,infoBean);
	}
	
	public PayItemBean findPayItem(int payId,String payNumber) {
		PayItemDao dao = new PayItemDao();
		return dao.findPayItem(payId, payNumber);
	}
	
	//for callbacktest
	public PayItemBean getNearestPayItem(int payId) {
		PayItemDao dao = new PayItemDao();
		return dao.getNearestPayItem(payId);
	}
	
	// 支付跳转时生成payItem
	public PayItemBean savePayItem(int payId, int channelId, int companyId, String currency, String ipAddress,String payStatus,int status) {
		PayItemBean bean = new PayItemBean();
		bean.setPayId(payId);
		bean.setChannelId(channelId);
		bean.setCompanyId(companyId);
		bean.setPayStatus(payStatus);
		bean.setCurrency(currency);
		bean.setIpAddress(ipAddress);
		bean.setStatus(status);
		int itemId =this.addPayItem(bean);
		bean.setPayItemId(itemId);
		return bean;
	}
	
}