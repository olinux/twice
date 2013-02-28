package ch.unifr.pai.twice.authentication.client;

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

import com.google.gwt.user.client.Window;

/**
 * A simple implementation of an authentication module used to gather the user name of a participant. This implementation asks the user for his/her name through
 * a standard prompt and stores the value in a static variable (valid for a client session).
 * 
 * Improved implementations can introduce the confirmation of user credentials (e.g. password, etc.) as well as connect to further information sources to gather
 * data about a user from external services (e.g. Google+, Facebook, etc.)
 * 
 * @author Oliver Schmid
 * 
 */
public class Authentication {

	private static String username;

	/**
	 * Returns the user name for the identification and the signature of triggered messages within the collaborative session
	 * 
	 * @return the user name
	 */
	public static String getUserName() {
		if (username == null) {
			String s = Window.prompt("What's your name?", "");
			if ((s != null) && !s.isEmpty()) {
				username = s;
			}
		}
		return username;
	}

}
