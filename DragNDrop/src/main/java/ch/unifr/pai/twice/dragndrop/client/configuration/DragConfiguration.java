package ch.unifr.pai.twice.dragndrop.client.configuration;

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

import ch.unifr.pai.twice.dragndrop.client.factories.DropHandlerFactory;
import ch.unifr.pai.twice.dragndrop.client.intf.DragNDropHandler;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * This configures the way how a drag can be visualized and restricted.
 * 
 * @author Oliver Schmid
 * 
 */
public class DragConfiguration {

	/**
	 * The drag and drop handler to be used to react on drag events
	 */
	private final DragNDropHandler handler;
	/**
	 * A boundary box. A dragged widget can not leave this widget
	 */
	private final Widget boundaryBox;
	/**
	 * A HTML representation of the drag proxy (the semi-transparent visualization of a currently dragged widget)
	 */
	private Element dragProxy;
	/**
	 * If a drag proxy shall be used
	 */
	private final boolean withProxy;

	/**
	 * @return a default configuration without a drag proxy - the widget will be dragged itself (it does therefore not remain in its original position)
	 */
	public static DragConfiguration withoutProxy() {
		return withoutProxy(null, null);
	}

	/**
	 * @return a default configuration with a drag proxy
	 */
	public static DragConfiguration withProxy() {
		return withProxy(null, null);
	}

	/**
	 * @param handler
	 * @return a default configuration with a custom {@link DragNDropHandler} without a drag proxy - the widget will be dragged itself (it does therefore not
	 *         remain in its original position)
	 */
	public static DragConfiguration withoutProxy(DragNDropHandler handler) {
		return withoutProxy(handler, null);
	}

	/**
	 * @param handler
	 * @return a default configuration with a custom {@link DragNDropHandler} with a proxy
	 */
	public static DragConfiguration withProxy(DragNDropHandler handler) {
		return withProxy(handler, null);
	}

	/**
	 * @param boundaryBox
	 * @return a default configuration with a boundary box and without a drag proxy - the widget will be dragged itself (it does therefore not remain in its
	 *         original position)
	 */
	public static DragConfiguration withoutProxy(Widget boundaryBox) {
		return withoutProxy(null, boundaryBox);
	}

	/**
	 * @param boundaryBox
	 * @return a default configuration with a boundary box and with a drag proxy
	 */
	public static DragConfiguration withProxy(Widget boundaryBox) {
		return withProxy(null, boundaryBox);
	}

	/**
	 * @param handler
	 * @param boundaryBox
	 * @return a default configuration with a custom {@link DragNDropHandler}, a boundary box and without a drag proxy - the widget will be dragged itself (it
	 *         does therefore not remain in its original position
	 */
	public static DragConfiguration withoutProxy(DragNDropHandler handler, Widget boundaryBox) {
		return new DragConfiguration(handler, boundaryBox, false);
	}

	/**
	 * @param handler
	 * @param boundaryBox
	 * @return a default configuration with a custom {@link DragNDropHandler}, a boundary box and with a drag proxy
	 */
	public static DragConfiguration withProxy(DragNDropHandler handler, Widget boundaryBox) {
		return new DragConfiguration(handler, boundaryBox, true);
	}

	/**
	 * A private constructor - use the static factory methods for instantiation
	 * 
	 * @param handler
	 *            - if null, the {@link DropHandlerFactory#defaultHandler()} will be used.
	 * @param boundaryBox
	 * @param withProxy
	 */
	private DragConfiguration(DragNDropHandler handler, Widget boundaryBox, boolean withProxy) {
		this.handler = handler != null ? handler : DropHandlerFactory.defaultHandler();
		this.boundaryBox = boundaryBox;
		this.withProxy = withProxy;
	}

	/**
	 * @return the assigned drag and drop handler
	 */
	public DragNDropHandler getDragNDropHandler() {
		return handler;
	}

	/**
	 * @return the boundaries of permitted drags - if no boundary box is provided, this is 0
	 */
	public int getMinX() {
		return boundaryBox != null ? boundaryBox.getAbsoluteLeft() : 0;
	}

	/**
	 * @return the boundaries of permitted drags - if no boundary box is provided, this equals to the client width
	 */
	public int getMaxX() {
		return boundaryBox != null ? boundaryBox.getAbsoluteLeft() + boundaryBox.getOffsetWidth() : Window.getClientWidth();
	}

	/**
	 * @return the boundaries of permitted drags - if no boundary box is provided, this is 0
	 */
	public int getMinY() {
		return boundaryBox != null ? boundaryBox.getAbsoluteTop() : 0;
	}

	/**
	 * @return the boundaries of permitted drags - if no boundary box is provided, this equals to the client height
	 */
	public int getMaxY() {
		return boundaryBox != null ? boundaryBox.getAbsoluteTop() + boundaryBox.getOffsetHeight() : Window.getClientHeight();
	}

	/**
	 * @return if this drag configuration contains a proxy
	 */
	public boolean isWithProxy() {
		return withProxy;
	}

	/**
	 * @return the drag proxy
	 */
	public Element getDragProxy() {
		return dragProxy;
	}

	/**
	 * @param dragProxy
	 */
	public void setDragProxy(Element dragProxy) {
		if (dragProxy.getId() != null && !dragProxy.getId().equals(""))
			dragProxy.setId(dragProxy.getId() + "PROXY");
		this.dragProxy = dragProxy;
	}

}
