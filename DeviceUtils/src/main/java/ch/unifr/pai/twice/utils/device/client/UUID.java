package ch.unifr.pai.twice.utils.device.client;

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

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.storage.client.Storage;

/**
 * UUID holder - this class allows to identify a specific device session by the initial generation of a unique identifier (UUID) which is hold throughout the
 * whole client session and therefore can be used as a identifier for the device within the collaborative session.
 * 
 * To keep the same UUID even if the client session is interrupted (e.g. by closing the browser window), this implementation tries to store and recover the once
 * generated UUID in the HTML5 session storage if available.
 * 
 * @author Oliver Schmid
 * 
 */
public class UUID {

	/**
	 * The currently valid identifier of this client device
	 */
	private static String uuid;

	/**
	 * If no identifier is defined, this method tries to recover it from the HTML5 session storage if available or generates a new UUID and stores it if
	 * possible.
	 * 
	 * 
	 * @return the currently valid identifier of this device (the return value is never null!)
	 */
	public static String get() {
		if (uuid == null) {
			Storage s = Storage.getSessionStorageIfSupported();
			if (s != null) {
				uuid = s.getItem("ch.unifr.pai.mice.uuid");
			}
			if (uuid == null) {
				uuid = UUIDGenerator.uuid();
				if (s != null)
					s.setItem("ch.unifr.pai.mice.uuid", uuid);
			}
		}
		return uuid;
	}

	/**
	 * Creates a new unique identifier but doesn't store it as the client's identifier. This is a useful method if other components (e.g. events) need to have
	 * their own unique identifier.
	 * 
	 * @return a new unique identifier
	 */
	public static String createNew() {
		return UUIDGenerator.uuid();
	}

	/**
	 * @param event
	 * @return the unique identifier of an extended native event - null if the event doesn't contain device information (e.g. if they are originating from
	 *         native input devices)
	 */
	public native static String getUUIDForEvent(NativeEvent event)
	/*-{
		return event.uuid;
	}-*/;
}
