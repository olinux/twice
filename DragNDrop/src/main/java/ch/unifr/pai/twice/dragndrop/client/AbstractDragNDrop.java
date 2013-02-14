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

import ch.unifr.pai.twice.dragndrop.client.configuration.DragConfiguration;
import ch.unifr.pai.twice.dragndrop.client.intf.Draggable;
import ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler;
import ch.unifr.pai.twice.dragndrop.client.utils.Triple;
import ch.unifr.pai.twice.dragndrop.client.utils.Tuple;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

abstract class AbstractDragNDrop {

	protected static Map<String, AbstractDragNDrop> activeHandlers = new HashMap<String, AbstractDragNDrop>();
	protected static final int DOWNUNTILDRAGTHRESHOLDMS = 200;
	protected  HandlerRegistration handlerReg;
	protected final static String DRAGGINGSTYLENAME = "dragging";
	
	protected Widget w;
	protected int percOffsetX;
	protected int percOffsetY;
	protected String originDisplay;
	protected DragConfiguration conf;
	protected static Map<Widget, DropTargetHandler> dropTargetRegistry = new HashMap<Widget, DropTargetHandler>();
	protected static Map<DropTargetHandler, Set<String>> hoverDropTargets = new HashMap<DropTargetHandler, Set<String>>();
	protected static Map<String, DropTargetHandler> currentHovering = new HashMap<String, DropTargetHandler>();
	
	void initialize(Widget w, int offsetX, int offsetY, DragConfiguration conf){
		this.w = w;
		this.percOffsetX = (int) (100.0 / w.getOffsetWidth() * offsetX);
		this.percOffsetY = (int) (100.0 / w.getOffsetHeight() * offsetY);
		this.originDisplay = w.getElement().getStyle().getDisplay();
		this.conf = conf;
	}
	
	
	private NativePreviewHandler nativePreviewHandler = createNativePreviewHandler();
	
	protected abstract NativePreviewHandler createNativePreviewHandler();
	
	private static class ValueHolder<T> {
		private T value;

		public void setValue(T value) {
			this.value = value;
		}

		public T getValue() {
			return value;
		}

	}
	
	protected static interface Callback<T>{
		void onDone(T value);
	}
	
	
	protected abstract void addDragHandler(Draggable w, final Callback<NativeEvent> callback);
	
	protected abstract HandlerRegistration registerEndHandler(Draggable w, final Callback<NativeEvent> callback);
	
	protected abstract String getDeviceId(NativeEvent event);
	
	protected abstract int getX(NativeEvent event);
	
	protected abstract int getY(NativeEvent event);
	
	
	void makeDraggable(final Draggable w,
			final DragConfiguration conf, final Element dragProxyTemplate) {
		if (w instanceof Widget) {
			 ((Widget) w).addStyleName("draggable");	
			 addDragHandler(w, new Callback<NativeEvent>() {
				@Override
				public void onDone(NativeEvent event) {
					if (w.isDraggable()) {
						final String deviceId = getDeviceId(event);
						final ValueHolder<Boolean> drag = new ValueHolder<Boolean>();
						drag.setValue(true);
						
						final HandlerRegistration end = registerEndHandler(w, new Callback<NativeEvent>(){

							@Override
							public void onDone(NativeEvent event) {
								if (getDeviceId(event).equals(
												deviceId))
									drag.setValue(false);
							}});
						final int offsetX = getX(event)
								- ((Widget)w).getElement().getAbsoluteLeft();
						final int offsetY = getY(event)
								- ((Widget)w).getElement().getAbsoluteTop();
						DOM.eventPreventDefault((Event) event);						
						Timer t = new Timer() {
							
							@Override
							public void run() {
								end.removeHandler();
								if (drag.getValue() && !((Widget)w).getStyleName().contains(DRAGGINGSTYLENAME))
									startDrag((Widget) w, deviceId, offsetX, offsetY, conf, dragProxyTemplate);								
							}
						};
						t.schedule(DOWNUNTILDRAGTHRESHOLDMS);
					}
				}
			});
		}
	}
	
