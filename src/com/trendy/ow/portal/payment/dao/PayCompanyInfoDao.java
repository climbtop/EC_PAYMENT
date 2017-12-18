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
import com.trendy.ow.portal.payment.bean.PayCompanyInfoBean;
import com.trendy.ow.portal.payment.factory.PayDBFactory;

public class PayCompanyInfoDao {
	private static Logger log = LoggerFactory.getLogger(PayCompanyInfoDao.class);
	public PayCompanyInfoBean getPayCompanyInfoByKey(int companyId) {
		PayCompanyInfoBean bean = null;
		PayDBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectR();
			String sql = "select * from pay_company_info where company_id = ?";
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setInt(1, companyId);

			ResultSet rs = db.executePstmtQuery();
			if (rs.next()) {
				bean = new PayCompanyInfoBean();
				bean = BeanKit.resultSet2Bean(rs, PayCompanyInfoBean.class);
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
	
	public PayCompanyInfoBean getPayCompanyInfoByCode(String companyCode) {
		PayCompanyInfoBean bean = null;
		PayDBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectR();
			String sql = "select * from pay_company_info where company_code = ?";
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setString(1, companyCode);

			ResultSet rs = db.executePstmtQuery();
			if (rs.next()) {
				bean = new PayCompanyInfoBean();
				bean = BeanKit.resultSet2Bean(rs, PayCompanyInfoBean.class);
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
	
	
	public List<PayCompanyInfoBean> getPayCompanyInfoAllList() {
		List<PayCompanyInfoBean> list = new ArrayList<PayCompanyInfoBean>();
		PayCompanyInfoBean bean = null;
		DBFactory dbFactory = new PayDBFactory();
		DBObject db = null;
		try {
			db = dbFactory.getDBObjectR();
			String sql = "select * from pay_company_info ";
			db.getPreparedStatement(sql);
			ResultSet rs = db.executePstmtQuery();
			while (rs.next()) {
				bean = new PayCompanyInfoBean();
				bean = BeanKit.resultSet2Bean(rs, PayCompanyInfoBean.class);
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

}
