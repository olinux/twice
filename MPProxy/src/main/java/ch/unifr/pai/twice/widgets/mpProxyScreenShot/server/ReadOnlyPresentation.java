package ch.unifr.pai.twice.widgets.mpProxyScreenShot.server;
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
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.unifr.pai.twice.widgets.mpproxy.server.SimpleHttpUrlConnectionServletFilter;
import ch.unifr.pai.twice.widgets.mpproxy.shared.Constants;

@WebServlet(urlPatterns = {"/miceScreenShot/manager", "/miceproxy/manager"})
public class ReadOnlyPresentation extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static double scaleFactor = 0.5;
	private static double scrollFactor = 0.65;

	private static class Screenshot {
		private String html;
		private String url;
		private int height;
		private int width;
		private int top;
		private int left;

		public Screenshot(String html, String url, String height, String width, String top, String left) {
			super();
			this.html = html;
			this.url = url;
			this.height = Integer.parseInt(height);
			this.width = Integer.parseInt(width);
			this.top = Integer.parseInt(top);
			this.left = Integer.parseInt(left);
		}
	}

	private static Map<String, Screenshot> uuidToScreenshot = Collections
			.synchronizedMap(new HashMap<String, Screenshot>());

	public static String getScreenshotForUUID(String uuid) {
		if (uuid == null)
			return null;
		Screenshot s = uuidToScreenshot.get(uuid);
		String html = s.html;
		html = html.replace("<body", "<body style=\"overflow:hidden; zoom: "+scaleFactor+"!important; -moz-transform: scale("+scaleFactor+"); -moz-transform-origin: 0 0;\"");
		//html = html.replaceAll("<div id=\"miceNavigation\".*?</div>", "");
		//html = html.replaceAll("<div id=\"contentWrapper\".?>", "");
		//html = html.replace("</div></body>", "</div>");
		html = html.replace("</body>", "<script>document.body.scrollTop="+(s.top*scrollFactor)+";document.body.scrollLeft="+(s.left*scrollFactor)+";</script></body>");
		return s != null ? html : null;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter w = resp.getWriter();
		for(String uuid : uuidToScreenshot.keySet()){	
			Screenshot s = uuidToScreenshot.get(uuid);
			w.print("<div style=\"width:");
			w.print((int)Math.ceil((double)s.width*scaleFactor));
			w.print("px; height:");
			w.print((int)Math.ceil((double)s.height*scaleFactor));
			w.print("px; overflow:hidden; display:inline-block;\">");
			w.print("<iframe ");
			w.print("name=\"");
			w.print(uuid);
			w.print("\" style=\"width:100%; height:100%;\" src=\"");
			w.print(SimpleHttpUrlConnectionServletFilter.getServletPath(req));
			w.print("/" + s.url + "/");
			w.print("miceScreenShot?uuid=" + uuid);
			w.print("\"></iframe></div>");
		}
		w.flush();
		w.close();
		return;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String url = req.getParameter("url");
		Object uuidObj = req.getSession().getAttribute(Constants.uuidCookie);
		String uuid = uuidObj != null ? uuidObj.toString() : null;
		BufferedReader reader = req.getReader();
		ByteArrayOutputStream byteArrOS = new ByteArrayOutputStream();
		int b;
		while ((b = reader.read()) != -1) {
			byteArrOS.write(b);
		}
		byteArrOS.flush();
		byteArrOS.close();
		reader.close();
		String html = new String(byteArrOS.toByteArray(), "UTF-8");
		if (!html.isEmpty() && url != null && !url.isEmpty())
			uuidToScreenshot.put(uuid, new Screenshot(html, url, req.getParameter("height"), req.getParameter("width"), req.getParameter("top"), req.getParameter("left")));
	}

}
