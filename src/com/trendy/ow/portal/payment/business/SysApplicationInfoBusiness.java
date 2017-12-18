package com.trendy.ow.portal.payment.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.trendy.ec.system.bean.SysApplicationInfoBean;
import com.trendy.ec.system.client.SystemServiceClient;

public class SysApplicationInfoBusiness {

	public Map<Integer, String> getSysApplicationInfoCodeAllMap() {
		Map<Integer, String> map = new HashMap<Integer, String>();
		SystemServiceClient serviceClient = new SystemServiceClient();
		List<SysApplicationInfoBean> list = serviceClient.getSysApplicationInfoAllList();
		for (SysApplicationInfoBean bean : list) {
			map.put(bean.getAppId(), bean.getAppCode());
		}
		return map;
	}
}
