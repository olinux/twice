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
import ch.unifr.pai.twice.authentication.client.security.MessagingException;
import ch.unifr.pai.twice.authentication.client.security.TWICESecurityManager;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public abstract class RemoteEvent<H extends RemoteEventHandler<?>> extends GwtEvent<H>{

	public final static String BLOCKINGEVENT = "b";
	public final static String EVENTTYPEKEY = "T";
	public final static String UUID = "i";
	public final static String TIMESTAMP = "t";
	public final static String CONTEXT = "c";
	public final static String SOURCE = "s";
	public final static String ORIGINATINGDEVICE = "o";
	public final static String RECEIPIENTS = "r";
	public final static String USERNAME = "u";
	
	protected JSONObject root = new JSONObject();
	protected JSONObject json = new JSONObject();
	protected JSONArray receipients;
	
	//No need to be serialized
	private boolean fireLocally = true;
	
	protected abstract String getEventType();
	
	public void setContext(String contextUUID){
		root.put(CONTEXT, new JSONString(contextUUID));
	}
	
	public void setOriginatingDevice(String deviceUUID){
		root.put(ORIGINATINGDEVICE, new JSONString(deviceUUID));
	}
	
	public void setReceipients(String... receipients){
		this.receipients = new JSONArray();
		int i=0;
		for(String s : receipients){
			this.receipients.set(i++, new JSONString(s));
		}		
	}
	
	public String getOriginatingDevice(){
		JSONValue value = root.get(ORIGINATINGDEVICE);
		if(value!=null && value.isString()!=null)
			return value.isString().stringValue();
		return null;
	}
	
	public void setSourceObject(String source){
		root.put(SOURCE, new JSONString(source));
	}
	
	public String getSourceObject(){
		JSONValue value = root.get(SOURCE);
		if(value!=null && value.isString()!=null)
			return value.isString().stringValue();
		return null;
	}
	
	public String getContext(){
		JSONValue value = root.get(CONTEXT);
		if(value!=null && value.isString()!=null)
			return value.isString().stringValue();
		return null;
	}
	
	public void setTimestamp(long timestamp){
		root.put(TIMESTAMP, new JSONString(String.valueOf(timestamp)));
	}
	
	public Long getTimestamp(){
		JSONValue value = root.get(TIMESTAMP);
		if(value!=null && value.isString()!=null)
			return Long.parseLong(value.isString().stringValue());
		return null;
	}
	
	public String serialize(TWICESecurityManager securityManager) throws MessagingException{
		root.put(EVENTTYPEKEY, new JSONString(getEventType()));
		root.put(UUID, new JSONString(root.get(UUID)!=null ? root.get(UUID).toString() : ch.unifr.pai.twice.utils.device.client.UUID.createNew()));
		root.put("data", new JSONString(securityManager.encryptMessage(json.toString())));
		if(isBlocking())
			root.put(BLOCKINGEVENT, new JSONString("1"));
		if(receipients!=null)
			root.put(RECEIPIENTS, receipients);
		return root.toString();
	}
	
	public boolean isBlocking(){
		return true;
	}

	public boolean isFireLocally() {
		return fireLocally;
	}

	public void setFireLocally(boolean fireLocally) {
		this.fireLocally = fireLocally;
	}

	public RemoteEvent<H> deserialize(String string, TWICESecurityManager securityManager) throws MessagingException{
		root = (JSONObject) JSONParser.parseStrict(string);
		JSONString data = (JSONString) root.get("data");
		json = (JSONObject) JSONParser.parseStrict(securityManager.decryptMessage(data.stringValue()));
		return this;
	}
	
	public void setProperty(String key, String value){
		json.put(key, new JSONString(value));
	}
	
	public String getProperty(String key){
		JSONValue v = json.get(key);
		if(v!=null && v.isString()!=null)
			return v.isString().stringValue();
		return null;
	}

	public void setUserName(String value){
		setProperty(USERNAME, value);
	}
	
	public String getUserName(){
		return getProperty(USERNAME);
	}
		
}
