package com.trendy.ow.portal.payment.cache;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.trendy.fw.common.cache.LocalCache;
import com.trendy.fw.common.config.Constants;
import com.trendy.ow.portal.payment.business.StoreInfoBusiness;
import com.trendy.ow.portal.payment.config.PayCacheConfig;

public class StoreInfoCache {
	private static Logger log = LoggerFactory.getLogger(StoreInfoCache.class);

	public Map<Integer, String> getStoreCodeByIdMap() {
		Map<Integer, String> map = new HashMap<Integer,String>();
		String key = PayCacheConfig.PAY_STORE_INFO_CODE_ALL_MAP_KEY;
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				map = new StoreInfoBusiness().getStoreCodeByIdMap();
				if (!map.isEmpty()) {
					localCache.set(key, map, Constants.CACHE_TIME_2_HOUR);
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
			map = (Map<Integer, String>) obj;
		}
		return map;
	}

	public boolean removeStoreCodeByIdMap() {
		boolean result = false;
		String key = PayCacheConfig.PAY_STORE_INFO_CODE_ALL_MAP_KEY;
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
