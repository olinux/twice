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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class RemoteKeyRecorder extends TextBox implements RemoteWidget {

	private String uniqueIdentifier;
	private ServerPushEventBus eventBus;
	private HandlerRegistration keyRegistration;
	
	@Override
	public String getEventSource() {
		return uniqueIdentifier;
	}
	
	private void send(Integer keycode, String text, Integer cursorPos){
		UndoableRemoteKeyPressEvent e = GWT.create(UndoableRemoteKeyPressEvent.class);
		if(keycode!=null)
			e.setKeyCode(keycode);
		if(text!=null)
			e.setText(text);
		if(cursorPos!=null)
			e.setCursorPos(cursorPos);
		eventBus.fireEventFromSource(e, this);
	}

	/**
	 * Define a static, unique identifier for this text box. This is needed for
	 * the linkage between the different clients. Make sure that this value is
	 * not composed dynamically - the best is a usage of a simple string not
	 * coming from any other method.
	 * 
	 * @param uniqueIdentifier
	 */
	public RemoteKeyRecorder(String uniqueIdentifier, ServerPushEventBus eventBus) {
		super();
		this.uniqueIdentifier = uniqueIdentifier;
		this.eventBus = eventBus;
		addFocusHandler(new FocusHandler(){

			@Override
			public void onFocus(FocusEvent event) {
				keyRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
					
					@Override
					public void onPreviewNativeEvent(NativePreviewEvent event) {
						switch(event.getTypeInt()){
						case Event.ONKEYUP:
							switch(event.getNativeEvent().getKeyCode()){
								case KeyCodes.KEY_DELETE:
								case KeyCodes.KEY_BACKSPACE:
									send(event.getNativeEvent().getKeyCode(), null, null);
									break;
								case KeyCodes.KEY_LEFT:
									send(null, null, -1);
									break;
								case KeyCodes.KEY_RIGHT:
									send(null, null, 1);
							}
							event.cancel();
							break;
						case Event.ONKEYPRESS:
								send(null, String.valueOf((char)event.getNativeEvent().getCharCode()), null);
								event.cancel();
								break;
						
						}
					
						
					}
				});
			}});
		addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				if(keyRegistration!=null)
					keyRegistration.removeHandler();
				keyRegistration = null;
			}
		});
	}
}
