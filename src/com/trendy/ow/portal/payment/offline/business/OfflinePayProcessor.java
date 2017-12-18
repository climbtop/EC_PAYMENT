package com.trendy.ow.portal.payment.offline.business;

import java.security.PrivateKey;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
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
import com.trendy.fw.tools.order.config.SyncStatusConfig;
import com.trendy.ow.portal.payment.bean.AppConfigBean;
import com.trendy.ow.portal.payment.bean.AppNotifyConfigBean;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayCompanyInfoBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.bean.PayItemRequestBean;
import com.trendy.ow.portal.payment.bean.PayResponseBean;
import com.trendy.ow.portal.payment.bean.PayedAddRequestBean;
import com.trendy.ow.portal.payment.business.PayBaseProcessor;
import com.trendy.ow.portal.payment.business.PayInfoBusiness;
import com.trendy.ow.portal.payment.business.PayItemBusiness;
import com.trendy.ow.portal.payment.business.PayLogBusiness;
import com.trendy.ow.portal.payment.cache.PayChannelCache;
import com.trendy.ow.portal.payment.cache.PayCompanyCache;
import com.trendy.ow.portal.payment.cache.SysApplicationInfoCache;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.offline.config.OfflinePayConfig;

public class OfflinePayProcessor extends PayBaseProcessor {
	private static Logger log = LoggerFactory.getLogger(OfflinePayProcessor.class);

	public void processRequest(HttpServletRequest request, HttpServletResponse response, PayInfoBean payInfo,
			PayedAddRequestBean requestBean) throws Exception {
		PayCompanyCache companyCache = new PayCompanyCache();
		int companyId = companyCache.getPayCompanyInfo(OfflinePayConfig.COMPANY_CODE).getCompanyId();
		Timestamp payTime = new Timestamp(new Date().getTime());

		List<PayItemBean> payItemBeans = parsePayItemRequest(requestBean.getItems(), companyId, payInfo.getIpAddress(),
				payTime, payInfo.getPayId());
		saveItems(payItemBeans, payInfo);

		printSuccess(request, response);
	}

