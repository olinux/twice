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

package ch.unifr.pai.twice.comm.clientServerTime.client;

import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ClientServerTimeOffset.class, GWT.class })
public class ClientServerTimeOffsetTest {

	Long offset;

	/**
	 * The response from the server giving the current time-stamp
	 */
	@Mock
	Response response;

	@Mock
	RequestBuilder rb;

	@Before
	public void setUp() {
		PowerMockito.mockStatic(GWT.class);
	}

	@Test
	public void testGetServerTimeOffset() throws Exception {
		// given
		AsyncCallback<Long> callback = new AsyncCallback<Long>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Long result) {
				offset = result;
			}
		};

		PowerMockito.spy(ClientServerTimeOffset.class);

		// Set the start-time to 1000 and the time of the received response to 2000 which results in a duration of 1000
		Mockito.when(ClientServerTimeOffset.getCurrentTime()).thenAnswer(new Answer<Long>() {
			boolean startTime = true;

			@Override
			public Long answer(InvocationOnMock invocation) throws Throwable {
				long returnValue;
				if (startTime)
					returnValue = 1000;
				else
					returnValue = 2000;
				startTime = !startTime;
				return returnValue;
			}
		});
		// Return a server-timestamp of 10000
		Mockito.when(response.getText()).thenReturn("10000");

		Mockito.when(rb.sendRequest(Mockito.anyString(), Mockito.any(RequestCallback.class))).thenAnswer(new Answer<Request>() {

			@Override
			public Request answer(InvocationOnMock invocation) throws Throwable {
				RequestCallback callback = (RequestCallback) invocation.getArguments()[1];
				Request req = Mockito.mock(Request.class);
				// Return the response to the callback
				callback.onResponseReceived(req, response);
				return req;
			}
		});

		PowerMockito.whenNew(RequestBuilder.class).withAnyArguments().thenReturn(rb);

		// when
		ClientServerTimeOffset.getServerTimeOffset(callback);

		// then
		assertNotNull(offset);

		// The offset should be 8500 (10000 - startTime - duration)
		Assert.assertEquals(8500L, offset.longValue());

	}
}
