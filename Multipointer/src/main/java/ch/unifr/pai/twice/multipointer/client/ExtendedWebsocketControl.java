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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.unifr.pai.twice.multipointer.client.MouseCursorTimeoutEvent.Handler;
import ch.unifr.pai.twice.utils.device.client.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class ExtendedWebsocketControl extends MultiCursorController implements
		ResizeHandler {

	private boolean opened = false;

	private JavaScriptObject websocket;

	private List<MouseCursor> visibleCursors = new ArrayList<MouseCursor>();

	private final Map<String, MouseCursor> assignedMouseCursors = new HashMap<String, MouseCursor>();

	private final Storage storage = Storage.getLocalStorageIfSupported();

	private List<CursorColor> cursorColors = new ArrayList<CursorColor>();

	private class CursorColor {
		String cursor;
		String colorCode;

		public CursorColor(String cursor, String colorCode) {
			super();
			this.cursor = cursor;
			this.colorCode = colorCode;
		}
	}

	int currentCursor = -1;

	private void initializeCursorList() {
		visibleCursors.clear();
		assignedMouseCursors.clear();
		cursorColors.clear();
		cursorColors.add(new CursorColor("black", "#1a1a1a"));
		cursorColors.add(new CursorColor("blue", "#336aa6"));
		cursorColors.add(new CursorColor("green", "#42a75b"));
		cursorColors.add(new CursorColor("grey", "#646663"));
		cursorColors.add(new CursorColor("red", "#d65555"));
		cursorColors.add(new CursorColor("yellow", "#f7d64c"));
		cursorColors.add(new CursorColor("purple", "#cb2c7a"));
	}

	
	
	private MouseCursor defineMouseCursor(String cursor, String color) {
		final MouseCursor c = new MouseCursor(cursor, color);
		c.addMouseCursorEventHandler(new Handler() {
			
			@Override
			public void onMouseCursorTimeout(MouseCursorTimeoutEvent event) {
				visibleCursors.remove(c);
			}
		});
		if(visibleCursors.size()<getMaxCursorsOnScreen()){
			c.show();
			visibleCursors.add(c);
		}
		
		RootPanel.get().add(c);
		return c;
	}

	HandlerRegistration r;

	public native boolean isInIFrame() /*-{
		return $wnd.top.location != $wnd.location;
	}-*/;

	@Override
	public void start() {
		if (!isInIFrame()) {
			initializeCursorList();
			if (r != null)
				r.removeHandler();
			r = Window.addResizeHandler(this);
			UrlBuilder b = Window.Location.createUrlBuilder();
			b.setProtocol("ws");
			b.setPath("mouseControlXBrowser");
			b.setHash(null);
			String p = Window.Location.getPort();
			Integer port;
			try {
				port = p != null ? Integer.parseInt(p) : 8080;
			} catch (NumberFormatException e) {
				port = 8080;
			}
			b.setPort(port + 1);
			for (String param : Window.Location.getParameterMap().keySet()) {
				b.removeParameter(param);
			}
			websocket = createWebsocket(this, b.buildString());
			if (websocket != null)
				createOnBeforeUnloadHandler(websocket);
		}
	}

	@Override
	public void stop() {
		super.stop();
		stopWebsocket(websocket);
		for (MouseCursor c : visibleCursors) {
			c.hide();
		}
		visibleCursors.clear();

	}

	private native void send(JavaScriptObject websocket, String message)/*-{
		if (websocket != null)
			websocket.send(message);
	}-*/;

	private native JavaScriptObject createOnBeforeUnloadHandler(
			JavaScriptObject websocket)/*-{
		$wnd.onbeforeunload = function() {
			//$wnd.alert("CLOSE WEBSOCKET: "+websocket);
			websocket.close();
		}
	}-*/;

	private native JavaScriptObject stopWebsocket(JavaScriptObject websocket)/*-{
		websocket.close();
	}-*/;

	private native JavaScriptObject createWebsocket(ExtendedWebsocketControl w,
			String url)/*-{
		if ("WebSocket" in $wnd) {
			// Let us open a web socket
			var ws = new WebSocket(url);
			ws.onmessage = function(evt) {
				w.@ch.unifr.pai.twice.multipointer.client.ExtendedWebsocketControl::onMessage(Ljava/lang/String;)(evt.data);
			}
			ws.onopen = function() {
				w.@ch.unifr.pai.twice.multipointer.client.ExtendedWebsocketControl::onOpen()();
			}
			ws.onclose = function() {
				w.@ch.unifr.pai.twice.multipointer.client.ExtendedWebsocketControl::onClose()();
			}

			//Add onbeforeunload to handle closing correctly in chrome
			$wnd.onbeforeunload = function() {
				ws.onclose = function() {
				};
				ws.close();
			}
			return ws;
		} else {
			$wnd
					.alert("This browser does not support multipointer functionalities (needs websockets)!");
		}

		return null;
	}-*/;

	private void onMessage(String data) {
		if (data != null) {
			String[] values = data.split("@");
			if (values.length > 0) {
				String uuid = values[0];
				MouseCursor m = getOrCreateCursor(uuid);
				if (m != null) {
					boolean isActive = visibleCursors.contains(m);
					if(isActive || visibleCursors.size()<getMaxCursorsOnScreen()){
						if(!isActive){
							GWT.log("New cursor. Current visible cursors: "+visibleCursors.size());
							visibleCursors.add(m);
						}
						if (values.length > 1) {
							String action = null;
							Map<String, String> params = new HashMap<String, String>();
							for (int i = 1; i < values.length; i++) {
								String[] param = values[i].split("=");
								if (param[0].equals("a"))
									action = param[1];
								else
									params.put(param[0], param[1]);
							}
							m.interpretMessage(action, params);
						}
					}
				}
			}
		}
	}

	private MouseCursor getOrCreateCursor(String uuid) {
		MouseCursor m = assignedMouseCursors.get(uuid);
		// If a cursor is already assigned to the uuid, return this one
		if (m != null)
			return m;

		// else create a new cursor with the next color
		currentCursor++;
		if (currentCursor >= cursorColors.size()) {
			currentCursor = 0;
		}
		CursorColor c = cursorColors.get(currentCursor);
		m = defineMouseCursor(c.cursor, c.colorCode);
		m.setUuid(uuid);
		assignedMouseCursors.put(uuid, m);
		if (storage != null)
			storage.setItem(
					"ch.unifr.pai.mice.multicursor.assignedCursor."
							+ m.getFileName(), uuid);
		if (opened && websocket != null) {
			send(websocket, UUID.get() + "@c@" + uuid + "@" + m.getColor());
		}
		return m;
	}

	private void onOpen() {
		this.opened = true;
		send(websocket, UUID.get() + "@s@" + Window.getClientWidth() + "@"
				+ Window.getClientHeight());
		// Window.alert("Multi cursor control started!");
	}

	private void onClose() {
		if (!opened) {
			Window.alert("The websocket server is not reachable!");
		} else {
			opened = false;
			// Window.alert("Stopping multi cursor control!");
		}
	}

	@Override
	public void onResize(ResizeEvent event) {
		if (opened && websocket != null) {
			send(websocket, UUID.get() + "@r@" + Window.getClientWidth() + "@"
					+ Window.getClientHeight());
		}
	}

	@Override
	public void notifyCursor(String uuid, String action) {
		send(websocket, UUID.get() + "@" + action + "@" + uuid);
	}

}