	protected abstract AbstractDragNDrop createDragNDrop();

	// Drag handler
	private void startDrag(Widget w, String deviceId, int offsetX, int offsetY,
			DragConfiguration conf, Element dragProxyTemplate) {
			if (conf == null)
				conf = DragConfiguration.withProxy();
			if (conf.getDragNDropHandler() != null) {
				conf.getDragNDropHandler().onStartDrag(deviceId, w);
			}
			conf.setDragProxy(DOM.clone(dragProxyTemplate==null ? w.getElement() : (com.google.gwt.user.client.Element)dragProxyTemplate, true));
			conf.getDragProxy().addClassName("drag-proxy");
			conf.getDragProxy().getStyle().setPosition(Position.ABSOLUTE);
			RootPanel.getBodyElement().appendChild(conf.getDragProxy());
			conf.getDragProxy().getStyle()
					.setLeft(w.getElement().getAbsoluteLeft(), Unit.PX);
			conf.getDragProxy().getStyle()
					.setTop(w.getElement().getAbsoluteTop(), Unit.PX);
			conf.getDragProxy().getStyle().setPosition(Position.ABSOLUTE);
//			DOM.eventPreventDefault((Event) event.getNativeEvent());
			if (activeHandlers.isEmpty())
				addEventPreview();
			AbstractDragNDrop d = createDragNDrop();
			d.initialize(w, offsetX, offsetY, conf);
			activeHandlers.put(deviceId, d);
			if (!conf.isWithProxy()) {
				w.getElement().getStyle().setDisplay(Display.BLOCK);
			} else {
				w.addStyleName(DRAGGINGSTYLENAME);
			}
	}

	protected void onDrag(String deviceId, Event event) {
		setPosition(getX(event), getY(event), true);
		if (!hoverDropTargets.isEmpty()) {
			AbstractDragNDrop h = activeHandlers.get(deviceId);
			if (h != null) {
				Triple<Double, Double, DropTargetHandler> handlerOfDropTarget = h
						.getHandlerOfDropTarget();
				DropTargetHandler dropTarget = null;
				Double intersectionPercentage = null;
				Double intersectionPercentageWithTarget = null;
				if (handlerOfDropTarget != null) {
					intersectionPercentage = handlerOfDropTarget.getFirst();
					intersectionPercentageWithTarget = handlerOfDropTarget
							.getSecond();
					dropTarget = handlerOfDropTarget.getThird();
				}
				handleHover(dropTarget, intersectionPercentage,
						intersectionPercentageWithTarget, deviceId, event, h.w,
						h.conf.getDragProxy());
			}
		}
	}

	private void handleHover(DropTargetHandler dropTarget,
			Double intersectionPercentage,
			Double intersectionPercentageWithTarget, String deviceId,
			Event event, Widget widget, Element dragProxy) {
		DropTargetHandler currentHover = currentHovering.get(deviceId);
		if (currentHover != null && currentHover == dropTarget) {
			return;
		} else {
			if (currentHover != null) {
				hoverDropTargets.get(currentHover).remove(deviceId);
				if (hoverDropTargets.get(currentHover).size() == 0)
					currentHover.onHoverEnd(deviceId, widget, dragProxy, event);
			}
			if (dropTarget == null || hoverDropTargets.get(dropTarget) == null) {
				currentHovering.remove(deviceId);
			} else {
				hoverDropTargets.get(dropTarget).add(deviceId);
				currentHovering.put(deviceId, dropTarget);
				dropTarget.onHover(deviceId, widget, dragProxy, event,
						intersectionPercentage,
						intersectionPercentageWithTarget);
			}
		}
	}

