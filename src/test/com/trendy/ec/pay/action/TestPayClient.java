package test.com.trendy.ec.pay.action;

import java.io.IOException;
import java.security.PublicKey;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.crypto.RSA;
import com.trendy.fw.common.util.BeanKit;
import com.trendy.fw.common.web.ParamKit;
import com.trendy.fw.tools.criphertext.CiphertextKit;
import com.trendy.ow.portal.payment.bean.PayRequestBean;

/**
 * Servlet implements class MaterialInfoManage
 * 
 * @author guilin.liao
 * 
 */
@WebServlet(urlPatterns = "/test/TestPayClient.do")
public class TestPayClient extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7698735162351888123L;
	private static Logger log = LoggerFactory.getLogger(TestPayClient.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TestPayClient() {
		// TODO Auto-generated constructor stub
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("x---i am come----------------");
		int appId = ParamKit.getIntParameter(request, "appId", 0);
		if (appId > 0) {
			try {
				PayRequestBean bean = BeanKit.request2Bean(request, PayRequestBean.class);
//				String urlParam = PayUtil.beanToUrlParam(bean);
//				// String sign=PayUtil.md5Sign(urlParam, "app1_md5_public_key");
//				String sign = MD5.getMD5(urlParam, "app1_md5_public_key");
//				bean.setSign(sign);
//				String beanJson = JsonKit.toJson(bean);
//				String publicRSAKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC1Osy5GhOzB+ugDYokFfYPwXQxPljJxxasQCt5EU7bHvRp8leymadqF4CqhiLsq9O4wlZ5T71vKFRX/Fjo4C2nIir2Ha8iWVFllHtPqz2tGZGhlUPwfHzM3AJ5djcGdy/MTF/OynHHm1Xyq/03xSYCA78JreXYV6TCQ1IHqpAmrwIDAQAB";
//				// String miwen=PayUtil.encryptByPrivateKey(beanJson,
//				// privateRSAKey);
//				String data = BASE64.encodeBase64(RSA.encryptByPublicKey(beanJson.getBytes(), publicRSAKey));
				String md5="s3fs3jha";
				String publicRSAKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC1Osy5GhOzB+ugDYokFfYPwXQxPljJxxasQCt5EU7bHvRp8leymadqF4CqhiLsq9O4wlZ5T71vKFRX/Fjo4C2nIir2Ha8iWVFllHtPqz2tGZGhlUPwfHzM3AJ5djcGdy/MTF/OynHHm1Xyq/03xSYCA78JreXYV6TCQ1IHqpAmrwIDAQAB";
				PublicKey key = RSA.getPublicKey(publicRSAKey);
				String data = CiphertextKit.encrypt(bean, key, md5);
				request.setAttribute("appId", appId);
				request.setAttribute("data", data);
				int option = ParamKit.getIntParameter(request, "option", 0);
				String path = "/WEB-INF/pages/test/" + "clientPayTest.jsp";
				if(option==1){
					path = "/WEB-INF/pages/test/" + "clientChooseTest.jsp";
				}
				RequestDispatcher rd = request.getRequestDispatcher(path);
				rd.forward(request, response);
				// response.getWriter().write(JsonKit.toJson(requestBean));
				// response.sendRedirect("PayChannelList.do");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			String path = "/WEB-INF/pages/test/" + "clientChooseTest.jsp";
			RequestDispatcher rd = request.getRequestDispatcher(path);
			rd.forward(request, response);
		}
		return;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);

	}
}
