package ch.unifr.pai.twice.widgets.mpProxyScreenShot.client;

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
import com.google.gwt.user.client.Window;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;

import com.google.gwt.user.client.ui.RootPanel;

import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Viewer implements EntryPoint {

	Element preparationStage;

	// SplitLayoutPanel browserSplit = new SplitLayoutPanel();
	// String previousHTML = ""; //

	TextArea ta = new TextArea();
	Button addButton = new Button("Add"); // Add
	Button scrollDown = new Button("&#9660"); // Down
	Button scrollUp = new Button("&#9650"); // Up
	int count = 0;

	static int height;
	static int width;

	static int top;
	static int left;

	MultiFocusTextBox textBox = new MultiFocusTextBox();

	VerticalPanel vPanel = new VerticalPanel();
	DockLayoutPanel panel = new DockLayoutPanel(Unit.PX);
	HorizontalPanel hPanel = new HorizontalPanel();
	DockLayoutPanel scrollBar = new DockLayoutPanel(Unit.PX);

	String oldValue = " ";
	String newValue = " ";

	@Override
	public void onModuleLoad() {

		if (DeviceType.getDeviceType() == DeviceType.MULTICURSOR) {

			MultiCursorController multiCursor = GWT
					.create(NoMultiCursorController.class);
			multiCursor.start();

			height = Window.getClientHeight();
			width = Window.getClientWidth();
			left = RootPanel.get().getAbsoluteLeft();
			top = RootPanel.get().getAbsoluteTop();

			scrollBar.getElement().getStyle().setBackgroundColor("lightgrey");
			scrollBar.addNorth(scrollUp, 30);
			scrollBar.addSouth(scrollDown, 30);

			textBox.setVisible(true);
			textBox.setWidth("100%");
			hPanel.add(textBox);
			hPanel.add(addButton);
			hPanel.setCellWidth(addButton, "52px");
			hPanel.setWidth("100%");

			ta.setVisibleLines(150);
			ta.setHeight("100%");
			ta.setWidth("100%");
			ta.getElement().getStyle().setFontSize(12, Unit.PX);

			panel.addSouth(hPanel, 30);
			panel.addEast(scrollBar, 26);
			panel.add(ta);

			panel.setSize(width / 2 + "px", height / 2 + "px");

			preparationStage = DOM.createDiv();
			preparationStage.getStyle().setDisplay(Display.NONE);

			RootPanel.get("chatbox").add(panel, width / 2, height / 2);

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

					double time = System.currentTimeMillis();

					newValue = username + ": " + textBox.getValue()
							+ "         @" + time;
					ta.setValue(oldValue + '\n' + newValue);
					oldValue = oldValue + '\n' + newValue;
					textBox.setValue("");

					if (count >= 13) { // number of lines in ta
						ta.getElement().setScrollTop(ta.getElement().getScrollTop() + 20); // auto scroll down
															
					}

				}
			});

			scrollDown.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {

					if (ta.getElement() != null) {
						ta.getElement().setScrollTop(
								Math.min(ta.getElement().getScrollHeight(), ta
										.getElement().getScrollTop() + 20));
					}
				}
			});

			scrollUp.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {

					if (ta.getElement() != null) {
						ta.getElement().setScrollTop(Math.max(0, ta.getElement().getScrollTop() - 20));
					}
				}
			});

			Timer t = new Timer() {

				@Override
				public void run() {
					try {

						RequestBuilder rb = new RequestBuilder(
								RequestBuilder.GET, GWT.getHostPageBaseURL()+ "miceScreenShot/manager");

						rb.sendRequest(null, new RequestCallback() {

							@Override
							public void onResponseReceived(Request request,
									Response response) {
								// if(!response.getText().equals(previousHTML)){}

								RootPanel.get("screenshotArea").getElement().setInnerHTML(preparationStage.getInnerHTML());
								RootPanel.get("screenshotArea").getElement().appendChild(preparationStage);

								preparationStage.setInnerHTML(response.getText());
								// previousHTML = response.getText();
								// RootPanel.getBodyElement().setInnerHTML(response.getText());
							}

							@Override
							public void onError(Request request,Throwable exception) {
							}
						});
					} catch (RequestException e) {
						e.printStackTrace();
					}
				}
			};
			// t.run();
			t.scheduleRepeating(6000); // 6sc

		}

		else {
			TouchPadWidget touchPad = GWT.create(TouchPadWidget.class);
			RootLayoutPanel.get().add(touchPad);
			// touchPad.initialize(UUID.get(), null, null);
			touchPad.start();
		}
	}

}
