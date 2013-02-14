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

public class UUID {
	
	private static String uuid;
		
	public static String get(){
		if(uuid==null){
			Storage s = Storage.getSessionStorageIfSupported();
			if(s!=null){
				uuid = s.getItem("ch.unifr.pai.mice.uuid");
			}
			if(uuid==null){
				uuid = UUIDGenerator.uuid();
				if(s!=null)
					s.setItem("ch.unifr.pai.mice.uuid", uuid);
			}
		}
		return uuid;		
	}
	
	public static String createNew(){
		return UUIDGenerator.uuid();
	}

	
	public native static String getUUIDForEvent(NativeEvent event)
	/*-{
		return event.uuid;
	}-*/;
}
