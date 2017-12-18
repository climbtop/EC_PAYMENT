package test.com.trendy.ec.pay.action;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.dom4j.DocumentException;

import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.crypto.MD5;
import com.trendy.fw.common.transfer.HttpClientKit;
import com.trendy.fw.common.transfer.HttpClientResultBean;
import com.trendy.fw.common.util.FileKit;
import com.trendy.fw.common.util.JsonKit;
import com.trendy.fw.common.util.ListKit;
import com.trendy.fw.common.util.StringKit;
import com.trendy.fw.tools.criphertext.CiphertextKit;
import com.trendy.fw.tools.order.config.SyncStatusConfig;
import com.trendy.ow.portal.payment.business.PayUtil;
import com.trendy.ow.portal.payment.config.PayConfig;
import com.trendy.ow.portal.payment.weixin.bean.WeixinPreOrderRequestBean;
import com.trendy.ow.portal.payment.weixin.bean.WeixinConfigBean;
import com.trendy.ow.portal.payment.weixin.bean.WeixinPreOrderResponseBean;

public class TestWeixinPay {
	public static void main(String[] args) throws Exception {
		
		// jsAPI
//		String mustValueParams = "appid,mch_id,nonce_str,body,out_trade_no,total_fee,spbill_create_ip,notify_url,trade_type,product_id";
//		WeixinConfigBean configBean = new WeixinConfigBean();
//		configBean.setAppId("wxd1a369ecf574d300");
//		configBean.setKey("6f59f89ea8fd49d787097f1bba2b069d");
//		configBean.setMchId("1244431302");
//		configBean.setBody("支付测试");
////		configBean.setAttach("支付测试");
//		configBean.setPayApiUrl("https://api.mch.weixin.qq.com/pay/unifiedorder");
//		TestWeixinPay testWeixinPay=new TestWeixinPay();
//		WeixinPreOrderRequestBean requestBean=testWeixinPay.getWeixinPreOrderRequestBean(configBean, "NATIVE", "http://localhost:9010/pay/weixin/WeixinNotifyReceiver.do", "127.0.0.1", "1", "432", "219");
//		TreeMap<String, String> paramTreeMap=testWeixinPay.getPreOrderParamTreeMap(requestBean, "6f59f89ea8fd49d787097f1bba2b069d", mustValueParams);
//		WeixinPreOrderResponseBean responseBean=testWeixinPay.preOrder(configBean, paramTreeMap);
//		System.out.println(JsonKit.toJson(responseBean));
	}
	
	public void testPublicNumberPay() throws Exception{
		// jsAPI
		String mustValueParams = "appid,mch_id,nonce_str,body,out_trade_no,total_fee,spbill_create_ip,notify_url,trade_type,openid";
		WeixinConfigBean configBean = new WeixinConfigBean();
		configBean.setAppId("wxd1a369ecf574d300");
		configBean.setKey("6f59f89ea8fd49d787097f1bba2b069d");
		configBean.setMchId("1244431302");
		configBean.setBody("JSAPI支付测试");
		configBean.setAttach("支付测试");
		configBean.setPayApiUrl("https://api.mch.weixin.qq.com/pay/unifiedorder");
		TestWeixinPay testWeixinPay=new TestWeixinPay();
		WeixinPreOrderRequestBean requestBean=testWeixinPay.getWeixinPreOrderRequestBean(configBean, "JSAPI", "http://www.baidu.com/", "14.23.150.211", "888", "oYx-XjowwgoRlU-7MPv1F84RMAVo", "1415659990");
		TreeMap<String, String> paramTreeMap=testWeixinPay.getPreOrderParamTreeMap(requestBean, "6f59f89ea8fd49d787097f1bba2b069d", mustValueParams);
		WeixinPreOrderResponseBean responseBean=testWeixinPay.preOrder(configBean, paramTreeMap);
		System.out.println(JsonKit.toJson(responseBean));
	}

	public WeixinPreOrderRequestBean getWeixinPreOrderRequestBean(WeixinConfigBean configBean, String tradeType,
			String notifyUrl, String spbillCreateIp, String totalFee, String openId, String outTradeNo) {
		WeixinPreOrderRequestBean preOrderBean = new WeixinPreOrderRequestBean();
		preOrderBean.setAppId(configBean.getAppId());
		preOrderBean.setAttach(configBean.getAttach());
		preOrderBean.setBody(configBean.getBody());
		preOrderBean.setDetail(configBean.getDetail());
		preOrderBean.setMchId(configBean.getMchId());
		preOrderBean.setAttach(configBean.getAttach());
//		preOrderBean.setOpenId(openId);
		preOrderBean.setProductId(openId);
//		preOrderBean.setNonceStr(CiphertextKit.getRandomValue(16));
		preOrderBean.setNonceStr("g24FeR5uFXTVVABb");
		preOrderBean.setOutTradeNo(outTradeNo);
		preOrderBean.setTotalFee(totalFee);
		preOrderBean.setSpbillCreateIp(spbillCreateIp);
		preOrderBean.setNotifyUrl(notifyUrl);
		preOrderBean.setTradeType(tradeType);
		return preOrderBean;
	}

