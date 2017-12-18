package com.trendy.ow.portal.payment.factory;

import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.ow.portal.payment.alipay.business.AliPayAppPayProcessor;
import com.trendy.ow.portal.payment.alipay.business.AliPayWapPayProcessor;
import com.trendy.ow.portal.payment.alipay.business.AliPayWebPayProcessor;
import com.trendy.ow.portal.payment.alipay.config.AliPayAppConfig;
import com.trendy.ow.portal.payment.alipay.config.AliPayWapConfig;
import com.trendy.ow.portal.payment.alipay.config.AliPayWebConfig;
import com.trendy.ow.portal.payment.business.PayProcessor;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.config.PayErrorCode;
import com.trendy.ow.portal.payment.tenpay.business.TenPayPayProcessor;
import com.trendy.ow.portal.payment.tenpay.config.TenPayConfig;
import com.trendy.ow.portal.payment.weixin.business.WeixinJsApiPayProcessor;
import com.trendy.ow.portal.payment.weixin.business.WeixinMicropayProcessor;
import com.trendy.ow.portal.payment.weixin.business.WeixinPublicNumberProcessor;
import com.trendy.ow.portal.payment.weixin.business.WeixinScanCodeProcessor;
import com.trendy.ow.portal.payment.weixin.config.WeixinConfig;
import com.trendy.ow.portal.payment.zero.business.ZeroPayProcessor;
import com.trendy.ow.portal.payment.zero.config.ZeroPayConfig;

public class PayProcessorFactory {

	public PayProcessor getPayProcessor(String companyCode, String channelCode) throws PortalServletException {
		if (companyCode.equals(AliPayWapConfig.COMPANY_CODE)) {
			return new AliPayWapPayProcessor();
		} else if (companyCode.equals(TenPayConfig.COMPANY_CODE)) {
			return new TenPayPayProcessor();
		} else if (companyCode.equals(AliPayWebConfig.COMPANY_CODE)) {
			return new AliPayWebPayProcessor();
		} else if (companyCode.equals(WeixinConfig.COMPANY_CODE)
				&& channelCode.equals(WeixinConfig.CHANNEL_CODE_PUBLICN_NUMBER)) {
			return new WeixinPublicNumberProcessor();
		} else if (companyCode.equals(WeixinConfig.COMPANY_CODE)
				&& channelCode.equals(WeixinConfig.CHANNEL_CODE_JS_API)) {
			return new WeixinJsApiPayProcessor();
		} else if (companyCode.equals(WeixinConfig.COMPANY_CODE)
				&& channelCode.equals(WeixinConfig.CHANNEL_CODE_MICROPAY)) {
			return new WeixinMicropayProcessor();
		} else if (companyCode.equals(WeixinConfig.COMPANY_CODE)) {
			return new WeixinScanCodeProcessor();
		} else if (companyCode.equals(ZeroPayConfig.COMPANY_CODE)
				|| channelCode.equals(ZeroPayConfig.CHANNEL_CODE_DEFAULT)) {
			return new ZeroPayProcessor();
		}else if (companyCode.equals(AliPayAppConfig.COMPANY_CODE)
				|| channelCode.equals(AliPayAppConfig.CHANNEL_CODE_WALLET)) {
			return new AliPayAppPayProcessor();
		} else {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REDIRECT_EXCEPTION,
					"hasNoProcessorConfig:companyCode=" + companyCode + ",channelCode="
							+ channelCode);
		}
	}

}
