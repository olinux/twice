package ch.unifr.pai.twice.widgets.mpproxyframe.client;

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
import ch.unifr.pai.twice.multipointer.provider.client.MultiCursorController;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * Implementation of a very basic onscreen keyboard interacting with the MPFrame
 * 
 * @author Oliver Schmid
 * 
 */
public class Controller extends FlexTable {

	private final MPFrame f;
	private final String[][] keys = { { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "'", "^" },
			{ "q", "w", "e", "r", "t", "z", "u", "i", "o", "p", "ü" }, { "a", "s", "d", "f", "g", "h", "j", "k", "l", "ö", "ä", "$" },
			{ "y", "x", "c", "v", "b", "n", "m", ",", ".", "-" } };

	public Controller(MPFrame frame) {
		super();
		this.f = frame;
		for (int i = 0; i < keys.length; i++) {
			for (int i2 = 0; i2 < keys[i].length; i2++) {
				this.setWidget(i, i2, createButton(keys[i][i2]));
			}
		}
		this.setWidget(0, keys[0].length, new Button("BSPC", new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				// f.sendDataToFrame(MultiPointerUtils.getDeviceId(arg0),
				// InputPrefix.KEYCODE + KeyCodes.KEY_BACKSPACE);
			}
		}));
		this.setWidget(0, keys[0].length + 1, new Button("DEL", new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				// f.sendDataToFrame(MultiPointerUtils.getDeviceId(arg0),
				// InputPrefix.KEYCODE + KeyCodes.KEY_DELETE);
			}
		}));
		Button spaceButton = new Button("Space", new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				f.sendDataToFrame(MultiCursorController.getUUID(arg0.getNativeEvent()), " ");
			}

		});

		this.setWidget(keys.length + 1, 0, spaceButton);

	}

	private Button createButton(final String value) {
		return new Button(value, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				f.sendDataToFrame(MultiCursorController.getUUID(event.getNativeEvent()), value);
			}
		});
	}

}
