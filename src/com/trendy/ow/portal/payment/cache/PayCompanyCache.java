package com.trendy.ow.portal.payment.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.trendy.fw.common.cache.LocalCache;
import com.trendy.fw.common.config.Constants;
import com.trendy.ow.portal.payment.bean.PayCompanyInfoBean;
import com.trendy.ow.portal.payment.business.PayCompanyInfoBusiness;
import com.trendy.ow.portal.payment.config.PayCacheConfig;

public class PayCompanyCache {
	private static Logger log = LoggerFactory.getLogger(PayCompanyInfoBean.class);

	public PayCompanyInfoBean getPayCompanyInfo(String companyCode) {
		PayCompanyInfoBean bean=new PayCompanyInfoBean();
		String key = PayCacheConfig.getPayCompanyInfoKey(companyCode);
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				obj = new PayCompanyInfoBusiness().getPayCompanyInfoByCode(companyCode);
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
			bean = (PayCompanyInfoBean) obj;
		}
		return bean;
	}

	public boolean removePayCompanyInfo(String companyCode) {
		boolean result = false;
		String key = PayCacheConfig.getPayCompanyInfoKey(companyCode);
		LocalCache localCache = LocalCache.getInstance();
		try {
			localCache.delete(key);
			result = true;
		} catch (Exception e) {
			log.error("", e);
		}
		return result;
	}

	public PayCompanyInfoBean getPayCompanyInfo(int companyId) {
		PayCompanyInfoBean bean=new PayCompanyInfoBean();
		String key = PayCacheConfig.getPayCompanyInfoKey(companyId);
		Object obj = null;
		LocalCache localCache = LocalCache.getInstance();
		try {
			obj = localCache.get(key);
		} catch (NeedsRefreshException nre) {
			boolean update = false;
			try {
				obj = new PayCompanyInfoBusiness().getCompanyByKey(companyId);
				if(obj!=null){
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
			bean = (PayCompanyInfoBean) obj;
		}
		return bean;
	}

	public boolean removePayCompanyInfo(int companyId) {
		boolean result = false;
		String key = PayCacheConfig.getPayCompanyInfoKey(companyId);
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
