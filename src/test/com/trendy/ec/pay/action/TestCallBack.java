package test.com.trendy.ec.pay.action;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.config.Constants;
import com.trendy.fw.common.crypto.MD5;
import com.trendy.fw.common.web.HttpResponseKit;
import com.trendy.fw.common.web.ParamKit;
import com.trendy.ow.portal.payment.alipay.business.AliPayWapPayProcessor;
import com.trendy.ow.portal.payment.bean.PayInfoBean;
import com.trendy.ow.portal.payment.bean.PayItemBean;
import com.trendy.ow.portal.payment.business.PayInfoBusiness;
import com.trendy.ow.portal.payment.business.PayItemBusiness;
import com.trendy.ow.portal.payment.business.PayProcessor;
import com.trendy.ow.portal.payment.business.PayUtil;
import com.trendy.ow.portal.payment.cache.SysApplicationInfoCache;
import com.trendy.ow.portal.payment.config.PayConfig;

@WebServlet(urlPatterns = "/test/TestCallBack.do")
public class TestCallBack extends HttpServlet {
	private static final long serialVersionUID = -8278987424657845535L;
	private static Logger log = LoggerFactory.getLogger(TestCallBack.class);

	public TestCallBack() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException {
		String callBackType = ParamKit.getParameter(request, "callBackType", "");
		if (callBackType.equalsIgnoreCase("TenPayCallBack")) {
			int appId=ParamKit.getIntParameter(request, "appId",0);
			Map<Integer, String> map=new SysApplicationInfoCache().getSysApplicationInfoCodeAllMap();
			if(!map.containsKey(appId)){
				HttpResponseKit.alertMessage(response, "不存在的appId", HttpResponseKit.ACTION_HISTORY_BACK);
				return;
			}
			int infoId = ParamKit.getIntParameter(request, "infoId", 0);
			PayInfoBusiness infoBusiness=new PayInfoBusiness();
			PayInfoBean info=infoBusiness.getPayInfoByKey(infoId, "FULL", appId);
			if(info==null){
				HttpResponseKit.alertMessage(response, "不存在的infoId", HttpResponseKit.ACTION_HISTORY_BACK);
				return;
			}
			String payResult = ParamKit.getParameter(request, "pay_result", "0");
			String payStatus = PayConfig.PAYS_WAIT_PAY;
			if (payResult.equals(PayConfig.ZERO)) {
				payStatus = PayConfig.PAYS_PAYED;
			} else {
				payStatus = PayConfig.PAYS_CANCELLED;
			}
			PayItemBusiness itemBusiness=new PayItemBusiness();
			PayItemBean itemBean=itemBusiness.getNearestPayItem(info.getPayId());
			int itemId=0;
			if (itemBean==null) {
				itemBean=new PayItemBean();
				itemBean.setPayId(info.getPayId());
				itemBean.setPayStatus(payStatus);
				itemBean.setCompanyId(info.getCompanyId());
				itemBean.setChannelId(info.getChannelId());
				itemBean.setPayAmount(info.getRequestAmount());
				itemBean.setPayTime(new Timestamp(new Date().getTime()));
				itemBean.setCurrency(info.getCurrency());
				itemBean.setIpAddress(info.getIpAddress());
				itemBean.setPayStatus(PayConfig.PAYS_WAIT_PAY);
				itemBean.setStatus(Constants.STATUS_VALID);
				itemId=itemBusiness.addPayItem(itemBean);
			}else {
				itemBean.setPayAmount(info.getRequestAmount());
				itemBean.setPayTime(new Timestamp(new Date().getTime()));
				itemBusiness.updatePayItem(itemBean);
				itemId=itemBean.getPayItemId();
			}
			
			TreeMap<String, String> paramMap = PayUtil.request2TreeMap(request);
			log.info(PayUtil.map2String(paramMap));
			String partner="1214469501";
			String bank_type ="";
			String notify_id = "123456789012345678901234567890";
			String total_fee =String.valueOf(info.getRequestAmount());
			
			String out_trade_no = ""+itemId;
			DateFormat format = new SimpleDateFormat("yyyyMMddhhmm");
			String time_end = format.format(new Date());
			String trade_state = paramMap.get("trade_state");
			String return_url = paramMap.get("return_url");

			String transaction_id = time_end + "000" + out_trade_no;
			TreeMap<String, String> treeMap = new TreeMap<String, String>();
			treeMap.put("trade_state", trade_state);
			treeMap.put("partner", partner);
			treeMap.put("bank_type", bank_type);
			treeMap.put("total_fee", total_fee);
			treeMap.put("notify_id", notify_id);
			treeMap.put("transaction_id", transaction_id);
			treeMap.put("out_trade_no", out_trade_no);
			treeMap.put("time_end", time_end);
			
			
			String sign = MD5.getMD5(PayUtil.map2String(treeMap), "1e61e57bc3f4bdbbdb23feafc6c7d858");
			treeMap.put("sign", sign);
			if (return_url==null) {
				return_url="http://dev.admin.teadmin.net:8080/pay/tenpay/TenpayCallback.do";
			}
			String redictPath=return_url+"?"+ PayUtil.map2String(treeMap);
			log.info(redictPath);
			// response.sendRedirect("http://test03.teadmin.net:3080/pay/TenpayCallback.do?"+PayUtil.buildURLParameters(treeMap));
			response.sendRedirect(redictPath);
			return;
		}else if(callBackType.equalsIgnoreCase("TenPayNotify")){
			int infoId = ParamKit.getIntParameter(request, "infoId", 0);
			int itemId = ParamKit.getIntParameter(request, "itemId", 0);
			String storeCode = ParamKit.getParameter(request, "storeCode", "OchirlyOfficial");
			PayProcessor processor=new AliPayWapPayProcessor();
			PayInfoBusiness infoBusiness=new PayInfoBusiness();
			PayInfoBean infoBean=infoBusiness.getPayInfoByKey(infoId);
			PayItemBusiness itemBusiness=new PayItemBusiness();
			PayItemBean itemBean=itemBusiness.getPayItemByKey(itemId);
			try {
//				String info=processor.clientNotify(infoBean, itemBean,  storeCode);
//				HttpResponseKit.alertMessage(response, "通知成功，业务系统返回："+info, HttpResponseKit.ACTION_NONE);
			} catch (Exception e) {
				HttpResponseKit.alertMessage(response, "通知失败："+e.getMessage(), HttpResponseKit.ACTION_NONE);
			}
			return ;
		}
		PayInfoBusiness infoBusiness=new PayInfoBusiness();
		PayInfoBean infoBean=infoBusiness.getNearestPayInfo();
		request.setAttribute("infoBean", infoBean);
		String path = "/WEB-INF/pages/test/" + "TestCallBack.jsp";
		RequestDispatcher rd = request.getRequestDispatcher(path);
		rd.forward(request, response);
	}

}