	public TreeMap<String, String> getPreOrderParamTreeMap(WeixinPreOrderRequestBean preOrderBean, String appKey,
			String mustValueParams) {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("appid", preOrderBean.getAppId());
		map.put("mch_id", preOrderBean.getMchId());
		map.put("device_info", preOrderBean.getDeviceInfo());
		map.put("nonce_str", preOrderBean.getNonceStr());
		map.put("body", preOrderBean.getBody());
		map.put("detail", preOrderBean.getDetail());
		map.put("attach", preOrderBean.getAttach());
		map.put("out_trade_no", preOrderBean.getOutTradeNo());
		map.put("fee_type", preOrderBean.getFeeType());
		map.put("total_fee", preOrderBean.getTotalFee());
		map.put("spbill_create_ip", preOrderBean.getSpbillCreateIp());
		map.put("time_start", preOrderBean.getTimeStart());
		map.put("time_expire", preOrderBean.getTimeExpire());
		map.put("goods_tag", preOrderBean.getGoodsTag());
		map.put("notify_url", preOrderBean.getNotifyUrl());
		map.put("trade_type", preOrderBean.getTradeType());
		map.put("product_id", preOrderBean.getProductId());
		map.put("openid", preOrderBean.getOpenId());
		// TreeMap<String, String> map=getParamTreeMap(preOrderBean);
		try {
			String sign = signParam(map, mustValueParams, appKey);
			map.put("sign", sign);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	public WeixinPreOrderResponseBean preOrder(WeixinConfigBean configBean,TreeMap<String, String> paramTreeMap) throws Exception{
		WeixinPreOrderResponseBean responseBean=new WeixinPreOrderResponseBean();
		String payApiUrl=configBean.getPayApiUrl();
		String postXml = getPostXml(paramTreeMap);
		System.out.println(postXml);
		HttpClientKit httpClientKit = new HttpClientKit();
		HttpClientResultBean resultBean = httpClientKit.postContent(payApiUrl, postXml, Constants.CODE_UNICODE);
		String returnXml = resultBean.getResultContent();
		Map<String, String> returnMap = PayUtil.xml2Map(returnXml);
		String returnCode = returnMap.get("return_code");
		responseBean.setReturnCode(returnCode);
		if (PayConfig.SUCCESS.equalsIgnoreCase(returnCode)) {
			String resultCode=returnMap.get("result_code");
			responseBean.setResultCode(resultCode);
			responseBean.setAppid(returnMap.get("appid"));
			responseBean.setMchId(returnMap.get("mch_id"));
			responseBean.setDeviceInfo(StringKit.validStr(returnMap.get("device_info")));
			responseBean.setNonceStr(returnMap.get("nonce_str"));
			responseBean.setSign(returnMap.get("sign"));
			responseBean.setErrCode(StringKit.validStr(returnMap.get("err_code")));
			responseBean.setErrCodeDes(StringKit.validStr(returnMap.get("err_code_des")));
			if (PayConfig.SUCCESS.equalsIgnoreCase(resultCode)) {
				responseBean.setTradeType(returnMap.get("trade_type"));
				responseBean.setPrepayId(returnMap.get("prepay_id"));
				responseBean.setCodeUrl(StringKit.validStr(returnMap.get("code_url")));
			}
		}else {
			responseBean.setReturnMsg(returnMap.get("return_msg"));
		}
		return responseBean;
	}

	public String getBrandWCPayRequest(String appId, String prepayId, String md5key) {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("appId", appId);
		map.put("timeStamp", getWxTimeStamp());
		map.put("nonceStr", CiphertextKit.getRandomValue(16));
		map.put("package", "prepay_id=" + prepayId);
		try {
			String sign = signParam(map, "", md5key);
			map.put("signType", "MD5");
			map.put("paySign", sign);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return JsonKit.toJson(map);
	}

	private String getWxTimeStamp() {
		return Long.toString(new Date().getTime() / 1000);
	}

	// jsAPI必须要获得openId
	public String getOpenid() {
		String URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

		return "";
	}

	public String signParam(TreeMap<String, String> map, String mustValueParams, String md5key) throws Exception {
		List<String> list = ListKit.string2List(mustValueParams, ",");
		StringBuffer sb = new StringBuffer();
		String paramString = "";
		Set<Entry<String, String>> entries = map.entrySet();
		for (Entry<String, String> entry : entries) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (key.equals("sign")) {
				continue;
			}
			if (StringKit.isValid(value)) {
				sb.append(key + "=" + value + "&");
			} else if (list.contains(key)) {
				System.out.println(key);
				throw new Exception("比要参数不齐全");
			}
		}
		paramString=sb.toString();
		return MD5.getMD5(paramString + "key=", md5key).toUpperCase();
	}

	public String getPostXml(TreeMap<String, String> map) {
		StringBuffer sb = new StringBuffer("<xml>");
		Set<Entry<String, String>> entries = map.entrySet();
		for (Entry<String, String> entry : entries) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (StringKit.isValid(value)) {
				sb.append("<" + key + ">").append(value).append("</" + key + ">");
			}
		}
		sb.append("</xml>");
		return sb.toString();
	}

	public static TreeMap<String, String> getParamTreeMap(Object obj) {
		TreeMap<String, String> map = new TreeMap<String, String>();
		Field[] fields = obj.getClass().getDeclaredFields();
		int len = fields.length;
		for (int i = 0; i < len; i++) {
			Field field = fields[i];
			String getMethodName = "get" + StringKit.upperFirstChar(field.getName());
			try {
				Method method = obj.getClass().getDeclaredMethod(getMethodName, null);
				if (method != null) {
					Object value = method.invoke(obj, null);
					if (value != null) {
						map.put(field.getName(), String.valueOf(value));
					}
				}
			} catch (Exception e) {

			}
		}
		return map;
	}

}
