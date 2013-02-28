package ch.unifr.pai.twice.dragndrop.client.factories;

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
import ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler;
import ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandlerAdapter;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * A special type of a {@link DropTargetHandler} which is highlighted if a dragged widget is hovering the drop target
 * 
 * @author Oliver Schmid
 * 
 */
public class HighlightDropTargetHandler extends DropTargetHandlerAdapter {

	private final Widget highlightElement;

	public HighlightDropTargetHandler(Widget highlightElement) {
		this(null, highlightElement);
	}

	public HighlightDropTargetHandler(Priority p, Widget highlightElement) {
		super(p);
		this.highlightElement = highlightElement;
	}

	@Override
	public void onHover(String deviceId, Widget widget, Element dragProxy, Event event, Double intersectionPercentage, Double intersectionPercentageWithTarget) {
		if (highlightElement != null)
			highlightElement.addStyleName("hover");
	}

	@Override
	public void onHoverEnd(String deviceId, Widget widget, Element dragProxy, Event event) {
		if (highlightElement != null)
			highlightElement.removeStyleName("hover");
	}

}
