package ch.unifr.pai.twice.authentication.client.security;

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


/**
 * The security manager handles the encryption and decryption of the messages passed through the distributed event bus of the toolkit. This implementation is a
 * very simple proof of concept implementation which holds a static key valid for all participants. Improved implementations will be able to hold personalized
 * keys, shared keys of work groups, etc.
 * 
 * @author Oliver Schmid
 * 
 */
public class TWICESecurityManager {

	Base64 encoding = new Base64();

//	TripleDesCipher cipher = new TripleDesCipher();

	private final static byte[] key = new byte[] { (byte) 4, (byte) 8, (byte) 3, (byte) 80, (byte) 12, (byte) -9, (byte) -5, (byte) 101, (byte) 15, (byte) -8,
			(byte) 3, (byte) 0, (byte) 90, (byte) -9, (byte) 55, (byte) -41, (byte) -9, (byte) 90, (byte) 3, (byte) 100, (byte) -40, (byte) 79, (byte) 5,
			(byte) 102 };

	public TWICESecurityManager() {
//		cipher.setKey(key);

	}

	/**
	 * Encrypts a message with the current key
	 * 
	 * @param message
	 *            - A clear text message
	 * @return the encrypted message
	 * @throws MessagingException
	 *             if the message can not be encrypted
	 */
	public String encryptMessage(String message) throws MessagingException {
		// try {
		// // message = encoding.encode(cipher.encrypt(message));
		// // message = cipher.encrypt(message);
		//
		// }
		// catch (DataLengthException e1) {
		// throw new MessagingException("Was not able to encode the message", e1);
		// }
		// catch (IllegalStateException e1) {
		// throw new MessagingException("Was not able to encode the message", e1);
		// }
		// catch (InvalidCipherTextException e1) {
		// throw new MessagingException("Was not able to encode the message", e1);
		// }
		return message;
	}

	/**
	 * Decrypts a message with the current key
	 * 
	 * @param message
	 *            - A encrypted message
	 * @return the decrypted message in clear text
	 * @throws MessagingException
	 *             if the message can not be decrypted. Improved implementations should also validate the signature of a message as well as the checksum to
	 *             ensure the validity of a message.
	 */
	public String decryptMessage(String message) throws MessagingException {
		// try {
		// // message = cipher.decrypt(encoding.decode(message));
		// message = cipher.decrypt(message);
		//
		// }
		// catch (DataLengthException e) {
		// e.printStackTrace();
		// }
		// catch (IllegalStateException e) {
		// e.printStackTrace();
		// }
		// catch (InvalidCipherTextException e) {
		// e.printStackTrace();
		// }
		return message;
	}

}
