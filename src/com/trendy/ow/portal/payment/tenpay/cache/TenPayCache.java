package com.trendy.ow.portal.payment.tenpay.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.trendy.fw.common.cache.LocalCache;
import com.trendy.fw.common.config.Constants;
import com.trendy.ow.portal.payment.config.PayCacheConfig;
import com.trendy.ow.portal.payment.tenpay.bean.TenPayConfigBean;
import com.trendy.ow.portal.payment.tenpay.business.TenPayConfigBusiness;

public class TenPayCache {
	private static Logger log = LoggerFactory.getLogger(TenPayCache.class);

	public TenPayConfigBean getTenPayConfig(String storeCode) {
		TenPayConfigBean bean = new TenPayConfigBean();
		String key = PayCacheConfig.getTenPayConfigKey(storeCode);
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				bean = new TenPayConfigBusiness().getTenPayConfig(storeCode);
				localCache.set(key, bean, Constants.CACHE_TIME_2_HOUR);
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
			bean = (TenPayConfigBean) obj;
		}
		return bean;
	}

	public boolean removeTenPayConfig(String storeCode) {
		boolean result = false;
		String key = PayCacheConfig.getTenPayConfigKey(storeCode);
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
