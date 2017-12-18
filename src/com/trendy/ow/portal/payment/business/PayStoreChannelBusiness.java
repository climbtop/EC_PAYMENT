package com.trendy.ow.portal.payment.business;

import java.util.List;

import com.trendy.ow.portal.payment.bean.PayStoreChannelBean;
import com.trendy.ow.portal.payment.dao.PayStoreChannelDao;

public class PayStoreChannelBusiness {

	public List<PayStoreChannelBean> getPayStoreChannelListByStoreId(int storeId) {
		PayStoreChannelDao dao = new PayStoreChannelDao();
		return dao.getPayStoreChannelListByStoreId(storeId);
	}

	public PayStoreChannelBean getPayStoreChannel(int storeId,int channelId) {
		PayStoreChannelDao dao = new PayStoreChannelDao();
		return dao.getPayStoreChannel(storeId,channelId);
	}
}