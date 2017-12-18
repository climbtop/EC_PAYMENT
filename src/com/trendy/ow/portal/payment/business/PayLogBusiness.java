package com.trendy.ow.portal.payment.business;

import com.trendy.ow.portal.payment.bean.PayLogBean;
import com.trendy.ow.portal.payment.dao.PayLogDao;

public class PayLogBusiness {
	public int addPayLog(PayLogBean bean) {
		PayLogDao dao = new PayLogDao();
		return dao.insertPayLog(bean);
	}

	public int savePayLog(int payId,String payStatus,String content){
		PayLogBean logBean=new PayLogBean();
		logBean.setPayStatus(payStatus);
		logBean.setPayId(payId);
		logBean.setPayContent(content);
		return addPayLog(logBean);
	}
}