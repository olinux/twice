package ch.unifr.pai.twice.widgets.client;
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
import ch.unifr.pai.twice.comm.serverPush.client.RemoteWidget;
import ch.unifr.pai.twice.comm.serverPush.client.ServerPushEventBus;
import ch.unifr.pai.twice.widgets.client.events.UndoableRemoteKeyPressEvent;
import ch.unifr.pai.twice.widgets.client.events.UndoableRemoteKeyPressEvent.UndoableRemoteKeyPressHandler;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class RemoteMultiFocusTextBox extends MultiFocusTextBox implements RemoteWidget{
	private RemoteTextInputInterpreter interpreter;
	private final String uniqueIdentifier;
	private final ServerPushEventBus eventBus;
	private String tmpValue;
	public RemoteMultiFocusTextBox(String uniqueIdentifier, ServerPushEventBus eventBus) {
		super();
		this.uniqueIdentifier = uniqueIdentifier;
		this.eventBus = eventBus;		
		eventBus.addHandlerToSource(UndoableRemoteKeyPressEvent.TYPE, uniqueIdentifier, new UndoableRemoteKeyPressEvent.UndoableRemoteKeyPressHandler() {
			
			@Override
			public void onEvent(UndoableRemoteKeyPressEvent event) {
				String device = event.getOriginatingDevice();
				if (event.getKeyCode() != null) {
					switch (event.getKeyCode()) {
					case KeyCodes.KEY_DELETE:
						delete(device);
						break;
					case KeyCodes.KEY_BACKSPACE:
						bckspc(device);
						break;
					}
				} else if (event.getText() != null) {
					addChar(event.getText(), device);
				}
				Integer cursorPos = event.getCursorPos();
				if (cursorPos != null) {
					if (cursorPos > 0) {
						shiftCursorPosRight(device, cursorPos);
					} else {
						shiftCursorPosLeft(device, Math.abs(cursorPos));
					}
				}
			}

			@Override
			public void undo(UndoableRemoteKeyPressEvent event) {
				tmpValue = event.getStorageProperty("value");
			}
			
			@Override
			public void saveState(UndoableRemoteKeyPressEvent event) {
				event.setStorageProperty("value", tmpValue);
			}
		});
	}
	
	private void addChar(String text, String device) {
		Cursor c = getOrCreateCursor(device);
		if(c!=null){
			StringBuilder sb = new StringBuilder();
			if (getValue() != null){
			sb.append(getValue().substring(0, Math.min(getValue().length(), c.getPosition())));
			}
			sb.append(text);
			if (getValue() != null && c.getPosition() < getValue().length()) {
				sb.append(getValue().substring(	Math.min(getValue().length(), c.getPosition())));
			}
			setValue(sb.toString());
			shiftAllToRight(c.getPosition(), text.length());
		}		
	}
	
	private void delete(String device) {
		Cursor c = getCursors().get(device);
		if(c!=null && c.getPosition()<getValue().length()){
			setValue(getValue().substring(0, c.getPosition())+getValue().substring(c.getPosition()+1));
			shiftAllToLeft(c.getPosition(), 1);
		}
	}
	
	private void shiftAllToRight(int curPos, int amount) {
		for(Cursor c : getCursors().values()){
			if(c.getPosition()>=curPos)
				c.setPosition(Math.min(getValue().length(), c.getPosition()+amount));			
		}
	}
	
	private void shiftAllToLeft(int curPos, int amount) {
		for(Cursor c : getCursors().values()){
			if(c.getPosition()<curPos)
				c.setPosition(Math.max(0, c.getPosition()-amount));			
		}
	}
	
	private void bckspc(String device) {
		Cursor c = getCursors().get(device);
		if(c!=null && c.getPosition()>0 && getValue()!=null && getValue().length()>0){
			setValue(getValue().substring(0, c.getPosition()-1)+(c.getPosition()<getValue().length() ? getValue().substring(c.getPosition()):""));
			shiftAllToLeft(c.getPosition()+1, 1);
		}
	}
	
	private void shiftCursorPosRight(String device, int abs) {
		Cursor c = getCursors().get(device);
		if(c!=null){
			c.setPosition(Math.min(getValue().length(), c.getPosition()+abs));
		}
	}
	
	private void shiftCursorPosLeft(String device, int abs) {
		Cursor c = getCursors().get(device);
		if(c!=null){
			c.setPosition(Math.max(0, c.getPosition()-abs));
		}
	}
	
	@Override
	public String getEventSource() {
		return uniqueIdentifier;
	}
	

}
