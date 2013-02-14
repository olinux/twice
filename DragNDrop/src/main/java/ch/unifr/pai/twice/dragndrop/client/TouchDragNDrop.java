package ch.unifr.pai.twice.dragndrop.client;

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

import ch.unifr.pai.twice.dragndrop.client.DragNDrop.ValueHolder;
import ch.unifr.pai.twice.dragndrop.client.configuration.DragConfiguration;
import ch.unifr.pai.twice.dragndrop.client.intf.Draggable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

public class TouchDragNDrop extends MPDragNDrop {

	@Override
	protected NativePreviewHandler createNativePreviewHandler() {
		return new NativePreviewHandler() {
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				switch (event.getTypeInt()) {
				case Event.ONTOUCHSTART:
					event.getNativeEvent().preventDefault();
					break;
				case Event.ONTOUCHMOVE:
				case Event.ONTOUCHEND:
					event.getNativeEvent().preventDefault();
					String deviceId = getDeviceId(event.getNativeEvent());
					MPDragNDrop handler = activeHandlers.get(deviceId);
					if (handler != null) {
						handler.onMouseEvent(deviceId,
								(Event) event.getNativeEvent());
					}
					break;
				}
				if(event.getTypeInt()==Event.ONTOUCHEND){
					removeEventPreview();
				}
			}
		};
	}
	@Override
	protected MPDragNDrop createDragNDrop(){
		return GWT.create(TouchDragNDrop.class);
	}

	@Override
	int getX(NativeEvent event) {
		if (event.getTouches().length() > 0) {
			Touch t = event.getTouches().get(0);
			return t.getClientX();
		}
//		if(event.getTouches().length()>0){
//			Touch t = event.getTouches().shift();
//			return t.getClientX();	
//		}
		return 0;
	}


	@Override
	int getY(NativeEvent event) {
		if (event.getTouches().length() > 0) {
			Touch t = event.getTouches().get(0);
			return t.getClientY();
		}
//		if(event.getTouches().length()>0){
//			Touch t = event.getTouches().shift();
//			return t.getClientY();	
//		}
		return 0;
	}

	
	

	@Override
	protected void addDragHandler(
			Draggable w, final Callback<NativeEvent> callback) {
		w.addTouchStartHandler(new TouchStartHandler() {
			
			@Override
			public void onTouchStart(TouchStartEvent event) {
				callback.onDone(event.getNativeEvent());
			}
		});
	}
	
	


	@Override
	protected void onMouseEvent(String deviceId, Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONTOUCHMOVE:
			onDrag(deviceId, event);
			break;
		case Event.ONTOUCHEND:
			endDrag(deviceId, event);
			break;
		}
	}


	@Override
	protected HandlerRegistration registerEndHandler(
			Draggable w, final Callback<NativeEvent> callback) {
		return w.addTouchEndHandler(new TouchEndHandler() {
			
			@Override
			public void onTouchEnd(TouchEndEvent event) {
				callback.onDone(event.getNativeEvent());
			}
		});
	}
	
	@Override
	protected int getDragDelayInMs() {
		return 1;
	}

}
