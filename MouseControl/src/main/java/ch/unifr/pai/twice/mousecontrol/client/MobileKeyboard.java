package ch.unifr.pai.twice.mousecontrol.client;
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
import java.util.Date;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;

public class MobileKeyboard extends TextBox{
	
	private StringBuilder log = new StringBuilder();

	private HandlerRegistration registration;
	
	private void addToLog(String name, String message, String attributes){
		log.append("<").append(name);
		log.append(" time=\"");
		log.append(new Date().getTime());
		log.append("\">");
		if(message!=null)
			log.append(message);
		log.append("</").append(name).append(">");
	}
	
	
	private NativePreviewHandler handler = new NativePreviewHandler() {

		@Override
		public void onPreviewNativeEvent(NativePreviewEvent event) {
			switch (event.getTypeInt()) {
			case Event.ONKEYDOWN:
				addToLog("keydown", null, null);
				break;
			case Event.ONKEYUP:
				addToLog("keyup", null, null);
				break;
			case Event.ONKEYPRESS:
				addToLog("keypress", null, null);
				break;
			}
		}
	};
	
	public MobileKeyboard(){
		this(null, null);
	}

	public MobileKeyboard(String openKeyboardText, String closeKeyboardText) {
		super();
		// Prevent IPhone and IPad to write everything in upper case
		getElement().setAttribute("autocapitalize", "off");
		setWidth("100%");
		setHeight("100%");
		getElement().getStyle().setFontSize(200, Unit.PCT);
		setAlignment(TextAlignment.CENTER);
		getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
		addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				getElement().getStyle().setZIndex(-1);
				registration = Event.addNativePreviewHandler(handler);
//				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
//
//					@Override
//					public void execute() {
//						Window.scrollTo(0, 1);
//						Document.get().setScrollTop(0);
//						RootLayoutPanel.get().onResize();
//					}
//				});
			}
		});
		addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				hide();
			}
		});
		setValue(openKeyboardText);
		getElement().getStyle().setZIndex(2);
	}
	
	
	public void stop(){
		log = new StringBuilder();
		hide();		
	}
	private void hide(){
		getElement().getStyle().setZIndex(2);
		if (registration != null)
			registration.removeHandler();
		registration = null;
	}
	
	public void start(){
		hide();
	}
	

}
