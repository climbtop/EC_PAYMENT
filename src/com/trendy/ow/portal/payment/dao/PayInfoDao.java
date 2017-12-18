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
import com.trendy.ow.portal.payment.factory.PayDBFactory;

public class PayInfoDao {
	private static Logger log = LoggerFactory.getLogger(PayInfoDao.class);

	public int insertPayInfo(PayInfoBean bean) {
		int result = -1;
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectW();
			String sql = "insert into pay_info ( info_id, refer_type, app_id, store_id, user_id, company_id, channel_id, currency, request_amount, fact_amount, pay_status, ip_address, sync_status, status, create_time, memo) value( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), ?)";
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setInt(1, bean.getInfoId());
			ps.setString(2, bean.getReferType());
			ps.setInt(3, bean.getAppId());
			ps.setInt(4, bean.getStoreId());
			ps.setInt(5, bean.getUserId());
			ps.setInt(6, bean.getCompanyId());
			ps.setInt(7, bean.getChannelId());
			ps.setString(8, bean.getCurrency());
			ps.setDouble(9, bean.getRequestAmount());
			ps.setDouble(10, bean.getFactAmount());
			ps.setString(11, bean.getPayStatus());
			ps.setString(12, bean.getIpAddress());
			ps.setString(13, bean.getSyncStatus());
			ps.setInt(14, bean.getStatus());
			ps.setString(15, bean.getMemo());

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

	public int updatePayInfo(PayInfoBean bean) {
		int result = -1;
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectW();
			String sql = "update pay_info set info_id = ?, refer_type = ?, app_id = ?, store_id = ?, user_id = ?, company_id = ?, channel_id = ?, currency = ?, request_amount = ?, fact_amount = ?, pay_status = ?, ip_address = ?, status = ?, modify_time = now(), memo = ? ,sync_status = ? where pay_id = ?";
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setInt(1, bean.getInfoId());
			ps.setString(2, bean.getReferType());
			ps.setInt(3, bean.getAppId());
			ps.setInt(4, bean.getStoreId());
			ps.setInt(5, bean.getUserId());
			ps.setInt(6, bean.getCompanyId());
			ps.setInt(7, bean.getChannelId());
			ps.setString(8, bean.getCurrency());
			ps.setDouble(9, bean.getRequestAmount());
			ps.setDouble(10, bean.getFactAmount());
			ps.setString(11, bean.getPayStatus());
			ps.setString(12, bean.getIpAddress());
			ps.setInt(13, bean.getStatus());
			ps.setString(14, bean.getMemo());
			ps.setString(15, bean.getSyncStatus());

			ps.setInt(16, bean.getPayId());

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

	public PayInfoBean getPayInfoByKey(int payId) {
		PayInfoBean bean = null;
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectR();
			String sql = "select * from pay_info where pay_id = ?";
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setInt(1, payId);

			ResultSet rs = db.executePstmtQuery();
			if (rs.next()) {
				bean = new PayInfoBean();
				bean = BeanKit.resultSet2Bean(rs, PayInfoBean.class);
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

	public PayInfoBean getPayInfoByKey(int infoId, String referType, int appId) {
		PayInfoBean bean = null;
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectR();
			String sql = "select * from pay_info where info_id = ? and refer_type = ? and app_id = ?";
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setInt(1, infoId);
			ps.setString(2, referType);
			ps.setInt(3, appId);
			ResultSet rs = db.executePstmtQuery();
			if (rs.next()) {
				bean = new PayInfoBean();
				bean = BeanKit.resultSet2Bean(rs, PayInfoBean.class);
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

	public PayInfoBean getNearestPayInfo() {
		PayInfoBean bean = null;
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectR();
			String sql = "select * from pay_info where pay_id = (select max(pay_id) from pay_info)";
			db.getPreparedStatement(sql);
			ResultSet rs = db.executePstmtQuery();
			if (rs.next()) {
				bean = new PayInfoBean();
				bean = BeanKit.resultSet2Bean(rs, PayInfoBean.class);
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
