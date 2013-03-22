package ch.unifr.pai.mindmap.client.mindmap;

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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

/**
 * A helper class that provides the widgets which allow to manipulate the overall font size.
 * 
 * @author Oliver Schmid
 * 
 */
public class TextSize {

	private int pixels = 14;

	/**
	 * A button to increase the font size that reacts only if it is pushed by the default (native) mouse pointer
	 */
	private final PushButton increase = new PushButton(new Image(GWT.getModuleBaseURL() + "images/textincrease.png")) {
		@Override
		public void onBrowserEvent(Event event) {
			if (NoMultiCursorController.isDefaultCursor(event)) {
				super.onBrowserEvent(event);
			}
		}
	};
	/**
	 * A button to decrease the font size that reacts only if it is pushed by the default (native) mouse pointer
	 */
	private final PushButton decrease = new PushButton(new Image(GWT.getModuleBaseURL() + "images/textdecrease.png")) {
		@Override
		public void onBrowserEvent(Event event) {
			if (NoMultiCursorController.isDefaultCursor(event)) {
				super.onBrowserEvent(event);
			}
		}
	};

	/**
	 * @param listener
	 *            - a callback which is called if the text size has changed
	 */
	public TextSize(final AsyncCallback<Integer> listener) {
		increase.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (NoMultiCursorController.isDefaultCursor(event.getNativeEvent())) {
					pixels = Math.min(60, pixels + 2);
					listener.onSuccess(pixels);
				}
			}
		});
		increase.setWidth("70px");
		increase.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

		decrease.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (NoMultiCursorController.isDefaultCursor(event.getNativeEvent())) {
					pixels = Math.max(2, pixels - 2);
					listener.onSuccess(pixels);
				}
			}
		});
		decrease.setWidth("70px");
		decrease.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
	}

	/**
	 * @return the current text size
	 */
	public int getTextSize() {
		return pixels;
	}

	/**
	 * @return the increase button
	 */
	public PushButton getIncreaseButton() {
		return increase;
	}

	/**
	 * @return the decrease button
	 */
	public PushButton getDecreaseButton() {
		return decrease;
	}

}
