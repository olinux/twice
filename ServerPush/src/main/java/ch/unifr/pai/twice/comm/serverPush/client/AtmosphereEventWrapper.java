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

import ch.unifr.pai.twice.authentication.client.security.MessagingException;
import ch.unifr.pai.twice.authentication.client.security.TWICESecurityManager;

/**
 * event wrapper for atmosphere
 * 
 * @author Oliver Schmid
 * 
 */
public class AtmosphereEventWrapper implements Serializable {
	private String serializedEvent;

	public void setEvent(RemoteEvent<?> e, TWICESecurityManager security) {
		try {
			this.serializedEvent = e.serialize(security);
		}
		catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public String getSerializedEvent() {
		return serializedEvent;
	}
}
