package com.trendy.ow.portal.payment.bean;

import java.util.ArrayList;
import java.util.List;

public class PayedAddRequestBean extends PayRequestBean {
	
	private List<PayItemRequestBean> items=new ArrayList<PayItemRequestBean>();

	public List<PayItemRequestBean> getItems() {
		return items;
	}

	public void setItems(List<PayItemRequestBean> items) {
		this.items = items;
	}

}
