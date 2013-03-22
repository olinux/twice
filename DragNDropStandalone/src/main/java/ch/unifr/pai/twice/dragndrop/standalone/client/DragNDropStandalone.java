package ch.unifr.pai.twice.dragndrop.standalone.client;

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

import ch.unifr.pai.twice.dragndrop.client.DragNDrop;
import ch.unifr.pai.twice.dragndrop.client.configuration.DragConfiguration;
import ch.unifr.pai.twice.dragndrop.client.factories.DropTargetHandlerFactory.Priority;
import ch.unifr.pai.twice.dragndrop.client.intf.DragNDropHandler;
import ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler;
import ch.unifr.pai.twice.multipointer.provider.client.MultiCursorController;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * An example application showing the drag and drop functionality with multiple pointer support
 * 
 * @author Oliver Schmid
 * 
 */
public class DragNDropStandalone implements EntryPoint {

	@Override
	public void onModuleLoad() {

		// Enable multicursor support
		MultiCursorController c = GWT.create(MultiCursorController.class);
		c.start();

		DockLayoutPanel mainpanel = new DockLayoutPanel(Unit.PX);
		final AbsolutePanel p = new AbsolutePanel();
		DraggableLabel l = new DraggableLabel();
		l.setText("DRAG ME");
		mainpanel.add(p);
		RootLayoutPanel.get().add(mainpanel);
		p.add(l);
		final FocusPanel drop = new FocusPanel();
		drop.setWidth("500px");
		drop.setHeight("400px");
		drop.getElement().getStyle().setBackgroundColor("green");
		p.add(drop);
		p.setWidgetPosition(drop, 400, 200);

		// define the green focus panel to be a drop target handler
		DragNDrop.setDropHandler(drop, new DropTargetHandler() {

			@Override
			public void onHoverEnd(String deviceId, Widget widget, Element dragProxy, Event event) {
				drop.getElement().getStyle().setBackgroundColor("yellow");

			}

			@Override
			public void onHover(String deviceId, Widget widget, Element dragProxy, Event event, Double intersectionPercentage,
					Double intersectionPercentageWithTarget) {
				drop.getElement().getStyle().setBackgroundColor("red");
			}

			@Override
			public boolean onDrop(String deviceId, Widget widget, Element dragProxy, Event event, Double intersectionPercentage,
					Double intersectionPercentageWithTarget) {
				Window.alert("Dropped");
				return false;
			}

			@Override
			public Priority getPriority() {
				return Priority.NORMAL;
			}
		}, true);

		// Make the label draggable
		DragNDrop.makeDraggable(l, DragConfiguration.withProxy(new DragNDropHandler() {

			@Override
			public void onStartDrag(String deviceId, Widget draggedWidget) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onEndOfDrop(String deviceId, Widget draggedWidget, int dragProxyLeft, int dragProxyTop, Event event) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onDrop(String deviceId, Widget draggedWidget, int dragProxyLeft, int dragProxyTop, Event event, DropTargetHandler dropTarget,
					boolean outOfBox) {
				p.setWidgetPosition(draggedWidget, dragProxyLeft - p.getAbsoluteLeft(), dragProxyTop - p.getAbsoluteTop());
				return true;
			}
		}));
	}

}
