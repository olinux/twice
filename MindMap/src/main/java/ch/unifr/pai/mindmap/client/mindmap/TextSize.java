package ch.unifr.pai.mindmap.client.mindmap;
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
import ch.unifr.pai.twice.multipointer.client.MultiCursorController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

public class TextSize{

	private int pixels = 14;
	
	private PushButton increase = new PushButton(new Image(GWT.getModuleBaseURL()+"images/textincrease.png")){
		@Override
		public void onBrowserEvent(Event event) {
			if (MultiCursorController.isDefaultCursor(event)) {
				super.onBrowserEvent(event);						
			}
		}	
	};
	private PushButton decrease = new PushButton(new Image(GWT.getModuleBaseURL()+"images/textdecrease.png")){
		@Override
		public void onBrowserEvent(Event event) {
			if (MultiCursorController.isDefaultCursor(event)) {
				super.onBrowserEvent(event);						
			}
		}	
	};
	
	public TextSize(final AsyncCallback<Integer> listener){
		increase.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				if (MultiCursorController.isDefaultCursor(event.getNativeEvent())) {
					pixels = Math.min(60, pixels+2);
					listener.onSuccess(pixels);
				}
			}
		});
		increase.setWidth("70px");
		increase.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		
		decrease.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				if (MultiCursorController.isDefaultCursor(event.getNativeEvent())) {
					pixels = Math.max(2, pixels-2);
					listener.onSuccess(pixels);
				}
			}
		});
		decrease.setWidth("70px");
		decrease.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
	}
	
	public int getTextSize(){
		return pixels;
	}
	
	public PushButton getIncreaseButton(){
		return increase;
	}
	public PushButton getDecreaseButton(){
		return decrease;
	}
	
}
