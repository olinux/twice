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
import ch.unifr.pai.twice.utils.device.client.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.Event.Type;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * An extension of the standard GWT event bus which transparently handles remote events and includes security mechanisms such as encryption and signature
 * validation. The implementation also handles the establishment of consistant event orders between the distributed systems.
 * 
 * 
 * 
 * @author Oliver Schmid
 * 
 */
public class ServerPushEventBus extends SimpleEventBus {

	// CONFIGURABLE:

	public ServerPushEventBus() {
		super();
		start();
	}

	/**
	 * The security manager to be applied. The implementation of this manager can easily be replaced through deferred binding.
	 */
	private final TWICESecurityManager security = GWT.create(TWICESecurityManager.class);

	/**
	 * Helper class to handle the remote eventing mechanism
	 */
	private final RemoteEventing remoteEventing = new RemoteEventing() {

		/*
		 * (non-Javadoc)
		 * @see ch.unifr.pai.twice.comm.serverPush.client.RemoteEventing#sendMessage(java.lang.String)
		 */
		@Override
		protected void sendMessage(String message) {
			atmosphereClient.post(message);
		}

		/*
		 * (non-Javadoc)
		 * @see ch.unifr.pai.twice.comm.serverPush.client.RemoteEventing#fireEventLocally(com.google.web.bindery.event.shared.Event)
		 */
		@Override
		protected void fireEventLocally(Event<?> e) {
			ServerPushEventBus.super.fireEvent(e);
		}

		/*
		 * (non-Javadoc)
		 * @see ch.unifr.pai.twice.comm.serverPush.client.RemoteEventing#fireEventFromSourceLocally(com.google.web.bindery.event.shared.Event, java.lang.Object)
		 */
		@Override
		protected void fireEventFromSourceLocally(Event<?> e, Object source) {
			ServerPushEventBus.super.fireEventFromSource(e, source);
		}

		/*
		 * (non-Javadoc)
		 * @see ch.unifr.pai.twice.comm.serverPush.client.RemoteEventing#getSecurityManager()
		 */
		@Override
		protected TWICESecurityManager getSecurityManager() {
			return security;
		}
	};

	/**
	 * The atmosphere client that establishes a bi-directional communication with the server
	 */
	private AtmosphereClient atmosphereClient;

	/**
	 * Fires the event to the remote instances as well
	 * 
	 * @see com.google.web.bindery.event.shared.SimpleEventBus#fireEventFromSource(com.google.web.bindery.event.shared.Event, java.lang.Object)
	 */
	@Override
	public void fireEventFromSource(Event<?> event, Object source) {
		remoteEventing.fireEventFromSource(event, source);
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.web.bindery.event.shared.SimpleEventBus#fireEvent(com.google.web.bindery.event.shared.Event)
	 */
	@Override
	public void fireEvent(Event<?> event) {
		fireEventFromSource(event, null);
	}

	/**
	 * Deserializer for the remote events
	 */
	private final RemoteEventDeserializer deserializer = GWT.create(RemoteEventDeserializer.class);

	/**
	 * Processes the received network message and translates it based on the security mechanism (decryption) and the deserializer
	 * 
	 * @param message
	 */
	private void handleRemoteMessage(Serializable message) {
		String messageStr;
		try {
			messageStr = security.decryptMessage(message.toString());
			RemoteEvent<?> event = deserializer.deserialize(messageStr, security);
			if (event != null) {
				GWT.log("Remote event timestamp: " + event.getTimestamp());
				remoteEventing.fireEventInOrder(event, event.getSourceObject(), false);
			}
		}
		catch (MessagingException e) {
			// Do not interpret errorenous messages by default
			e.printStackTrace();
		}
	}

	/**
	 * Starts the server event bus (establishes the communication and starts to listen for remote events).
	 */
	private void start() {
		if (atmosphereClient != null)
			atmosphereClient.stop();
		AtmosphereGWTSerializer serializer = GWT.create(AtmosphereEventWrapperSerializer.class);
		atmosphereClient = new AtmosphereClient(GWT.getHostPageBaseURL() + Constants.BASEPATH + Constants.ATMOSPHERE, serializer, new AtmosphereListener() {

			@Override
			public void onConnected(int heartbeat, int connectionID) {
				GWT.log("comet.connected [" + heartbeat + ", " + connectionID + "]");
				sendPingEvent();
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
					statuscode = ((StatusCodeException) exception).getStatusCode();
				}
				GWT.log("comet.error [connected=" + connected + "] (" + statuscode + ")" + exception.getMessage());
			}

			@Override
			public void onHeartbeat() {
				GWT.log("comet.heartbeat [" + atmosphereClient.getConnectionID() + "]");
			}

			@Override
			public void onRefresh() {
				GWT.log("comet.refresh [" + atmosphereClient.getConnectionID() + "]");
			}

			@Override
			public void onAfterRefresh() {
				GWT.log("comet.afterrefresh [" + atmosphereClient.getConnectionID() + "]");

			}

			@Override
			public void onMessage(List<?> messages) {
				for (Object message : messages) {
					GWT.log("received message: " + message);
					if (message instanceof AtmosphereEventWrapper)
						handleRemoteMessage(((AtmosphereEventWrapper) message).getSerializedEvent());
					else if (message instanceof Serializable)
						handleRemoteMessage((Serializable) message);
				}
			}
		});
		this.atmosphereClient.start();
		addHandler(PingEvent.TYPE, new PingEventHandler() {

			/**
			 * A special event - if a blocking event arrives at the server, the server triggers a PingEvent to all clients. If the client receives such a ping
			 * event, a response has to be sent immediately. This way the client confirms, that there are no other events in its queue and that the blocking
			 * event can further be processed by the server
			 * 
			 * @param event
			 */
			@Override
			public void onEvent(PingEvent event) {
				sendPingEvent();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.web.bindery.event.shared.SimpleEventBus#addHandlerToSource(com.google.web.bindery.event.shared.Event.Type, java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public <H> HandlerRegistration addHandlerToSource(Type<H> type, Object source, H handler) {
		if (source instanceof RemoteWidget) {
			source = ((RemoteWidget) source).getEventSource();
		}
		return super.addHandlerToSource(type, source, handler);
	}

	public void sendPingEvent() {
		PingEvent e = GWT.create(PingEvent.class);
		e.setTimestamp(remoteEventing.getEstimatedServerTime(null));
		e.setOriginatingDevice(UUID.get());
		// AtmosphereEventWrapper wrapper = new AtmosphereEventWrapper();
		// wrapper.setEvent(e, security);
		GWT.log("Send ping");
		// atmosphereClient.post(wrapper);
		try {
			atmosphereClient.post(e.serialize(security));
		}
		catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
