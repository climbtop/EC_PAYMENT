package com.trendy.ow.portal.payment.weixin.business;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.trendy.fw.common.util.StringKit;
import com.trendy.fw.tools.exception.PortalServletException;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.bean.PayGatewayRequestBean;
import com.trendy.ow.portal.payment.business.PayLogBusiness;
import com.trendy.ow.portal.payment.business.PayProcessor;
import com.trendy.ow.portal.payment.business.PayUtil;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.config.PayErrorCode;
import com.trendy.ow.portal.payment.weixin.bean.WeixinConfigBean;
import com.trendy.ow.portal.payment.weixin.bean.WeixinPreOrderResponseBean;
import com.trendy.ow.portal.payment.weixin.cache.WeixinCache;
import com.trendy.ow.portal.payment.weixin.config.WeixinConfig;

public class WeixinScanCodeProcessor extends PayProcessor {
	private static Logger log = LoggerFactory.getLogger(WeixinScanCodeProcessor.class);
	final String MUST_VALUE_PARAMS = "appid,mch_id,nonce_str,body,out_trade_no,total_fee,spbill_create_ip,notify_url,trade_type,product_id";
	final String TRADE_TYPE = "NATIVE";

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response, PayInfoBean payInfo,
			PayItemBean payItem) throws PortalServletException {

		// 获取支付链接
		String basePath = PayUtil.getBasePath(request);
		WeixinPreOrderResponseBean responseBean = getWeixinPreOrderResponse(basePath, payInfo, payItem);
		try {
			response.sendRedirect("/weixin/WeixinScanCodePay.do?codeUrl=" + responseBean.getCodeUrl() + "&itemId="
					+ payItem.getPayItemId());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REDIRECT_EXCEPTION, e.getMessage());
		}

	}

	public BufferedImage createScanCodeImage(String codeUrl, int width, int height) throws PortalServletException {
		MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		try {
			BitMatrix bitMatrix = multiFormatWriter.encode(codeUrl, BarcodeFormat.QR_CODE,
					WeixinConfig.SCAN_CODE_IMG_WIDTH, WeixinConfig.SCAN_CODE_IMG_HEIGHT, hints);
			return fileToBufferedImage(bitMatrix);
		} catch (WriterException e) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION,
					"createScanCodeImage exception:" + e.getMessage());
		}
	}

	public WeixinPreOrderResponseBean getWeixinPreOrderResponse(String basePath, PayInfoBean payInfo,
			PayItemBean payItem) throws PortalServletException {
		String storeCode = getStoreCode(payInfo);
		WeixinConfigBean configBean = new WeixinCache().getWeixinConfig(storeCode);
		return getWeixinPreOrderResponse(basePath, payInfo, payItem, configBean);
	}

	public WeixinPreOrderResponseBean getWeixinPreOrderResponse(String basePath, PayInfoBean payInfo,
			PayItemBean payItem, WeixinConfigBean configBean) throws PortalServletException {
		PayLogBusiness logBusiness = new PayLogBusiness();
		WeixinPayProcessor processor = new WeixinPayProcessor();
		String notifyUrl = basePath + WeixinConfig.WX_NOTIFY_URL;
		String productId = String.valueOf(payInfo.getInfoId());
		WeixinPreOrderResponseBean responseBean = processor.preNativeOrder(configBean, TRADE_TYPE, notifyUrl,
				productId, MUST_VALUE_PARAMS, payInfo, payItem);
		if (responseBean.getReturnCode().equalsIgnoreCase(PayConfig.FAIL)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION,
					responseBean.getReturnMsg());
		}
		if (responseBean.getResultCode().equalsIgnoreCase(PayConfig.FAIL)) {
			throw new PortalServletException(PayConfig.APP_ID, PayErrorCode.PAY_REQUEST_EXCEPTION,
					responseBean.getErrCodeDes());
		}
		logBusiness.savePayLog(payInfo.getPayId(), PayConfig.PAYS_WAIT_PAY,
				"增加支付明细记录,生成微信支付CodeUrl:" + responseBean.getCodeUrl());
		return responseBean;
	}

	private BufferedImage fileToBufferedImage(BitMatrix bm) {
		BufferedImage image = null;
		try {
			int w = bm.getWidth(), h = bm.getHeight();
			image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					image.setRGB(x, y, bm.get(x, y) ? 0xFF000000 : 0xFFCCDDEE);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}

	@Override
	public Map<String, String> processPayGatewayRequest(PayInfoBean payInfo, PayChannelInfoBean channel, String basePath,
			PayGatewayRequestBean requestBean) throws Exception {
		Map<String, String> context = new HashMap<String, String>();
		try {
			PayItemBean payItem = savePayItem(payInfo, channel);
			if (payItem.getPayItemId() <= 0) {
				PayLogBusiness logBusiness = new PayLogBusiness();
				logBusiness.savePayLog(payInfo.getPayId(), PayConfig.PAYS_WAIT_PAY, "增加支付明细记录失败");
				log.info("save payItem record fail。");
				throw new Exception("InternalError");
			}
			if (requestBean.getRequestType() == PayConfig.REQUEST_TYPE_ZERO) {
				context.put("tradeNo", String.valueOf(payItem.getPayItemId()));
				context.put("notifyUrl", basePath + WeixinConfig.WX_NOTIFY_URL);
			} else if (requestBean.getRequestType() == PayConfig.REQUEST_TYPE_ONE) {
				WeixinCache cache = new WeixinCache();
				String storeCode = getStoreCode(payInfo);
				WeixinConfigBean config = cache.getWeixinConfig(storeCode);
				String shopNumber = requestBean.getShopNumber();
				String secret = WeixinConfig.getShopWeixinConfigSecret();
				if (StringKit.isValid(shopNumber)) {
					WeixinConfigBusiness configBusiness = new WeixinConfigBusiness();
					config = configBusiness.getWeixinConfig(shopNumber, secret, storeCode);
				}
				WeixinPreOrderResponseBean resposeBean = getWeixinPreOrderResponse(basePath, payInfo, payItem,
						config);
				context.put("codeUrl", resposeBean.getCodeUrl());
				context.put("prepayId", resposeBean.getPrepayId());
				context.put("scanUrl", basePath + WeixinConfig.WX_CREATE_SCAN_CODE_URL);
			} else {
				throw new Exception("nowThisPayChannelNotSupportThisRequestType");
			}
		} catch (Exception e) {
			if (e instanceof PortalServletException) {
				log.info("processPayOrderFail:" + e.getMessage());
				e = new Exception(e.getMessage());
			}
			throw e;
		}
		return context;
	}
}
