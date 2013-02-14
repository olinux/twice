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

public class DragConfiguration {
	
	private final DragNDropHandler handler;
	private final Widget boundaryBox;
	private Element dragProxy;
	private final boolean withProxy;

	public static DragConfiguration withoutProxy() {
		return withoutProxy(null, null);
	}
	
	public static DragConfiguration withProxy(){
		return withProxy(null, null);
	}
	
	public static DragConfiguration withoutProxy(DragNDropHandler handler){
		return withoutProxy(handler, null);
	}
	
	public static DragConfiguration withProxy(DragNDropHandler handler) {
		return withProxy(handler, null);
	}
	
	public static DragConfiguration withoutProxy(Widget boundaryBox) {
		return withoutProxy(null, boundaryBox);
	}
	
	public static DragConfiguration withProxy(Widget boundaryBox) {
		return withProxy(null, boundaryBox);
	}
	
	public static DragConfiguration withoutProxy(DragNDropHandler handler, Widget boundaryBox) {
		return new DragConfiguration(handler, boundaryBox, false);
	}
	
	public static DragConfiguration withProxy(DragNDropHandler handler, Widget boundaryBox) {
		return new DragConfiguration(handler, boundaryBox, true);
	}	

	private DragConfiguration(DragNDropHandler handler, Widget boundaryBox, boolean withProxy) {
		this.handler = handler != null ? handler : DropHandlerFactory.defaultHandler();
		this.boundaryBox = boundaryBox;
		this.withProxy = withProxy;
	}

	public DragNDropHandler getDragNDropHandler() {
		return handler;
	}

	public int getMinX() {
		return boundaryBox != null ? boundaryBox.getAbsoluteLeft() : 0;
	}

	public int getMaxX() {
		return boundaryBox != null ? boundaryBox.getAbsoluteLeft()
				+ boundaryBox.getOffsetWidth() : Window.getClientWidth();
	}

	public int getMinY() {
		return boundaryBox != null ? boundaryBox.getAbsoluteTop() : 0;
	}

	public int getMaxY() {
		return boundaryBox != null ? boundaryBox.getAbsoluteTop()
				+ boundaryBox.getOffsetHeight() : Window.getClientHeight();
	}
	
	public boolean isWithProxy(){
		return withProxy;
	}
	
	public Element getDragProxy(){
		return dragProxy;
	}
	
	public void setDragProxy(Element dragProxy){
		if(dragProxy.getId()!=null && !dragProxy.getId().equals(""))
			dragProxy.setId(dragProxy.getId()+"PROXY");
		this.dragProxy = dragProxy;
	}

}
