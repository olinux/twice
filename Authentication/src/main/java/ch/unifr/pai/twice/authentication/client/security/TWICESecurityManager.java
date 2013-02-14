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

import com.googlecode.gwt.crypto.bouncycastle.DataLengthException;
import com.googlecode.gwt.crypto.bouncycastle.InvalidCipherTextException;
import com.googlecode.gwt.crypto.client.TripleDesCipher;

public class TWICESecurityManager {
	
	Base64 encoding = new Base64();
	
	TripleDesCipher cipher = new TripleDesCipher();
	
	private final static byte[] key = new byte[]{
        (byte)4,(byte)8,(byte)3,(byte)80,(byte)12,(byte)-9,(byte)-5,(byte)101, 
        (byte)15,(byte)-8,(byte)3,(byte)0,(byte)90,(byte)-9,(byte)55,(byte)-41, 
        (byte)-9,(byte)90,(byte)3,(byte)100,(byte)-40,(byte)79,(byte)5,(byte)102};
	
	public TWICESecurityManager(){
		cipher.setKey(key);
		
	}
	
	public String encryptMessage(String message) throws MessagingException{
		try {
//			  message = encoding.encode(cipher.encrypt(message));
			  message = cipher.encrypt(message);
			} catch (DataLengthException e1) {
			  e1.printStackTrace();
			} catch (IllegalStateException e1) {
			  e1.printStackTrace();
			} catch (InvalidCipherTextException e1) {
			  e1.printStackTrace();
			}
		return message;
	}
	
	
	public String decryptMessage(String message) throws MessagingException{
		try {
//			message = cipher.decrypt(encoding.decode(message));
			message = cipher.decrypt(message);

		} catch (DataLengthException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (InvalidCipherTextException e) {
			e.printStackTrace();
		}
		return message;
	}
	
	
}
