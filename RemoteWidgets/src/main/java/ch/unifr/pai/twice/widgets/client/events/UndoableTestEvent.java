package ch.unifr.pai.twice.widgets.client.events;
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
import ch.unifr.pai.twice.comm.serverPush.client.UndoableRemoteEvent;
import ch.unifr.pai.twice.comm.serverPush.client.UndoableRemoteEventHandler;
import ch.unifr.pai.twice.widgets.client.events.UndoableTestEvent.UndoableTestHandler;

public abstract class UndoableTestEvent extends UndoableRemoteEvent<UndoableTestHandler>{
	public static final Type<UndoableTestHandler> TYPE = new Type<UndoableTestHandler>();

	public static interface UndoableTestHandler extends UndoableRemoteEventHandler<UndoableTestEvent>{
	}
	
	public void setOldHistory(String oldvalue){
		setStorageProperty("oldHistory", oldvalue);
	}
	
	public String getOldHistory(){
		return getStorageProperty("oldHistory");
	}
	
	public void setOldValue(String oldvalue){
		setStorageProperty("oldValue", oldvalue);
	}
	
	public String getOldValue(){
		return getStorageProperty("oldValue");
	}
	
	public void setFoo(String foo){
		setProperty("foo", foo);
	}
	
	public String getFoo(){
		return getProperty("foo");
	}
	
}
