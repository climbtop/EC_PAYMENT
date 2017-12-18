package com.trendy.ow.portal.payment.alipay.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.trendy.fw.common.cache.LocalCache;
import com.trendy.fw.common.config.Constants;
import com.trendy.ow.portal.payment.alipay.bean.AliPayAppConfigBean;
import com.trendy.ow.portal.payment.alipay.business.AliPayAppConfigBusiness;
import com.trendy.ow.portal.payment.config.PayCacheConfig;

public class AliPayAppCache {
	private static Logger log = LoggerFactory.getLogger(AliPayAppCache.class);
	public AliPayAppConfigBean getAliPayAppConfig(String storeCode) {
		AliPayAppConfigBean bean = new AliPayAppConfigBean();
		String key = PayCacheConfig.getAliPayAppConfigKey(storeCode);
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				bean = new AliPayAppConfigBusiness().getAliPayAppConfig(storeCode);
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
			bean = (AliPayAppConfigBean) obj;
		}
		return bean;
	}

	public boolean removeAliPayAppConfig(String storeCode) {
		boolean result = false;
		String key = PayCacheConfig.getAliPayAppConfigKey(storeCode);
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
