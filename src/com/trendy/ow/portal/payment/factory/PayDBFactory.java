package com.trendy.ow.portal.payment.factory;

import java.sql.SQLException;

import com.trendy.fw.common.db.DBExecutor;
import com.trendy.fw.common.db.DBFactory;
import com.trendy.fw.common.db.DBObject;
import com.trendy.fw.common.db.MysqlDBExecutor;
import com.trendy.fw.common.db.MysqlDBObject;
import com.trendy.ow.portal.payment.config.PayDBConfig;

public class PayDBFactory extends DBFactory {

	@Override
	public DBObject getDBObjectR() throws SQLException {
		return new MysqlDBObject(PayDBConfig.DB_LINK_ORDER_R);
	}

	@Override
	public DBObject getDBObjectW() throws SQLException {
		return new MysqlDBObject(PayDBConfig.DB_LINK_ORDER_W);
	}

	@Override
	public DBExecutor getDBExecutor() {
		return new MysqlDBExecutor();
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub

	}

}
