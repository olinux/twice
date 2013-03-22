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
import ch.unifr.pai.twice.multipointer.provider.client.MultiCursorController;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * This is a browser in a browser. It provides URL bars to enter standard URLs which then are translated to the proxy server URLs and the component provides
 * back and forward buttons per frame as well as multi-cursor aware scroll bars
 * 
 * @author Oliver Schmid
 * 
 */
public class MPBrowser implements EntryPoint {

	private final MultiCursorController multiCursor = new MultiCursorController();
	HorizontalPanel navig = new HorizontalPanel();
	HorizontalPanel navig2 = new HorizontalPanel();
	Frame frame = new NamedFrame("mpFrame");
	Frame frame2 = new NamedFrame("mpFrame2");
	TextBox textBox = new TextBox();
	TextBox textBox2 = new TextBox();
	DockLayoutPanel scrollBar = new DockLayoutPanel(Unit.PX);
	DockLayoutPanel scrollBar2 = new DockLayoutPanel(Unit.PX);
	SplitLayoutPanel browserSplit = new SplitLayoutPanel();

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

	Button forward2 = new Button("Forward", new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			History.forward();
		}
	});
	Button backward2 = new Button("Backward", new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			History.back();
		}
	});

	Button scrollDown2 = new Button("Down", new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			Document d = IFrameElement.as(frame2.getElement()).getContentDocument();
			if (d != null) {
				d.getBody().setScrollTop(Math.min(d.getBody().getScrollTop() + 20, d.getBody().getScrollHeight()));
			}
		}
	});
	Button scrollUp2 = new Button("Up", new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			Document d = IFrameElement.as(frame2.getElement()).getContentDocument();
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

	private void updateScrollBar2() {
		Document d = IFrameElement.as(frame2.getElement()).getContentDocument();
		boolean scrollVertical = d.getBody().getScrollHeight() > frame2.getOffsetHeight();
		boolean scrollHorizontal = d.getBody().getScrollWidth() > frame2.getOffsetWidth();
		scrollDown2.setEnabled(scrollVertical);
		scrollUp2.setEnabled(scrollVertical);
		scrollBar2.getElement().getStyle().setBackgroundColor(scrollVertical ? "lightgrey" : "lightgrey");
	}

	@Override
	public void onModuleLoad() {
		multiCursor.start();
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
		navig.setWidth("100%");
		Storage s = Storage.getSessionStorageIfSupported();
		if (s != null) {
			String type = s.getItem("ch.unifr.pai.mice.deviceType");
			if (type == null || !type.equals("multicursor")) {
				s.setItem("ch.unifr.pai.mice.deviceType", "multicursor");
			}
		}
		frame.setUrl(GWT.getHostPageBaseURL() + "http://www.google.ch");
		frame.setHeight("100%");
		frame.setWidth("100%");
		frame.getElement().setAttribute("scrolling", "no");
		frame.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
				if (frame.getUrl() != null && !frame.getUrl().startsWith(GWT.getHostPageBaseURL())) {
					frame.setUrl(GWT.getHostPageBaseURL() + frame.getUrl());
					updateScrollBar();
				}
				Document d = IFrameElement.as(frame.getElement()).getContentDocument();
				textBox.setValue(d.getURL());
			}
		});
		scrollBar2.getElement().getStyle().setBackgroundColor("lightgrey");
		scrollBar2.addNorth(scrollUp2, 30);
		scrollBar2.addSouth(scrollDown2, 30);
		scrollBar2.add(new HTML());
		textBox2.setWidth("100%");
		navig2.add(backward2);
		navig2.add(forward2);
		navig2.setCellWidth(backward2, "50px");
		navig2.setCellWidth(forward2, "50px");
		navig2.add(textBox2);
		navig2.setWidth("100%");

		frame2.setUrl(GWT.getHostPageBaseURL() + "http://www.google.ch");
		frame2.setHeight("100%");
		frame2.setWidth("100%");
		frame2.getElement().setAttribute("scrolling", "no");
		frame2.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
				if (frame2.getUrl() != null && !frame2.getUrl().startsWith(GWT.getHostPageBaseURL())) {
					frame2.setUrl(GWT.getHostPageBaseURL() + frame2.getUrl());
					updateScrollBar2();
				}
				Document d = IFrameElement.as(frame2.getElement()).getContentDocument();
				textBox2.setValue(d.getURL());
			}
		});

		DockLayoutPanel p = new DockLayoutPanel(Unit.PX);
		p.addNorth(navig, 25);
		p.addEast(scrollBar, 30);
		p.add(frame);
		browserSplit.addNorth(p, 500);

		DockLayoutPanel p2 = new DockLayoutPanel(Unit.PX);
		p2.addNorth(navig2, 25);
		p2.addEast(scrollBar2, 30);
		p2.add(frame2);
		browserSplit.add(p2);

		RootLayoutPanel.get().add(browserSplit);
	}

}
