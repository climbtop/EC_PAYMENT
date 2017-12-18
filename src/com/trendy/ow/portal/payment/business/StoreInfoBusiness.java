package com.trendy.ow.portal.payment.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.trendy.ec.system.bean.StoreInfoBean;
import com.trendy.ec.system.client.SystemServiceClient;

public class StoreInfoBusiness {
	
	public Map<Integer, String> getStoreCodeByIdMap() {
		SystemServiceClient serviceClient=new SystemServiceClient();
		List<StoreInfoBean> list = serviceClient.getStoreInfoAllList();
		Map<Integer, String> map = new HashMap<Integer, String>();
		for (StoreInfoBean bean : list) {
			map.put(bean.getStoreId(), bean.getStoreCode());
		}
		return map;
	}
}
