package com.trendy.ow.portal.payment.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.db.DBFactory;
import com.trendy.fw.common.db.DBObject;
import com.trendy.fw.common.util.BeanKit;
import com.trendy.ow.portal.payment.bean.PayConfigInfoBean;
import com.trendy.ow.portal.payment.factory.PayDBFactory;

public class PayConfigInfoDao {
	private static Logger log = LoggerFactory.getLogger(PayConfigInfoDao.class);

	public PayConfigInfoBean getPayConfigInfoByCode(String code) {
		PayConfigInfoBean bean=null;
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectR();
			String sql="select * from pay_config_info where config_code = ?";
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setString(1, code);
			ResultSet rs = db.executePstmtQuery();
			if (rs.next()) {
				bean=new PayConfigInfoBean();
				bean = BeanKit.resultSet2Bean(rs, PayConfigInfoBean.class);
			}
			rs.close();
		} catch (Exception e) {
			log.error("", e);
			bean=null;
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
