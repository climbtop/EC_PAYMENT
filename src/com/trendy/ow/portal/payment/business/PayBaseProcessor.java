package com.trendy.ow.portal.payment.business;

import java.security.PrivateKey;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.crypto.RSA;
import com.trendy.fw.common.transfer.HttpClientKit;
import com.trendy.fw.common.transfer.HttpClientResultBean;
import com.trendy.fw.common.util.JsonKit;
import com.trendy.fw.common.util.NumeralOperationKit;
import com.trendy.fw.common.util.StringKit;
import com.trendy.fw.common.web.HttpResponseKit;
import com.trendy.fw.common.web.ReturnMessageBean;
import com.trendy.fw.tools.criphertext.CiphertextKit;
import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.fw.tools.order.config.SyncStatusConfig;
import com.trendy.ow.portal.payment.bean.AppConfigBean;
import com.trendy.ow.portal.payment.bean.AppNotifyConfigBean;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayCompanyInfoBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.bean.PayResponseBean;
import com.trendy.ow.portal.payment.cache.PayChannelCache;
import com.trendy.ow.portal.payment.cache.PayCompanyCache;
import com.trendy.ow.portal.payment.cache.PayConfigCache;
import com.trendy.ow.portal.payment.cache.StoreInfoCache;
import com.trendy.ow.portal.payment.cache.SysApplicationInfoCache;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.config.PayErrorCode;

public abstract class PayBaseProcessor {
	private static Logger log = LoggerFactory.getLogger(PayBaseProcessor.class);

	// 通过payItem获得PayInfo
	protected PayInfoBean getPayInfo(PayItemBean itemBean) {
		int payId = itemBean.getPayId();
		PayInfoBusiness infoBusiness = new PayInfoBusiness();
		return infoBusiness.getPayInfoByKey(payId);
	}

	// 通过payInfo获得ClientConfig
	protected AppNotifyConfigBean getAppNotifyConfig(PayInfoBean infoBean) {
		String storeCode = getStoreCode(infoBean);
		PayConfigCache configCache = new PayConfigCache();
		return configCache.getAppNotifyConfig(storeCode);
	}

	// 通过payInfo获得storeCode
	protected String getStoreCode(PayInfoBean infoBean) {
		StoreInfoCache storeInfoCache = new StoreInfoCache();
		return StringKit.validStr(storeInfoCache.getStoreCodeByIdMap().get(infoBean.getStoreId()));
	}

	// 通过payInfo获得ClientConfig
	protected AppConfigBean getAppConfig(PayInfoBean infoBean) {
		SysApplicationInfoCache appCache = new SysApplicationInfoCache();
		String appCode = appCache.getSysApplicationInfoCodeAllMap().get(infoBean.getAppId());
		PayConfigCache configCache = new PayConfigCache();
		return configCache.getAppConfigBean(appCode);
	}

	// 插入支付明细表
	protected PayItemBean savePayItem(PayInfoBean payInfo, PayChannelInfoBean channel) {
		PayItemBusiness itemBusiness = new PayItemBusiness();
		PayItemBean payItem = itemBusiness.savePayItem(payInfo.getPayId(), channel.getChannelId(),
				channel.getCompanyId(), payInfo.getCurrency(), payInfo.getIpAddress(), PayConfig.PAYS_WAIT_PAY,
				Constants.STATUS_VALID);
		return payItem;
	}

	// 支付平台通知支付成功更新payItem
	protected int savePayResult(PayInfoBean infoBean, PayItemBean itemBean, String payStatus, Timestamp payTime,
			String payNumber, double payAmount) {
		itemBean.setPayTime(payTime);
		itemBean.setPayNumber(payNumber);
		itemBean.setPayAmount(payAmount);
		itemBean.setPayStatus(payStatus);
		if (payStatus.equals(PayConfig.PAYS_PAYED)) {
			double factAmount = NumeralOperationKit.add(infoBean.getFactAmount(), itemBean.getPayAmount());// 累加
			if (factAmount >= infoBean.getRequestAmount()) {
				infoBean.setPayStatus(payStatus);
			} else if (factAmount > 0) {
				infoBean.setPayStatus(PayConfig.PAYS_PAYED_PART);
			}
			infoBean.setFactAmount(factAmount);
		} else {
			infoBean.setPayStatus(payStatus);
		}
		return new PayItemBusiness().updatePayItemAndInfo(itemBean, infoBean);
	}

	// 回调信息格式
	protected Map<String, String> getClientReturnMap(boolean isNotify, PayInfoBean infoBean, PayItemBean itemBean,
			AppConfigBean appConfigBean) throws PortalServletException {
		Map<String, String> paramMap = new HashMap<String, String>();
		PayResponseBean responseBean = new PayResponseBean();
		SysApplicationInfoCache appCache = new SysApplicationInfoCache();
		responseBean.setAppCode(appCache.getSysApplicationInfoCodeAllMap().get(infoBean.getAppId()));
		responseBean.setInfoId(infoBean.getInfoId());
		responseBean.setReferType(infoBean.getReferType());
		responseBean.setUserId(infoBean.getUserId());
		responseBean.setStoreId(infoBean.getStoreId());

		PayCompanyCache companyCache = new PayCompanyCache();
		PayCompanyInfoBean company = companyCache.getPayCompanyInfo(itemBean.getCompanyId());
		responseBean.setCompanyCode(company.getCompanyCode());

		PayChannelCache channelCache = new PayChannelCache();
		PayChannelInfoBean channel = channelCache.getPayChannelInfo(itemBean.getChannelId());
		responseBean.setChannelCode(channel.getChannelCode());

		responseBean.setPayStatus(itemBean.getPayStatus());
		if (isNotify) {
			responseBean.setCurrency(itemBean.getCurrency());
			responseBean.setFactAmount(itemBean.getPayAmount());
			responseBean.setTimestamp(String.valueOf(itemBean.getPayTime().getTime()));
		}

		responseBean.setRandomValue(CiphertextKit.getRandomValue(8));
		PrivateKey key = RSA.getPrivateKey(appConfigBean.getRsaPrivateKey());
		String encodeData = CiphertextKit.encrypt(responseBean, key, appConfigBean.getMd5Key());
		String appCode = appCache.getSysApplicationInfoCodeAllMap().get(infoBean.getAppId());
		paramMap.put("data", encodeData);
		paramMap.put("appCode", appCode);
		return paramMap;
	}

