package ch.unifr.pai.twice.widgets.mpbrowser.client;
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
import ch.unifr.pai.twice.multipointer.provider.client.widgets.MultiFocusTextBox;
import ch.unifr.pai.twice.utils.device.client.DeviceType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

public class BrowserWindow extends DockLayoutPanel{
	
	
	Frame frame;
	HorizontalPanel navig = new HorizontalPanel();
	MultiFocusTextBox textBox = new MultiFocusTextBox();
	DockLayoutPanel scrollBar = new DockLayoutPanel(Unit.PX);
	
	public BrowserWindow(String frameName){
		super(Unit.PX);
		frame = new NamedFrame(frameName);
		scrollBar.getElement().getStyle().setBackgroundColor("lightgrey");
		scrollBar.addNorth(scrollUp, 30);
		scrollBar.addSouth(scrollDown, 30);
		scrollBar.add(new HTML());
		textBox.setWidth("100%");
		navig.add(backward);
		navig.add(forward);
		navig.setCellWidth(backward, "50px");
		navig.setCellWidth(forward, "50px");
		navig.add(textBox);
		navig.add(go);
		navig.setCellWidth(go, "50px");
		navig.setWidth("100%");
		
		frame.setUrl("https://duckduckgo.com/?deviceType=multicursor");
		frame.setHeight("100%");
		frame.setWidth("100%");
		frame.getElement().setAttribute("scrolling", "no");
		frame.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
				if(!frame.getUrl().contains(DeviceType.MULTICURSOR.name().toLowerCase())){
					String appendParam = (frame.getUrl().contains("?") ? "&":"?")+"deviceType="+DeviceType.MULTICURSOR.name().toLowerCase();
					frame.setUrl(frame.getUrl()+appendParam);
					updateScrollBar();
				}
				Document d = IFrameElement.as(frame.getElement()).getContentDocument();
				textBox.setValue(d.getURL());
			}
		});
		addNorth(navig, 25);
		addEast(scrollBar, 30);
		add(frame);
	}
	
	Button forward = new Button("Forward", new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			History.forward();
		}
	});
	Button backward = new Button("Backward", new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			History.back();
		}
	});
	Button go = new Button("Go", new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			frame.setUrl(textBox.getValue());
		}
	});


	Button scrollDown = new Button("Down", new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			Document d = IFrameElement.as(frame.getElement()).getContentDocument();
			if (d != null) {
				d.getBody().setScrollTop(Math.min(d.getBody().getScrollTop() + 20, d.getBody().getScrollHeight()));
			}
		}
	});
	Button scrollUp = new Button("Up", new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			Document d = IFrameElement.as(frame.getElement()).getContentDocument();
			if (d != null) {
				d.getBody().setScrollTop(Math.max(d.getBody().getScrollTop() - 20, 0));
			}
		}
	});
	
	
	private void updateScrollBar() {
		Document d = IFrameElement.as(frame.getElement()).getContentDocument();
		boolean scrollVertical = d.getBody().getScrollHeight() > frame.getOffsetHeight();
		boolean scrollHorizontal = d.getBody().getScrollWidth() > frame.getOffsetWidth();
		scrollDown.setEnabled(scrollVertical);
		scrollUp.setEnabled(scrollVertical);
		scrollBar.getElement().getStyle().setBackgroundColor(scrollVertical ? "lightgrey" : "lightgrey");
	}
	
}
