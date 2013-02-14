package ch.unifr.pai.twice.multipointer.client.widgets;
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

import ch.unifr.pai.twice.multipointer.client.MultiCursorController;
import ch.unifr.pai.twice.utils.device.client.UUID;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.TextArea;

public class RemoteTextArea extends TextArea {

	public RemoteTextArea() {
		super();
		extend();
	}

	public RemoteTextArea(Element element) {
		super(element);
		extend();
	}

	private void extend() {
		this.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (MultiCursorController.getUUID(event.getNativeEvent()) != null) {
					if (getValue() == null)
						setValue(String.valueOf(event.getCharCode()));
					else if (getCursorPos() <= getValue().length()) {
						setValue(getValue().substring(0, getCursorPos())
								+ event.getCharCode()
								+ ((getCursorPos() == getValue().length()) ? ""
										: getValue().substring(getCursorPos())));
					}
				}
			}
		});
		this.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				int cursorPos = getCursorPos();
				if (MultiCursorController.getUUID(event.getNativeEvent()) != null) {
					switch (event.getNativeKeyCode()) {

					case KeyCodes.KEY_DELETE:
						if (getValue() != null
								&& cursorPos < getValue().length()) {
							setValue(getValue().substring(0, cursorPos)
									+ getValue().substring(cursorPos + 1));
						}
						break;
					case KeyCodes.KEY_BACKSPACE:
						if (getValue() != null && cursorPos > 0
								&& cursorPos <= getValue().length()) {
							setValue(getValue().substring(0, cursorPos - 1)
									+ getValue().substring(cursorPos));
							setCursorPos(cursorPos - 1);
						}
						break;
					}
				}
			}
		});
	}

}
