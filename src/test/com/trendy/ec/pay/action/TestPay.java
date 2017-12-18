package test.com.trendy.ec.pay.action;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;

import com.trendy.fw.common.crypto.BASE64;
import com.trendy.fw.common.crypto.MD5;
import com.trendy.fw.common.crypto.RSA;
import com.trendy.fw.common.util.BeanKit;
import com.trendy.fw.common.web.ReturnMessageBean;
import com.trendy.fw.tools.criphertext.CiphertextKit;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.bean.PayRequestBean;
import com.trendy.ow.portal.payment.business.PayUtil;
import com.trendy.ow.portal.payment.tenpay.bean.TenPayConfigBean;
import com.trendy.ow.portal.payment.tenpay.cache.TenPayCache;

public abstract class TestPay {
	static{
		try {
			JAXPConfigurator.configure("src/config/proxool.xml", false);
		} catch (ProxoolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public abstract String test();
	public static void tenpay() {
		TenPayCache business = new TenPayCache();
		TenPayConfigBean bean=business.getTenPayConfig("OchirlyOfficial");
		PayInfoBean payInfoBean = new PayInfoBean();
		payInfoBean.setRequestAmount(10);
		payInfoBean.setUserId(2);
		PayItemBean itemBean = new PayItemBean();
		itemBean.setPayItemId(2);
		try {
//			business.toPay(payId, channelId, request)
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void alipay() throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyPair keyPair=RSA.getKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
	    System.out.println( BASE64.encodeBase64(publicKey.getEncoded()));
	    
	    String privateKeyStr=BASE64.encodeBase64(privateKey.getEncoded());
	    System.out.println(privateKeyStr );
	    RSA.getPrivateKey(privateKeyStr);
		byte[] privateKeybytes=BASE64.decodeBase64(privateKeyStr);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeybytes);
//		X509EncodedKeySpec keySpec=new X509EncodedKeySpec(privateKeybytes); 
		KeyFactory keyFactory=KeyFactory.getInstance("RSA"); 
        PrivateKey privateKey2=keyFactory.generatePrivate(keySpec);  //生成公钥时报错 
        privateKeyStr=BASE64.encodeBase64(privateKey2.getEncoded());
        System.out.println(privateKeyStr );
//		AliPayWapPayProcessor aliPayWapBusiness = new AliPayWapPayProcessor();
//		AliPayWapConfigBean bean = aliPayWapBusiness.getAliPayWapConfigBeanFromCache("OchirlyOfficial", "AliPayWap");
//		PayInfoBean payInfoBean = new PayInfoBean();
//		payInfoBean.setRequestAmount(10);
//		payInfoBean.setUserId(2);
//		PayItemBean itemBean = new PayItemBean();
//		itemBean.setItemId(2);
		try {
//			String token = aliPayWapBusiness.getToken(bean, "http://www.xx.com/", payInfoBean, itemBean);
//			System.err.println(token);
//			String url = aliPayWapBusiness.buildUrl(bean, token);
//			System.out.println(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void createKeyPairs() throws Exception {
//	    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	    // create the keys
	    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
	    generator.initialize(512, new SecureRandom());
	    KeyPair pair = generator.generateKeyPair();
	    PublicKey pubKey = pair.getPublic();
	    PrivateKey privKey = pair.getPrivate();
	    byte[] pk = pubKey.getEncoded();
	    byte[] privk = privKey.getEncoded();
	    String strpk = new String(Base64.encodeBase64(pk));
	    String strprivk = new String(Base64.encodeBase64(privk));

	    System.out.println("公钥:" + Arrays.toString(pk));
	    System.out.println("私钥:" + Arrays.toString(privk));
	    System.out.println("公钥Base64编码:" + strpk);
	    System.out.println("私钥Base64编码:" + strprivk);

	    X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(Base64.decodeBase64(strpk.getBytes()));
	    PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(strprivk.getBytes()));

	    KeyFactory keyf = KeyFactory.getInstance("RSA");
	    PublicKey pubkey2 = keyf.generatePublic(pubX509);
	    PrivateKey privkey2 = keyf.generatePrivate(priPKCS8);

	    System.out.println(pubKey.equals(pubkey2));
	    System.out.println(privKey.equals(privkey2));
	  }
	
	
	public static void main(String[] args) throws Exception {
		
//		System.out.println("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOCoONwCQKpAmn1kA2zdB+B3lwe98qHzWAmbNbW71K8OsO307ZOXim2e5kMlHxtkJ/33KL33xMakJ6/1FSFrE3K1qruO/4bn5pq9P73iU/p4jGs8UiMRedV0uMj1Z8L/tKhJ69WAISF0FOPZHR6UqQIJr6qB6q9vfAx+QlEZhChjAgMBAAECgYBtsbYRtwfcGa9VioAhp8swzzPvk8/lQ+y4xBB1rEEtOP8rMWzPpC7zV6MXJgkKVt0wJH7AQzrRw1WdlQ28Q4w6jAY2jxxrZOhv5bZA6W6qqHFYgPRfPUOi8ZaNY9BNIVWix87WJfS/ZALZqvuBuF7aky5QpN2AEMCrfRnuzLLgAQJBAPtHHz0J+5/qD0uWIHsw1hg9UtERAUCKTveIT6BbgbgWlJNWkOjoJVJVyIxiKZa5Nlx3OOVjxNHT9UdmWzNE3dMCQQDk4QeraEapnq6op0IDbbu+Sv3nkUIv433fCIvPLafLb3O70XG8gu0V89x4FLYc6HneTSYxqW/guQRJwoV2XKExAkATN3k4Hc9Uh33r9es+AJoe+HGg83/5A5rOa2pLhTQCdBegvPoQFdDk3xKbSdaMZFW39JYxVNP0iBU2BKns3dNpAkEAk2NiB/xnfLVF7i9/MCwK+XjbVrLQ93u8w9KCDdFtu3CzC6DNaEK7oCZgtCdKOwVt7TyrOtHMzN74JvOP9G+kEQJBAOv3Z8iHaiupAbqhAwTtLB8AK9vBwn2c2zmHoo0AzeaSV6fFMwlpwMG755Ez8geAFWAVnBNIILNh5iRv5LtWu5A=");
		PayRequestBean bean = new PayRequestBean();
		bean.setChannelCode("channelCode");
		String md5="s3fs3jha";
		String publicRSAKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgqDjcAkCqQJp9ZANs3Qfgd5cHvfKh81gJmzW1u9SvDrDt9O2Tl4ptnuZDJR8bZCf99yi998TGpCev9RUhaxNytaq7jv+G5+aavT+94lP6eIxrPFIjEXnVdLjI9WfC/7SoSevVgCEhdBTj2R0elKkCCa+qgeqvb3wMfkJRGYQoYwIDAQAB";
		PublicKey key = RSA.getPublicKey(publicRSAKey);
		String data = CiphertextKit.encrypt(bean, key, md5);
		String rsaPrivateKey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOCoONwCQKpAmn1kA2zdB+B3lwe98qHzWAmbNbW71K8OsO307ZOXim2e5kMlHxtkJ/33KL33xMakJ6/1FSFrE3K1qruO/4bn5pq9P73iU/p4jGs8UiMRedV0uMj1Z8L/tKhJ69WAISF0FOPZHR6UqQIJr6qB6q9vfAx+QlEZhChjAgMBAAECgYBtsbYRtwfcGa9VioAhp8swzzPvk8/lQ+y4xBB1rEEtOP8rMWzPpC7zV6MXJgkKVt0wJH7AQzrRw1WdlQ28Q4w6jAY2jxxrZOhv5bZA6W6qqHFYgPRfPUOi8ZaNY9BNIVWix87WJfS/ZALZqvuBuF7aky5QpN2AEMCrfRnuzLLgAQJBAPtHHz0J+5/qD0uWIHsw1hg9UtERAUCKTveIT6BbgbgWlJNWkOjoJVJVyIxiKZa5Nlx3OOVjxNHT9UdmWzNE3dMCQQDk4QeraEapnq6op0IDbbu+Sv3nkUIv433fCIvPLafLb3O70XG8gu0V89x4FLYc6HneTSYxqW/guQRJwoV2XKExAkATN3k4Hc9Uh33r9es+AJoe+HGg83/5A5rOa2pLhTQCdBegvPoQFdDk3xKbSdaMZFW39JYxVNP0iBU2BKns3dNpAkEAk2NiB/xnfLVF7i9/MCwK+XjbVrLQ93u8w9KCDdFtu3CzC6DNaEK7oCZgtCdKOwVt7TyrOtHMzN74JvOP9G+kEQJBAOv3Z8iHaiupAbqhAwTtLB8AK9vBwn2c2zmHoo0AzeaSV6fFMwlpwMG755Ez8geAFWAVnBNIILNh5iRv5LtWu5A=";
		PrivateKey privateKey = RSA.getPrivateKey(rsaPrivateKey);
		ReturnMessageBean result = CiphertextKit.decrypt(data, privateKey, md5, PayRequestBean.class);
		PayRequestBean detailBean = (PayRequestBean) result.getContent();
		System.out.println(detailBean.getChannelCode());
//		alipay();
//		TestPay testPay=new TestPay() {
//			
//			@Override
//			public String test() {
//				AliPayWebPayProcessor processor=new AliPayWebPayProcessor();
//				String url;
//				try {
//					 PayConfigCache cache=new PayConfigCache();
//					 AppConfigBean bean=cache.getAppConfigBean("System");
//					 byte[] publicKey1=BASE64.decodeBase64(bean.getRsaPrivateKey());
//					 X509EncodedKeySpec keySpec=new X509EncodedKeySpec(publicKey1); 
//					 KeyFactory keyFactory=KeyFactory.getInstance("RSA"); 
//		             PublicKey publickey=keyFactory.generatePublic(keySpec);  //生成公钥时报错 
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				return null;
//			}
//		};
//		testPay.test();
	}
	
	@Test
	public static void aliPayNotice() throws Exception{
		String noticeUrl="http://dev.admin.teadmin.net:8080/PAYMENT/pay/AlipayWapCallback.do?";
		TreeMap<String, String> tree=new TreeMap<String, String>();
		
		tree.put("service", "alipay.wap.trade.create.direct");
		tree.put("v", "1.0");
		tree.put("sec_id", "MD5");
		String notifyData="<notify><payment_type>1</payment_type><subject>收银台[1283134629741]</subject><trade_no>5</trade_no><buyer_email>dinglang@a.com</buyer_email><gmt_payment>2010-08-30 10:17:24</gmt_payment><notify_type>trade_status_sync</notify_type><quantity>1</quantity><out_trade_no>3</out_trade_no><notify_time>2010-08-30 10:18:15</notify_time><seller_id>2088101000137799</seller_id><trade_status>TRADE_FINISHED</trade_status><is_total_fee_adjust>N</is_total_fee_adjust><total_fee>1.00</total_fee></notify>";
		tree.put("notify_data", notifyData);
		String key="ro34331c5hbfo6eyov3y4ajjifx8aygz";
		String content=PayUtil.map2String(tree);
		String sign=MD5.getMD5(content, key);
		
		tree.put("notify_data", PayUtil.urlEncode(notifyData, "UTF-8"));
		String url=noticeUrl+PayUtil.buildEncodeQueryString(tree)+"&sign="+sign;
//		System.out.println(PayUtil.doHttpPost(url, ""));
		
	}
}
