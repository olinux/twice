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
import ch.unifr.pai.twice.widgets.client.events.UndoableRemoteKeyPressEvent.UndoableRemoteKeyPressHandler;

import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.event.shared.Event.Type;

public abstract class UndoableRemoteKeyPressEvent extends UndoableRemoteEvent<UndoableRemoteKeyPressHandler>{

	public static final Type<UndoableRemoteKeyPressHandler> TYPE = new Type<UndoableRemoteKeyPressHandler>();
	
	public static interface UndoableRemoteKeyPressHandler extends UndoableRemoteEventHandler<UndoableRemoteKeyPressEvent>{}
	
	public static abstract class HasValueUndoableRemoteKeyPressHandler extends HasValueUndoableRemoteEventHandler<UndoableRemoteKeyPressEvent> implements UndoableRemoteKeyPressHandler{
		public HasValueUndoableRemoteKeyPressHandler(HasValue<?> source) {
			super(source);
		}		
	}
	
	public void setText(String text){
		setProperty("text", text);
	}
	
	public String getText(){
		return getProperty("text");
	}
	
	public void setCursorPos(int cursorPos){
		setProperty("cursorPos", String.valueOf(cursorPos));
	}
	
	public Integer getCursorPos(){
		String pos = getProperty("cursorPos");
		if(pos!=null)
			try {
				return Integer.parseInt(pos);
			} catch (NumberFormatException e) {
			}
		return null;
		
	}
	
	public void setKeyCode(int keyCode){
		setProperty("key", String.valueOf(keyCode));
	}
	
	public Integer getKeyCode(){
		String key = getProperty("key");
		if(key!=null)
			try {
				return Integer.parseInt(key);
			} catch (NumberFormatException e) {
			}
		return null;
	}
}
