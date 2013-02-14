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

public class ServerRemoteEvent implements Comparable<ServerRemoteEvent>{

	private Object message;
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
	
	public boolean isBlockingEvent(){
		return blockingEvent!=null && blockingEvent.equals("1");
	}

	private void extract() {
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			public List<?> creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new HashMap();
			}

		};

		try {
			Map json = (Map) parser.parse(message!=null ? message.toString() : null, containerFactory);
			timeStamp = (String) json.get(RemoteEvent.TIMESTAMP);
			context = (String)json.get(RemoteEvent.CONTEXT);
			blockingEvent = (String)json.get(RemoteEvent.BLOCKINGEVENT);
			type = (String)json.get(RemoteEvent.EVENTTYPEKEY);
			originUUID = (String)json.get(RemoteEvent.ORIGINATINGDEVICE);
			receipients = (List<String>) json.get(RemoteEvent.RECEIPIENTS);
			
		} catch (ParseException pe) {
			System.out.println(pe);
		}
	}
	
	public String getContext(){
		return context;
	}
	
	public String getTimestamp(){
		return timeStamp;
	}
	
	public String getType(){
		return type;
	}
	
	public long getDelay(){
		if(timeStamp==null)
			return 0l;
		long delay = new Date().getTime() - Long.parseLong(timeStamp);
		if(delay<0)
			return 0l;
		return delay;
		
	}
	
	
	public Long getTimestampAsLong(){
		try {
			return getTimestamp()==null ? null : Long.parseLong(getTimestamp());
		} catch (NumberFormatException e) {
			//It's not a valid timestamp, therefore it's null.
			return null;
		}
	}

	public String getSerialized() {
		return message!=null ? message.toString() : null;
	}
	
	public Object getMessage(){
		return message;
	}

	@Override
	public int compareTo(ServerRemoteEvent o) {
		return getTimestamp().compareTo(o.getTimestamp());
	}
	
	public String getOriginUUID(){
		return originUUID;
	}
	
	public List<String> getReceipients(){
		return receipients;
	}
}
