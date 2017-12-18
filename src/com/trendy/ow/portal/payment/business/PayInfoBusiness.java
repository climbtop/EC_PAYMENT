package com.trendy.ow.portal.payment.business;

import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.util.StringKit;
import com.trendy.fw.tools.order.config.SyncStatusConfig;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayCompanyInfoBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayRequestBean;
import com.trendy.ow.portal.payment.cache.PayChannelCache;
import com.trendy.ow.portal.payment.cache.PayCompanyCache;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.dao.PayInfoDao;

public class PayInfoBusiness {
	public int addPayInfo(PayInfoBean bean) {
		PayInfoDao dao = new PayInfoDao();
		return dao.insertPayInfo(bean);
	}

	public int updatePayInfo(PayInfoBean bean) {
		PayInfoDao dao = new PayInfoDao();
		return dao.updatePayInfo(bean);
	}

	public PayInfoBean getPayInfoByKey(int payId) {
		PayInfoDao dao = new PayInfoDao();
		return dao.getPayInfoByKey(payId);
	}

	public PayInfoBean getPayInfoByKey(int infoId, String referType, int appId) {
		PayInfoDao dao = new PayInfoDao();
		return dao.getPayInfoByKey(infoId, referType, appId);
	}
	
	//for callbacktest
	public PayInfoBean getNearestPayInfo() {
		PayInfoDao dao = new PayInfoDao();
		return dao.getNearestPayInfo();
	}
	
	public int savePayInfo(PayRequestBean requestBean, String ipAddress) {
		PayLogBusiness logBusiness = new PayLogBusiness();
		PayInfoBean payInfo = new PayInfoBean();
		String companyCode = requestBean.getCompanyCode();
		String channelCode = requestBean.getChannelCode();
		if (StringKit.isValid(companyCode)) {
			PayCompanyCache companyCache = new PayCompanyCache();
			PayCompanyInfoBean company = companyCache.getPayCompanyInfo(companyCode);
			payInfo.setCompanyId(company.getCompanyId());
		}
		if (StringKit.isValid(channelCode)) {
			PayChannelCache channelCache = new PayChannelCache();
			PayChannelInfoBean channel = channelCache.getPayChannelInfo(channelCode);
			payInfo.setChannelId(channel.getChannelId());
		}
		payInfo.setAppId(requestBean.getAppId());
		payInfo.setCurrency(requestBean.getCurrency());
		payInfo.setRequestAmount(Double.valueOf(requestBean.getRequestAmount()));
		payInfo.setInfoId(requestBean.getInfoId());
		payInfo.setReferType(requestBean.getReferType());
		payInfo.setIpAddress(ipAddress);
		payInfo.setStoreId(requestBean.getStoreId());
		payInfo.setUserId(requestBean.getUserId());
		payInfo.setStatus(Constants.STATUS_VALID);
		payInfo.setPayStatus(PayConfig.PAYS_WAIT_PAY);
		payInfo.setSyncStatus(SyncStatusConfig.SYNCS_NONE);
		int payId = addPayInfo(payInfo);
		if (payId > 0) {
			logBusiness.savePayLog(payId, PayConfig.PAYS_WAIT_PAY, "新增支付信息记录成功");
		}
		return payId;
	}

}