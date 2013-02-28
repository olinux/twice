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

import ch.unifr.pai.twice.dragndrop.client.configuration.DragConfiguration;
import ch.unifr.pai.twice.dragndrop.client.intf.Draggable;
import ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.user.client.ui.Widget;

/**
 * The main entry point of the drag and drop functionality provided for multi-pointer, touch and single-pointer scenarios.
 * 
 * @author Oliver Schmid
 * 
 */
public class DragNDrop {

	/**
	 * The device dependent drag and drop handler implementation (instantiated by deferred binding)
	 */
	private static DragNDropIntf handler = GWT.create(MPDragNDrop.class);

	/**
	 * A private constructor to handle the initialization of the {@link DragNDrop#handler}
	 * 
	 * @param w
	 *            - the widget to be dragged
	 * @param offsetX
	 *            - the offset on the X-axis between the left border of the widget to be dragged and the mouse position on which the drag started
	 * @param offsetY
	 *            - the offset on the Y-axis between the top border of the widget to be dragged and the mouse position on which the drag started
	 * @param conf
	 *            - the configuration of the way how the drag shall be executed ({@link DragConfiguration})
	 */
	private DragNDrop(Widget w, int offsetX, int offsetY, DragConfiguration conf) {
		handler.initialize(w, offsetX, offsetY, conf);
	}

	/**
	 * Registers a widget to be draggable
	 * 
	 * @param w
	 *            - the widget that shall be made draggable
	 */
	public static void makeDraggable(Draggable w) {
		makeDraggable(w, (DragConfiguration) null);
	}

	/**
	 * Registers a widget to be draggable while providing a HTML element to be a template for the drag proxy (the element which is shown in a semi-transparent
	 * way below the mouse pointer during the drag)
	 * 
	 * @param w
	 *            - the widget that shall be made draggable
	 * @param dragProxyTemplate
	 *            - a HTML element which acts as a template for the drag proxy of this widget. If not defined, the proxy will be a visual copy of the draggable
	 *            widget.
	 */
	public static void makeDraggable(Draggable w, Element dragProxyTemplate) {
		handler.makeDraggable(w, null, dragProxyTemplate);
	}

	/**
	 * Registers a widget to be draggable while providing a specific {@link DragConfiguration} which shall be applied to the drags for this widget
	 * 
	 * @param w
	 *            - the widget that shall be made draggable
	 * @param conf
	 *            - the {@link DragConfiguration} which shall be applied to a drag of this widget
	 */
	public static void makeDraggable(Draggable w, DragConfiguration conf) {
		handler.makeDraggable(w, conf, null);
	}

	/**
	 * Registers a widget to be draggable while providing a {@link DragConfiguration} which shall be applied to the drags for this widget as well as a HTML
	 * element to be a template for the drag proxy (the element which is shown in a semi-transparent way below the mouse pointer during the drag)
	 * 
	 * @param w
	 *            - the widget that shall be made draggable
	 * @param conf
	 *            - the {@link DragConfiguration} which shall be applied to a drag of this widget
	 * @param dragProxyTemplate
	 *            - a HTML element which acts as a template for the drag proxy of this widget. If not defined, the proxy will be a visual copy of the draggable
	 *            widget.
	 */
	public static void makeDraggable(Draggable w, DragConfiguration conf, Element dragProxyTemplate) {
		handler.makeDraggable(w, conf, dragProxyTemplate);
	}

