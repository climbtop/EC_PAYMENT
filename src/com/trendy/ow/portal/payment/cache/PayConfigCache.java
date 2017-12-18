package com.trendy.ow.portal.payment.cache;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.trendy.fw.common.cache.LocalCache;
import com.trendy.fw.common.config.Constants;
import com.trendy.ow.portal.payment.bean.AppConfigBean;
import com.trendy.ow.portal.payment.bean.AppNotifyConfigBean;
import com.trendy.ow.portal.payment.business.AppConfigBusiness;
import com.trendy.ow.portal.payment.business.AppNotifyConfigBusiness;
import com.trendy.ow.portal.payment.business.PayConfigInfoBusiness;
import com.trendy.ow.portal.payment.config.PayCacheConfig;

public class PayConfigCache {
	private static Logger log = LoggerFactory.getLogger(PayConfigCache.class);

	public AppNotifyConfigBean getAppNotifyConfig(String storeCode) {
		AppNotifyConfigBean bean = new AppNotifyConfigBean();
		String key = PayCacheConfig.getClientConfigKey(storeCode);
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				bean = new AppNotifyConfigBusiness().getAppNotifyConfig(storeCode);
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
			bean = (AppNotifyConfigBean) obj;
		}
		return bean;
	}

	public boolean removeClientConfig(String storeCode) {
		boolean result = false;
		String key = PayCacheConfig.getClientConfigKey(storeCode);
		LocalCache localCache = LocalCache.getInstance();
		try {
			localCache.delete(key);
			result = true;
		} catch (Exception e) {
			log.error("", e);
		}
		return result;
	}

	public AppConfigBean getAppConfigBean(String appCode) {
		AppConfigBean bean = new AppConfigBean();
		String key = PayCacheConfig.getAppConfigKey(appCode);
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				bean = new AppConfigBusiness().getAppConfig(appCode);
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
			bean = (AppConfigBean) obj;
		}
		return bean;
	}

	public boolean removeAppConfigBean(String appCode) {
		boolean result = false;
		String key = PayCacheConfig.getAppConfigKey(appCode);
		LocalCache localCache = LocalCache.getInstance();
		try {
			localCache.delete(key);
			result = true;
		} catch (Exception e) {
			log.error("", e);
		}
		return result;
	}

	public HashMap<String, String> getCompanyConfigValueMap(String companyCode) {
		HashMap<String, String> configMap = new HashMap<String, String>();
		String key = PayCacheConfig.getCompanyConfigValueMapKey(companyCode);
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				obj = new PayConfigInfoBusiness().getCompanyConfigValueMap(companyCode);
				localCache.set(key, obj, Constants.CACHE_TIME_2_HOUR);
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
			configMap = (HashMap<String, String>) obj;
		}
		return configMap;
	}

	public boolean removeCompanyConfigValueMap(String companyCode) {
		boolean result = false;
		String key = PayCacheConfig.getCompanyConfigValueMapKey(companyCode);
		LocalCache localCache = LocalCache.getInstance();
		try {
			localCache.delete(key);
			result = true;
		} catch (Exception e) {
			log.error("", e);
		}
		return result;
	}

	public HashMap<String, String> getStoreConfigValueMap(String companyCode, String storeCode) {
		HashMap<String, String> configMap = new HashMap<String, String>();
		String key = PayCacheConfig.getStoreConfigValueMapKey(companyCode, storeCode);
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				obj = new PayConfigInfoBusiness().getStoreConfigValueMap(companyCode, storeCode);
				localCache.set(key, obj, Constants.CACHE_TIME_2_HOUR);
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
			configMap = (HashMap<String, String>) obj;
		}
		return configMap;
	}

	public boolean removeStoreConfigValueMap(String companyCode, String storeCode) {
		boolean result = false;
		String key = PayCacheConfig.getStoreConfigValueMapKey(companyCode, storeCode);
		LocalCache localCache = LocalCache.getInstance();
		try {
			localCache.delete(key);
			result = true;
		} catch (Exception e) {
			log.error("", e);
		}
		return result;
	}

	public HashMap<String, String> getChannelConfigValueMap(String companyCode, String channelCode) {
		HashMap<String, String> configMap = new HashMap<String, String>();
		String key = PayCacheConfig.getChannelConfigValueMapKey(companyCode, channelCode);
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				obj = new PayConfigInfoBusiness().getChannelConfigValueMap(companyCode, channelCode);
				localCache.set(key, obj, Constants.CACHE_TIME_2_HOUR);
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
			configMap = (HashMap<String, String>) obj;
		}
		return configMap;
	}

	public boolean removeChannelConfigValueMap(String companyCode, String channelCode) {
		boolean result = false;
		String key = PayCacheConfig.getChannelConfigValueMapKey(companyCode, channelCode);
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
