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

import ch.unifr.pai.twice.dragndrop.client.factories.DropTargetHandlerFactory.Priority;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * The handler defining the logic for drop targets
 * 
 * @author Oliver Schmid
 * 
 */
public interface DropTargetHandler {

	/**
	 * Invoked when a widget is dropped above the drop target
	 * 
	 * @param deviceId
	 *            - the identifier of the device that triggered the drop
	 * @param widget
	 *            - the widget which is dragged (this is the original widget and not the dragging proxy!)
	 * @param dragProxy
	 *            - the HTML element of the proxy
	 * @param event
	 *            - the event that has caused the invocation of the onEndOfDrop (e.g. a mouse up event)
	 * @param intersectionPercentage
	 *            - the percentage of intersection with the drop target
	 * @param intersectionPercentageWithTarget
	 * @return true if the drop shall be accepted, false if the drop shall be rejected
	 */
	public boolean onDrop(String deviceId, Widget widget, Element dragProxy, Event event, Double intersectionPercentage, Double intersectionPercentageWithTarget);

	/**
	 * Invoked when a widget hovers the drop target
	 * 
	 * @param deviceId
	 *            - the identifier of the device that triggered the drop
	 * @param widget
	 *            - the widget which is dragged (this is the original widget and not the dragging proxy!)
	 * @param dragProxy
	 *            - the HTML element of the proxy
	 * @param event
	 *            - the event that has caused the invocation of the onEndOfDrop (e.g. a mouse up event)
	 * @param intersectionPercentage
	 *            - the percentage of intersection with the drop target
	 * @param intersectionPercentageWithTarget
	 */
	public void onHover(String deviceId, Widget widget, Element dragProxy, Event event, Double intersectionPercentage, Double intersectionPercentageWithTarget);

	/**
	 * Invoked when a widget leaves the region of the drop target
	 * 
	 * @param deviceId
	 *            - the identifier of the device that triggered the drop
	 * @param widget
	 *            - the widget which is dragged (this is the original widget and not the dragging proxy!)
	 * @param dragProxy
	 *            - the HTML element of the proxy
	 * @param event
	 *            - the event that has caused the invocation of the onEndOfDrop (e.g. a mouse up event)
	 */
	public void onHoverEnd(String deviceId, Widget widget, Element dragProxy, Event event);

	/**
	 * @return the {@link Priority} of the drop target
	 */
	public Priority getPriority();
}
