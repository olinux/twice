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

import ch.unifr.pai.twice.dragndrop.client.intf.Draggable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

/**
 * Adaptation of the {@link MPDragNDrop} handler to provide drag and drop functionalities for touch based devices
 * 
 * @author Oliver Schmid
 * 
 */
public class TouchDragNDrop extends MPDragNDrop {

	/**
	 * Replaces the native preview handler to listen to touch events instead of mouse events
	 * 
	 * @see ch.unifr.pai.twice.dragndrop.client.MPDragNDrop#createNativePreviewHandler()
	 */
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
							handler.onMouseEvent(deviceId, (Event) event.getNativeEvent());
						}
						break;
				}
				if (event.getTypeInt() == Event.ONTOUCHEND) {
					removeEventPreview();
				}
			}
		};
	}

	/**
	 * Instantiate {@link TouchDragNDrop} instead of {@link MPDragNDrop}
	 * 
	 * @see ch.unifr.pai.twice.dragndrop.client.MPDragNDrop#createDragNDrop()
	 */
	@Override
	protected MPDragNDrop createDragNDrop() {
		return GWT.create(TouchDragNDrop.class);
	}

	/**
	 * return the x position of the touch instead of the mouse
	 * 
	 * @see ch.unifr.pai.twice.dragndrop.client.MPDragNDrop#getX(com.google.gwt.dom.client.NativeEvent)
	 */
	@Override
	int getX(NativeEvent event) {
		if (event.getTouches().length() > 0) {
			Touch t = event.getTouches().get(0);
			return t.getClientX();
		}
		// if(event.getTouches().length()>0){
		// Touch t = event.getTouches().shift();
		// return t.getClientX();
		// }
		return 0;
	}

	/**
	 * return the y position of the touch instead of the mouse
	 * 
	 * @see ch.unifr.pai.twice.dragndrop.client.MPDragNDrop#getY(com.google.gwt.dom.client.NativeEvent)
	 */
	@Override
	int getY(NativeEvent event) {
		if (event.getTouches().length() > 0) {
			Touch t = event.getTouches().get(0);
			return t.getClientY();
		}
		// if(event.getTouches().length()>0){
		// Touch t = event.getTouches().shift();
		// return t.getClientY();
		// }
		return 0;
	}

	/**
	 * Delegate the drag handler to the {@link TouchStartHandler}
	 * 
	 * @see ch.unifr.pai.twice.dragndrop.client.MPDragNDrop#addDragHandler(ch.unifr.pai.twice.dragndrop.client.intf.Draggable,
	 *      ch.unifr.pai.twice.dragndrop.client.MPDragNDrop.Callback)
	 */
	@Override
	protected void addDragHandler(Draggable w, final Callback<NativeEvent> callback) {
		w.addTouchStartHandler(new TouchStartHandler() {

			@Override
			public void onTouchStart(TouchStartEvent event) {
				callback.onDone(event.getNativeEvent());
			}
		});
	}

	/**
	 * react to touch events instead of mouse events
	 * 
	 * @see ch.unifr.pai.twice.dragndrop.client.MPDragNDrop#onMouseEvent(java.lang.String, com.google.gwt.user.client.Event)
	 */
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

	/**
	 * delegate the endHandler to the {@link TouchEndHandler}
	 * 
	 * @see ch.unifr.pai.twice.dragndrop.client.MPDragNDrop#registerEndHandler(ch.unifr.pai.twice.dragndrop.client.intf.Draggable,
	 *      ch.unifr.pai.twice.dragndrop.client.MPDragNDrop.Callback)
	 */
	@Override
	protected HandlerRegistration registerEndHandler(Draggable w, final Callback<NativeEvent> callback) {
		return w.addTouchEndHandler(new TouchEndHandler() {

			@Override
			public void onTouchEnd(TouchEndEvent event) {
				callback.onDone(event.getNativeEvent());
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.dragndrop.client.MPDragNDrop#getDragDelayInMs()
	 */
	@Override
	protected int getDragDelayInMs() {
		return 1;
	}

}
