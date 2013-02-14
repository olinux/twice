package ch.unifr.pai.twice.comm.serverPush.client;
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
import java.io.Serializable;
import java.util.List;

import org.atmosphere.gwt.client.AtmosphereClient;
import org.atmosphere.gwt.client.AtmosphereGWTSerializer;
import org.atmosphere.gwt.client.AtmosphereListener;

import ch.unifr.pai.twice.authentication.client.security.MessagingException;
import ch.unifr.pai.twice.authentication.client.security.TWICESecurityManager;
import ch.unifr.pai.twice.comm.serverPush.shared.Constants;
import ch.unifr.pai.twice.comm.serverPush.shared.PingEvent;
import ch.unifr.pai.twice.comm.serverPush.shared.PingEvent.PingEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.Event.Type;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class ServerPushEventBus extends SimpleEventBus {

	// CONFIGURABLE:

	public ServerPushEventBus() {
		super();
		start();
	}

	private TWICESecurityManager security = GWT
			.create(TWICESecurityManager.class);

	private RemoteEventing remoteEventing = new RemoteEventing() {

		@Override
		protected void sendMessage(String message) {
			atmosphereClient.post(message);
		}

		@Override
		protected void fireEventLocally(Event<?> e) {
			ServerPushEventBus.super.fireEvent(e);
		}
		// TODO Auto-generated catch block
		@Override
		protected void fireEventFromSourceLocally(Event<?> e, Object source) {
			ServerPushEventBus.super.fireEventFromSource(e, source);
		}

		@Override
		protected TWICESecurityManager getSecurityManager() {
			return security;
		}
	};

	private AtmosphereClient atmosphereClient;

	@Override
	public void fireEventFromSource(Event<?> event, Object source) {
		remoteEventing.fireEventFromSource(event, source);
	}

	@Override
	public void fireEvent(Event<?> event) {
		fireEventFromSource(event, null);
	}

	private RemoteEventDeserializer deserializer = GWT
			.create(RemoteEventDeserializer.class);

	private void handleRemoteMessage(Serializable message) {
		String messageStr;
		try {
			messageStr = security.decryptMessage(message.toString());
			RemoteEvent<?> event = deserializer.deserialize(messageStr, security);
			if (event != null) {
				GWT.log("Remote event timestamp: " + event.getTimestamp());
				remoteEventing.fireEventInOrder(event, event.getSourceObject(),
						false);
			}
		} catch (MessagingException e) {
			//Do not interpret errorenous messages by default
			e.printStackTrace();
		}
	}

	private void start() {
		if (atmosphereClient != null)
			atmosphereClient.stop();
		AtmosphereGWTSerializer serializer = GWT
				.create(AtmosphereEventWrapperSerializer.class);
		atmosphereClient = new AtmosphereClient(GWT.getHostPageBaseURL()
				+ Constants.BASEPATH + Constants.ATMOSPHERE, serializer,
				new AtmosphereListener() {

					@Override
					public void onConnected(int heartbeat, int connectionID) {
						GWT.log("comet.connected [" + heartbeat + ", "
								+ connectionID + "]");
					}

					@Override
					public void onBeforeDisconnected() {
						GWT.log("comet.beforeDisconnected");
					}

					@Override
					public void onDisconnected() {
						GWT.log("comet.disconnected");
					}

					@Override
					public void onError(Throwable exception, boolean connected) {
						int statuscode = -1;
						if (exception instanceof StatusCodeException) {
							statuscode = ((StatusCodeException) exception)
									.getStatusCode();
						}
						GWT.log("comet.error [connected=" + connected + "] ("
								+ statuscode + ")" + exception.getMessage());
					}

					@Override
					public void onHeartbeat() {
						GWT.log("comet.heartbeat ["
								+ atmosphereClient.getConnectionID() + "]");
					}

					@Override
					public void onRefresh() {
						GWT.log("comet.refresh ["
								+ atmosphereClient.getConnectionID() + "]");
					}

					@Override
					public void onAfterRefresh() {
						GWT.log("comet.afterrefresh ["
								+ atmosphereClient.getConnectionID() + "]");

					}

					@Override
					public void onMessage(List<?> messages) {
						for (Object message : messages) {
							GWT.log("received message: " + message);
							if (message instanceof AtmosphereEventWrapper)
								handleRemoteMessage(((AtmosphereEventWrapper) message)
										.getSerializedEvent());
							else if (message instanceof Serializable)
								handleRemoteMessage((Serializable) message);
						}
					}
				});
		this.atmosphereClient.start();
		addHandler(PingEvent.TYPE, new PingEventHandler() {

			@Override
			public void onEvent(PingEvent event) {
				PingEvent e = GWT.create(PingEvent.class);
				e.setTimestamp(remoteEventing.getEstimatedServerTime(null));
				AtmosphereEventWrapper wrapper = new AtmosphereEventWrapper();
				wrapper.setEvent(e, security);
				GWT.log("Send ping");
				atmosphereClient.post(wrapper);
			}
		});
	}

	@Override
	public <H> HandlerRegistration addHandlerToSource(Type<H> type,
			Object source, H handler) {
		if (source instanceof RemoteWidget) {
			source = ((RemoteWidget) source).getEventSource();
		}
		return super.addHandlerToSource(type, source, handler);
	}
}
