package com.trendy.ow.portal.payment.alipay.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.trendy.ec.console.cache.LocalCacheController;
import com.trendy.fw.common.cache.LocalCache;
import com.trendy.fw.common.config.Constants;
import com.trendy.ow.portal.payment.alipay.bean.AliPayWapConfigBean;
import com.trendy.ow.portal.payment.alipay.business.AliPayWapConfigBusiness;
import com.trendy.ow.portal.payment.config.PayCacheConfig;
import com.trendy.ow.portal.payment.config.PayConfig;

public class AliPayWapCache {
	private static Logger log = LoggerFactory.getLogger(AliPayWapCache.class);

	public AliPayWapConfigBean getAliPayWapConfig(String storeCode) {
		AliPayWapConfigBean bean = new AliPayWapConfigBean();
		String key = PayCacheConfig.getAliPayWapConfigKey(storeCode);
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				bean = new AliPayWapConfigBusiness().getAliPayWapConfig(storeCode);
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
			bean = (AliPayWapConfigBean) obj;
		}
		return bean;
	}

	public boolean removeAliPayWapConfig(String storeCode) {
		boolean result = false;
		String key = PayCacheConfig.getAliPayWapConfigKey(storeCode);
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
