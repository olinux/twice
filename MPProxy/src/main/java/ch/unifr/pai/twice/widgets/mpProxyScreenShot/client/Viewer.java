package ch.unifr.pai.twice.widgets.mpProxyScreenShot.client;
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
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class Viewer implements EntryPoint {

	@Override
	public void onModuleLoad() {
		Timer t = new Timer(){

			@Override
			public void run() {
				try {
					RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, Window.Location.getProtocol()+"//"+Window.Location.getHost()+"/miceScreenShot/manager");
					rb.sendRequest(null, new RequestCallback() {
						
						@Override
						public void onResponseReceived(Request request, Response response) {
							RootPanel.getBodyElement().setInnerHTML(response.getText());
						}
						
						@Override
						public void onError(Request request, Throwable exception) {
						}
					} );
				} catch (RequestException e) {
					e.printStackTrace();
				}
			}};
			t.run();
//		t.scheduleRepeating(1000);
		
	}

}
