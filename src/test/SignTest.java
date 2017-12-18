package test;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.trendy.fw.common.crypto.MD5;
import com.trendy.fw.common.util.ListKit;
import com.trendy.fw.common.util.StringKit;

public class SignTest {

	
	public static void main(String[] args) throws Exception {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("appId", "wx2421b1c4370ec43b");
		map.put("nonceStr", "e61463f8efa94090b1f366cccfbbb444");
		map.put("package", "prepay_id=u802345jgfjsdfgsdg888");
		//map.put("paySign", "70EA570631E4BB79628FBCA90534C63FF7FADD89");
		//map.put("signType", "MD5");
		map.put("timeStamp", "1395712654");
		
		String md5key = "67854406ad060dca907f7b1769aabb47";  //9A0A8659F005D6984697E2CA0A9CF3B7
		
		String sign = signParam(map, "", md5key);
		System.out.println(sign);
	}
	
	
	public static void main1(String[] args) throws Exception {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("appId", "wx51a8bdc78df50544");
		map.put("nonceStr", "UZRDGUZ0YIQ11aQK");
		map.put("package", "prepay_id=wx20151223174244f4018ae1970288642722");
		//map.put("paySign", "3F3928C94E5F2AB70568AF924CE0908E");
		//map.put("signType", "MD5");
		map.put("timeStamp", "1450863764");
		
		String md5key = "67854406ad060dca907f7b1769aabb47";  //9A0A8659F005D6984697E2CA0A9CF3B7
		
		String sign = signParam(map, "", md5key);
		System.out.println(sign);
	}
	
	
	public static String signParam(TreeMap<String, String> map, String mustValueParams, String md5key) throws Exception {
		List<String> list = ListKit.string2List(mustValueParams, ",");
		StringBuffer sb = new StringBuffer();
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
				throw new Exception(key + " must value");
			}
		}

		return MD5.getMD5(sb.toString() + "key=", md5key).toUpperCase();
	}

}
