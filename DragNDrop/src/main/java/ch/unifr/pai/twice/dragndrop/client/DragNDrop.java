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
import ch.unifr.pai.twice.multipointer.client.MultiCursorController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.user.client.ui.Widget;

public class DragNDrop {

	private static DragNDropIntf handler = GWT.create(MPDragNDrop.class);
	private MultiCursorController cursorController;
	
	private DragNDrop(MultiCursorController controller, Widget w, int offsetX, int offsetY, DragConfiguration conf) {
		this.cursorController = controller;
		handler.initialize(w, offsetX, offsetY, conf);
	}

	// Register widget to be draggable

	public static void makeDraggable(Draggable w) { 
		makeDraggable(w, (DragConfiguration)null);
	}
	
	public static void makeDraggable(Draggable w, Element dragProxyTemplate){
		handler.makeDraggable(w, null, dragProxyTemplate);
	}

	public static void makeDraggable(Draggable w, DragConfiguration conf){
		handler.makeDraggable(w, conf, null);
	}
	
	public static void makeDraggable(Draggable w, DragConfiguration conf, Element dragProxyTemplate){
		handler.makeDraggable(w, conf, dragProxyTemplate);
	}
	

	public static void setDropHandler(HasMouseOverHandlers w,
			DropTargetHandler dropHandler, boolean hoverAware) {
		handler.setDropHandler(w, dropHandler, hoverAware);
	}

	public static void removeDropHandler(HasMouseOverHandlers w) {
		handler.removeDropHandler(w);
	}
	
	
	static interface DragNDropIntf {

		void initialize(Widget w, int offsetX, int offsetY, DragConfiguration conf);
		
		void makeDraggable(final Draggable w,
				final DragConfiguration conf, final Element dragProxyTemplate);
		
		void setDropHandler(HasMouseOverHandlers w,
				DropTargetHandler dropHandler, boolean hoverAware);
		
		void removeDropHandler(HasMouseOverHandlers w);
	}

	
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
