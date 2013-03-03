package ch.unifr.pai.twice.utils.qrcode;

/*
 * Copyright 2013 Oliver Schmid
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * A servlet that dynamically creates visual QR tags based on the URL passed as a GET parameter
 * 
 * @author Oliver Schmid
 * 
 */
@WebServlet("/utils/qr")
public class QRCodeGeneratorServlet extends HttpServlet {

	/**
	 * Returns in its HTTP-Response a QR tag which redirects a client to the URL defined in the "url" GET-parameter
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (req.getParameter("url") != null) {
			String s = "http://" + req.getLocalAddr() + ":" + req.getServerPort() + "/" + req.getParameter("url");
			QRCodeWriter w = new QRCodeWriter();
			try {
				MatrixToImageWriter.writeToStream(w.encode(s, BarcodeFormat.QR_CODE, 300, 200), "png", resp.getOutputStream());
			}
			catch (WriterException e) {
				e.printStackTrace();
			}
		}
		// else{
		// String s = req.getLocalAddr()+":"+req.getServerPort();
		// resp.getWriter().write(s);
		// // Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		// // if(interfaces!=null){
		// // while(interfaces.hasMoreElements()){
		// // NetworkInterface i = interfaces.nextElement();
		// // Enumeration<InetAddress> addresses = i.getInetAddresses();
		// // if(addresses!=null){
		// // while(addresses.hasMoreElements()){
		// // InetAddress inet = addresses.nextElement();
		// // inet.get
		// //
		// // }
		// // }
		// // }
		// // }
		// // for(NetworkInterface i : NetworkI
		// //
		// }
	}
}
