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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import ch.unifr.pai.twice.authentication.client.Authentication;
import ch.unifr.pai.twice.authentication.client.security.MessagingException;
import ch.unifr.pai.twice.authentication.client.security.TWICESecurityManager;
import ch.unifr.pai.twice.comm.clientServerTime.client.ClientServerTimeOffset;
import ch.unifr.pai.twice.utils.device.client.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.Event;

/**
 * The fundamental functionality of the remote eventing mechanism
 * 
 * @author Oliver Schmid
 * 
 */
public abstract class RemoteEventing {

	/**
	 * Delay the of the event delivery in milliseconds. The bigger the value the less probable is a conflict between events, but the bigger is the latency
	 * between firing and executing the event.
	 */
	private int eventDeliveryDelay;

	/**
	 * Non-blocking events are executed locally (the execution doesn't have to wait for the event to come back from the server. Since this evicts the network
	 * latency, those events will be executed very fast. If they are in conflict with remote events, they will regularly be rolled back. To prevent this, a
	 * delay can be introduced to reduce that effect. If this value is smaller than the eventDeliveryDelay, the latter will be applied.
	 */
	private int localEventDeliveryDelay;

	/**
	 * The estimated server time offset - used to normalize the event time stamp to establish a common time base for all leaf nodes
	 */
	private Long serverTimeOffset;

	private final Stack<UndoableRemoteEvent<?>> eventHistory = new Stack<UndoableRemoteEvent<?>>();
	private final Map<String, Long> discardedEventsHistory = new HashMap<String, Long>();

	/**
	 * Returns the estimated server time for the given timestamp of the local system clock
	 * 
	 * @param timestamp
	 *            - the local system clock timestamp or null for "now"
	 * @return the corresponding, estimated timestamp of the server side clock
	 */
	public long getEstimatedServerTime(Long timestamp) {
		if (timestamp == null)
			timestamp = new Date().getTime();
		if (serverTimeOffset != null) {
			return timestamp + serverTimeOffset;
		}
		return timestamp;
	}

	public RemoteEventing() {
		updateServerTimeOffset();
	}

	/**
	 * Calculate the server time offset
	 */
	private void updateServerTimeOffset() {
		ClientServerTimeOffset.getServerTimeOffset(new AsyncCallback<Long>() {

			@Override
			public void onSuccess(Long result) {
				serverTimeOffset = result;
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Was not able to synchronize the clock with the server");
			}
		});
	}

	/**
	 * Fires the event to the distributed eventing mechanism if the event is an instance of {@link RemoteEvent} This includes the serialization and the
	 * encryption of the message as well as the enrichment with data such as origin client identifier and user name
	 * 
	 * @param event
	 * @param source
	 */
	public void fireEventFromSource(Event<?> event, Object source) {
		if (source instanceof RemoteWidget) {
			source = ((RemoteWidget) source).getEventSource();
		}
		if (event instanceof RemoteEvent) {
			RemoteEvent<?> remoteEvent = ((RemoteEvent<?>) event);
			remoteEvent.setTimestamp(getEstimatedServerTime(((RemoteEvent<?>) event).getTimestamp()));
			remoteEvent.setOriginatingDevice(UUID.get());
			remoteEvent.setUserName(Authentication.getUserName());
			if (source != null)
				remoteEvent.setSourceObject(source.toString());
			GWT.log("Send message");
			try {
				sendMessage(remoteEvent.serialize(getSecurityManager()));
			}
			catch (MessagingException e) {
				e.printStackTrace();
			}
			// atmosphereClient.broadcast(remoteEvent.serialize());
			if (!remoteEvent.isBlocking())
				fireEventInOrder(remoteEvent, source, true);
		}
		else {
			fireEventInOrder(event, source, true);
		}
	}

	/**
	 * @return the {@link TWICESecurityManager} to be used for the decryption and encryption of messages
	 */
	protected abstract TWICESecurityManager getSecurityManager();

	/**
	 * Send the message to the distributed eventing mechanism
	 * 
	 * @param message
	 */
	protected abstract void sendMessage(String message);

	/**
	 * fire the event within the local event bus
	 * 
	 * @param e
	 */
	protected abstract void fireEventLocally(Event<?> e);

