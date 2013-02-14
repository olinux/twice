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
import ch.unifr.pai.twice.comm.serverPush.client.ServerPushEventBus;
import ch.unifr.pai.twice.widgets.client.events.UndoableRemoteKeyPressEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class MyNewTextBox extends AbsolutePanel{
	
	private NativePreviewHandler previewHandler = new Event.NativePreviewHandler() {
		
		@Override
		public void onPreviewNativeEvent(NativePreviewEvent event) {
			UndoableRemoteKeyPressEvent e;
			if(Event.ONKEYUP == event.getTypeInt()){
				switch(event.getNativeEvent().getKeyCode()){
				case KeyCodes.KEY_BACKSPACE:
				case KeyCodes.KEY_DELETE:
					e = GWT.create(UndoableRemoteKeyPressEvent.class);
					e.setKeyCode(event.getNativeEvent().getKeyCode());
					eventBus.fireEventFromSource(e, source);	
					break;
				case KeyCodes.KEY_LEFT:
					e = GWT.create(UndoableRemoteKeyPressEvent.class);
					e.setCursorPos(-1);
					eventBus.fireEventFromSource(e, source);
					break;
				case KeyCodes.KEY_RIGHT:
					e = GWT.create(UndoableRemoteKeyPressEvent.class);
					e.setCursorPos(1);
					eventBus.fireEventFromSource(e, source);
					break;
				}
				event.cancel();
			}
			else if(Event.ONKEYPRESS == event.getTypeInt()){	
				switch(event.getNativeEvent().getKeyCode()){
				case KeyCodes.KEY_BACKSPACE:
				case KeyCodes.KEY_LEFT:
				case KeyCodes.KEY_RIGHT:
				case KeyCodes.KEY_DELETE:
					break;
					default:
						e = GWT.create(UndoableRemoteKeyPressEvent.class);
						e.setText(String.valueOf((char)event.getNativeEvent().getCharCode()));
						eventBus.fireEventFromSource(e, source);
				}
				event.cancel();
			}
		}
	};
	
	private HandlerRegistration previewRegistration;
	private RemoteTextInputInterpreter interpreter;
	private ServerPushEventBus eventBus;
	private String source;
	private TextBox t = new TextBox();
	private Label measurement = new Label();
	private Label l2 = new Label();
	private HTML cursor = new HTML();
	private Timer cursorBlink;
	
	
	public MyNewTextBox(ServerPushEventBus eventBus, String source){
		this.eventBus = eventBus;
		this.source = source;
		l2.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		l2.getElement().getStyle().setBorderWidth(1, Unit.PX);
		l2.getElement().getStyle().setWidth(100, Unit.PCT);
		measurement.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		cursor.setHeight("15px");
		cursor.getElement().getStyle().setBackgroundColor("black");
//		cursor.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		cursor.setWidth("1px");
		cursorBlink = new Timer() {
			
			@Override
			public void run() {
				if(cursor.getElement().getStyle().getVisibility().equals(Visibility.VISIBLE.getCssName())){
					cursor.getElement().getStyle().setVisibility(Visibility.HIDDEN);
				}
				else{
					cursor.getElement().getStyle().setVisibility(Visibility.VISIBLE);
				}
			}
		};
		cursor.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		this.setWidth("100px");
		this.setHeight("120px");
		this.add(measurement, 0, 0);
		this.add(cursor, 0, 0);
		this.add(l2, 0, 0);
		this.add(t, 0, 0);		
		this.interpreter = new RemoteTextInputInterpreter(new Command(){

			@Override
			public void execute() {
				measurement.setText(interpreter.getValue().substring(0, interpreter.getThisCursorPos()));
				t.setText(interpreter.getValue());
				l2.setText(interpreter.getValue());
				MyNewTextBox.this.setWidgetPosition(cursor, measurement.getOffsetWidth(), measurement.getOffsetHeight()-15);				
				
			}}, eventBus, source);
		t.addFocusHandler(new FocusHandler() {
			
			@Override
			public void onFocus(FocusEvent event) {
				previewRegistration = Event.addNativePreviewHandler(previewHandler);
				t.getElement().getStyle().setVisibility(Visibility.HIDDEN);
				cursorBlink.scheduleRepeating(600);
			}
		});
		t.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				if(previewRegistration!=null){
					previewRegistration.removeHandler();
					previewRegistration = null;
				}
				t.getElement().getStyle().setVisibility(Visibility.VISIBLE);
				cursor.getElement().getStyle().setVisibility(Visibility.HIDDEN);
				cursorBlink.cancel();
			}
		});
		
		
	}
	
}
