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

package ch.unifr.pai.twice.authentication.client.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TWICESecurityManagerTest {

	TWICESecurityManager manager;
	String stringToEncrypt = "Hello world";

	@Before
	public void setUp() {
		manager = new TWICESecurityManager();
	}

	@Test
	@Ignore("Encryption is temporary unavailable")
	public void testEncryptAndDecryptMessage() throws MessagingException {
		// given

		// when
		String encrypted = manager.encryptMessage(stringToEncrypt);
		String decrypted = manager.decryptMessage(encrypted);

		// then
		assertNotNull(encrypted);
		assertNotEquals(stringToEncrypt, encrypted);
		assertNotNull(decrypted);
		assertNotEquals(encrypted, decrypted);
		assertEquals(stringToEncrypt, decrypted);
	}

}
