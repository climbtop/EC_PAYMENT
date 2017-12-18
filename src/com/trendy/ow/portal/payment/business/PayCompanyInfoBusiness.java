package com.trendy.ow.portal.payment.business;

import java.util.List;

import com.trendy.ow.portal.payment.bean.PayCompanyInfoBean;
import com.trendy.ow.portal.payment.dao.PayCompanyInfoDao;

public class PayCompanyInfoBusiness {

	public List<PayCompanyInfoBean> getPayCompanyInfoAllList() {
		PayCompanyInfoDao dao = new PayCompanyInfoDao();
		return dao.getPayCompanyInfoAllList();
	}

	public PayCompanyInfoBean getPayCompanyInfoByCode(String companyCode) {
		PayCompanyInfoDao dao = new PayCompanyInfoDao();
		return dao.getPayCompanyInfoByCode(companyCode);
	}
	
	
	public PayCompanyInfoBean getCompanyByKey(int companyId){
		PayCompanyInfoDao dao = new PayCompanyInfoDao();
		return dao.getPayCompanyInfoByKey(companyId);
	}
	
}