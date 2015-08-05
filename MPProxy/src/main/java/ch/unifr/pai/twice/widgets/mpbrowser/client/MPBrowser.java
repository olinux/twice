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

import ch.unifr.pai.twice.multipointer.controller.client.TouchPadWidget;
import ch.unifr.pai.twice.multipointer.provider.client.MultiCursorController;
import ch.unifr.pai.twice.multipointer.provider.client.NoMultiCursorController;
import ch.unifr.pai.twice.multipointer.provider.client.widgets.MultiFocusTextBox;
import ch.unifr.pai.twice.utils.device.client.DeviceType;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.storage.client.Storage;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.TextArea;

/**
 * This is a browser in a browser. It provides URL bars to enter standard URLs
 * which then are translated to the proxy server URLs and the component provides
 * back and forward buttons per frame as well as multi-cursor aware scroll bars
 * 
 * @author Oliver Schmid
 * 
 */
public class MPBrowser implements EntryPoint {

	SplitLayoutPanel browserSplit = new SplitLayoutPanel();
	VerticalPanel vPanel1 = new VerticalPanel();
	VerticalPanel vPanel2 = new VerticalPanel();
	DockLayoutPanel scrollBar = new DockLayoutPanel(Unit.PX);

	HorizontalPanel hPanel = new HorizontalPanel();

	MultiFocusTextBox textBox = new MultiFocusTextBox();
	Button addButton = new Button("Add");
	Button scrollDown = new Button("&#9660"); // Down
	Button scrollUp = new Button("&#9650"); // Up
	TextArea textArea = new TextArea();
	DockLayoutPanel ChatBox = new DockLayoutPanel(Unit.PX);

	String oldValue = " ";
	String newValue = " ";
	int count = 0;

	static int height;
	static int width;

	@Override
	public void onModuleLoad() {
		if (DeviceType.getDeviceType() == DeviceType.MULTICURSOR) {

			MultiCursorController multiCursor = GWT
					.create(NoMultiCursorController.class);
			multiCursor.start();

			height = Window.getClientHeight();
			width = Window.getClientWidth();

			scrollBar.getElement().getStyle().setBackgroundColor("lightgrey");
			scrollBar.addNorth(scrollUp, 26);
			scrollBar.addSouth(scrollDown, 26);

			textBox.setVisible(true);
			textBox.setWidth("100%");
			textBox.setHeight("22px");

			hPanel.add(textBox);
			hPanel.add(addButton);
			hPanel.setCellWidth(addButton, "51px");
			hPanel.setWidth("100%");

			textArea.setVisibleLines(150);
			textArea.setHeight("100%");
			textArea.setWidth("100%");
			textArea.getElement().getStyle().setFontSize(12, Unit.PX);

			BrowserWindow window1 = new BrowserWindow("window1");
			BrowserWindow window2 = new BrowserWindow("window2");
			BrowserWindow window3 = new BrowserWindow("window3");

			Storage s = Storage.getSessionStorageIfSupported();
			if (s != null) {
				String type = s.getItem(DeviceType.SESSION_STORAGE_VARIABLE);
				if (type == null
						|| !type.equals(DeviceType.MULTICURSOR.name()
								.toLowerCase())) {
					s.setItem(DeviceType.SESSION_STORAGE_VARIABLE,
							DeviceType.MULTICURSOR.name().toLowerCase());
				}
			}

			// browserSplit.addEast(window3,400); //User3
			// browserSplit.addNorth(window2, 400); //User2
			// browserSplit.insertNorth(window1, 400, window2); //User1

			/* ChatBox */
			ChatBox.addSouth(hPanel, 25);
			ChatBox.addEast(scrollBar, 30);
			ChatBox.add(textArea);

			vPanel1.setSize("100%", "100%");

			vPanel1.add(window3); // User3
			vPanel1.getWidget(0).setSize("100%", "100%");
			vPanel1.add(ChatBox); // ChatBox
			vPanel1.getWidget(1).setSize("100%", "100%");

			vPanel2.setSize("100%", "100%");

			vPanel2.add(window2); // User2
			vPanel2.getWidget(0).setSize("100%", "100%");
			vPanel2.add(window1); // User1
			vPanel2.getWidget(1).setSize("100%", "100%");

			browserSplit.addEast(vPanel1, width / 2);
			browserSplit.add(vPanel2);

			RootLayoutPanel.get().add(browserSplit);

			/* ChatBox Buttons */
			addButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					count++;

					String color = textBox.getTextColor();
					String username = "";

					if (color.equalsIgnoreCase("#1a1a1a")) {
						username = "User1";
					}
					if (color.equalsIgnoreCase("#ff0000")) {
						username = "User2";
					}
					if (color.equalsIgnoreCase("#336aa6")) {
						username = "User3";
					}

					newValue = username + ": " + textBox.getValue();

					textArea.setValue(oldValue + '\n' + newValue);
					oldValue = oldValue + '\n' + newValue;
					textBox.setValue("");

					if (count >= 13) { // number of lines in ta
						textArea.getElement().setScrollTop(
								textArea.getElement().getScrollTop() + 20); // auto scroll down
					}
				}
			});
			scrollDown.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {

					if (textArea.getElement() != null) {
						textArea.getElement().setScrollTop(
								Math.min(textArea.getElement()
										.getScrollHeight(), textArea
										.getElement().getScrollTop() + 20)); // scroll down
					}
				}
			});

			scrollUp.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {

					if (textArea.getElement() != null) {
						textArea.getElement().setScrollTop(
								Math.max(0, textArea.getElement()
										.getScrollTop() - 20)); // scroll up
					}
				}
			});

		} else {
			TouchPadWidget touchPad = GWT.create(TouchPadWidget.class);
			RootLayoutPanel.get().add(touchPad);
			// touchPad.initialize(UUID.get(), null, null);
			touchPad.start();
		}
	}
}