	/**
	 * This method allows to register a widget that implements {@link HasMouseOverHandlers} to become a drop target. The corresponding methods of the provided
	 * {@link DropTargetHandler} will be invoked if a widget is dragged while hovering this widget.
	 * 
	 * @param w
	 *            - the widget that shall be registered as a drop target
	 * @param dropHandler
	 *            - the {@link DropTargetHandler} defining the actions that shall be taken if a widget is dragged while hovering the widget
	 * @param hoverAware
	 *            - to make a drop target aware if a widget hovers it might have negative impact for the performance. If the drop target does not need to react
	 *            on the hovering of dragged widgets but only on drops of them while overlaying the widgets area, this property should be set to false. If this
	 *            property is set to false, the methods
	 *            {@link DropTargetHandler#onHover(String, Widget, Element, com.google.gwt.user.client.Event, Double, Double)} and
	 *            {@link DropTargetHandler#onHoverEnd(String, Widget, Element, com.google.gwt.user.client.Event)} will not be triggered.
	 */
	public static void setDropHandler(HasMouseOverHandlers w, DropTargetHandler dropHandler, boolean hoverAware) {
		handler.setDropHandler(w, dropHandler, hoverAware);
	}

	/**
	 * Unregisters the widget implementing {@link HasMouseOverHandlers} to be a drop handler.
	 * 
	 * @param w
	 */
	public static void removeDropHandler(HasMouseOverHandlers w) {
		handler.removeDropHandler(w);
	}

	/**
	 * The contract interface for the device specific implementations of the drag and drop functionality
	 * 
	 * @author Oliver Schmid
	 * 
	 */
	static interface DragNDropIntf {

		/**
		 * Initializes a drag
		 * 
		 * @param w
		 *            - the widget to be dragged
		 * @param offsetX
		 *            - the offset on the X-axis between the left coordinate of the widget to be dragged and the mouse pointer at the beginning of the drag
		 * @param offsetY
		 *            - the offset on the Y-axis between the top coordinate of the widget to be dragged and the mouse pointer at the beginning of the drag
		 * @param conf
		 *            - the {@link DragConfiguration} defining details about how the drag should be treatened
		 */
		void initialize(Widget w, int offsetX, int offsetY, DragConfiguration conf);

		/**
		 * Registers a widget implementing the interface {@link Draggable} to be draggable while providing a {@link DragConfiguration} which shall be applied to
		 * the drags for this widget as well as a HTML element to be a template for the drag proxy (the element which is shown in a semi-transparent way below
		 * the mouse pointer during the drag)
		 * 
		 * @param w
		 *            - the widget that shall be made draggable
		 * @param conf
		 *            - the {@link DragConfiguration} which shall be applied to a drag of this widget
		 * @param dragProxyTemplate
		 *            - a HTML element which acts as a template for the drag proxy of this widget
		 */
		void makeDraggable(final Draggable w, final DragConfiguration conf, final Element dragProxyTemplate);

		/**
		 * This method allows to register a widget that implements {@link HasMouseOverHandlers} to become a drop target. The corresponding methods of the
		 * provided {@link DropTargetHandler} will be invoked if a widget is dragged while hovering this widget.
		 * 
		 * @param w
		 *            - the widget that shall be registered as a drop target
		 * @param dropHandler
		 *            - the {@link DropTargetHandler} defining the actions that shall be taken if a widget is dragged while hovering the widget
		 * @param hoverAware
		 *            - to make a drop target aware if a widget hovers it might have negative impact for the performance. If the drop target does not need to
		 *            react on the hovering of dragged widgets but only on drops of them while overlaying the widgets area, this property should be set to
		 *            false. If this property is set to false, the methods
		 *            {@link DropTargetHandler#onHover(String, Widget, Element, com.google.gwt.user.client.Event, Double, Double)} and
		 *            {@link DropTargetHandler#onHoverEnd(String, Widget, Element, com.google.gwt.user.client.Event)} will not be triggered.
		 */
		void setDropHandler(HasMouseOverHandlers w, DropTargetHandler dropHandler, boolean hoverAware);

		/**
		 * Unregisters the widget implementing {@link HasMouseOverHandlers} to be a drop handler.
		 * 
		 * @param w
		 */
		void removeDropHandler(HasMouseOverHandlers w);
	}

	/**
	 * A helper class to hold a value
	 * 
	 * @author Oliver Schmid
	 * 
	 * @param <T>
	 */
	static class ValueHolder<T> {
		private T value;

		public void setValue(T value) {
			this.value = value;
		}

		public T getValue() {
			return value;
		}

	}
}
