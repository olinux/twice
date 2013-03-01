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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;

/**
 * Entry point for the multi pointer functionality. This is the functionality required by the shared screen instance to enable multiple mouse pointers.
 * 
 * @author Oliver Schmid
 * 
 */
public class MultiCursorController {

	private static MultiCursorController controller;

	protected MultiCursorController() {
	}

	/**
	 * @return the multi cursor controller instantiated through deferred binding - by default this
	 */
	public static MultiCursorController getInstance() {
		if (controller == null)
			controller = GWT.create(MultiCursorController.class);
		return controller;
	}

	/**
	 * Start the execution of the component
	 */
	public void start() {
		// There is nothing to do since the browser doesn't support the multi
		// cursors
	}

	/**
	 * Stop the execution of the component
	 */
	public void stop() {
		// There is nothing to do since the browser doesn't support the multi
		// cursors
	}

	/**
	 * Notify the cursor with the given unique identifier about a given action
	 * 
	 * @param uuid
	 * @param action
	 */
	public void notifyCursor(String uuid, String action) {
	}

	/**
	 * @param event
	 * @return true if the event has been triggered by the native mouse pointer
	 */
	public static boolean isDefaultCursor(NativeEvent event) {
		return "default".equals(getUUID(event));
	}

	/**
	 * @param event
	 * @return the UUID of the device which has triggered the event or "default" if the event was triggered by the native mouse pointer
	 */
	public native static String getUUID(NativeEvent event)
	/*-{
		return event.uuid == null ? 'default' : event.uuid;
	}-*/;

	/**
	 * @param event
	 * @return the color of the mouse pointer that has triggered the event
	 */
	public native static String getColorNative(NativeEvent event)
	/*-{
		return event.color;
	}-*/;

	private int maxCursorsOnScreen = 6;

	/**
	 * Sets the maximal number of mouse pointers on the shared screen
	 * 
	 * @param maxCursorsOnScreen
	 */
	public void setMaxCursorsOnScreen(int maxCursorsOnScreen) {
		this.maxCursorsOnScreen = maxCursorsOnScreen;
	}

	/**
	 * @return the maximal number of mouse pointers on the shared screen
	 */
	public int getMaxCursorsOnScreen() {
		return maxCursorsOnScreen;

	}
}
