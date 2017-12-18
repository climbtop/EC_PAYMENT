package com.trendy.ow.portal.payment.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.db.DBFactory;
import com.trendy.fw.common.db.DBObject;
import com.trendy.ow.portal.payment.bean.PayLogBean;
import com.trendy.ow.portal.payment.factory.PayDBFactory;

public class PayLogDao {
	private static Logger log = LoggerFactory.getLogger(PayLogDao.class);

	public int insertPayLog(PayLogBean bean) {
		int result = -1;
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectW();
			String sql = "insert into pay_log ( pay_id, create_time, pay_status, pay_content) value( ?, now(), ?, ?)";
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setInt(1, bean.getPayId());
			ps.setString(2, bean.getPayStatus());
			ps.setString(3, bean.getPayContent());

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

}
