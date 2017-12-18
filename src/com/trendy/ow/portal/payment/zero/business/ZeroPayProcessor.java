package com.trendy.ow.portal.payment.zero.business;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.bean.PayGatewayRequestBean;
import com.trendy.ow.portal.payment.business.PayItemBusiness;
import com.trendy.ow.portal.payment.business.PayLogBusiness;
import com.trendy.ow.portal.payment.business.PayProcessor;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.config.PayErrorCode;

public class ZeroPayProcessor extends PayProcessor {
	private static Logger log = LoggerFactory.getLogger(ZeroPayProcessor.class);

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response, PayInfoBean payInfo,
			PayItemBean payItem) throws PortalServletException {
		double requestAmount=payInfo.getRequestAmount();
		if (Double.compare(requestAmount, 0)!=0) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION,
					"invaildRequestAmount");
		}
		PayLogBusiness logBusiness = new PayLogBusiness();
		int result = savePayResult(payInfo, payItem);
		if(result>0){
			ZeroPayReceiver receiver=new ZeroPayReceiver();
			logBusiness.savePayLog(payInfo.getPayId(), PayConfig.PAYS_PAYED, "保存零支付记录成功");
			// 业务系统通知
			receiver.processNotify(payItem);
			//回调
			receiver.processCallback(payItem, response);
		}else {
			logBusiness.savePayLog(payInfo.getPayId(), PayConfig.PAYS_PAYED, "保存零支付记录失败");
			log.error("ZeroPayProcessor.processRequest:savePayResultFail");
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION,
					"savePayResultFail");
		}

	}
	
	// 支付平台通知支付成功更新payItem
	protected int savePayResult(PayInfoBean infoBean, PayItemBean itemBean) {
		// 更新支付信息payItem payInfo
		Long now=new Date().getTime();
		Random random=new Random();
		Timestamp payTime=new Timestamp(now); 
		itemBean.setPayTime(payTime);
		itemBean.setPayNumber("000"+now+random.nextInt(9));
		itemBean.setPayAmount(infoBean.getRequestAmount());
		itemBean.setPayStatus(PayConfig.PAYS_PAYED);
		infoBean.setPayStatus(PayConfig.PAYS_PAYED);
		return new PayItemBusiness().updatePayItemAndInfo(itemBean, infoBean);
	}

	@Override
	public Map<String, String> processPayGatewayRequest(PayInfoBean payInfo, PayChannelInfoBean channel,
			String basePath,PayGatewayRequestBean requestBean) throws Exception {
		log.info("notSupportThisChannel zeropay");
		throw new Exception("notSupportThisChannel");
	}


}
