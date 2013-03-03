package ch.unifr.pai.twice.widgets.mpproxy.client;

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
import ch.unifr.pai.twice.widgets.mpproxy.shared.Rewriter;
import ch.unifr.pai.twice.widgets.mpproxy.shared.URLParser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

/**
 * Takes a snapshot of the current HTML DOM structure, removes script tags and submits the content to the server in regular intervals
 * 
 * @author Oliver Schmid
 * 
 */
public class ScreenShotDistributor {
	private String lastSent;
	private int lastTop = -1;
	private int lastLeft = -1;
	private final Timer t = new Timer() {

		@Override
		public void run() {
			sendScreenShot();
		}
	};

	/**
	 * Begin to update the server with screenshots
	 */
	public void start() {
		t.scheduleRepeating(1000);
	}

	/**
	 * Stop the updating of the server with screenshots
	 */
	public void stop() {
		t.cancel();
	}

	/**
	 * Send the screenshot to the server
	 */
	public void sendScreenShot() {
		String screen = Document.get().getDocumentElement().getInnerHTML();
		if (!screen.equals(lastSent) || lastLeft != Window.getScrollLeft() || lastTop != Window.getScrollTop()) {
			String url = Window.Location.getHref();
			URLParser p = new URLParser(url, Rewriter.getServletPath(Window.Location.getHref()));
			RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, GWT.getModuleBaseURL() + "manager?url=" + p.getProxyBasePath() + "&width="
					+ Window.getClientWidth() + "&height=" + Window.getClientHeight() + "&top=" + Window.getScrollTop() + "&left=" + Window.getScrollLeft());
			lastSent = screen;
			lastLeft = Window.getScrollLeft();
			lastTop = Window.getScrollTop();
			screen = screen.replace('\n', ' ');
			screen = screen.replaceAll("<body",
					"<body><div class=\"readOnlyView\" style=\"width:" + Window.getClientWidth() + "; height:" + Window.getClientHeight() + ";\"");
			screen = screen.replaceAll("<\\/body>", "</div></body>");
			screen = screen.replaceAll("(<script).*?(\\/script>)", "");
			try {
				rb.sendRequest(screen, new RequestCallback() {

					@Override
					public void onResponseReceived(Request request, Response response) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onError(Request request, Throwable exception) {
						Window.alert("Screenshot sent");
					}
				});
			}
			catch (RequestException e) {
				e.printStackTrace();
			}
		}
	}

}
