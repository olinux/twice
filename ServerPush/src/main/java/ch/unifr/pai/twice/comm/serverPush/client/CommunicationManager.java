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

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;

public class CommunicationManager {
//	
//	private static EventBus unidirectionalEventBus;
	private static EventBus bidirectionalEventBus;
	
//	public static void sendDirectMessageToDevice(String receipientUuid, String message){
//		getUnidirectionalEventBus().
//		
//	}
	
	public void sendEvent(RemoteEvent<?> event){
		bidirectionalEventBus.fireEvent(event);
	}	
	
	/**
	 * Gives the unidirectional event bus of an application. Be aware that only {@link DiscardingRemoteEvent}s 
	 * can be sent through this event bus since the client is not notified if there are any conflicts
	 * @return a unidirectional event bus unless a bidirectional already exists.
	 */
	public static EventBus getUnidirectionalEventBus(){
//		//If a bidirectional event bus already exists, use that one
//		if(bidirectionalEventBus!=null){
//			return bidirectionalEventBus;
//		}
//		else if(unidirectionalEventBus==null){
//			//Lazy initialization of the unidiresendMessagectional event bus
//			unidirectionalEventBus = GWT.create(UnidirectionalEventBus.class);
//		}
//		return unidirectionalEventBus;
		return getBidirectionalEventBus();
	}
	
	/**
	 * Gives the bidirectional event bus for. The underlying logic negotiates the best communication pattern by itself.
	 * @return
	 */
	public static EventBus getBidirectionalEventBus(){
		if(bidirectionalEventBus==null){
			//Lazy initialization of the bidirectional event bus
			bidirectionalEventBus = GWT.create(ServerPushEventBus.class);
		}
		return bidirectionalEventBus;
	}
}
