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

/**
 * The base of a remote event including the event message header
 * 
 * @author Oliver Schmid
 * 
 * @param <H>
 */
public abstract class RemoteEvent<H extends RemoteEventHandler<?>> extends GwtEvent<H> {

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

	// No need to be serialized
	private boolean fireLocally = true;

	protected abstract String getEventType();

	/**
	 * @param contextUUID
	 *            - a unique identifier in which context the event has been fired (e.g. the identifier of a specific mindmap session)
	 */
	public void setContext(String contextUUID) {
		root.put(CONTEXT, new JSONString(contextUUID));
	}

	/**
	 * @param deviceUUID
	 *            - the unique identifier of the device which has originated the event
	 */
	public void setOriginatingDevice(String deviceUUID) {
		root.put(ORIGINATINGDEVICE, new JSONString(deviceUUID));
	}

	/**
	 * Allows to define a list of receipients (UUIDs) to which the event shall be sent. If not defined, the events will go to all involved devices
	 * 
	 * @param receipients
	 */
	public void setReceipients(String... receipients) {
		this.receipients = new JSONArray();
		int i = 0;
		for (String s : receipients) {
			this.receipients.set(i++, new JSONString(s));
		}
	}

	/**
	 * @return the unique identifier of the device that has originated the event
	 */
	public String getOriginatingDevice() {
		JSONValue value = root.get(ORIGINATINGDEVICE);
		if (value != null && value.isString() != null)
			return value.isString().stringValue();
		return null;
	}

	/**
	 * @param source
	 *            the source of the event
	 */
	public void setSourceObject(String source) {
		root.put(SOURCE, new JSONString(source));
	}

	/**
	 * @return the source of the event
	 */
	public String getSourceObject() {
		JSONValue value = root.get(SOURCE);
		if (value != null && value.isString() != null)
			return value.isString().stringValue();
		return null;
	}

	/**
	 * @return the context identifier of the event
	 */
	public String getContext() {
		JSONValue value = root.get(CONTEXT);
		if (value != null && value.isString() != null)
			return value.isString().stringValue();
		return null;
	}

	/**
	 * sets the timestamp of the event in ms (should be normalized with the server time offset first)
	 * 
	 * @param timestamp
	 */
	public void setTimestamp(long timestamp) {
		root.put(TIMESTAMP, new JSONString(String.valueOf(timestamp)));
	}

	/**
	 * @return the timestamp of the event in ms
	 */
	public Long getTimestamp() {
		JSONValue value = root.get(TIMESTAMP);
		if (value != null && value.isString() != null)
			return Long.parseLong(value.isString().stringValue());
		return null;
	}

	/**
	 * Serializes and encrypts the message data but not the message header to allow server instances to forward the message without needing to decrypt them.
	 * 
	 * @param securityManager
	 * @return
	 * @throws MessagingException
	 */
	public String serialize(TWICESecurityManager securityManager) throws MessagingException {
		root.put(EVENTTYPEKEY, new JSONString(getEventType()));
		root.put(UUID, new JSONString(root.get(UUID) != null ? root.get(UUID).toString() : ch.unifr.pai.twice.utils.device.client.UUID.createNew()));
		root.put("data", new JSONString(securityManager.encryptMessage(json.toString())));
		if (isBlocking())
			root.put(BLOCKINGEVENT, new JSONString("1"));
		if (receipients != null)
			root.put(RECEIPIENTS, receipients);
		return root.toString();
	}

	/**
	 * @return true if the event shall be blocking before it can be fired
	 */
	public boolean isBlocking() {
		return true;
	}

	/**
	 * @return if the event shall be fired locally
	 */
	public boolean isFireLocally() {
		return fireLocally;
	}

	/**
	 * sets if the event shall be fired locally
	 * 
	 * @param fireLocally
	 */
	public void setFireLocally(boolean fireLocally) {
		this.fireLocally = fireLocally;
	}

	/**
	 * Decrypts and deserializes the event data
	 * 
	 * @param string
	 * @param securityManager
	 * @return
	 * @throws MessagingException
	 */
	public RemoteEvent<H> deserialize(String string, TWICESecurityManager securityManager) throws MessagingException {
		root = (JSONObject) JSONParser.parseStrict(string);
		JSONString data = (JSONString) root.get("data");
		json = (JSONObject) JSONParser.parseStrict(securityManager.decryptMessage(data.stringValue()));
		return this;
	}

	/**
	 * Add another property to the object
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, String value) {
		json.put(key, new JSONString(value));
	}

	/**
	 * @param key
	 * @return the property value for the given key or null if not set.
	 */
	public String getProperty(String key) {
		JSONValue v = json.get(key);
		if (v != null && v.isString() != null)
			return v.isString().stringValue();
		return null;
	}

	/**
	 * sets the user name of the user that has originated the event
	 * 
	 * @param value
	 */
	public void setUserName(String value) {
		setProperty(USERNAME, value);
	}

	/**
	 * @return the user name of the user that has originated the event
	 */
	public String getUserName() {
		return getProperty(USERNAME);
	}

}
