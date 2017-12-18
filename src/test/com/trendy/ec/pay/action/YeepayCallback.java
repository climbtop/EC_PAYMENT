package test.com.trendy.ec.pay.action;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.crypto.BASE64;
import com.trendy.fw.common.crypto.RSA;
import com.trendy.fw.common.web.ParamKit;

/**
 * Servlet implements class MaterialInfoManage
 * 
 * @author guilin.liao
 * 
 */
@WebServlet(urlPatterns = "/pay/YeepayCallback.do")
public class YeepayCallback extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7754986918766394659L;
	private static Logger log = LoggerFactory
			.getLogger(YeepayCallback.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public YeepayCallback() {
		// TODO Auto-generated constructor stub
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
		return;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println(ParamKit.getParameter(request, "test"));
		String xx=request.getQueryString();
		response.getWriter().print("success");
		response.getWriter().flush();
		response.getWriter().close();
		String miwen=getRequestMsg(request);
		System.out.println(miwen);
		//私钥解密看看
		String privateRSAKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALU6zLkaE7MH66AN"+
				"iiQV9g/BdDE+WMnHFqxAK3kRTtse9GnyV7KZp2oXgKqGIuyr07jCVnlPvW8oVFf8"+
				"WOjgLaciKvYdryJZUWWUe0+rPa0ZkaGVQ/B8fMzcAnl2NwZ3L8xMX87KccebVfKr"+
				"/TfFJgIDvwmt5dhXpMJDUgeqkCavAgMBAAECgYBwdOMyfT76GhEZSS2ORN5iWn3a"+
				"TMDVvKeSDWOshZP0Hpo13/6RQg2DpL/fkMq9J8aCYH0+W7/F6TWlP16AaxOIFMjd"+
				"EpMd1wzB7sJuY56poLOp59VRAFnL6OatazNDGEP3egfr81qCQoGlK0BVB7bYq5O7"+
				"nLGjMZEJBqEFeAUCyQJBAO7fp+4DINP6jcMfVBd9cc9ab1gUekiuKRF4HOfzhEBo"+
				"WCjl7A6HlMSgOfYmMQnUFwxZVObSDlql43Zm8QiUvvsCQQDCOSHviXTJwZNMqGQP"+
				"zoarrGZLuB8gFG/9xAc/CVWcQS6qqM169BplH3etUDXMo24FHJNayeLVXe8jX7wV"+
				"s1jdAkEAho3NVjDE6SMVf3fCMoki9p4GYiMGzrHryD9UaQOu12jvX/pDgdu1XRy0"+
				"CYdx0At8ACTBwlNIap9PBX7u/tpqyQJAStO6GFAr14MlndYOXuyhg8hyzN9N1o0p"+
				"LGp2pDmTaxTNxuAr8h/Tf3wlHneVkpawT3XX65V2N9/tvwImM3IaXQJBANWECFTB"+
				"/uIATjYejQ99Y2boBHnqeCIdkmBb15I2UFGqWxthgtdqZNR+6mCkLnSEaqT10EXG"+
				"Udt25aD6klorfEA=";
		try {
			
			System.out.println(RSA.decryptByPrivateKey(BASE64.decodeBase64(miwen), privateRSAKey));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	private String getRequestMsg(HttpServletRequest request){
		StringBuilder sb=new StringBuilder();
		InputStream is=null;
		try {
			is = request.getInputStream();
			byte[] buf = new byte[1024];  
			int x=0;
			while((x=is.read(buf))!=-1){
				String temp=new String(buf,0,x);
				sb.append(temp);
			}
			is.close();
		} catch (IOException e) {
			log.error("支付系统解析request失败",e);
			return null;
		}
		return sb.toString();
	}

}
