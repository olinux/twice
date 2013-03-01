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
import java.util.HashMap;
import java.util.Map;

import ch.unifr.pai.twice.widgets.client.events.UndoableRemoteKeyPressEvent;
import ch.unifr.pai.twice.widgets.client.events.UndoableRemoteKeyPressEvent.UndoableRemoteKeyPressHandler;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Command;

/**
 * Handler for an {@link UndoableRemoteKeyPressEvent}
 * 
 * @author Oliver Schmid
 * 
 */
public class UndoableRemoteKeyPressHandlerImpl implements UndoableRemoteKeyPressHandler {
	private String value;
	private final Map<String, Integer> cursorPositions = new HashMap<String, Integer>();
	private final Command updateCallback;
	private int thisCursorPos;

	public String getValue() {
		return value;
	}

	public Map<String, Integer> getCursorPositions() {
		return cursorPositions;
	}

	public void setValue(String value) {
		this.value = value;
		updateCallback.execute();
	}

	public void setCursorPositions(Map<String, Integer> positions) {
		for (String p : positions.keySet()) {
			cursorPositions.put(p, positions.get(p).intValue());
		}
		updateCallback.execute();
	}

	@Override
	public void onEvent(UndoableRemoteKeyPressEvent event) {
		String device = event.getOriginatingDevice();
		if (event.getKeyCode() != null) {
			switch (event.getKeyCode()) {
				case KeyCodes.KEY_DELETE:
					delete(device);
					break;
				case KeyCodes.KEY_BACKSPACE:
					bckspc(device);
					break;
			}
		}
		else if (event.getText() != null) {
			addChar(event.getText(), device);
		}
		Integer cursorPos = event.getCursorPos();
		if (cursorPos != null) {
			if (cursorPos > 0) {
				shiftCursorPosRight(device, cursorPos);
			}
			else {
				shiftCursorPosLeft(device, Math.abs(cursorPos));
			}
		}
	}

	@Override
	public void undo(UndoableRemoteKeyPressEvent event) {
		value = event.getStorageProperty("value");
	}

	@Override
	public void saveState(UndoableRemoteKeyPressEvent event) {
		event.setStorageProperty("value", value);
	}

	public UndoableRemoteKeyPressHandlerImpl(Command updateCallback) {
		this.updateCallback = updateCallback;
	}

	public int getThisCursorPos() {
		return thisCursorPos;
	}

	public int getCursorPos(String device) {
		Integer pos = cursorPositions.get(device);
		return pos == null ? 0 : pos;
	}

	private void delete(String device) {
		if (thisCursorPos < value.length()) {
			value = value.substring(0, thisCursorPos) + value.substring(thisCursorPos + 1);
			shiftAll(thisCursorPos, device, -1);
			updateCallback.execute();
		}
	}

	private void bckspc(String device) {
		if (thisCursorPos > 0 && value != null && value.length() > 0) {
			value = value.substring(0, thisCursorPos - 1) + (thisCursorPos < value.length() ? value.substring(thisCursorPos) : "");
			shiftAll(thisCursorPos + 1, device, -1);
			updateCallback.execute();
		}
	}

	private void shiftCursorPosLeft(String device, int amount) {
		int pos = thisCursorPos;
		if (pos >= amount) {
			thisCursorPos = pos - amount;
			updateCallback.execute();
		}
	}

	private void shiftCursorPosRight(String device, int amount) {
		int pos = thisCursorPos;
		if (value != null && pos + amount <= value.length()) {
			thisCursorPos = pos + amount;
			updateCallback.execute();
		}
	}

	private void addChar(String text, String device) {
		StringBuilder sb = new StringBuilder();
		if (value != null && thisCursorPos > -1)
			sb.append(value.substring(0, Math.min(value.length(), thisCursorPos)));
		sb.append(text);
		if (value != null && thisCursorPos > -1 && thisCursorPos < value.length()) {
			sb.append(value.substring(Math.min(value.length(), thisCursorPos)));
		}
		value = sb.toString();
		shiftAll(thisCursorPos, device, text.length());
		updateCallback.execute();
	}

	private void shiftAll(int curPos, String device, int amount) {
		if ((amount > 0 && curPos <= thisCursorPos) || (amount < 0 && curPos > thisCursorPos))
			thisCursorPos = thisCursorPos + amount;
		for (String s : cursorPositions.keySet()) {
			int p = cursorPositions.get(s);
			// if (!s.equals(device)) {
			// shift right
			if ((amount < 0 && curPos < p) || (amount > 0 && curPos > p))
				cursorPositions.put(s, p + amount);
			// }
		}
		updateCallback.execute();
	}
}