	protected void endDrag(String deviceId, Event event) {
		AbstractDragNDrop h = activeHandlers.get(deviceId);
		if (h != null && h.w != null) {
			h.w.removeStyleName(DRAGGINGSTYLENAME);
			h.w.removeStyleName("drag-proxy");
			Triple<Double, Double, DropTargetHandler> handlerOfDropTarget = h
					.getHandlerOfDropTarget();
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
				boolean resetByOwner = !h.conf.getDragNDropHandler().onDrop(
						deviceId, h.w, proxyLeft, proxyTop, event, dropTarget, outOfBox);
				// boolean resetByDropTarget = false;
				handleHover(null, Double.valueOf(0), Double.valueOf(0),
						deviceId, event, h.w, h.conf.getDragProxy());
				if (dropTarget != null) {
					Scheduler.get().scheduleDeferred(
							h.new DropCommand(dropTarget, deviceId, h, event,
									handlerOfDropTarget.getFirst(),
									handlerOfDropTarget.getSecond(), proxyLeft, proxyTop,
									resetByOwner));
				} else if (resetByOwner) {
					h.resetPosition();
					h.conf.getDragNDropHandler().onEndOfDrop(deviceId, h.w, proxyLeft, proxyTop,
							event);
				} else {
					h.conf.getDragNDropHandler().onEndOfDrop(deviceId, h.w, proxyLeft, proxyTop,
							event);
				}
			}

		}

