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
import ch.unifr.pai.twice.utils.device.client.DeviceType;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

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

	@Override
	public void onModuleLoad() {
		if (DeviceType.getDeviceType() == DeviceType.MULTICURSOR) {
			MultiCursorController multiCursor = GWT
					.create(NoMultiCursorController.class);
			multiCursor.start();

			BrowserWindow window1 = new BrowserWindow("window1");
			BrowserWindow window2 = new BrowserWindow("window2");
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
			browserSplit.addNorth(window1, 500);
			browserSplit.add(window2);

			RootLayoutPanel.get().add(browserSplit);
		} else {
			TouchPadWidget touchPad = GWT.create(TouchPadWidget.class);
			RootLayoutPanel.get().add(touchPad);
			touchPad.start();
		}
	}

}
