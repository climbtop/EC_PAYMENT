package com.trendy.ow.portal.payment.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.db.DBFactory;
import com.trendy.fw.common.db.DBObject;
import com.trendy.fw.common.util.BeanKit;
import com.trendy.ow.portal.payment.bean.PayStoreChannelBean;
import com.trendy.ow.portal.payment.factory.PayDBFactory;

public class PayStoreChannelDao {
	private static Logger log = LoggerFactory.getLogger(PayStoreChannelDao.class);

	public List<PayStoreChannelBean> getPayStoreChannelListByStoreId(int storeId) {
		List<PayStoreChannelBean> list = new ArrayList<PayStoreChannelBean>();
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectR();
			String sql = "select *  from pay_store_channel where store_id = ? and status = " + Constants.STATUS_VALID
					+ " and use_status = " + Constants.STATUS_VALID + " order by priority asc";
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setInt(1, storeId);
			ResultSet rs = db.executePstmtQuery();
			while (rs.next()) {
				PayStoreChannelBean bean = new PayStoreChannelBean();
				bean = BeanKit.resultSet2Bean(rs, PayStoreChannelBean.class);
				list.add(bean);
			}
			rs.close();
		} catch (Exception e) {
			log.error("", e);
		} finally {
			try {
				db.close();
			} catch (SQLException sqle) {
				log.error("", sqle);
			}
		}
		return list;
	}
	
	public PayStoreChannelBean getPayStoreChannel(int storeId,int channelId) {
		PayStoreChannelBean bean = null;
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectR();
			String sql = "select *  from pay_store_channel  where store_id = ? and channel_id = ? ";
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setInt(1, storeId);
			ps.setInt(2, channelId);
			ResultSet rs = db.executePstmtQuery();
			if (rs.next()) {
				bean = new PayStoreChannelBean();
				bean = BeanKit.resultSet2Bean(rs, PayStoreChannelBean.class);
			}
			rs.close();
		} catch (Exception e) {
			log.error("", e);
			bean = null;
		} finally {
			try {
				db.close();
			} catch (SQLException sqle) {
				log.error("", sqle);
			}
		}
		return bean;
	}
}
