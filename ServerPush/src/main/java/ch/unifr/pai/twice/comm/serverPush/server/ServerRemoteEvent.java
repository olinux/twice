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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ch.unifr.pai.twice.comm.serverPush.client.RemoteEvent;

/**
 * Server-side representation of a remote event including the required fields for further processing
 * 
 * @author Oliver Schmid
 * 
 */
public class ServerRemoteEvent implements Comparable<ServerRemoteEvent> {

	private final Object message;
	private String timeStamp;
	private String context;
	private String blockingEvent;
	private String type;
	private String originUUID;
	private List<String> receipients;

	public ServerRemoteEvent(Object message) {
		this.message = message;
		extract();
	}

	/**
	 * @return true if it is a blocking event
	 */
	public boolean isBlockingEvent() {
		return blockingEvent != null && blockingEvent.equals("1");
	}

	/**
	 * update the object with the value of the JSON message
	 */
	private void extract() {
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			@Override
			public List<?> creatArrayContainer() {
				return new LinkedList();
			}

			@Override
			public Map createObjectContainer() {
				return new HashMap();
			}

		};

		try {
			Map json = (Map) parser.parse(message != null ? message.toString() : null, containerFactory);
			timeStamp = (String) json.get(RemoteEvent.TIMESTAMP);
			context = (String) json.get(RemoteEvent.CONTEXT);
			blockingEvent = (String) json.get(RemoteEvent.BLOCKINGEVENT);
			type = (String) json.get(RemoteEvent.EVENTTYPEKEY);
			originUUID = (String) json.get(RemoteEvent.ORIGINATINGDEVICE);
			receipients = (List<String>) json.get(RemoteEvent.RECEIPIENTS);

		}
		catch (ParseException pe) {
			System.out.println(pe);
		}
	}

	/**
	 * @return the context identifier of the event
	 */
	public String getContext() {
		return context;
	}

	/**
	 * @return the timestamp of the event as string
	 */
	public String getTimestamp() {
		return timeStamp;
	}

	/**
	 * @return the type of the event as string
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the current delay between now and the timeStamp
	 */
	public long getDelay() {
		if (timeStamp == null)
			return 0l;
		long delay = new Date().getTime() - Long.parseLong(timeStamp);
		if (delay < 0)
			return 0l;
		return delay;

	}

	/**
	 * @return the timestamp as a long or null if it can not be parsed properly
	 */
	public Long getTimestampAsLong() {
		try {
			return getTimestamp() == null ? null : Long.parseLong(getTimestamp());
		}
		catch (NumberFormatException e) {
			// It's not a valid timestamp, therefore it's null.
			return null;
		}
	}

	/**
	 * @return the serialized message
	 */
	public String getSerialized() {
		return message != null ? message.toString() : null;
	}

	/**
	 * @return
	 */
	public Object getMessage() {
		return message;
	}

	@Override
	public int compareTo(ServerRemoteEvent o) {
		return getTimestamp().compareTo(o.getTimestamp());
	}

	/**
	 * @return the identifier of the originating device
	 */
	public String getOriginUUID() {
		return originUUID;
	}

	/**
	 * @return the unique identifiers of the receipients if there are some
	 */
	public List<String> getReceipients() {
		return receipients;
	}
}
