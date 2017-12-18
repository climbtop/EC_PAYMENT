package com.trendy.ow.portal.payment.cache;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.trendy.fw.common.cache.LocalCache;
import com.trendy.fw.common.config.Constants;
import com.trendy.ow.portal.payment.business.SysApplicationInfoBusiness;
import com.trendy.ow.portal.payment.config.PayCacheConfig;

public class SysApplicationInfoCache {
	private static Logger log = LoggerFactory.getLogger(SysApplicationInfoCache.class);

	public Map<Integer, String> getSysApplicationInfoCodeAllMap()  {
		Map<Integer, String> map = new HashMap<Integer, String>();
		String key = PayCacheConfig.SYS_APPLICATION_CODE_ALL_MAP_KEY;
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				map = new SysApplicationInfoBusiness().getSysApplicationInfoCodeAllMap();
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

	public boolean reomoveSysApplicationInfoCodeAllMap() {
		boolean result = false;
		String key = PayCacheConfig.SYS_APPLICATION_CODE_ALL_MAP_KEY;
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
