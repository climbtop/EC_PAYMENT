package com.trendy.ow.portal.payment.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.db.DBFactory;
import com.trendy.fw.common.db.DBObject;
import com.trendy.fw.common.util.BeanKit;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.factory.PayDBFactory;

public class PayItemDao {
	private static Logger log = LoggerFactory.getLogger(PayItemDao.class);

	public int insertPayItem(PayItemBean bean) {
		int result = -1;
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectW();
			String sql = "insert into pay_item ( pay_id, company_id, channel_id, pay_number, currency, pay_amount, ip_address, pay_time, pay_status, status, create_time) value( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setInt(1, bean.getPayId());
			ps.setInt(2, bean.getCompanyId());
			ps.setInt(3, bean.getChannelId());
			ps.setString(4, bean.getPayNumber());
			ps.setString(5, bean.getCurrency());
			ps.setDouble(6, bean.getPayAmount());
			ps.setString(7, bean.getIpAddress());
			ps.setTimestamp(8, bean.getPayTime());
			ps.setString(9, bean.getPayStatus());
			ps.setInt(10, bean.getStatus());

			result = db.executePstmtInsert();
		} catch (Exception e) {
			log.error("", e);
			result = -1;
		} finally {
			try {
				db.close();
			} catch (SQLException sqle) {
				log.error("", sqle);
			}
		}
		return result;
	}

	public int updatePayItem(PayItemBean bean) {
		int result = -1;
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectW();
			String sql = "update pay_item set pay_id = ?, company_id = ?, channel_id = ?, pay_number = ?, currency = ?, pay_amount = ?, ip_address = ?, pay_time = ?, pay_status = ?, status = ?, modify_time = now() where pay_item_id = ?";
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setInt(1, bean.getPayId());
			ps.setInt(2, bean.getCompanyId());
			ps.setInt(3, bean.getChannelId());
			ps.setString(4, bean.getPayNumber());
			ps.setString(5, bean.getCurrency());
			ps.setDouble(6, bean.getPayAmount());
			ps.setString(7, bean.getIpAddress());
			ps.setTimestamp(8, bean.getPayTime());
			ps.setString(9, bean.getPayStatus());
			ps.setInt(10, bean.getStatus());

			ps.setInt(11, bean.getPayItemId());

			result = db.executePstmtUpdate();
		} catch (Exception e) {
			log.error("", e);
			result = -1;
		} finally {
			try {
				db.close();
			} catch (SQLException sqle) {
				log.error("", sqle);
			}
		}
		return result;
	}

	public int updatePayItemAndInfo(PayItemBean itemBean, PayInfoBean infoBean) {
		int result = -1;
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectW();
			db.beginTransition();
			String sql = "update pay_item set pay_id = ?, company_id = ?, channel_id = ?, pay_number = ?, currency = ?, pay_amount = ?, ip_address = ?, pay_time = ?, pay_status = ?, status = ?, modify_time = now() where pay_item_id = ? ;";
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setInt(1, itemBean.getPayId());
			ps.setInt(2, itemBean.getCompanyId());
			ps.setInt(3, itemBean.getChannelId());
			ps.setString(4, itemBean.getPayNumber());
			ps.setString(5, itemBean.getCurrency());
			ps.setDouble(6, itemBean.getPayAmount());
			ps.setString(7, itemBean.getIpAddress());
			ps.setTimestamp(8, itemBean.getPayTime());
			ps.setString(9, itemBean.getPayStatus());
			ps.setInt(10, itemBean.getStatus());
			ps.setInt(11, itemBean.getPayItemId());
			result = db.executePstmtUpdate();
			if(result>0){
				String sql2 = "update pay_info set info_id = ?, refer_type = ?, app_id = ?, store_id = ?, user_id = ?, company_id = ?, channel_id = ?, currency = ?, request_amount = ?, fact_amount = ?, pay_status = ?, ip_address = ?, status = ?, modify_time = now(), memo = ?,sync_status = ? where pay_id = ?";
				PreparedStatement ps2 = db.getPreparedStatement(sql2);
				ps2.setInt(1, infoBean.getInfoId());
				ps2.setString(2, infoBean.getReferType());
				ps2.setInt(3, infoBean.getAppId());
				ps2.setInt(4, infoBean.getStoreId());
				ps2.setInt(5, infoBean.getUserId());
				ps2.setInt(6, infoBean.getCompanyId());
				ps2.setInt(7, infoBean.getChannelId());
				ps2.setString(8, infoBean.getCurrency());
				ps2.setDouble(9, infoBean.getRequestAmount());
				ps2.setDouble(10, infoBean.getFactAmount());
				ps2.setString(11, infoBean.getPayStatus());
				ps2.setString(12, infoBean.getIpAddress());
				ps2.setInt(13, infoBean.getStatus());
				ps2.setString(14, infoBean.getMemo());
				ps2.setString(15, infoBean.getSyncStatus());
				
				ps2.setInt(16, infoBean.getPayId());
				result = db.executePstmtUpdate();
				if(result==0){
					db.rollback();
				}else {
					db.commit();
				}
			}else {
				db.rollback();
			}

		} catch (Exception e) {
			log.error("", e);
			result = -1;
		} finally {
			try {
				db.close();
			} catch (SQLException sqle) {
				log.error("", sqle);
			}
		}
		return result;
	}

	public PayItemBean getPayItemByKey(int itemId) {
		PayItemBean bean = null;
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectR();
			String sql = "select * from pay_item where pay_item_id = ?";
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setInt(1, itemId);

			ResultSet rs = db.executePstmtQuery();
			if (rs.next()) {
				bean = new PayItemBean();
				bean = BeanKit.resultSet2Bean(rs, PayItemBean.class);
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
	
	public PayItemBean findPayItem(int payId,String payNumber) {
		PayItemBean bean = null;
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectR();
			String sql = "select * from pay_item where pay_id = ? and pay_number= ?";
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setInt(1, payId);
			ps.setString(2, payNumber);
			ResultSet rs = db.executePstmtQuery();
			if (rs.next()) {
				bean = new PayItemBean();
				bean = BeanKit.resultSet2Bean(rs, PayItemBean.class);
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
	
	
	
	public PayItemBean getNearestPayItem(int payId) {
		PayItemBean bean = null;
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectR();
			String sql = "select * from pay_item where create_time = ( select MAX(create_time) from pay_item where pay_id = ? ) AND pay_id = ? ";
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setInt(1, payId);
			ps.setInt(2, payId);
			ResultSet rs = db.executePstmtQuery();
			if (rs.next()) {
				bean = new PayItemBean();
				bean = BeanKit.resultSet2Bean(rs, PayItemBean.class);
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
