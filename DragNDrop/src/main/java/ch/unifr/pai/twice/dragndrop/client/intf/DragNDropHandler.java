package ch.unifr.pai.twice.dragndrop.client.intf;

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

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * Drag and drop handler contract - used to define the functionality of a dragged widget.
 * 
 * @author Oliver Schmid
 * 
 */
public interface DragNDropHandler {

	/**
	 * Invoked when the drag of a widget starts
	 * 
	 * @param deviceId
	 *            - the identifier of the device that triggered the drag
	 * @param draggedWidget
	 *            - the widget which is dragged (this is the original widget and not the dragging proxy!)
	 */
	public void onStartDrag(String deviceId, Widget draggedWidget);

	/**
	 * Invoked when a widget has been dropped. Please note, that the position of the widget can still be reset after that method - for final handling after a
	 * drag, use {@link DragNDropHandler#onEndOfDrop(String, Widget, int, int, Event)}.
	 * 
	 * @param deviceId
	 *            - the identifier of the device that triggered the drop
	 * @param draggedWidget
	 *            - the widget which is dragged (this is the original widget and not the dragging proxy!)
	 * @param dragProxyLeft
	 *            - the absolute left position of the proxy widget
	 * @param dragProxyTop
	 *            - the absolute top position of the proxy widget
	 * @param event
	 *            - the event that has caused the invocation of the onDrop (e.g. a mouse up event)
	 * @param dropTarget
	 *            - the drop target or null
	 * @param outOfBox
	 *            - boolean declaring if the drop happened outside of the defined boundary box
	 * @return true if the drop shall be accepted or false if it shall be rejected (and the widget shall be repositioned to its original coordinates)
	 */
	public boolean onDrop(String deviceId, Widget draggedWidget, int dragProxyLeft, int dragProxyTop, Event event, DropTargetHandler dropTarget,
			boolean outOfBox);

	/**
	 * Invoked at the end of the drop
	 * 
	 * @param deviceId
	 *            - the identifier of the device that triggered the drop
	 * @param draggedWidget
	 *            - the widget which is dragged (this is the original widget and not the dragging proxy!)
	 * @param dragProxyLeft
	 *            - the absolute left position of the proxy widget
	 * @param dragProxyTop
	 *            - the absolute top position of the proxy widget
	 * @param event
	 *            - the event that has caused the invocation of the onEndOfDrop (e.g. a mouse up event)
	 */
	public void onEndOfDrop(String deviceId, Widget draggedWidget, int dragProxyLeft, int dragProxyTop, Event event);

}
