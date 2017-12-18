package com.trendy.ow.portal.payment.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.trendy.fw.common.cache.LocalCache;
import com.trendy.fw.common.config.Constants;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.business.PayChannelInfoBusiness;
import com.trendy.ow.portal.payment.config.PayCacheConfig;

public class PayChannelCache {
	private static Logger log = LoggerFactory.getLogger(PayChannelCache.class);

	public PayChannelInfoBean getPayChannelInfo(String channelCode) {
		PayChannelInfoBean bean = new PayChannelInfoBean();
		String key = PayCacheConfig.getPayChannelInfoKey(channelCode);
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				obj = new PayChannelInfoBusiness().getPayChannelInfo(channelCode);
				if (obj != null) {
					localCache.set(key, obj, Constants.CACHE_TIME_2_HOUR);
					update = true;
				}
			} catch (Exception e) {
				log.error("", e);
			} finally {
				if (!update) {
					obj = nre.getCacheContent();
					localCache.cancelUpdate(key);
				}
			}
		}
		if (obj != null) {
			bean = (PayChannelInfoBean) obj;
		}
		return bean;
	}

	public boolean removePayChannelInfo(String channelCode) {
		boolean result = false;
		String key = PayCacheConfig.getPayChannelInfoKey(channelCode);
		LocalCache localCache = LocalCache.getInstance();
		try {
			localCache.delete(key);
			result = true;
		} catch (Exception e) {
			log.error("", e);
		}
		return result;
	}

	public PayChannelInfoBean getPayChannelInfo(int channelId) {
		PayChannelInfoBean bean = new PayChannelInfoBean();
		String key = PayCacheConfig.getPayChannelInfoKey(channelId);
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				obj = new PayChannelInfoBusiness().getPayChannelInfo(channelId);
				if (obj != null) {
					localCache.set(key, obj, Constants.CACHE_TIME_2_HOUR);
					update = true;
				}
			} catch (Exception e) {
				log.error("", e);
			} finally {
				if (!update) {
					obj = nre.getCacheContent();
					localCache.cancelUpdate(key);
				}
			}
		}
		if (obj != null) {
			bean = (PayChannelInfoBean) obj;
		}
		return bean;
	}

	public boolean removePayChannelInfo(int channelId) {
		boolean result = false;
		String key = PayCacheConfig.getPayChannelInfoKey(channelId);
		LocalCache localCache = LocalCache.getInstance();
		try {
			localCache.delete(key);
			result = true;
		} catch (Exception e) {
			log.error("", e);
		}
		return result;
	}

	public List<PayChannelInfoBean> getPayChannelListByCompanyId(int companyId) {
		List<PayChannelInfoBean> list = new ArrayList<PayChannelInfoBean>();
		String key = PayCacheConfig.getPayChannelInfoListByCompanyIdKey(companyId);
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				list = new PayChannelInfoBusiness().getPayChannelListByCompanyId(companyId);
				if (!list.isEmpty()) {
					localCache.set(key, list, Constants.CACHE_TIME_2_HOUR);
					update = true;
				}
			} catch (Exception e) {
				log.error("", e);
			} finally {
				if (!update) {
					obj = nre.getCacheContent();
					localCache.cancelUpdate(key);
				}
			}
		}
		if (obj != null) {
			list = (List<PayChannelInfoBean>) obj;
		}
		return list;
	}

	public boolean removePayChannelListByCompanyId(int companyId) {
		boolean result = false;
		String key = PayCacheConfig.getPayChannelInfoListByCompanyIdKey(companyId);
		LocalCache localCache = LocalCache.getInstance();
		try {
			localCache.delete(key);
			result = true;
		} catch (Exception e) {
			log.error("", e);
		}
		return result;
	}

	public Map<Integer, List<PayChannelInfoBean>> getChannelInfoListMap(int store, int payLocal, String companyCode,
			String channelCode) {
		Map<Integer, List<PayChannelInfoBean>> map = new HashMap<Integer, List<PayChannelInfoBean>>();
		String key = PayCacheConfig
				.getPayChannelInfoListGroupByCompanyMapKey(store, payLocal, companyCode, channelCode);
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				obj = new PayChannelInfoBusiness().getChannelInfoListMap(store, payLocal, companyCode, channelCode);
				if (obj != null) {
					localCache.set(key, obj, Constants.CACHE_TIME_2_HOUR);
					update = true;
				}
			} catch (Exception e) {
				log.error("", e);
			} finally {
				if (!update) {
					obj = nre.getCacheContent();
					localCache.cancelUpdate(key);
				}
			}
		}
		if (obj != null) {
			map = (Map<Integer, List<PayChannelInfoBean>>) obj;
		}
		return map;
	}

	public boolean removeChannelInfoListMap(int store, int payLocal, String companyCode, String channelCode) {
		boolean result = false;
		String key = PayCacheConfig
				.getPayChannelInfoListGroupByCompanyMapKey(store, payLocal, companyCode, channelCode);
		LocalCache localCache = LocalCache.getInstance();
		try {
			localCache.delete(key);
			result = true;
		} catch (Exception e) {
			log.error("", e);
		}
		return result;
	}
}