	// 支付后跳转
	protected void callbackApplication(PayItemBean itemBean, HttpServletResponse response)
			throws PortalServletException {
		PayLogBusiness logBusiness = new PayLogBusiness();
		PayInfoBean infoBean = getPayInfo(itemBean);
		AppConfigBean appConfigBean = getAppConfig(infoBean);
		AppNotifyConfigBean notifyConfigBean = getAppNotifyConfig(infoBean);

		String callbackUrl = notifyConfigBean.getAppCallBackUrl();
		if (!StringKit.isValid(callbackUrl)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.SEND_CALLBACK_EXCEPTION, "The"
					+ notifyConfigBean.getStoreCode() + "_CallBackUrl config is empty");
		}
		Map<String, String> paramMap = getClientReturnMap(false, infoBean, itemBean, appConfigBean);
		logBusiness.savePayLog(itemBean.getPayId(), itemBean.getPayStatus(), "成功生成跳转到业务系统url:" + callbackUrl);
		HttpResponseKit.printForm(response, callbackUrl, paramMap);
	}

	// 支付后通知
	protected void notifyApplication(PayItemBean itemBean) throws PortalServletException {
		PayLogBusiness logBusiness = new PayLogBusiness();
		PayInfoBean infoBean = getPayInfo(itemBean);
		AppConfigBean appConfigBean = getAppConfig(infoBean);
		AppNotifyConfigBean appNotifyConfigBean = getAppNotifyConfig(infoBean);

		String notifyUrl = appNotifyConfigBean.getAppNotifyUrl();
		if (!StringKit.isValid(notifyUrl)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.SEND_NOTIFY_EXCEPTION, "The"
					+ appNotifyConfigBean.getStoreCode() + "_NotifyUrl config is empty");
		}

		try {
			infoBean.setSyncStatus(SyncStatusConfig.SYNCS_SENT);
			Map<String, String> paramMap = getClientReturnMap(true, infoBean, itemBean, appConfigBean);
			HttpClientKit httpClientKit = new HttpClientKit();
			HttpClientResultBean resultBean = httpClientKit.postContent(notifyUrl, paramMap, Constants.CODE_UNICODE);
			if (resultBean.getResult()) {
				if (StringKit.isValid(resultBean.getResultContent())) {
					ReturnMessageBean messageBean = new ReturnMessageBean();
					try {
						messageBean = JsonKit.toBean(resultBean.getResultContent(), ReturnMessageBean.class);
						if (messageBean.getCode() == 1) {
							infoBean.setSyncStatus(SyncStatusConfig.SYNCS_SUCCESS);
							log.info("已同步通知成功");
							logBusiness.savePayLog(itemBean.getPayId(), itemBean.getPayStatus(), "已同步通知成功");
						} else {
							infoBean.setSyncStatus(SyncStatusConfig.SYNCS_FAIL);
							log.info("已同步通知成功,但返回客户端处理失败，itemId:" + infoBean.getPayId() + "，返回："
									+ resultBean.getResultContent());
							logBusiness.savePayLog(itemBean.getPayId(), itemBean.getPayStatus(),
									"已同步通知成功,但返回客户端处理失败，返回：" + resultBean.getResultContent());
						}
					} catch (Exception e) {
						log.error("已同步通知成功,解析返回信息异常：" + JsonKit.toJson(resultBean), e);
						logBusiness.savePayLog(itemBean.getPayId(), itemBean.getPayStatus(),
								"已同步通知成功,解析返回信息异常：" + e.getMessage() + ",resultBean=" + JsonKit.toJson(resultBean));
					}
				} else {
					infoBean.setSyncStatus(SyncStatusConfig.SYNCS_FAIL);
					log.info("已同步通知成功,但返回信息为空，payId:" + infoBean.getPayId() + "。notifyUrl:" + notifyUrl + ",paramMap:"
							+ JsonKit.toJson(paramMap));
					logBusiness
							.savePayLog(itemBean.getPayId(), itemBean.getPayStatus(),
									"已同步通知成功,但返回信息为空，payId:" + infoBean.getPayId() + "。notifyUrl:" + notifyUrl
											+ ",paramMap:" + JsonKit.toJson(paramMap));
				}
			} else {
				log.info("已同步通知，请求失败,payId:" + infoBean.getPayId() + "。notifyUrl:" + notifyUrl);
				logBusiness.savePayLog(itemBean.getPayId(), itemBean.getPayStatus(),
						"已同步通知，请求失败,payId:" + infoBean.getPayId() + "。notifyUrl:" + notifyUrl);

			}
		} catch (Exception e) {
			log.error("notifyReceiver 通知业务系统支付信息失败", e);
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.SEND_NOTIFY_EXCEPTION,
					"notifyReceiver send to client exception:" + e.getMessage());
		} finally {
			new PayInfoBusiness().updatePayInfo(infoBean);
		}
	}

}
