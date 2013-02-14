package ch.unifr.pai.twice.widgets.client;
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
import ch.unifr.pai.twice.comm.serverPush.client.RemoteWidget;
import ch.unifr.pai.twice.comm.serverPush.client.ServerPushEventBus;
import ch.unifr.pai.twice.utils.device.client.UUID;
import ch.unifr.pai.twice.widgets.client.events.UndoableRemoteKeyPressEvent;
import ch.unifr.pai.twice.widgets.client.events.UndoableRemoteKeyPressEvent.UndoableRemoteKeyPressHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class RemoteTextBox extends TextBox implements RemoteWidget {

	private RemoteTextInputInterpreter interpreter;

	private void updateState() {
		setValue(interpreter.getValue());
		setCursorPos(Math.min(
				Math.max(interpreter.getCursorPos(UUID.get()), -1), interpreter
						.getValue().length()));
	}

	private int tmpNativeCursor;
	private String tmpLocalValue;
	private String uniqueIdentifier;
	private ServerPushEventBus eventBus;

	@Override
	public String getEventSource() {
		return uniqueIdentifier;
	}

	private void calculateDifferenceAndFire(String oldValue, String newValue,
			int cursorPos) {
		oldValue = interpreter.getValue() == null ? "" : interpreter.getValue();
		tmpNativeCursor = Math.max(interpreter.getCursorPos(UUID.get()), 0);
		UndoableRemoteKeyPressEvent e = GWT
				.create(UndoableRemoteKeyPressEvent.class);
		if (oldValue.length() > newValue.length()) {
			// Deletion
			if (oldValue.length() - 1 == newValue.length()) {
				// One character removal
				if (cursorPos < tmpNativeCursor)
					// Backspace
					e.setKeyCode(KeyCodes.KEY_BACKSPACE);
				else
					// Delete
					e.setKeyCode(KeyCodes.KEY_DELETE);
			} else {
				// More complex deletion
				// TODO
			}
		} else if (newValue.length() > oldValue.length()) {
			// Insertion
			e.setText(String.valueOf(newValue.charAt(cursorPos - 1)));
		} else {
			e.setCursorPos(cursorPos - tmpNativeCursor);
		}
		RemoteTextBox.this.eventBus.fireEventFromSource(e, RemoteTextBox.this);
	}

	/**
	 * Define a static, unique identifier for this text box. This is needed for
	 * the linkage between the different clients. Make sure that this value is
	 * not composed dynamically - the best is a usage of a simple string not
	 * coming from any other method.
	 * 
	 * @param uniqueIdentifier
	 */
	public RemoteTextBox(String uniqueIdentifier, ServerPushEventBus eventBus) {
		super();
		this.uniqueIdentifier = uniqueIdentifier;
		this.eventBus = eventBus;
		this.interpreter = new RemoteTextInputInterpreter(new Command() {

			@Override
			public void execute() {
				updateState();
			}
		}, eventBus, uniqueIdentifier);

		addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				tmpNativeCursor = getCursorPos();
				tmpLocalValue = getValue();
			}
		});
		addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				final String newValue = getValue();
				final String oldValue = tmpLocalValue.toString();
				final int cursorPos = getCursorPos();
				// Make sure, that the event has been interpreted by the textbox
				calculateDifferenceAndFire(oldValue, newValue, cursorPos);
			}
		});
	}

	public HandlerRegistration addUndoableKeyPressEventHandler(
			UndoableRemoteKeyPressHandler handler) {
		return eventBus.addHandlerToSource(UndoableRemoteKeyPressEvent.TYPE,
				this, handler);
	}

}
