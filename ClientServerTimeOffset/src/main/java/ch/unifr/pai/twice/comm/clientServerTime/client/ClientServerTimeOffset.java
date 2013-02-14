package ch.unifr.pai.twice.comm.clientServerTime.client;

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

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ClientServerTimeOffset {
	
	
	public static void getServerTimeOffset(final AsyncCallback<Long> callback){
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, GWT.getHostPageBaseURL()+"ping");
		final long startTime = new Date().getTime();
		try {
			rb.sendRequest(null, new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					long endTime = new Date().getTime();
					long duration = endTime - startTime;
					String result = response.getText();
					if(result!=null){
						long serverTime = Long.parseLong(result);
						//We assume that the upload and download for a very small request are the same (50% of the request duration for upload, 50% for download)
						long difference = serverTime - (endTime - (duration/2));
						callback.onSuccess(difference);
					}
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					callback.onFailure(exception);
					
				}
			});
		} catch (RequestException e) {
			callback.onFailure(e);
		}		
	}
	
}
