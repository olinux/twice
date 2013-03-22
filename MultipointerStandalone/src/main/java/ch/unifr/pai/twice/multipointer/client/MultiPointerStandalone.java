package ch.unifr.pai.twice.multipointer.client;

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
import ch.unifr.pai.twice.multipointer.provider.client.NoMultiCursorController;
import ch.unifr.pai.twice.multipointer.provider.client.widgets.MultiFocusTextBox;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * The application which provides functionality for the shared screen
 * 
 * @author Oliver Schmid
 * 
 */
public class MultiPointerStandalone implements EntryPoint {

	NoMultiCursorController c = GWT.create(NoMultiCursorController.class);

	Label l = new Label("TEST");

	HTML spacer = new HTML();
	Anchor a = new Anchor("Google", "http://www.google.ch");
	TextBox box = new TextBox();

	@Override
	public void onModuleLoad() {
		c.start();
		RootPanel.get().add(l);
		RootPanel.get().add(a);
		RootPanel.get().add(box);
		MultiFocusTextBox textBox = new MultiFocusTextBox();
		RootPanel.get().add(textBox);

		Event.addNativePreviewHandler(new NativePreviewHandler() {

			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				switch (event.getTypeInt()) {
					case Event.ONKEYDOWN:
						GWT.log("KEYDOWN: " + event.getNativeEvent().getKeyCode());
				}
			}
		});

		// box.addKeyDownHandler(new KeyDownHandler(){
		//
		// @Override
		// public void onKeyDown(KeyDownEvent event) {
		// Window.alert("KEY DOWN: "+event.getNativeEvent().getKeyCode());
		// }});
		// RootPanel.get().add(spacer);

		spacer.setHeight("2000px");
		spacer.setWidth("2000px");
		l.addMouseOverHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				l.getElement().getStyle().setBackgroundColor("yellow");
				GWT.log("Mouse over " + NoMultiCursorController.getUUID(event.getNativeEvent()) + " "
						+ NoMultiCursorController.getColorNative(event.getNativeEvent()));
			}
		});
		l.addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				l.getElement().getStyle().setBackgroundColor(null);
				GWT.log("Mouse out " + NoMultiCursorController.getUUID(event.getNativeEvent()) + " "
						+ NoMultiCursorController.getColorNative(event.getNativeEvent()));

			}
		});
		l.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				GWT.log("Mouse down " + NoMultiCursorController.getUUID(event.getNativeEvent()) + " "
						+ NoMultiCursorController.getColorNative(event.getNativeEvent()));
			}
		});
		l.addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent event) {
				GWT.log("Mouse move " + NoMultiCursorController.getUUID(event.getNativeEvent()) + " "
						+ NoMultiCursorController.getColorNative(event.getNativeEvent()));
			}
		});
		l.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				GWT.log("Mouse up " + NoMultiCursorController.getUUID(event.getNativeEvent()) + " "
						+ NoMultiCursorController.getColorNative(event.getNativeEvent()));

			}
		});
		l.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				GWT.log("Click " + NoMultiCursorController.getUUID(event.getNativeEvent()) + " " + NoMultiCursorController.getColorNative(event.getNativeEvent()));
			}
		});
	}

}
