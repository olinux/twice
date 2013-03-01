package ch.unifr.pai.twice.layout.client.mobile;

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
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Utility functionalities for mobile use
 * 
 * @author Oliver Schmid
 * 
 */
public class MobileUtils {

	/**
	 * Add necessary html tags to ensure unified initial zoom levels of the web page, fullscreen establishment and add proprietary tags (e.g.
	 * "apple-mobile-web-app-capable" for extended functionalities).
	 * 
	 * The main purpose is to establish a "native" look of the application even within the boundaries of the web browser.
	 */
	public static void preparePage() {
		RootPanel.getBodyElement().getStyle().setHeight(100, Unit.PCT);
		RootPanel.getBodyElement().getStyle().setOverflow(Overflow.HIDDEN);
		RootPanel.getBodyElement().getStyle().setMargin(0, Unit.PX);
		RootPanel.getBodyElement().getStyle().setPadding(0, Unit.PX);
		Document.get().getDocumentElement().getStyle().setProperty("minHeight", "300px");
		Document.get().getDocumentElement().getStyle().setHeight(100, Unit.PCT);
		NodeList<Element> tags = Document.get().getElementsByTagName("head");
		Element head;
		if (tags.getLength() > 0) {
			head = tags.getItem(0);
		}
		else {
			head = DOM.createElement("head");
			Document.get().insertFirst(head);
		}
		Element meta = DOM.createElement("meta");
		meta.setAttribute("name", "viewport");
		meta.setAttribute("content", "width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0");
		head.appendChild(meta);
		LinkElement e = Document.get().createLinkElement();
		e.setRel("stylesheet");
		e.setHref(GWT.getModuleBaseURL() + "master.css");
		head.appendChild(e);

		Element iphoneFullscreen = DOM.createElement("meta");
		iphoneFullscreen.setAttribute("name", "apple-touch-fullscreen");
		iphoneFullscreen.setAttribute("content", "yes");
		head.appendChild(iphoneFullscreen);

		Element iphoneWebAppCapable = DOM.createElement("meta");
		iphoneWebAppCapable.setAttribute("name", "apple-mobile-web-app-capable");
		iphoneWebAppCapable.setAttribute("content", "yes");
		head.appendChild(iphoneWebAppCapable);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				Window.scrollTo(0, 1);
			}
		});
	}
}
