package com.trendy.ow.portal.payment.weixin.business;

import java.util.HashMap;
import java.util.Map;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.trendy.fw.common.cache.LocalCache;
import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.transfer.HttpClientKit;
import com.trendy.fw.common.transfer.HttpClientResultBean;
import com.trendy.fw.common.util.BeanKit;
import com.trendy.fw.common.util.JsonKit;
import com.trendy.fw.common.util.StringKit;
import com.trendy.ow.portal.payment.business.PayConfigInfoBusiness;
import com.trendy.ow.portal.payment.config.PayCacheConfig;
import com.trendy.ow.portal.payment.weixin.bean.WeixinConfigBean;
import com.trendy.ow.portal.payment.weixin.cache.WeixinCache;
import com.trendy.ow.portal.payment.weixin.config.WeixinConfig;

public class WeixinConfigBusiness {
	public WeixinConfigBean getWeixinConfig(String storeCode) {
		WeixinConfigBean configBean = new WeixinConfigBean();
		configBean.setStoreCode(storeCode);
		PayConfigInfoBusiness business = new PayConfigInfoBusiness();

		HashMap<String, Object> configMap = new HashMap<String, Object>();
		for (String key : WeixinConfig.COMPANY_CONFIG_KEY_LIST) {
			String configCode = WeixinConfig.getCompanyConfigCode(key);
			configMap.put(StringKit.lowerFirstChar(key), business.getPayConfigValue(configCode));
		}
		for (String key : WeixinConfig.STORE_CONFIG_KEY_LIST) {
			String configCode = WeixinConfig.getStoreConfigCode(storeCode, key);
			configMap.put(StringKit.lowerFirstChar(key), business.getPayConfigValue(configCode));
		}
		configMap.put("storeCode", storeCode);

		try {
			configBean = BeanKit.map2Bean(configMap, WeixinConfigBean.class);
		} catch (Exception e) {
		}

		return configBean;

	}

	public WeixinConfigBean getWeixinConfig(String shopNumber, String secret,String storeCode) throws Exception {
		String key = PayCacheConfig.getShopWeixinConfigKey(shopNumber,storeCode);
		LocalCache localCache = LocalCache.getInstance();
		try {
			Object obj = localCache.get(key);
			if (obj != null) {
				return (WeixinConfigBean) obj;
			}
		} catch (NeedsRefreshException e) {}
		WeixinConfigBean configBean = new WeixinConfigBean();
		try {
			HttpClientKit kit = new HttpClientKit();
			Map<String, String> map = new HashMap<String, String>();
			map.put("shop_number", shopNumber);
			map.put("secret", secret);
			Map<String, String> param = new HashMap<String, String>();
			param.put("data", JsonKit.toJson(map));
			HttpClientResultBean requestBean = kit.postContent(WeixinConfig.getShopWeixinConfigUrl(), param);
			if (requestBean.getResult()) {
				@SuppressWarnings("unchecked")
				Map<String, String> resultMap = JsonKit.toBean(requestBean.getResultContent(), HashMap.class);
				String code = resultMap.get("code");
				if (code.equals("1")) {
					@SuppressWarnings("unchecked")
					Map<String, String> dataMap = JsonKit.toBean(resultMap.get("content"), HashMap.class);
					configBean.setAppId(dataMap.get("saccountId"));
					configBean.setMchId(dataMap.get("smerchantNumber"));
					configBean.setKey(dataMap.get("swechatKey"));
					WeixinCache cache = new WeixinCache();
					WeixinConfigBean config = cache.getWeixinConfig(storeCode);
					configBean.setBody(config.getBody());
					configBean.setStoreCode(config.getStoreCode());
					configBean.setAttach(config.getAttach());
					configBean.setPayApiUrl(config.getPayApiUrl());
					configBean.setDetail(config.getDetail());
					key = PayCacheConfig.getShopWeixinConfigKey(shopNumber,storeCode);
					localCache.set(key, configBean, Constants.CACHE_TIME_2_HOUR);
				} else {
					throw new Exception(requestBean.getResultContent());
				}
			}
		} catch (Exception e) {
			throw new Exception("getShopConfigFail:"+e.getMessage());
		}
		return configBean;

	}

	// public WeixinConfigBean getWeixinConfig(String storeCode) {
	// WeixinConfigBean configBean = new WeixinConfigBean();
	//
	// PayConfigCache cache=new PayConfigCache();
	// HashMap<String, Object> configMap = new HashMap<String, Object>();
	//
	// configMap.putAll(cache.getCompanyConfigValueMap(WeixinConfig.COMPANY_CODE));
	// configMap.putAll(cache.getStoreConfigValueMap(WeixinConfig.COMPANY_CODE,
	// storeCode));
	// configMap.put("storeCode", storeCode);
	//
	// try {
	// configBean = BeanKit.map2Bean(configMap, WeixinConfigBean.class);
	// } catch (Exception e) {
	// }
	//
	// return configBean;
	//
	// }
}
