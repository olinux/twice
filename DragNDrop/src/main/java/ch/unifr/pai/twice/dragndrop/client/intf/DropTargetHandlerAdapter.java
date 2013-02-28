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
 * An adapter of the {@link DropTargetHandler} providing typical standard implementations and values
 * 
 * @author Oliver Schmid
 * 
 */
public class DropTargetHandlerAdapter implements DropTargetHandler {

	public DropTargetHandlerAdapter() {
		this(null);
	}

	public DropTargetHandlerAdapter(Priority p) {
		if (p == null)
			p = Priority.NORMAL;
		this.p = p;
	}

	private Priority p = Priority.NORMAL;

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler#getPriority()
	 */
	@Override
	public Priority getPriority() {
		return p;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler#onDrop(java.lang.String, com.google.gwt.user.client.ui.Widget,
	 * com.google.gwt.dom.client.Element, com.google.gwt.user.client.Event, java.lang.Double, java.lang.Double)
	 */
	@Override
	public boolean onDrop(String deviceId, Widget widget, Element dragProxy, Event event, Double intersectionPercentage, Double intersectionPercentageWithTarget) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler#onHover(java.lang.String, com.google.gwt.user.client.ui.Widget,
	 * com.google.gwt.dom.client.Element, com.google.gwt.user.client.Event, java.lang.Double, java.lang.Double)
	 */
	@Override
	public void onHover(String deviceId, Widget widget, Element dragProxy, Event event, Double intersectionPercentage, Double intersectionPercentageWithTarget) {

	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler#onHoverEnd(java.lang.String, com.google.gwt.user.client.ui.Widget,
	 * com.google.gwt.dom.client.Element, com.google.gwt.user.client.Event)
	 */
	@Override
	public void onHoverEnd(String deviceId, Widget widget, Element dragProxy, Event event) {
	}

}
