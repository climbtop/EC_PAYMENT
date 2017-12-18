package com.trendy.ow.portal.payment.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.trendy.fw.common.bean.StatusBean;
import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.util.PropertiesKit;
import com.trendy.fw.common.util.StatusKit;

public class PayConfig {
	// 配置文件路径
	public static final String PAYMENT_PROP_FILE_NAME = Constants.PROP_FILE_PATH + "/payment_config";

	// 页面路径
	public final static String PAGE_PATH = Constants.PAGE_PATH + "{0}/{1}/payment/";

	// appId
	public static String APP_ID = PropertiesKit.getBundleProperties(PAYMENT_PROP_FILE_NAME, "APP_ID");

	// 端口号
	public static String SERVER_PORT = PropertiesKit.getBundleProperties(PAYMENT_PROP_FILE_NAME, "SERVER_PORT");

	// 支付状态
	public final static String PAYS_WAIT_PAY = "WAIT_PAY";// 未支付
	public final static String PAYS_PAYED = "PAYED";// 已支付
	public final static String PAYS_PAYED_PART = "PAYED_PART";// 部分支付
	public final static String PAYS_CANCELLED = "CANCELLED";// 取消

	public final static String SUCCESS = "success";
	public final static String ZERO = "0";
	public final static String CANCEL = "cancel";
	public final static String OK = "ok";
	public final static String FAIL = "fail";

	public static String IS_TEST = PropertiesKit.getBundleProperties(PAYMENT_PROP_FILE_NAME, "IS_TEST");

	// 支付场合
	public static List<StatusBean> PAY_LOCALE_LIST = new ArrayList<StatusBean>();
	public static HashMap<String, String> PAY_LOCALE_MAP = new HashMap<String, String>();
	public final static int PL_WEB = 1;// 页面
	public final static int PL_APP = 2;// APP
	public final static int PL_WAP = 3;// 手机页面
	public final static int PL_WEIXIN = 4;// 微信
	public final static int PL_CREDIT_CARD = 5;// 刷卡
	public final static int PL_CASH = 6;// 现金
	public final static int PL_SHOP = 7;// 实体店

	// 渠道类型
	public static List<StatusBean> PAY_CHANNEL_TYPE_LIST = new ArrayList<StatusBean>();
	public static HashMap<String, String> PAY_CHANNEL_TYPE_MAP = new HashMap<String, String>();
	public final static int PCT_BANK = 1;// 银行
	public final static int PCT_THIRD_PARTY = 2;// 第三方

	// 相关类型
	public static List<StatusBean> REFER_TYPE_LIST = new ArrayList<StatusBean>();
	public static HashMap<String, String> REFER_TYPE_MAP = new HashMap<String, String>();
	public final static String RT_FULL = "FULL";// 全额
	public final static String RT_FRONT = "FRONT";// 订金
	public final static String RT_FINAL = "FINAL";// 尾款
	
	// requestBean.output接口请求返回的类型
	public final static String OP_PAGE = "PAGE";// 页面  
	public final static String OP_IMAGE = "IMAGE";// 图片
	public final static String OP_STRING = "STRING";// 字符串
	public static List<String> OUTPUT_TYPE_LIST = new ArrayList<String>();
	
	public final static int REQUEST_TYPE_ZERO = 0;  //接口参数，自定义处理返回
	public final static int REQUEST_TYPE_ONE = 1;
	


	static {
		initPayLocaleList();
		initPayLocaleMap();

		initPayChannelTypeList();
		initPayChannelTypeMap();

		initReferTypeList();
		initReferTypeMap();
		initOutPutTypeList();
	}
	
	private static void initPayChannelTypeList() {
		PAY_CHANNEL_TYPE_LIST.add(new StatusBean(PCT_BANK, "银行"));
		PAY_CHANNEL_TYPE_LIST.add(new StatusBean(PCT_THIRD_PARTY, "第三方"));
	}

	private static void initPayChannelTypeMap() {
		PAY_CHANNEL_TYPE_MAP = StatusKit.toMap(PAY_CHANNEL_TYPE_LIST);
	}

	private static void initReferTypeList() {
		REFER_TYPE_LIST.add(new StatusBean(RT_FULL, "全额"));
		REFER_TYPE_LIST.add(new StatusBean(RT_FRONT, "订金"));
		REFER_TYPE_LIST.add(new StatusBean(RT_FINAL, "尾款"));
	}

	private static void initReferTypeMap() {
		REFER_TYPE_MAP = StatusKit.toMap(REFER_TYPE_LIST);
	}

	private static void initPayLocaleList() {
		PAY_LOCALE_LIST.add(new StatusBean(PL_WEB, "页面"));
		PAY_LOCALE_LIST.add(new StatusBean(PL_APP, "APP"));
		PAY_LOCALE_LIST.add(new StatusBean(PL_WAP, "手机页面"));
		PAY_LOCALE_LIST.add(new StatusBean(PL_WEIXIN, "微信"));
		PAY_LOCALE_LIST.add(new StatusBean(PL_CREDIT_CARD, "刷卡"));
		PAY_LOCALE_LIST.add(new StatusBean(PL_CASH, "现金"));
	}

	private static void initPayLocaleMap() {
		PAY_LOCALE_MAP = StatusKit.toMap(PAY_LOCALE_LIST);
	}
	
	private static void initOutPutTypeList(){
		OUTPUT_TYPE_LIST.add(OP_PAGE);
		OUTPUT_TYPE_LIST.add(OP_IMAGE);
		OUTPUT_TYPE_LIST.add(OP_STRING);
	}

}
