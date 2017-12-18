package test.com.trendy.ec.pay.action;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.crypto.RSA;
import com.trendy.fw.common.transfer.HttpClientKit;
import com.trendy.fw.common.transfer.HttpClientResultBean;
import com.trendy.fw.common.util.BeanKit;
import com.trendy.fw.common.util.JsonKit;
import com.trendy.fw.common.web.HttpResponseKit;
import com.trendy.fw.common.web.ReturnMessageBean;
import com.trendy.fw.tools.criphertext.CiphertextBean;
import com.trendy.fw.tools.criphertext.CiphertextKit;
import com.trendy.ow.portal.payment.bean.PayItemRequestBean;
import com.trendy.ow.portal.payment.bean.PayRequestBean;
import com.trendy.ow.portal.payment.offline.config.OfflinePayConfig;

public class TestSpecialPay {
	public static void main(String[] args) {
		PayRequestBean bean = new PayRequestBean();
		bean.setAppId(5);
		bean.setCompanyCode(OfflinePayConfig.COMPANY_CODE);
		bean.setCurrency("CNY");
		bean.setInfoId(654334211);
		bean.setStoreId(2);
		bean.setRequestAmount(99);
		bean.setReferType("FULL");
		bean.setUserId(20);
		List<PayItemRequestBean> list=new ArrayList<PayItemRequestBean>();
		String md5="s3fs3jha";
		String publicRSAKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC1Osy5GhOzB+ugDYokFfYPwXQxPljJxxasQCt5EU7bHvRp8leymadqF4CqhiLsq9O4wlZ5T71vKFRX/Fjo4C2nIir2Ha8iWVFllHtPqz2tGZGhlUPwfHzM3AJ5djcGdy/MTF/OynHHm1Xyq/03xSYCA78JreXYV6TCQ1IHqpAmrwIDAQAB";
		PublicKey key = RSA.getPublicKey(publicRSAKey);
		PayItemRequestBean itemRequestBean=new PayItemRequestBean();
		itemRequestBean.setPayAmount(36);
		itemRequestBean.setChannelCode(OfflinePayConfig.CHANNEL_CODE_CASH);
		itemRequestBean.setPayNumber("25sdsd1523");
		itemRequestBean.setCurrency("CNY");
		PayItemRequestBean itemRequestBean2=new PayItemRequestBean();
		itemRequestBean2.setPayAmount(64);
		itemRequestBean2.setChannelCode(OfflinePayConfig.CHANNEL_CODE_CASH);
		itemRequestBean2.setPayNumber("564sdsd15");
		itemRequestBean2.setCurrency("CNY");
		
		list.add(itemRequestBean);
		list.add(itemRequestBean2);
		bean.setItems(list);
		System.out.println(JsonKit.toJson(bean));
		String data = CiphertextKit.encrypt(bean, key, md5);
		Map<String, String> paraMap=new HashMap<String, String>();
		paraMap.put("data", data);
		paraMap.put("appId", "5");
		String payApiUrl="http://localhost:8080/pay/m/PayInfoAdd.action";
		HttpClientKit httpClientKit = new HttpClientKit();
		HttpClientResultBean resultBean = httpClientKit.postContent(payApiUrl, paraMap, Constants.CODE_UNICODE);
		
		System.out.println(JsonKit.toJson(resultBean));
//		String rsaPrivateKey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALU6zLkaE7MH66ANiiQV9g/BdDE+WMnHFqxAK3kRTtse9GnyV7KZp2oXgKqGIuyr07jCVnlPvW8oVFf8WOjgLaciKvYdryJZUWWUe0+rPa0ZkaGVQ/B8fMzcAnl2NwZ3L8xMX87KccebVfKr/TfFJgIDvwmt5dhXpMJDUgeqkCavAgMBAAECgYBwdOMyfT76GhEZSS2ORN5iWn3aTMDVvKeSDWOshZP0Hpo13/6RQg2DpL/fkMq9J8aCYH0+W7/F6TWlP16AaxOIFMjdEpMd1wzB7sJuY56poLOp59VRAFnL6OatazNDGEP3egfr81qCQoGlK0BVB7bYq5O7nLGjMZEJBqEFeAUCyQJBAO7fp+4DINP6jcMfVBd9cc9ab1gUekiuKRF4HOfzhEBoWCjl7A6HlMSgOfYmMQnUFwxZVObSDlql43Zm8QiUvvsCQQDCOSHviXTJwZNMqGQPzoarrGZLuB8gFG/9xAc/CVWcQS6qqM169BplH3etUDXMo24FHJNayeLVXe8jX7wVs1jdAkEAho3NVjDE6SMVf3fCMoki9p4GYiMGzrHryD9UaQOu12jvX/pDgdu1XRy0CYdx0At8ACTBwlNIap9PBX7u/tpqyQJAStO6GFAr14MlndYOXuyhg8hyzN9N1o0pLGp2pDmTaxTNxuAr8h/Tf3wlHneVkpawT3XX65V2N9/tvwImM3IaXQJBANWECFTB/uIATjYejQ99Y2boBHnqeCIdkmBb15I2UFGqWxthgtdqZNR+6mCkLnSEaqT10EXGUdt25aD6klorfEA=";
//		PrivateKey privateKey = RSA.getPrivateKey(rsaPrivateKey);
//		ReturnMessageBean result = CiphertextKit.decrypt(data, privateKey, md5, PayRequestBean.class);
//		PayRequestBean detailBean = (PayRequestBean) result.getContent();
//		detailBean.getItems().size();
//	    System.out.println(JsonKit.toJson(detailBean));
	}
}