	/**
	 * fire the event within the local event bus with the given source
	 * 
	 * @param e
	 * @param source
	 */
	protected abstract void fireEventFromSourceLocally(Event<?> e, Object source);

	/**
	 * Fire the event in their correct order of appearance. If necessary also handle conflicts
	 * 
	 * @param event
	 * @param source
	 * @param localEvent
	 */
	public void fireEventInOrder(Event<?> event, Object source, boolean localEvent) {
		if (event instanceof RemoteEvent) {
			RemoteEvent<?> remoteEvent = (RemoteEvent<?>) event;
			if (eventHistory != null) {
				Long timestamp = remoteEvent.getTimestamp();
				Stack<UndoableRemoteEvent<?>> undidEvents = new Stack<UndoableRemoteEvent<?>>();
				UndoableRemoteEvent<?> e;
				if (timestamp != null) {
					// Undo all events that should have been executed after this
					// event
					while (eventHistory.size() > 0) {
						e = eventHistory.pop();
						if (e.getTimestamp() == null || e.getTimestamp().longValue() <= timestamp.longValue()) {
							eventHistory.push(e);
							break;
						}
						else {
							e.setUndo(true);
							// Fire events directly - there is no need for an
							// additional delay
							fireEventLocally(e);
							e.setUndo(false);
							undidEvents.push(e);
						}
					}

					// Fire this event
					if (event instanceof DiscardingRemoteEvent) {
						// If the event is a discarding event, it has only to be
						// fired if no later event with the same identifier has
						// been fired already
						DiscardingRemoteEvent<?> discardingRemoteEvent = ((DiscardingRemoteEvent<?>) event);
						if (discardingRemoteEvent.getInstanceId() != null) {
							Long lastTimeStamp = discardedEventsHistory.get(discardingRemoteEvent.getInstanceId());
							Long eventTimeStamp = discardingRemoteEvent.getTimestamp();
							if (lastTimeStamp == null || (eventTimeStamp != null && lastTimeStamp.longValue() < eventTimeStamp.longValue())) {
								discardedEventsHistory.put(discardingRemoteEvent.getInstanceId(), discardingRemoteEvent.getTimestamp());
								fireEventToTheLocalBusWithDelay(event, source, localEvent);
							}
						}
						else {
							// If the discarding event has no identifier, it
							// will be fired.
							fireEventToTheLocalBusWithDelay(event, source, localEvent);
						}
					}
					else {
						fireEventToTheLocalBusWithDelay(event, source, localEvent);
						// If the event is blocking, it is ensured that there
						// will not be another event with a timestamp before the
						// one of the remote event anymore (everything else
						// would be an error). Therefore, the undoable events
						// until that point in time can be skipped.
						if (remoteEvent.isBlocking())
							eventHistory.clear();
					}

					// Re-fire all events that have previously been undone
					if (event instanceof UndoableRemoteEvent)
						eventHistory.push((UndoableRemoteEvent<?>) event);
					for (UndoableRemoteEvent<?> undidEvent : undidEvents) {
						fireEventToTheLocalBusWithDelay(undidEvent, source, localEvent);
						eventHistory.push(undidEvent);
					}

				}
			}
		}
	}

	/**
	 * Fires the event to the local event bus after a specific delay
	 * 
	 * @param event
	 * @param source
	 * @param localEvent
	 */
	private void fireEventToTheLocalBusWithDelay(final Event<?> event, final Object source, boolean localEvent) {
		if (event instanceof RemoteEvent) {
			if (((RemoteEvent<?>) event).isFireLocally()) {
				int delay = localEvent ? Math.max(localEventDeliveryDelay, eventDeliveryDelay) : eventDeliveryDelay;
				if (delay == 0) {
					if (source == null)
						fireEventLocally(event);
					else
						fireEventFromSourceLocally(event, source);
				}
				else {
					Timer t = new Timer() {

						@Override
						public void run() {
							if (source == null)
								fireEventLocally(event);
							else
								fireEventFromSourceLocally(event, source);
						}
					};
					t.schedule(delay);
				}
			}

		}

	}
}
