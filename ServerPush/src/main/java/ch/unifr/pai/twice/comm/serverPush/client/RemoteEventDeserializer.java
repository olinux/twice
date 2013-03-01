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

import com.google.gwt.json.client.JSONObject;

/**
 * The deserializer for remote events. For convenience, we are using JSON as a message format - this could be replaced with more compact and compressed message
 * formats
 * 
 * @author Oliver Schmid
 * 
 */
public abstract class RemoteEventDeserializer {

	public final RemoteEvent<?> deserialize(String string, TWICESecurityManager security) throws MessagingException {
		com.google.gwt.json.client.JSONValue value = com.google.gwt.json.client.JSONParser.parseStrict(string);
		com.google.gwt.json.client.JSONObject o = value.isObject();
		if (o != null) {
			com.google.gwt.json.client.JSONValue type = o.get(ch.unifr.pai.twice.comm.serverPush.client.RemoteEvent.EVENTTYPEKEY);
			String t = null;
			if (type != null && type.isString() != null) {
				t = type.isString().stringValue();
				return deserialize(o, t, string, security);
			}
		}
		return null;
	};

	protected abstract RemoteEvent<?> deserialize(JSONObject o, String type, String string, TWICESecurityManager security) throws MessagingException;
}
