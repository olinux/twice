package ch.unifr.pai.twice.comm.serverPush.server;

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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;

import ch.unifr.pai.twice.comm.serverPush.shared.PingEvent;

/**
 * Server-side event processing logic
 * 
 * @author Oliver Schmid
 * 
 */
public class EventProcessing {

	private static Map<AtmosphereResource, Long> lastEventOfAtmosphereResource = Collections.synchronizedMap(new HashMap<AtmosphereResource, Long>());
	private static Map<ServerRemoteEvent, Broadcaster> waitingBlockingEvents = Collections.synchronizedMap(new HashMap<ServerRemoteEvent, Broadcaster>());
	private static Map<String, AtmosphereResource> uuidToResource = Collections.synchronizedMap(new HashMap<String, AtmosphereResource>());

	private static int waitForEventsInMs = 0;

	/**
	 * Track the last event of all connected clients.
	 * 
	 * @param event
	 * @param sender
	 */
	private void updateLastEventOfAtmospherResource(ServerRemoteEvent event, AtmosphereResource sender) {
		if (event.getOriginUUID() != null)
			uuidToResource.put(event.getOriginUUID(), sender);
		Long eventTimestamp = event.getTimestampAsLong();
		if (eventTimestamp != null) {
			Long lastEventTimestamp = lastEventOfAtmosphereResource.get(sender);
			if (lastEventTimestamp == null || lastEventTimestamp.longValue() < eventTimestamp.longValue())
				lastEventOfAtmosphereResource.put(sender, eventTimestamp);
		}
	}

	/**
	 * Process the received message (order and distribute it to the appropriate clients).
	 * 
	 * @param message
	 * @param sender
	 */
	public void processMessage(Object message, final AtmosphereResource sender) {
		System.out.println("Process message: " + message);
		final ServerRemoteEvent e = new ServerRemoteEvent(message);
		updateLastEventOfAtmospherResource(e, sender);
		processBlockingEvents();
		// Do not process ping events
		if (e.getType() == null || !e.getType().equals(PingEvent.class.getName())) {
			long executeInMs = waitForEventsInMs - e.getDelay();
			if (executeInMs < 0)
				executeInMs = 0;
			ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();

			svc.schedule(new Runnable() {

				@Override
				public void run() {
					Broadcaster b = BroadcasterFactory.getDefault().lookup(e.getContext() != null ? e.getContext() : AtmosphereHandler.GLOBALBROADCASTERID);

					if (b != null) {
						if (e.isBlockingEvent()) {
							Set<AtmosphereResource> missingRessources = broadcastBlockingEvents(e, b);
							if (missingRessources != null) {
								waitingBlockingEvents.put(e, b);
								String ping = getJSON();
								for (AtmosphereResource res : missingRessources) {
									b.broadcast(ping, res);
								}
							}
						}
						else {
							if (e.getReceipients() != null && e.getReceipients().size() > 0) {
								Set<AtmosphereResource> receipients = new HashSet<AtmosphereResource>();
								for (String r : e.getReceipients()) {
									AtmosphereResource resource = uuidToResource.get(r);
									if (resource != null)
										receipients.add(resource);
								}
								b.broadcast(e.getMessage(), receipients);
							}
							// Other events are broadcasted to the other clients
							// only, because the sender handles its native event
							// by itself already.
							else {
								b.broadcast(e.getMessage(), excludeSender(b, sender));
							}
						}
					}
				}
			}, executeInMs, TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * Iterate through the blocking events on hold and send those which can be sent.
	 */
	private void processBlockingEvents() {
		Set<ServerRemoteEvent> broadcasted = new HashSet<ServerRemoteEvent>();
		for (ServerRemoteEvent event : waitingBlockingEvents.keySet()) {
			if (broadcastBlockingEvents(event, waitingBlockingEvents.get(event)) == null) {
				broadcasted.add(event);
			}
		}
		for (ServerRemoteEvent r : broadcasted) {
			waitingBlockingEvents.remove(r);
		}
	}

	/**
	 * Check if the blocking events in the queue are ready to be fired (if all clients have confirmed that they do not have conflicting events anymore).
	 * 
	 * @param event
	 * @param b
	 * @return
	 */
	private Set<AtmosphereResource> broadcastBlockingEvents(ServerRemoteEvent event, Broadcaster b) {
		Long eventTimestamp = event.getTimestampAsLong();
		if (eventTimestamp == null) {
			throw new RuntimeException("Event sent without valid timestamp!");
		}
		Set<AtmosphereResource> missingResources = new HashSet<AtmosphereResource>();
		for (AtmosphereResource r : b.getAtmosphereResources()) {
			Long broadcasterLastEvent = lastEventOfAtmosphereResource.get(r);
			if (broadcasterLastEvent == null || eventTimestamp > broadcasterLastEvent) {
				missingResources.add(r);
			}
		}
		if (missingResources.size() == 0)
			b.broadcast(event.getMessage());
		return missingResources.size() > 0 ? missingResources : null;
	}

	/**
	 * Exclude the sender of a message from the broadcasting (this prevents that the sender receives the messages which are originated by himself)
	 * 
	 * @param b
	 * @param sender
	 * @return
	 */
	private Set<AtmosphereResource> excludeSender(Broadcaster b, AtmosphereResource sender) {
		Set<AtmosphereResource> subset = new HashSet<AtmosphereResource>();
		for (AtmosphereResource r : b.getAtmosphereResources()) {
			if (!r.equals(sender))
				subset.add(r);
		}
		return subset;
	}

	/**
	 * @return the JSON string for the PING-event
	 */
	public static String getJSON() {
		return "{\"t\":\"" + new Date().getTime() + "\", \"T\":\"" + PingEvent.class.getName() + "\", \"data\":{\"instanceid\":\"ping\"}}";
	}
}
