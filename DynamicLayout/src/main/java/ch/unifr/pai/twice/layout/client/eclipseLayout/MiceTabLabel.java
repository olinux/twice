package ch.unifr.pai.twice.layout.client.eclipseLayout;

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
import ch.unifr.pai.twice.dragndrop.client.intf.Draggable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * The tab label which implements the {@link Draggable} interface including a close button to remove the component from the layout as well as a reference to the
 * actual component widget
 * 
 * @author Oliver Schmid
 * 
 */
public class MiceTabLabel extends FocusPanel implements Draggable {

	private final Widget widget;
	private MiceLayoutTabPanel currentParent;
	private boolean initializedAsDraggable;
	private boolean selected;
	private final HorizontalPanel flowPanel = new HorizontalPanel();
	private final HTML html = new HTML();

	private final MiceResourceBundle resource = GWT.create(MiceResourceBundle.class);

	/**
	 * @param text
	 *            - the label used to mark the tab (html allowed)
	 * @param widget
	 *            - the actual component widget
	 */
	public MiceTabLabel(String text, Widget widget) {
		super();
		resource.miceLayoutStyle().ensureInjected();
		this.widget = widget;
		setWidget(flowPanel);
		flowPanel.add(html);
		html.setHTML(text);
		Image close = new Image(resource.closeButton());
		close.addStyleName(resource.miceLayoutStyle().tabButton());
		close.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				currentParent.remove(MiceTabLabel.this);
			}
		});
		flowPanel.add(close);
	}

	/**
	 * Sets the label of the tab (html allowed)
	 * 
	 * @param html
	 */
	public void setText(String html) {
		this.html.setHTML(html);
	}

	@Override
	public Widget getWidget() {
		return widget;
	}

	/**
	 * @return
	 */
	MiceLayoutTabPanel getCurrentParent() {
		return currentParent;
	}

	/**
	 * sets the parent {@link MiceLayoutTabPanel}
	 * 
	 * @param currentParent
	 */
	void setCurrentParent(MiceLayoutTabPanel currentParent) {
		this.currentParent = currentParent;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.dragndrop.client.intf.Draggable#isDraggable()
	 */
	@Override
	public boolean isDraggable() {
		return true;
	}

	/**
	 * set the property "initializedAsDraggable" to true
	 */
	void initializeAsDraggable() {
		this.initializedAsDraggable = true;
	}

	/**
	 * @return if the label is initialized already
	 */
	boolean isInitializedAsDraggable() {
		return this.initializedAsDraggable;
	}

	/**
	 * @param selected
	 *            sets the property "selected" this is for internal state holding only and does not affect the actual visualization of the tab
	 */
	void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return true if the tab is actually active (selected)
	 */
	boolean isSelected() {
		return selected;
	}

}
