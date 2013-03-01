package ch.unifr.pai.twice.layout.client.commons;

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
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.DialogBox.Caption;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * The caption of the dialog for the eclipse layout (including fullscreen and close button)
 * 
 * @author Oliver Schmid
 * 
 */
public class MiceDialogCaption extends HorizontalPanel implements Caption {

	private final HTML title = new HTML();
	private final Image close = new Image(GWT.getModuleBaseURL() + "images/close_hover.png");

	private final Image fullscreen = new Image(GWT.getModuleBaseURL() + "images/fullscreen.png");

	public void setHandlers(ClickHandler fullscreenHandler, ClickHandler closeHandler) {

		fullscreen.addClickHandler(fullscreenHandler);

		close.addClickHandler(closeHandler);
	}

	public MiceDialogCaption() {
		super();
		add(title);
		setCellWidth(title, "100%");
		// add(fullscreen);
		add(close);
		this.setStyleName("Caption");
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return title.addMouseDownHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return title.addMouseUpHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return title.addMouseOutHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return title.addMouseOverHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return title.addMouseMoveHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
		return title.addMouseWheelHandler(handler);
	}

	@Override
	public String getHTML() {
		return title.getHTML();
	}

	@Override
	public void setHTML(String html) {
		title.setHTML(html);
	}

	@Override
	public String getText() {
		return title.getText();
	}

	@Override
	public void setText(String text) {
		title.setText(text);
	}

	@Override
	public void setHTML(SafeHtml html) {
		title.setHTML(html);
	}
}
