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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ch.unifr.pai.twice.dragndrop.client.DragNDrop.DragNDropIntf;
import ch.unifr.pai.twice.dragndrop.client.configuration.DragConfiguration;
import ch.unifr.pai.twice.dragndrop.client.factories.DropTargetHandlerFactory.Priority;
import ch.unifr.pai.twice.dragndrop.client.intf.DragNDropHandler;
import ch.unifr.pai.twice.dragndrop.client.intf.Draggable;
import ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler;
import ch.unifr.pai.twice.dragndrop.client.utils.Triple;
import ch.unifr.pai.twice.dragndrop.client.utils.Tuple;
import ch.unifr.pai.twice.multipointer.client.MultiCursorController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A drag and drop implementation for multiple mouse pointers (as well as single mouse pointers)
 * 
 * @author Oliver Schmid
 * 
 */
class MPDragNDrop implements DragNDropIntf {

	/**
	 * Currently active drag and drop handlers (for the drags which are currently in progress) registered in a map depending on the UUID of the device which
	 * executes the drag
	 */
	protected static Map<String, MPDragNDrop> activeHandlers = new HashMap<String, MPDragNDrop>();
	/**
	 * The kept reference of the registered event preview for handling mouse events.
	 */
	protected static HandlerRegistration handlerReg;
	/**
	 * A constant style name attached to widgets which are currently dragged
	 */
	protected final static String DRAGGINGSTYLENAME = "dragging";

	/**
	 * The widget to be dragged
	 */
	protected Widget w;

	/**
	 * The calculated percentage of the x-axis offset between the left coordinate of the dragged widget and the mouse position at the beginning of the drag
	 */
	protected int percOffsetX;

	/**
	 * The calculated percentage of the y-axis offset between the top coordinate of the dragged widget and the mouse poisition at the beginning of the drag
	 */
	protected int percOffsetY;
	/**
	 * Origin value of the CSS-display attribute of the widget to be dragged (used for resetting the attribute after drag)
	 */
	protected String originDisplay;
	/**
	 * The {@link DragConfiguration} applicable
	 */
	protected DragConfiguration conf;
	/**
	 * The global drop target registry (application wide)
	 */
	protected static Map<Widget, DropTargetHandler> dropTargetRegistry = new HashMap<Widget, DropTargetHandler>();
	/**
	 * Map of a list of devices (UUIDs) currently hovering a specific drop target
	 */
	protected static Map<DropTargetHandler, Set<String>> hoverDropTargets = new HashMap<DropTargetHandler, Set<String>>();

