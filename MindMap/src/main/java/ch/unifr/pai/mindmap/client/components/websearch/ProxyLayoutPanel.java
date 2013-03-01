package ch.unifr.pai.mindmap.client.components.websearch;

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

import java.util.ArrayList;
import java.util.List;

import ch.unifr.pai.twice.utils.device.client.UUID;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A component that makes use of the MPProxy mechanism of the toolkit that allows multiple users to interact with third party web pages and therefore to
 * collaboratively browse the web.
 * 
 * @author Oliver Schmid
 * 
 */
public class ProxyLayoutPanel extends DockLayoutPanel {

	private final List<String> history = new ArrayList<String>();
	private final List<String> forwardHistory = new ArrayList<String>();

	/**
	 * An iFrame that adapts to resizing container elements.
	 * 
	 * @author Oliver Schmid
	 * 
	 */
	private class ResizingFrame extends Frame implements RequiresResize {

		public ResizingFrame() {
			super();
			setStyleName("resizingFrame");
		}

		@Override
		public void onResize() {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					ResizingFrame.this.setWidth(getWidgetContainerElement(ResizingFrame.this).getOffsetWidth() + "px");
					ResizingFrame.this.setHeight(getWidgetContainerElement(ResizingFrame.this).getOffsetHeight() + "px");
				}
			});
		}

	}

	HorizontalPanel addressBar = new HorizontalPanel();
	Button b = new Button("back");
	Button f = new Button("forward");
	private boolean navigateByHistory;

	final TextBox url = new TextBox();
	private final HandlerRegistration initialLoader;
	private final ResizingFrame frame = new ResizingFrame();

	/**
	 * builds a wrapper around the frame providing an address bar and multi-user aware forward- and backward-buttons
	 */
	public ProxyLayoutPanel() {
		super(Unit.PX);
		addressBar.add(b);
		b.setHeight("100%");
		f.setHeight("100%");
		url.setHeight("100%");
		addressBar.add(f);
		addressBar.add(url);
		addressBar.setCellWidth(url, "100%");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (history.size() > 1) {
					String url = history.remove(history.size() - 2);
					forwardHistory.add(getCurrentFrameUrl());
					navigateByHistory = true;
					setUrl(url, false);
				}
			}
		});
		f.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (forwardHistory.size() > 0) {
					String url = forwardHistory.remove(forwardHistory.size() - 1);
					setUrl(url, false);
				}
			}
		});
		url.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setUrl(event.getValue(), true);
			}
		});
		url.setWidth("100%");
		addNorth(addressBar, 30);
		setUrl("mice/init?miceManaged=true&uuid=" + UUID.get(), false);
		initialLoader = frame.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
				load();
			}
		});
		add(frame);
	}

	/**
	 * Change the url of the frame
	 * 
	 * @param url
	 *            - the url of the original web page (without proxy prefix)
	 * @param init
	 *            - true if called within the load process
	 */
	public void setUrl(String url, boolean init) {
		if (init && !url.matches(".*//.*/.*"))
			url = url + "/";
		frame.setUrl(proxyUrlPrefix() + url);
	}

	/**
	 * @return the prefix of the current forward-proxy that shall be added to the url of the frame
	 */
	private String proxyUrlPrefix() {
		return Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/";
	}

	/**
	 * @return the original URL (without proxy prefix) of the current web page within the frame.
	 */
	private String getCurrentFrameUrl() {
		return IFrameElement.as(frame.getElement()).getContentDocument().getURL().substring(proxyUrlPrefix().length());

	}

	/**
	 * Initially load the frame and open a web page accessed through the provided forward proxy server
	 */
	private void load() {
		if (initialLoader != null) {
			initialLoader.removeHandler();
			frame.addLoadHandler(new LoadHandler() {

				@Override
				public void onLoad(LoadEvent event) {
					String urlString = getCurrentFrameUrl();
					url.setText(urlString);
					if (!navigateByHistory)
						history.add(urlString);
					navigateByHistory = false;
				}
			});
			setUrl("http://www.google.ch/", true);
		}
	}

}
