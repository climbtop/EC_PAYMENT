package com.trendy.ow.portal.payment.weixin.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.trendy.fw.common.cache.LocalCache;
import com.trendy.fw.common.config.Constants;
import com.trendy.ow.portal.payment.config.PayCacheConfig;
import com.trendy.ow.portal.payment.weixin.bean.WeixinConfigBean;
import com.trendy.ow.portal.payment.weixin.business.WeixinConfigBusiness;

public class WeixinCache {
	private static Logger log = LoggerFactory.getLogger(WeixinCache.class);
	public WeixinConfigBean getWeixinConfig(String storeCode) {
		WeixinConfigBean bean = new WeixinConfigBean();
		String key = PayCacheConfig.getWeixinConfigKey(storeCode);
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				bean = new WeixinConfigBusiness().getWeixinConfig(storeCode);
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
			bean = (WeixinConfigBean) obj;
		}
		return bean;
	}

	public boolean removeWeixinConfig(String storeCode) {
		boolean result = false;
		String key = PayCacheConfig.getWeixinConfigKey(storeCode);
		LocalCache localCache = LocalCache.getInstance();
		try {
			localCache.delete(key);
			result = true;
		} catch (Exception e) {
			log.error("", e);
		}
		return result;
	}

	public boolean removeShopWeixinConfig(String shopNumber,String storeCode) {
		boolean result = false;
		String key = PayCacheConfig.getShopWeixinConfigKey(shopNumber,storeCode);
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