	public void processNotify(final PayInfoBean infoBean) {
		if (infoBean.getPayStatus().equals(PayConfig.PAYS_PAYED)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					notifyApplication(infoBean);
					
				}
			}).start();
		}
	}
	
	private void printSuccess(HttpServletRequest request, HttpServletResponse response){
		ReturnMessageBean resultBean = new ReturnMessageBean();
		resultBean.setCode(Constants.STATUS_VALID);
		HttpResponseKit.printJson(request, response, resultBean, "callback");
	}
	
	
	private List<PayItemBean> parsePayItemRequest(List<PayItemRequestBean> items, int companyId, String ipAddress,
			Timestamp payTime, int payId) throws Exception {
		PayChannelCache channelCache = new PayChannelCache();

		List<PayItemBean> list = new ArrayList<PayItemBean>();
		for (PayItemRequestBean item : items) {
			String channelCode = item.getChannelCode();
			String currency = item.getCurrency();
			String payNumber = item.getPayNumber();
			double payAmount = item.getPayAmount();
			if (!StringKit.isValid(channelCode) || !StringKit.isValid(currency) || payAmount < 0) {
				log.info("OfflinePayProcessor processRequest- Missing parameters:channelCode||currency is empty or payAmount<=0");
				throw new Exception("Missing parameters");
			}
			PayItemBean itemBean = new PayItemBean();
			itemBean.setPayId(payId);
			itemBean.setCompanyId(companyId);
			itemBean.setPayNumber(StringKit.validStr(payNumber));
			itemBean.setPayTime(payTime);
			itemBean.setPayStatus(PayConfig.PAYS_PAYED);
			itemBean.setStatus(Constants.STATUS_VALID);
			itemBean.setCreateTime(payTime);
			itemBean.setChannelId(channelCache.getPayChannelInfo(channelCode).getChannelId());
			itemBean.setCurrency(currency);
			itemBean.setIpAddress(ipAddress);
			itemBean.setPayAmount(payAmount);
			list.add(itemBean);
		}
		return list;
	}

	private void saveItems(List<PayItemBean> payItemBeans, PayInfoBean payInfo) throws Exception {
		try {
			
			PayItemBusiness itemBusiness = new PayItemBusiness();
			PayInfoBusiness infoBusiness = new PayInfoBusiness();
			double totalFee=payInfo.getFactAmount();
			for (PayItemBean itemBean : payItemBeans) {
				String payNumber = itemBean.getPayNumber();
				PayItemBean payItem = itemBusiness.findPayItem(payInfo.getPayId(), payNumber);
				if (payItem != null) {
					totalFee=NumeralOperationKit.subtract(totalFee, payItem.getPayAmount());
					payItem.setChannelId(itemBean.getChannelId());
					payItem.setCurrency(itemBean.getCurrency());
					payItem.setIpAddress(itemBean.getIpAddress());
					payItem.setPayTime(itemBean.getPayTime());
					payItem.setPayAmount(itemBean.getPayAmount());
					itemBusiness.updatePayItem(payItem);
				} else {
					itemBusiness.addPayItem(itemBean);
				}
				totalFee = NumeralOperationKit.add(totalFee,itemBean.getPayAmount());
			}
			payInfo.setFactAmount(totalFee);
			if(totalFee>=payInfo.getRequestAmount()){
				payInfo.setPayStatus(PayConfig.PAYS_PAYED);
			}else {
				payInfo.setPayStatus(PayConfig.PAYS_PAYED_PART);
			}
			infoBusiness.updatePayInfo(payInfo);
		} catch (Exception e) {
			log.info("save record fail-exception:" + e.getMessage());
			throw new Exception("save record fail");
		}
	}

	private void notifyApplication(PayInfoBean infoBean) {
		PayLogBusiness logBusiness = new PayLogBusiness();
		AppConfigBean appConfigBean = getAppConfig(infoBean);
		AppNotifyConfigBean appNotifyConfigBean = getAppNotifyConfig(infoBean);
		SysApplicationInfoCache appCache=new SysApplicationInfoCache();
		
		
		try {
			String notifyUrl = appNotifyConfigBean.getAppNotifyUrl();
			if (!StringKit.isValid(notifyUrl)) {
				throw new Exception("The" + appNotifyConfigBean.getStoreCode() + "_ClientNotifyUrl config is empty");
			}
			infoBean.setSyncStatus(SyncStatusConfig.SYNCS_SENT);
			String encodeData = getReturnEncodeData(infoBean, appConfigBean);
			String appCode=appCache.getSysApplicationInfoCodeAllMap().get(infoBean.getAppId());
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("data", encodeData);
			paramMap.put("appCode", appCode);
			
			HttpClientKit httpClientKit = new HttpClientKit();
			HttpClientResultBean resultBean = httpClientKit.postContent(notifyUrl, paramMap, Constants.CODE_UNICODE);
			ReturnMessageBean messageBean= new ReturnMessageBean();
			try {
				messageBean=JsonKit.toBean(resultBean.getResultContent(), ReturnMessageBean.class);
			} catch (Exception e) {
				log.error("notifyReceiver -HttpClientResultBean:"+JsonKit.toJson(resultBean),e);
			}
			if (messageBean.getCode()==1) {
				infoBean.setSyncStatus(SyncStatusConfig.SYNCS_SUCCESS);
			} else {
				infoBean.setSyncStatus(SyncStatusConfig.SYNCS_FAIL);
			}
			log.info("notifyReceiver 业务系统payId:" + infoBean.getPayId()+ "，通知成功，返回："
					+ resultBean.getResultContent());
			logBusiness.savePayLog(infoBean.getPayId(), infoBean.getPayStatus(), "notifyReceiver 业务系统storeId:"
					+ infoBean.getStoreId() + "，通知成功，返回：" + resultBean.getResultContent());
		} catch (Exception e) {
			log.error("notifyReceiver 通知业务系统支付信息失败", e);
			logBusiness.savePayLog(infoBean.getPayId(), infoBean.getPayStatus(),
					"notifyReceiver 通知业务系统支付信息失败:" + e.getMessage());
		} finally {
			new PayInfoBusiness().updatePayInfo(infoBean);
		}
	}

	protected String getReturnEncodeData(PayInfoBean infoBean, AppConfigBean appConfigBean) {
		PayResponseBean responseBean = new PayResponseBean();
		SysApplicationInfoCache appCache = new SysApplicationInfoCache();
		responseBean.setAppCode(appCache.getSysApplicationInfoCodeAllMap().get(infoBean.getAppId()));
		responseBean.setInfoId(infoBean.getInfoId());
		responseBean.setReferType(infoBean.getReferType());
		responseBean.setUserId(infoBean.getUserId());
		responseBean.setStoreId(infoBean.getStoreId());

		if (infoBean.getCompanyId() > 0) {
			PayCompanyCache companyCache = new PayCompanyCache();
			PayCompanyInfoBean company = companyCache.getPayCompanyInfo(infoBean.getCompanyId());
			responseBean.setCompanyCode(company.getCompanyCode());
		}

		if (infoBean.getChannelId() > 0) {
			PayChannelCache channelCache = new PayChannelCache();
			PayChannelInfoBean channel = channelCache.getPayChannelInfo(infoBean.getChannelId());
			responseBean.setChannelCode(channel.getChannelCode());
		}
		responseBean.setPayStatus(infoBean.getPayStatus());
		responseBean.setCurrency(infoBean.getCurrency());
		responseBean.setFactAmount(infoBean.getFactAmount());
		responseBean.setTimestamp(String.valueOf(new Date().getTime()));
		responseBean.setRandomValue(CiphertextKit.getRandomValue(8));
		PrivateKey key = RSA.getPrivateKey(appConfigBean.getRsaPrivateKey());
		String encodeData = CiphertextKit.encrypt(responseBean, key, appConfigBean.getMd5Key());
		return encodeData;
	}

}