		activeHandlers.remove(deviceId);
		if (activeHandlers.isEmpty())
			removeEventPreview();
	}

	private class DropCommand implements Command {
		private DropTargetHandler dropTarget;
		private String deviceId;
		private AbstractDragNDrop h;
		private Event event;
		private Double intersectionPercentage;
		private Double intersectionPercentageWithTarget;
		private boolean resetByOwner;
		private int proxyLeft;
		private int proxyTop;
		
		public DropCommand(DropTargetHandler dropTarget, String deviceId,
				AbstractDragNDrop h, Event event, Double intersectionPercentage,
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

		@Override
		public void execute() {
			boolean resetByDropTarget = !dropTarget.onDrop(deviceId, h.w,
					conf.getDragProxy(), event, intersectionPercentage,
					intersectionPercentageWithTarget);
			if (resetByDropTarget || resetByOwner) {
				h.resetPosition();
			}
			h.conf.getDragNDropHandler().onEndOfDrop(deviceId, h.w, proxyLeft, proxyTop, event);
		}

	}

	private Triple<Double, Double, DropTargetHandler> getHandlerOfDropTarget() {
		Tuple<Widget, Long> target = getHoverDropTarget(conf.getDragProxy());
		if (target == null || target.getFirst() == null
				|| target.getSecond() == null)
			return null;
		else
			return new Triple<Double, Double, DropTargetHandler>(
					100.0 * target.getSecond()
							/ getAreaOfWidget(conf.getDragProxy()), 100.0
							* target.getSecond()
							/ getAreaOfWidget(target.getFirst().getElement()),
					dropTargetRegistry.get(target.getFirst()));
	}

	private long getAreaOfWidget(Element e) {
		return e.getOffsetHeight() * e.getOffsetHeight();
	}

	private boolean outOfBox(Event event) {
		return getX(event) < conf.getMinX()
				|| getY(event) < conf.getMinY()
				|| getX(event) > conf.getMaxX()
				|| getY(event) > conf.getMaxY();
	}

	// Positioning

	private void setPosition(int x, int y, boolean withOffset) {
		Element dragProxy = conf.getDragProxy();
		int newX = x
				- (withOffset ? (int) (dragProxy.getOffsetWidth() / 100.0 * percOffsetX)
						: 0);
		int newY = y
				- (withOffset ? (int) (dragProxy.getOffsetHeight() / 100.0 * percOffsetY)
						: 0);
		dragProxy.getStyle().setLeft(
				Math.max(
						conf.getMinX(),
						Math.min(newX,
								conf.getMaxX() - dragProxy.getOffsetWidth())),
				Unit.PX);
		dragProxy.getStyle().setTop(
				Math.max(
						conf.getMinY(),
						Math.min(newY,
								conf.getMaxY() - dragProxy.getOffsetHeight())),
				Unit.PX);
	}

	private void resetPosition() {
		if (conf.isWithProxy()) {
			conf.getDragProxy().removeFromParent();
			conf.setDragProxy(null);
		} else {
			w.getElement().getStyle().setProperty("display", originDisplay);
		}
		for (Widget w2 : dropTargetRegistry.keySet()) {
			String idStyle = w2.getElement().getId() != null
					&& !w2.getElement().getId().equals("") ? "hover-"
					+ w2.getElement().getId() : null;
			if (idStyle != null) {
				w.removeStyleName(idStyle);
			}
		}
	}

	// Helper methods

	private void addEventPreview() {
		handlerReg = Event.addNativePreviewHandler(nativePreviewHandler);
	}

	private void removeEventPreview() {
		if (handlerReg != null)
			handlerReg.removeHandler();
	}

	// Separate events by type

	private Tuple<Widget, Long> getHoverDropTarget(Element e) {
		Set<Widget> affected = getAffectedDropTargets(e);
		if (affected == null || affected.isEmpty())
			return null;
		Widget max = null;
		long maxArea = 0;
		for (Widget target : affected) {
			boolean widgetIsLeftOfTarget = target.getAbsoluteLeft()
					- e.getAbsoluteLeft() > 0;
			boolean widgetIsTopOfTarget = target.getAbsoluteTop()
					- e.getAbsoluteTop() > 0;
			long collision = getCollisionArea(
					widgetIsLeftOfTarget ? e : target.getElement(),
					widgetIsLeftOfTarget ? target.getElement() : e,
					widgetIsTopOfTarget ? e : target.getElement(),
					widgetIsTopOfTarget ? target.getElement() : e);
			if (collision > maxArea || max == null) {
				max = target;
				maxArea = collision;
			}
		}
		return new Tuple<Widget, Long>(max, maxArea);
	}

	private long getCollisionArea(Element left, Element right,
			Element top, Element bottom) {
		int collX = Math.min(left.getAbsoluteLeft() + left.getOffsetWidth(),
				right.getAbsoluteLeft() + right.getOffsetWidth())
				- right.getAbsoluteLeft();
		int collY = Math.min(top.getAbsoluteTop() + top.getOffsetHeight(),
				bottom.getAbsoluteTop() + bottom.getOffsetHeight())
				- bottom.getAbsoluteTop();
		return collX * collY;
	}

	private Set<Widget> getAffectedDropTargets(Element e) {
		int w1X = e.getAbsoluteLeft();
		int w1Y = e.getAbsoluteTop();
		int w1Width = e.getOffsetWidth();
		int w1Height = e.getOffsetHeight();
		Map<Integer, HashSet<Widget>> targets = new HashMap<Integer, HashSet<Widget>>();
		for (Widget w2 : dropTargetRegistry.keySet()) {
			int w2X = w2.getAbsoluteLeft();
			int w2Y = w2.getAbsoluteTop();
			boolean xCollision = w1X < w2X ? w2X - w1X < w1Width
					: w1X - w2X < w2.getOffsetWidth();
			boolean yCollision = w1Y < w2Y ? w2Y - w1Y < w1Height
					: w1Y - w2Y < w2.getOffsetHeight();
			String idStyle = w2.getElement().getId() != null
					&& !w2.getElement().getId().equals("") ? "hover-"
					+ w2.getElement().getId() : null;
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
			} else if (idStyle != null) {
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
	
	

	void setDropHandler(HasMouseOverHandlers w,
			DropTargetHandler dropHandler, boolean hoverAware) {
		if (w instanceof Widget && w instanceof HasMouseOutHandlers) {
			if (hoverAware) {
				hoverDropTargets.put(dropHandler, new HashSet<String>());
			}
			dropTargetRegistry.put((Widget) w, dropHandler);
		}
	}

	void removeDropHandler(HasMouseOverHandlers w) {
		dropTargetRegistry.remove(w);
	}
}
