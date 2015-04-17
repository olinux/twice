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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.atmosphere.gwt.client.AtmosphereClient;
import org.atmosphere.gwt.client.AtmosphereGWTSerializer;
import org.atmosphere.gwt.client.AtmosphereListener;

import ch.unifr.pai.twice.comm.serverPush.shared.Constants;
import ch.unifr.pai.twice.comm.serverPush.shared.MiceEvent;
import ch.unifr.pai.twice.comm.serverPush.shared.MiceEvent.MiceEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.StatusCodeException;

/**
 * This class is only here until all dependent projects are updated and it will be removed afterwards. Please use {@link ServerPushEventBus} instead.
 * 
 * @author Oliver Schmid
 * 
 */
@Deprecated
public class ServerPush {
	private final AtmosphereClient atmosphereClient;
	private static ServerPush client;
	private static Map<Enum<?>, Set<MiceEventHandler<?, ?>>> handlers = new HashMap<Enum<?>, Set<MiceEventHandler<?, ?>>>();
	private boolean initialized = false;

	private final Timer connectionTimeout;

	private ServerPush(AtmosphereGWTSerializer serializer, final Command onConnected) {
		this.connectionTimeout = new Timer() {

			@Override
			public void run() {
				if (!initialized) {
					onConnected.execute();
					initialized = true;
				}
			}
		};
		connectionTimeout.schedule(4000);
		this.atmosphereClient = new AtmosphereClient(GWT.getHostPageBaseURL() + Constants.BASEPATH + Constants.ATMOSPHERE, serializer,
				new AtmosphereListener() {

					@Override
					public void onConnected(int heartbeat, int connectionID) {
						GWT.log("comet.connected [" + heartbeat + ", " + connectionID + "]");
						connectionTimeout.cancel();
						if (!initialized) {
							onConnected.execute();
							initialized = true;
						}
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
							if (message instanceof Serializable)
								handleMessage((Serializable) message);
						}
					}
				});
		this.atmosphereClient.start();
	}

	private void handleMessage(Serializable message) {
		// boolean ownEvent = (message instanceof BasicMiceEvent && ((BasicMiceEvent)message).getOriginatorUUID().equals(clientUUID));
		if (message instanceof MiceEvent) {
			Set<MiceEventHandler<?, ?>> set = handlers.get(((MiceEvent) message).getType());
			if (set != null) {
				for (MiceEventHandler<?, ?> handler : set) {
					handler.processEvent(message);
				}
			}

		}

	}

	public static void start(AtmosphereGWTSerializer serializer, Command onConnected) {
		if (client == null)
			client = new ServerPush(serializer, onConnected);
	}

	public static void stop() {
		client.atmosphereClient.stop();
		client = null;
	}

	// private static ServerPush getInstance(){
	// if(client==null){
	// throw new RuntimeException("You made use of the ServerToClient before you started it. Please invoke ServerToClient.start first!");
	// }
	// return client;
	// }
	//
	public static void send(Serializable message) {
		client.atmosphereClient.post(message);
	}

	//
	//
	// public static void send(Serializable message, AsyncCallback<Void> callback){
	// // if(message instanceof MiceEvent){
	// // ((BasicMiceEvent)message).setOriginatorUUID(clientUUID);
	// // }
	// getInstance().atmosphereClient.post(message, callback);
	// }

	public static void addEventHandler(MiceEventHandler<?, ?> eventHandler) {
		Set<MiceEventHandler<?, ?>> set = handlers.get(eventHandler.getType());
		if (set == null) {
			set = new HashSet<MiceEventHandler<?, ?>>();
			handlers.put(eventHandler.getType(), set);
		}
		set.add(eventHandler);
	}

	public static void removeEventHandler(MiceEventHandler<?, ?> eventHandler) {
		Set<MiceEventHandler<?, ?>> set = handlers.get(eventHandler.getType());
		if (set != null)
			set.remove(eventHandler);
	}
}
