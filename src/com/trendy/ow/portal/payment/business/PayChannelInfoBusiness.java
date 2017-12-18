package com.trendy.ow.portal.payment.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.util.StringKit;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayCompanyInfoBean;
import com.trendy.ow.portal.payment.bean.PayStoreChannelBean;
import com.trendy.ow.portal.payment.cache.PayChannelCache;
import com.trendy.ow.portal.payment.cache.PayCompanyCache;
import com.trendy.ow.portal.payment.cache.PayStoreChannelCache;
import com.trendy.ow.portal.payment.dao.PayChannelInfoDao;
import com.trendy.ow.portal.payment.zero.config.ZeroPayConfig;

public class PayChannelInfoBusiness {
	public PayChannelInfoBean getPayChannelInfo(String channelCode) {
		PayChannelInfoDao dao = new PayChannelInfoDao();
		return dao.getPayChannelInfo(channelCode);
	}

	public PayChannelInfoBean getPayChannelInfo(int channelId) {
		PayChannelInfoDao dao = new PayChannelInfoDao();
		return dao.getPayChannelInfo(channelId);
	}

	public List<PayChannelInfoBean> getPayChannelListByCompanyId(int companyId) {
		PayChannelInfoDao dao = new PayChannelInfoDao();
		return dao.getPayChannelInfoListByCompanyId(companyId);
	}

	public Map<Integer, List<PayChannelInfoBean>> getChannelInfoListMap(int storeId, int payLocal, String companyCode,
			String channelCode) {
		Map<Integer, List<PayChannelInfoBean>> map = new HashMap<Integer, List<PayChannelInfoBean>>();
		PayChannelCache channelCache = new PayChannelCache();
		PayCompanyCache companyCache = new PayCompanyCache();
		PayStoreChannelCache storeChannelCache = new PayStoreChannelCache();

		List<PayStoreChannelBean> storeChannelBeans = storeChannelCache.getPayStoreChannelList(storeId);

		for (PayStoreChannelBean storeChannelBean : storeChannelBeans) {
			PayChannelInfoBean channelInfoBean = channelCache.getPayChannelInfo(storeChannelBean.getChannelId());
			int channelType = channelInfoBean.getChannelType();
			if (channelInfoBean != null && channelInfoBean.getPayLocale() == payLocal
					&& channelInfoBean.getStatus() == Constants.STATUS_VALID
					&& channelInfoBean.getUseStatus() == Constants.STATUS_VALID) {
				PayCompanyInfoBean companyInfoBean = companyCache.getPayCompanyInfo(channelInfoBean.getCompanyId());
				boolean isValid = true;
				// 判断指定companyCode存在下 渠道所属支付公司companyCode 是否一致
				if (StringKit.isValid(companyCode)) {
					if (companyInfoBean == null || !companyCode.equals(companyInfoBean.getCompanyCode())) {
						isValid = false;
					}
					if (isValid && StringKit.isValid(channelCode)
							&& !channelCode.equals(channelInfoBean.getChannelCode())) {
						isValid = false;
					}

				} else if (StringKit.isValid(channelCode) && !channelCode.equals(channelInfoBean.getChannelCode())) {
					isValid = false;
				}
				if(ZeroPayConfig.CHANNEL_CODE_DEFAULT.equals(channelInfoBean.getChannelCode()) && !ZeroPayConfig.CHANNEL_CODE_DEFAULT.equals(channelCode)){
					isValid = false;
				}
				if(ZeroPayConfig.COMPANY_CODE.equals(companyInfoBean.getCompanyCode()) && !ZeroPayConfig.COMPANY_CODE.equals(companyCode)){
					isValid = false;
				}
				if (isValid) {
					List<PayChannelInfoBean> list = map.get(channelType);
					if (list != null) {
						list.add(channelInfoBean);
					} else {
						list = new ArrayList<PayChannelInfoBean>();
						list.add(channelInfoBean);
						map.put(channelType, list);
					}
				}
			}
		}
		return map;
	}
}