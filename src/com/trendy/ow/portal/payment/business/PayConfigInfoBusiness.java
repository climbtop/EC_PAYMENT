package com.trendy.ow.portal.payment.business;

import java.util.HashMap;

import com.trendy.fw.common.util.StringKit;
import com.trendy.ow.portal.payment.alipay.config.AliPayAppConfig;
import com.trendy.ow.portal.payment.alipay.config.AliPayWapConfig;
import com.trendy.ow.portal.payment.alipay.config.AliPayWebConfig;
import com.trendy.ow.portal.payment.bean.PayConfigInfoBean;
import com.trendy.ow.portal.payment.dao.PayConfigInfoDao;
import com.trendy.ow.portal.payment.tenpay.config.TenPayConfig;
import com.trendy.ow.portal.payment.weixin.config.WeixinConfig;

public class PayConfigInfoBusiness {
	public PayConfigInfoBean getPayConfigInfoByCode(String configCode) {
		PayConfigInfoDao dao = new PayConfigInfoDao();
		return dao.getPayConfigInfoByCode(configCode);
	}

	public String getPayConfigValue(String configCode) {
		PayConfigInfoBean bean = getPayConfigInfoByCode(configCode);
		if (bean != null) {
			return bean.getConfigValue();
		}
		return "";
	}
	
	public HashMap<String, String> getStoreConfigValueMap(String companyCode,String storeCode) {
		HashMap<String, String> configMap = new HashMap<String, String>();
		if (AliPayWapConfig.COMPANY_CODE.equals(companyCode)) {
			for (String key : AliPayWapConfig.STORE_CONFIG_KEY_LIST) {
				String configCode = AliPayWapConfig.getStoreConfigCode(storeCode, key);
				configMap.put(StringKit.lowerFirstChar(key), getPayConfigValue(configCode));
			}
		}else if (AliPayWebConfig.COMPANY_CODE.equals(companyCode)) {
			for (String key : AliPayWebConfig.STORE_CONFIG_KEY_LIST) {
				String configCode = AliPayWebConfig.getStoreConfigCode(storeCode, key);
				configMap.put(StringKit.lowerFirstChar(key), getPayConfigValue(configCode));
			}
		}else if (TenPayConfig.COMPANY_CODE.equals(companyCode)) {
			for (String key : TenPayConfig.STORE_CONFIG_KEY_LIST) {
				String configCode = TenPayConfig.getStoreConfigCode(storeCode, key);
				configMap.put(StringKit.lowerFirstChar(key), getPayConfigValue(configCode));
			}
			
		}else if (WeixinConfig.COMPANY_CODE.equals(companyCode)) {
			for (String key : WeixinConfig.STORE_CONFIG_KEY_LIST) {
				String configCode = WeixinConfig.getStoreConfigCode(storeCode, key);
				configMap.put(StringKit.lowerFirstChar(key), getPayConfigValue(configCode));
			}
			
		}else if (AliPayAppConfig.COMPANY_CODE.equals(companyCode)) {
			for (String key : AliPayAppConfig.STORE_CONFIG_KEY_LIST) {
				String configCode = AliPayAppConfig.getStoreConfigCode(storeCode,key);
				configMap.put(StringKit.lowerFirstChar(key), getPayConfigValue(configCode));
			}
		}
		return configMap;
	}
	
	public HashMap<String, String> getCompanyConfigValueMap(String companyCode) {
		HashMap<String, String> configMap = new HashMap<String, String>();
		if (AliPayWapConfig.COMPANY_CODE.equals(companyCode)) {
			for (String key : AliPayWapConfig.COMPANY_CONFIG_KEY_LIST) {
				String configCode = AliPayWapConfig.getCompanyConfigCode(key);
				configMap.put(StringKit.lowerFirstChar(key), getPayConfigValue(configCode));
			}
		}else if (AliPayWebConfig.COMPANY_CODE.equals(companyCode)) {
			for (String key : AliPayWebConfig.COMPANY_CONFIG_KEY_LIST) {
				String configCode = AliPayWebConfig.getCompanyConfigCode(key);
				configMap.put(StringKit.lowerFirstChar(key), getPayConfigValue(configCode));
			}
		}else if (TenPayConfig.COMPANY_CODE.equals(companyCode)) {
			for (String key : TenPayConfig.COMPANY_CONFIG_KEY_LIST) {
				String configCode = TenPayConfig.getCompanyConfigCode(key);
				configMap.put(StringKit.lowerFirstChar(key), getPayConfigValue(configCode));
			}
		}else if (WeixinConfig.COMPANY_CODE.equals(companyCode)) {
			for (String key : WeixinConfig.COMPANY_CONFIG_KEY_LIST) {
				String configCode = WeixinConfig.getCompanyConfigCode(key);
				configMap.put(StringKit.lowerFirstChar(key), getPayConfigValue(configCode));
			}
		}else if (AliPayAppConfig.COMPANY_CODE.equals(companyCode)) {
			for (String key : AliPayAppConfig.COMPANY_CONFIG_KEY_LIST) {
				String configCode = AliPayAppConfig.getCompanyConfigCode(key);
				configMap.put(StringKit.lowerFirstChar(key), getPayConfigValue(configCode));
			}
		}
		return configMap;
	}
	
	public HashMap<String, String>  getChannelConfigValueMap(String companyCode,String channelCode) {
		HashMap<String, String> configMap = new HashMap<String, String>();
		if (AliPayWapConfig.COMPANY_CODE.equals(companyCode)) {
			//。。。
		}else if (AliPayWebConfig.COMPANY_CODE.equals(companyCode)) {
			for (String key : AliPayWebConfig.CHANNEL_CONFIG_KEY_LIST) {
				String configCode = AliPayWebConfig.getChannelConfigCode(channelCode, key);
				configMap.put(StringKit.lowerFirstChar(key), getPayConfigValue(configCode));
			}
		}else if (TenPayConfig.COMPANY_CODE.equals(companyCode)) {
			for (String key : TenPayConfig.CHANNEL_CONFIG_KEY_LIST) {
				String configCode = TenPayConfig.getChannelConfigCode(channelCode, key);
				configMap.put(StringKit.lowerFirstChar(key), getPayConfigValue(configCode));
			}
		}else if (WeixinConfig.COMPANY_CODE.equals(companyCode)) {
			//...
		}
		return configMap;
	}
}