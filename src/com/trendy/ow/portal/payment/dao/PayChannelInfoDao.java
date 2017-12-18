package com.trendy.ow.portal.payment.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.db.DBFactory;
import com.trendy.fw.common.db.DBObject;
import com.trendy.fw.common.util.BeanKit;
import com.trendy.ow.portal.payment.bean.PayChannelInfoBean;
import com.trendy.ow.portal.payment.factory.PayDBFactory;

public class PayChannelInfoDao {
	private static Logger log = LoggerFactory.getLogger(PayChannelInfoDao.class);

	public List<PayChannelInfoBean> getPayChannelInfoList(int[] channelIds) {
		List<PayChannelInfoBean> list = new ArrayList<PayChannelInfoBean>();
		PayChannelInfoBean bean = null;
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectR();
			StringBuilder sql = new StringBuilder("select * from pay_channel_info where channel_id in (0");
			if (channelIds != null) {
				for (int i = 0; i < channelIds.length; i++) {
					sql.append(",?");
				}
			}
			sql.append(")");
			PreparedStatement ps = db.getPreparedStatement(sql.toString());
			if (channelIds != null) {
				for (int i = 0; i < channelIds.length; i++) {
					ps.setInt(i + 1, channelIds[i]);
				}
			}
			ResultSet rs = db.executePstmtQuery();
			while (rs.next()) {
				bean = new PayChannelInfoBean();
				bean = BeanKit.resultSet2Bean(rs, PayChannelInfoBean.class);
				list.add(bean);
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
		return list;
	}

	public List<PayChannelInfoBean> getPayChannelInfoListByCompanyId(int companyId) {
		List<PayChannelInfoBean> list = new ArrayList<PayChannelInfoBean>();
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectR();
			String sql = "select * from pay_channel_info where company_id = ? ";
			PreparedStatement ps = db.getPreparedStatement(sql.toString());
			ps.setInt(1, companyId);
			ResultSet rs = db.executePstmtQuery();
			while (rs.next()) {
				PayChannelInfoBean bean = new PayChannelInfoBean();
				bean = BeanKit.resultSet2Bean(rs, PayChannelInfoBean.class);
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

	public PayChannelInfoBean getPayChannelInfo(int channelId) {
		PayChannelInfoBean bean = null;
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectR();
			String sql = "select * from pay_channel_info where channel_id = ?";
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setInt(1, channelId);

			ResultSet rs = db.executePstmtQuery();
			if (rs.next()) {
				bean = new PayChannelInfoBean();
				bean = BeanKit.resultSet2Bean(rs, PayChannelInfoBean.class);
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

	public PayChannelInfoBean getPayChannelInfo(String channelCode) {
		PayChannelInfoBean bean = null;
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectR();
			String sql = "select * from pay_channel_info where channel_code = ?";
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setString(1, channelCode);

			ResultSet rs = db.executePstmtQuery();
			if (rs.next()) {
				bean = new PayChannelInfoBean();
				bean = BeanKit.resultSet2Bean(rs, PayChannelInfoBean.class);
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
