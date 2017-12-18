package com.trendy.ow.portal.payment.cache;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.trendy.fw.common.cache.LocalCache;
import com.trendy.fw.common.config.Constants;
import com.trendy.ow.portal.payment.bean.PayStoreChannelBean;
import com.trendy.ow.portal.payment.business.PayStoreChannelBusiness;
import com.trendy.ow.portal.payment.config.PayCacheConfig;

public class PayStoreChannelCache {
	private static Logger log = LoggerFactory.getLogger(PayStoreChannelCache.class);
	public PayStoreChannelBean getPayStoreChannel(int storeId,int channelId) {
		PayStoreChannelBean bean = new PayStoreChannelBean();
		String key = PayCacheConfig.getPayStoreChannelKey(storeId, channelId);
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				obj = new PayStoreChannelBusiness().getPayStoreChannel(storeId, channelId);
				if (obj!=null) {
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
			bean = (PayStoreChannelBean) obj;
		}
		return bean;
	}
	
	public boolean reomovePayStoreChannel(int storeId,int channelId) {
		boolean result = false;
		String key = PayCacheConfig.getPayStoreChannelKey(storeId, channelId);
		LocalCache localCache = LocalCache.getInstance();
		try {
			localCache.delete(key);
			result = true;
		} catch (Exception e) {
			log.error("", e);
		}
		return result;
	}
	
	public List<PayStoreChannelBean> getPayStoreChannelList(int storeId) {
		List<PayStoreChannelBean> list = new ArrayList<PayStoreChannelBean>();
		String key = PayCacheConfig.getPayStoreChannelAllListKey(storeId);
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				list = new PayStoreChannelBusiness().getPayStoreChannelListByStoreId(storeId);
				localCache.set(key, list, Constants.CACHE_TIME_2_HOUR);
				update = true;
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
			list = (List<PayStoreChannelBean>) obj;
		}
		return list;
	}
	
	public boolean reomovePayStoreChannelList(int storeId) {
		boolean result = false;
		String key = PayCacheConfig.getPayStoreChannelAllListKey(storeId);
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
