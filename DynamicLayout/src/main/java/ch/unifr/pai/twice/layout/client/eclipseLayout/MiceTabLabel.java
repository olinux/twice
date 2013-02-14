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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MiceTabLabel extends FocusPanel implements Draggable{

	private Widget widget;
	private MiceLayoutTabPanel currentParent;
	private boolean initializedAsDraggable;
	private boolean selected;
	private HorizontalPanel flowPanel = new HorizontalPanel();
	private HTML html = new HTML();
	
	private MiceResourceBundle resource = GWT.create(MiceResourceBundle.class);

	public MiceTabLabel(String text, Widget widget) {
		super();		
		resource.miceLayoutStyle().ensureInjected();
		this.widget = widget;
		setWidget(flowPanel);
		flowPanel.add(html);
		html.setHTML(text);
		Image close = new Image(resource.closeButton());
		close.addStyleName(resource.miceLayoutStyle().tabButton());
		close.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				currentParent.remove(MiceTabLabel.this);
			}});
		flowPanel.add(close);
	}
	
	public void setText(String html){
		this.html.setHTML(html);
	}

	public Widget getWidget(){
		return widget;
	}
	
	
	public MiceLayoutTabPanel getCurrentParent() {
		return currentParent;
	}

	public void setCurrentParent(MiceLayoutTabPanel currentParent) {
		this.currentParent = currentParent;
	}

	@Override
	public boolean isDraggable() {
		return true;
	}
	
	public void initializeAsDraggable(){
		this.initializedAsDraggable = true;
	}
	
	public boolean isInitializedAsDraggable(){
		return this.initializedAsDraggable;
	}
	
	public void setSelected(boolean selected){
		this.selected = selected;
	}
	
	public boolean isSelected(){
		return selected;
	}
	
}