	/**
	 * Map for the drop target, a device (UUIS) is currently hovering
	 */
	protected static Map<String, DropTargetHandler> currentHovering = new HashMap<String, DropTargetHandler>();

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.dragndrop.client.DragNDrop.DragNDropIntf#initialize(com.google.gwt.user.client.ui.Widget, int, int,
	 * ch.unifr.pai.twice.dragndrop.client.configuration.DragConfiguration)
	 */
	@Override
	public void initialize(Widget w, int offsetX, int offsetY, DragConfiguration conf) {
		this.w = w;
		this.percOffsetX = (int) (100.0 / w.getOffsetWidth() * offsetX);
		this.percOffsetY = (int) (100.0 / w.getOffsetHeight() * offsetY);
		this.originDisplay = w.getElement().getStyle().getDisplay();
		this.conf = conf;
	}

	/**
	 * @return a delay in ms which has to be waited for to start the drag (necessary for separating a simple click from a drag)
	 */
	protected int getDragDelayInMs() {
		return 200;
	}

	/**
	 * The native preview handler listening for all mouse pointer events which are important to us
	 */
	private final NativePreviewHandler nativePreviewHandler = createNativePreviewHandler();

	/**
	 * @return the native preview handler handling all mouse events for the drag and drop mechansim
	 */
	protected NativePreviewHandler createNativePreviewHandler() {
		return new NativePreviewHandler() {
			/**
			 * This event preview handler is active during the actual drag only and prevents the browser default behavior.
			 * 
			 * @see com.google.gwt.user.client.Event.NativePreviewHandler#onPreviewNativeEvent(com.google.gwt.user.client.Event.NativePreviewEvent)
			 */
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				switch (event.getTypeInt()) {
					case Event.ONMOUSEDOWN:
						event.getNativeEvent().preventDefault();
						break;
					case Event.ONMOUSEMOVE:
					case Event.ONMOUSEUP:
						event.getNativeEvent().preventDefault();
						String deviceId = getDeviceId(event.getNativeEvent());
						MPDragNDrop handler = activeHandlers.get(deviceId);
						if (handler != null) {
							handler.onMouseEvent(deviceId, (Event) event.getNativeEvent());
						}
						break;
				}
			}
		};
	}

	/**
	 * A helper class to hold a value
	 * 
	 * @author Oliver Schmid
	 * 
	 * @param <T>
	 */
	private static class ValueHolder<T> {
		private T value;

		public void setValue(T value) {
			this.value = value;
		}

		public T getValue() {
			return value;
		}

	}

	/**
	 * A simple callback interface
	 * 
	 * @author Oliver Schmid
	 * 
	 * @param <T>
	 */
	protected static interface Callback<T> {
		void onDone(T value);
	}

	/**
	 * Delegates the registration of a generic "dragHandler" to the {@link MouseDownHandler} of the current widget since we are working with mouse pointers in
	 * this kind of drag and drop implementation
	 * 
	 * @param w
	 * @param callback
	 */
	protected void addDragHandler(Draggable w, final Callback<NativeEvent> callback) {
		w.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(final MouseDownEvent event) {
				callback.onDone(event.getNativeEvent());
			}
		});
	}

	/**
	 * Delegates the endHandler to the {@link MouseUpHandler} of the current widget since we are working with mouse pointers in this kind of drag and drop
	 * implementation
	 * 
	 * @param w
	 * @param callback
	 * @return
	 */
	protected HandlerRegistration registerEndHandler(Draggable w, final Callback<NativeEvent> callback) {
		return w.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				callback.onDone(event.getNativeEvent());
			}
		});
	}

	/**
	 * Helper method to get the device id of the originator of an event.
	 * 
	 * @param event
	 * @return the UUID of the device
	 */
	String getDeviceId(NativeEvent event) {
		return MultiCursorController.getUUID(event);
	}

	/**
	 * Helper method to get the x-position of a event (relative to the client)
	 * 
	 * @param event
	 * @return
	 */
	int getX(NativeEvent event) {
		return event.getClientX();
	}

	/**
	 * Helper method to get the y-position of a event (relative to the client)
	 * 
	 * @param event
	 * @return
	 */
	int getY(NativeEvent event) {
		return event.getClientY();
	}

	/**
	 * @see ch.unifr.pai.twice.dragndrop.client.DragNDrop.DragNDropIntf#makeDraggable(ch.unifr.pai.twice.dragndrop.client.intf.Draggable,
	 *      ch.unifr.pai.twice.dragndrop.client.configuration.DragConfiguration, com.google.gwt.dom.client.Element)
	 * 
	 *      Make the widget draggable by attaching a drag handler to it. If the drag handler is invoked, the logic checks if the widget is currently draggable
	 *      (e.g. not locked by another user) and starts the drag by the invocation of
	 *      {@link MPDragNDrop#startDrag(Widget, String, int, int, DragConfiguration, Element)} after making sure, that the delay defined by
	 *      {@link MPDragNDrop#getDragDelayInMs()} is exceeded and it therefore is a valid drag
	 * 
	 */
	@Override
	public void makeDraggable(final Draggable w, final DragConfiguration conf, final Element dragProxyTemplate) {
		if (w instanceof Widget) {
			((Widget) w).addStyleName("draggable");
			addDragHandler(w, new Callback<NativeEvent>() {
				@Override
				public void onDone(NativeEvent event) {
					if (w.isDraggable()) {
						final String deviceId = getDeviceId(event);
						final ValueHolder<Boolean> drag = new ValueHolder<Boolean>();
						drag.setValue(true);
						final HandlerRegistration end = registerEndHandler(w, new Callback<NativeEvent>() {

							@Override
							public void onDone(NativeEvent event) {

								if (getDeviceId(event).equals(deviceId))
									drag.setValue(false);
							}
						});
						final int offsetX = getX(event) - ((Widget) w).getElement().getAbsoluteLeft();
						final int offsetY = getY(event) - ((Widget) w).getElement().getAbsoluteTop();
						DOM.eventPreventDefault((Event) event);
						Timer t = new Timer() {

							@Override
							public void run() {
								end.removeHandler();
								if (drag.getValue() && !((Widget) w).getStyleName().contains(DRAGGINGSTYLENAME))
									startDrag((Widget) w, deviceId, offsetX, offsetY, conf, dragProxyTemplate);
							}
						};
						t.schedule(getDragDelayInMs());
					}
				}
			});
		}
	}

	/**
	 * Invoked when a drag starts. This method handles the full dragging
	 * 
	 * @param w
	 *            - the widget to be dragged
	 * @param deviceId
	 *            - the deviceId of the device dragging the widget
	 * @param offsetX
	 * @param offsetY
	 * @param conf
	 * @param dragProxyTemplate
	 */
	protected void startDrag(Widget w, String deviceId, int offsetX, int offsetY, DragConfiguration conf, Element dragProxyTemplate) {

		MultiCursorController.getInstance().notifyCursor(deviceId, "startDrag");
		if (conf == null)
			conf = DragConfiguration.withProxy();
		if (conf.getDragNDropHandler() != null) {
			conf.getDragNDropHandler().onStartDrag(deviceId, w);
		}
		conf.setDragProxy(DOM.clone(dragProxyTemplate == null ? w.getElement() : (com.google.gwt.user.client.Element) dragProxyTemplate, true));
		conf.getDragProxy().addClassName("drag-proxy");
		conf.getDragProxy().getStyle().setPosition(Position.ABSOLUTE);
		RootPanel.getBodyElement().appendChild(conf.getDragProxy());
		conf.getDragProxy().getStyle().setLeft(w.getElement().getAbsoluteLeft(), Unit.PX);
		conf.getDragProxy().getStyle().setTop(w.getElement().getAbsoluteTop(), Unit.PX);
		conf.getDragProxy().getStyle().setPosition(Position.ABSOLUTE);
		// DOM.eventPreventDefault((Event) event.getNativeEvent());
		if (activeHandlers.isEmpty())
			addEventPreview();
		MPDragNDrop d = createDragNDrop();
		d.initialize(w, offsetX, offsetY, conf);
		activeHandlers.put(deviceId, d);
		if (!conf.isWithProxy()) {
			w.getElement().getStyle().setDisplay(Display.BLOCK);
		}
		else {
			w.addStyleName(DRAGGINGSTYLENAME);
		}
	}

	/**
	 * A delegator method to instantiate a drag and drop handler through deferred binding.
	 * 
	 * @return
	 */
	protected MPDragNDrop createDragNDrop() {
		return GWT.create(MPDragNDrop.class);
	}

	/**
	 * Called when a new repositioning event is triggered during the drag.
	 * 
	 * @param deviceId
	 * @param event
	 */
	protected void onDrag(String deviceId, Event event) {
		setPosition(getX(event), getY(event), true);
		if (!hoverDropTargets.isEmpty()) {
			MPDragNDrop h = activeHandlers.get(deviceId);
			if (h != null) {
				Triple<Double, Double, DropTargetHandler> handlerOfDropTarget = h.getHandlerOfDropTarget();
				DropTargetHandler dropTarget = null;
				Double intersectionPercentage = null;
				Double intersectionPercentageWithTarget = null;
				if (handlerOfDropTarget != null) {
					intersectionPercentage = handlerOfDropTarget.getFirst();
					intersectionPercentageWithTarget = handlerOfDropTarget.getSecond();
					dropTarget = handlerOfDropTarget.getThird();
				}
				handleHover(dropTarget, intersectionPercentage, intersectionPercentageWithTarget, deviceId, event, h.w, h.conf.getDragProxy());
			}
		}
	}

	/**
	 * Handle the hovering of a dragged widget by checking if it hovers a registered drop target
	 * 
	 * @param dropTarget
	 * @param intersectionPercentage
	 * @param intersectionPercentageWithTarget
	 * @param deviceId
	 * @param event
	 * @param widget
	 * @param dragProxy
	 */
	protected void handleHover(DropTargetHandler dropTarget, Double intersectionPercentage, Double intersectionPercentageWithTarget, String deviceId,
			Event event, Widget widget, Element dragProxy) {
		DropTargetHandler currentHover = currentHovering.get(deviceId);
		if (currentHover != null && currentHover == dropTarget) {
			return;
		}
		else {
			if (currentHover != null) {
				hoverDropTargets.get(currentHover).remove(deviceId);
				if (hoverDropTargets.get(currentHover).size() == 0)
					currentHover.onHoverEnd(deviceId, widget, dragProxy, event);
			}
			if (dropTarget == null || hoverDropTargets.get(dropTarget) == null) {
				currentHovering.remove(deviceId);
			}
			else {
				hoverDropTargets.get(dropTarget).add(deviceId);
				currentHovering.put(deviceId, dropTarget);
				dropTarget.onHover(deviceId, widget, dragProxy, event, intersectionPercentage, intersectionPercentageWithTarget);
			}
		}
	}

	/**
	 * Functionality triggered on the end of the drag (e.g. the release of the mouse button). This method resets all the necessary management as well as the
	 * event preview needed during the drag. It checks if the element is dropped above a drop target and triggers the event on the drop target. Additionally, it
	 * checks if the drag and drop handler accepts the drop (if {@link DragNDropHandler#onDrop(String, Widget, int, int, Event, DropTargetHandler, boolean)}
	 * returns true). If there is a drop target available, it will hand over the information about the acceptance or the rejection of the drop through the
	 * widget itself as part of the parameters ("resetByOwner") and lets the drop target decide how to handle the situation. If there is no drop target
	 * available and the drop is rejected, the widget is repositioned to its original coordinates. In the end, the onEndHover logic is invoked for the drag and
	 * drop handler.
	 * 
	 * 
	 * @param deviceId
	 * @param event
	 */
	protected void endDrag(String deviceId, Event event) {
		MPDragNDrop h = activeHandlers.get(deviceId);
		if (h != null && h.w != null) {
			MultiCursorController.getInstance().notifyCursor(deviceId, "endDrag");
			h.w.removeStyleName(DRAGGINGSTYLENAME);
			h.w.removeStyleName("drag-proxy");
			Triple<Double, Double, DropTargetHandler> handlerOfDropTarget = h.getHandlerOfDropTarget();
			final int proxyLeft = h.conf.getDragProxy().getAbsoluteLeft();
			final int proxyTop = h.conf.getDragProxy().getAbsoluteTop();

			DropTargetHandler dropTarget = null;
			if (handlerOfDropTarget != null) {
				dropTarget = handlerOfDropTarget.getThird();
			}
			boolean outOfBox = h.outOfBox(event);
			if (h.conf != null && h.conf.getDragNDropHandler() != null) {
				if (h.conf.getDragProxy() != null)
					h.conf.getDragProxy().removeFromParent();
				boolean resetByOwner = !h.conf.getDragNDropHandler().onDrop(deviceId, h.w, proxyLeft, proxyTop, event, dropTarget, outOfBox);
				// boolean resetByDropTarget = false;
				handleHover(null, Double.valueOf(0), Double.valueOf(0), deviceId, event, h.w, h.conf.getDragProxy());
				if (dropTarget != null) {
					Scheduler.get().scheduleDeferred(
							h.new DropCommand(dropTarget, deviceId, h, event, handlerOfDropTarget.getFirst(), handlerOfDropTarget.getSecond(), proxyLeft,
									proxyTop, resetByOwner));
				}
				else if (resetByOwner) {
					h.resetPosition();
					h.conf.getDragNDropHandler().onEndOfDrop(deviceId, h.w, proxyLeft, proxyTop, event);
				}
				else {
					h.conf.getDragNDropHandler().onEndOfDrop(deviceId, h.w, proxyLeft, proxyTop, event);
				}
			}

		}

		activeHandlers.remove(deviceId);
		if (activeHandlers.isEmpty())
			removeEventPreview();
	}

	/**
	 * The drop command is a transport object to hand over the information of a drop from the widget's drag handler to a drop target.
	 * 
	 * @author Oliver Schmid
	 * 
	 */
	private class DropCommand implements Command {
		private final DropTargetHandler dropTarget;
		private final String deviceId;
		private final MPDragNDrop h;
		private final Event event;
		private final Double intersectionPercentage;
		private final Double intersectionPercentageWithTarget;
		private final boolean resetByOwner;
		private final int proxyLeft;
		private final int proxyTop;

		public DropCommand(DropTargetHandler dropTarget, String deviceId, MPDragNDrop h, Event event, Double intersectionPercentage,
				Double intersectionPercentageWithTarget, int proxyLeft, int proxyTop, boolean resetByOwner) {
			super();
			this.dropTarget = dropTarget;
			this.deviceId = deviceId;
			this.h = h;
			this.resetByOwner = resetByOwner;
			this.event = event;
			this.proxyLeft = proxyLeft;
			this.proxyTop = proxyTop;
			this.intersectionPercentage = intersectionPercentage;
			this.intersectionPercentageWithTarget = intersectionPercentageWithTarget;
		}

		/**
		 * In the standard behavior, the onDrop method of the drop target is executed (even if the event is rejected by the dragged widget!!!) Then, if either
		 * the drop target or the owning device has rejected the drop, the dragged widget is reset to its original position.
		 * 
		 * @see com.google.gwt.user.client.Command#execute()
		 */
		@Override
		public void execute() {
			boolean resetByDropTarget = !dropTarget.onDrop(deviceId, h.w, conf.getDragProxy(), event, intersectionPercentage, intersectionPercentageWithTarget);
			if (resetByDropTarget || resetByOwner) {
				h.resetPosition();
			}
			h.conf.getDragNDropHandler().onEndOfDrop(deviceId, h.w, proxyLeft, proxyTop, event);
		}

	}

	/**
	 * 
	 * @return the information of the percentage of the area of the dragged proxy which intersects with the drop target, the percentage of the drop target area
	 *         which is covered by the proxy and the drop target handler or null if no drop target intersects with the dragged widget
	 */
	protected Triple<Double, Double, DropTargetHandler> getHandlerOfDropTarget() {
		Tuple<Widget, Long> target = getHoverDropTarget(conf.getDragProxy());
		if (target == null || target.getFirst() == null || target.getSecond() == null)
			return null;
		else
			return new Triple<Double, Double, DropTargetHandler>(100.0 * target.getSecond() / getAreaOfWidget(conf.getDragProxy()), 100.0 * target.getSecond()
					/ getAreaOfWidget(target.getFirst().getElement()), dropTargetRegistry.get(target.getFirst()));
	}

	/**
	 * @param e
	 *            - a HTML element
	 * @return the area of the HTML element in square pixels
	 */
	protected long getAreaOfWidget(Element e) {
		return e.getOffsetHeight() * e.getOffsetWidth();
	}

	/**
	 * If the configuration of the drag defines a bounding box (a specific area which can not be left by the dragged widget), this method checks if the widget
	 * will be outside of the box by the application of the given event
	 * 
	 * @param event
	 *            to be applied
	 * @return if the widget is outside of the defined bounding box
	 */
	protected boolean outOfBox(Event event) {
		return getX(event) < conf.getMinX() || getY(event) < conf.getMinY() || getX(event) > conf.getMaxX() || getY(event) > conf.getMaxY();
	}

	/**
	 * Repositions the dragged widget to a specific x and y position taking into account the offset between the original widget coordinates and the mouse
	 * pointer position at the begin of the drag. The offset is kept in percentages to make the offset calculation independent of the drag-proxy size.
	 * 
	 * @param x
	 * @param y
	 * @param withOffset
	 */
	protected void setPosition(int x, int y, boolean withOffset) {
		Element dragProxy = conf.getDragProxy();
		int newX = x - (withOffset ? (int) (dragProxy.getOffsetWidth() / 100.0 * percOffsetX) : 0);
		int newY = y - (withOffset ? (int) (dragProxy.getOffsetHeight() / 100.0 * percOffsetY) : 0);
		dragProxy.getStyle().setLeft(Math.max(conf.getMinX(), Math.min(newX, conf.getMaxX() - dragProxy.getOffsetWidth())), Unit.PX);
		dragProxy.getStyle().setTop(Math.max(conf.getMinY(), Math.min(newY, conf.getMaxY() - dragProxy.getOffsetHeight())), Unit.PX);
	}

	/**
	 * Resets the position of the dragged widget to its original coordinates and removes the dragging styles
	 */
	protected void resetPosition() {
		if (conf.isWithProxy()) {
			conf.getDragProxy().removeFromParent();
			conf.setDragProxy(null);
		}
		else {
			w.getElement().getStyle().setProperty("display", originDisplay);
		}
		for (Widget w2 : dropTargetRegistry.keySet()) {
			String idStyle = w2.getElement().getId() != null && !w2.getElement().getId().equals("") ? "hover-" + w2.getElement().getId() : null;
			if (idStyle != null) {
				w.removeStyleName(idStyle);
			}
		}
	}

	/**
	 * Adds the event preview listening for mouse events.
	 */
	protected void addEventPreview() {
		handlerReg = Event.addNativePreviewHandler(nativePreviewHandler);
		GWT.log("ADD EVENT PREVIEW");
	}

	/**
	 * Removes the event preview listening for mouse events
	 */
	protected void removeEventPreview() {
		if (handlerReg != null) {
			handlerReg.removeHandler();
			GWT.log("REMOVE EVENT PREVIEW");
		}
		else {
			GWT.log("NO REMOVE EVENT PREVIEW");
		}
	}

	/**
	 * Handles the different mouse events by type
	 * 
	 * @param deviceId
	 * @param event
	 */
	protected void onMouseEvent(String deviceId, Event event) {
		switch (DOM.eventGetType(event)) {
			case Event.ONMOUSEMOVE:
				onDrag(deviceId, event);
				break;
			case Event.ONMOUSEUP:
				endDrag(deviceId, event);
				break;
		}
	}

	/**
	 * Returns the drop target that has the highest priority and that has the biggest intersection area with the given element.
	 * 
	 * @param e
	 * @return a tuple of the drop target widget and the intersecting area or null if the element does not intersect with another element
	 */
	protected Tuple<Widget, Long> getHoverDropTarget(Element e) {
		Set<Widget> affected = getAffectedDropTargets(e);
		if (affected == null || affected.isEmpty())
			return null;
		Widget max = null;
		long maxArea = 0;
		for (Widget target : affected) {
			boolean widgetIsLeftOfTarget = target.getAbsoluteLeft() - e.getAbsoluteLeft() > 0;
			boolean widgetIsTopOfTarget = target.getAbsoluteTop() - e.getAbsoluteTop() > 0;
			long collision = getCollisionArea(widgetIsLeftOfTarget ? e : target.getElement(), widgetIsLeftOfTarget ? target.getElement() : e,
					widgetIsTopOfTarget ? e : target.getElement(), widgetIsTopOfTarget ? target.getElement() : e);
			if (collision > maxArea || max == null) {
				max = target;
				maxArea = collision;
			}
		}
		return new Tuple<Widget, Long>(max, maxArea);
	}

	/**
	 * This method takes two different elements which are passed multiple times depending on their relative position and calculates the collision area in square
	 * pixels.
	 * 
	 * @param left
	 *            - the element which is further left than the other
	 * @param right
	 *            - the element which is further right than the other
	 * @param top
	 *            - the element which is further top than the other
	 * @param bottom
	 *            - the element which is further bottom than the other
	 * @return the collision area between the elements in square pixels
	 */
	protected long getCollisionArea(Element left, Element right, Element top, Element bottom) {
		int collX = Math.min(left.getAbsoluteLeft() + left.getOffsetWidth(), right.getAbsoluteLeft() + right.getOffsetWidth()) - right.getAbsoluteLeft();
		int collY = Math.min(top.getAbsoluteTop() + top.getOffsetHeight(), bottom.getAbsoluteTop() + bottom.getOffsetHeight()) - bottom.getAbsoluteTop();
		return collX * collY;
	}

	/**
	 * This method looks up all the drop targets which are intersecting with the given element. If the drop targets differ in their priorities ({@link Priority}
	 * ), only widgets of the highest priority are returned.
	 * 
	 * @param e
	 *            - a HTML element (typically the HTML element of the dragged widget)
	 * @return the drop target widgets which are intersecting with the given element and do have the highest priority
	 */
	protected Set<Widget> getAffectedDropTargets(Element e) {
		int w1X = e.getAbsoluteLeft();
		int w1Y = e.getAbsoluteTop();
		int w1Width = e.getOffsetWidth();
		int w1Height = e.getOffsetHeight();
		Map<Integer, HashSet<Widget>> targets = new HashMap<Integer, HashSet<Widget>>();
		for (Widget w2 : dropTargetRegistry.keySet()) {
			int w2X = w2.getAbsoluteLeft();
			int w2Y = w2.getAbsoluteTop();
			boolean xCollision = w1X < w2X ? w2X - w1X < w1Width : w1X - w2X < w2.getOffsetWidth();
			boolean yCollision = w1Y < w2Y ? w2Y - w1Y < w1Height : w1Y - w2Y < w2.getOffsetHeight();
			String idStyle = w2.getElement().getId() != null && !w2.getElement().getId().equals("") ? "hover-" + w2.getElement().getId() : null;
			if (xCollision && yCollision) {
				if (idStyle != null) {
					e.addClassName(idStyle);
				}
				DropTargetHandler h = dropTargetRegistry.get(w2);
				if (h != null) {
					int prio = h.getPriority().getValue();
					HashSet<Widget> widgetsForPrio = targets.get(prio);
					if (widgetsForPrio == null) {
						widgetsForPrio = new HashSet<Widget>();
						targets.put(prio, widgetsForPrio);
					}
					widgetsForPrio.add(w2);
				}
			}
			else if (idStyle != null) {
				e.removeClassName(idStyle);
			}
		}
		if (targets.isEmpty())
			return null;
		int maxprio = 0;
		for (Integer i : targets.keySet()) {
			if (i > maxprio) {
				maxprio = i;
			}
		}
		return targets.get(maxprio);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.dragndrop.client.DragNDrop.DragNDropIntf#setDropHandler(com.google.gwt.event.dom.client.HasMouseOverHandlers,
	 * ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler, boolean)
	 */
	@Override
	public void setDropHandler(HasMouseOverHandlers w, DropTargetHandler dropHandler, boolean hoverAware) {
		if (w instanceof Widget && w instanceof HasMouseOutHandlers) {
			if (hoverAware) {
				hoverDropTargets.put(dropHandler, new HashSet<String>());
			}
			dropTargetRegistry.put((Widget) w, dropHandler);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.dragndrop.client.DragNDrop.DragNDropIntf#removeDropHandler(com.google.gwt.event.dom.client.HasMouseOverHandlers)
	 */
	@Override
	public void removeDropHandler(HasMouseOverHandlers w) {
		dropTargetRegistry.remove(w);
	}
}
