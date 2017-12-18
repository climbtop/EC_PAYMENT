package com.trendy.ow.portal.payment.weixin.action;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trendy.fw.common.util.StringKit;
import com.trendy.fw.common.web.ParamKit;
import com.trendy.ow.portal.payment.weixin.business.WeixinScanCodeProcessor;
import com.trendy.ow.portal.payment.weixin.config.WeixinConfig;

@WebServlet(urlPatterns = "/weixin/WeixinCreatScanCode.do")
public class WeixinCreatScanCode extends HttpServlet {
	private static final long serialVersionUID = 8680348107146677258L;
	private static Logger log = LoggerFactory.getLogger(WeixinCreatScanCode.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WeixinScanCodeProcessor processor = new WeixinScanCodeProcessor();
		try {
			String codeUrl = ParamKit.getParameter(request, "codeUrl", "");
			log.info("codeUrl:"+codeUrl);
			int width=ParamKit.getIntParameter(request, "w", WeixinConfig.SCAN_CODE_IMG_WIDTH);
			if (width>WeixinConfig.SCAN_CODE_IMG_WIDTH||width<=0) {
				width=WeixinConfig.SCAN_CODE_IMG_WIDTH;
			}
			int height=ParamKit.getIntParameter(request, "h", WeixinConfig.SCAN_CODE_IMG_HEIGHT);
			if (height>WeixinConfig.SCAN_CODE_IMG_HEIGHT||height<=0) {
				height=WeixinConfig.SCAN_CODE_IMG_HEIGHT;
			}
			if (StringKit.isValid(codeUrl)) {
				BufferedImage image = processor.createScanCodeImage(codeUrl,width,height);
				response.setContentType("image/gif"); // 设置返回的文件类型
				OutputStream os = response.getOutputStream();
				ImageIO.write(image, "gif", os);
				os.flush();
			}else {
				log.info("codeUrl is invaid");
			}
		} catch (Exception e) {
			log.error("WeixinPay_ScanCode 处理异常:" + e.getMessage(), e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

}
