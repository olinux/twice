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

public class MultiCursorController {

	private static MultiCursorController controller;
	
	protected MultiCursorController(){
	}
	
	public static MultiCursorController getInstance(){
		if(controller==null)
			controller = GWT.create(MultiCursorController.class);
		return controller;
	}
	
	public void start() {
		// There is nothing to do since the browser doesn't support the multi
		// cursors
	}
	
	public void stop(){
		// There is nothing to do since the browser doesn't support the multi
		// cursors
	}
	
	public void notifyCursor(String uuid, String action){}

	public static boolean isDefaultCursor(NativeEvent event){
		return "default".equals(getUUID(event));
	}
	
	
	public native static String getUUID(NativeEvent event)
	/*-{
		return event.uuid == null ? 'default' : event.uuid;
	}-*/;

	public native static String getColorNative(NativeEvent event)
	/*-{
		return event.color;
	}-*/;
	
	private int maxCursorsOnScreen = 6;
	public void setMaxCursorsOnScreen(int maxCursorsOnScreen){
		this.maxCursorsOnScreen = maxCursorsOnScreen;
	}
	
	public int getMaxCursorsOnScreen(){
		return maxCursorsOnScreen;
		
	}
}
