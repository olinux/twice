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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.unifr.pai.twice.multipointer.client.MouseCursorTimeoutEvent.Handler;
import ch.unifr.pai.twice.utils.device.client.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class WebsocketControl extends MultiCursorController implements
		ResizeHandler {

	private boolean opened = false;

	private JavaScriptObject websocket;

	private final List<MouseCursor> cursors = new ArrayList<MouseCursor>();

	private final Map<String, MouseCursor> assignedMouseCursors = new HashMap<String, MouseCursor>();

	private final Map<String, MouseCursor> preferredMouseCursors = new HashMap<String, MouseCursor>();

	private final Set<MouseCursor> activeCursorsCounter = new HashSet<MouseCursor>();

	private final Storage storage = Storage.getLocalStorageIfSupported();

	private int maxCursorsOnScreen = 5;

	private void initializeCursorList() {
		cursors.clear();
		assignedMouseCursors.clear();
		cursors.add(defineMouseCursor("black", "#1a1a1a"));
		cursors.add(defineMouseCursor("blue", "#336aa6"));
		cursors.add(defineMouseCursor("green", "#42a75b"));
		cursors.add(defineMouseCursor("grey", "#646663"));
		cursors.add(defineMouseCursor("red", "#d65555"));
		cursors.add(defineMouseCursor("yellow", "#f7d64c"));
		cursors.add(defineMouseCursor("purple", "#cb2c7a"));

	}
	
	private MouseCursor getCursorByColor(String color){
		for(MouseCursor c : activeCursorsCounter){
			if(c.getColor().equals(color))
				return c;
		}
		return null;
		
	}

	private MouseCursor defineMouseCursor(String cursor, String color) {
		final MouseCursor c = new MouseCursor(cursor, color);
		c.addMouseCursorEventHandler(new Handler() {

			@Override
			public void onMouseCursorTimeout(MouseCursorTimeoutEvent event) {
				if (event.isDetached()) {
					GWT.log("Detaching cursor " + c.getFileName());
					String uuid = null;
					for (String assignedCursor : assignedMouseCursors.keySet()) {
						if (assignedMouseCursors.get(assignedCursor) == c) {
							uuid = assignedCursor;
							break;
						}
					}
					if (uuid != null) {
						assignedMouseCursors.remove(uuid);
						if (storage != null)
							storage.removeItem("ch.unifr.pai.mice.multicursor.assignedCursor."
									+ c.getFileName());
					}
					if (!cursors.contains(c))
						cursors.add(c);
				}
				activeCursorsCounter.remove(c);
				if (storage != null)
					storage.removeItem("ch.unifr.pai.mice.multicursor.activeCursor."
							+ c.getFileName());

			}
		});
		if (storage != null) {
			String assigneduuid = storage
					.getItem("ch.unifr.pai.mice.multicursor.assignedCursor."
							+ cursor);
			if (assigneduuid != null)
				assignedMouseCursors.put(assigneduuid, c);
			String preferreduuid = storage
					.getItem("ch.unifr.pai.mice.multicursor.preferredCursor."
							+ cursor);
			if (preferreduuid != null)
				preferredMouseCursors.put(preferreduuid, c);
			String activeCursor = storage
					.getItem("ch.unifr.pai.mice.multicursor.activeCursor."
							+ cursor);
			if (activeCursor != null)
				activeCursorsCounter.add(c);
		}
		if (!activeCursorsCounter.contains(c))
			c.getElement().getStyle().setDisplay(Display.NONE);
		else
			c.show();
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
		for(MouseCursor c : activeCursorsCounter){
			c.hide();
		}
	
	}

	private native void send(JavaScriptObject websocket, String message)/*-{
		if(websocket!=null)
			websocket.send(message);
	}-*/;

	private native JavaScriptObject createOnBeforeUnloadHandler(
			JavaScriptObject websocket)/*-{
		$wnd.onbeforeunload = function() {
			//$wnd.alert("CLOSE WEBSOCKET: "+websocket);
			websocket.close();
		}
	}-*/;
	
	private native JavaScriptObject stopWebsocket(
			JavaScriptObject websocket)/*-{
			websocket.close();
	}-*/;

	private native JavaScriptObject createWebsocket(WebsocketControl w,
			String url)/*-{
		if ("WebSocket" in $wnd) {
			// Let us open a web socket
			var ws = new WebSocket(url);
			ws.onmessage = function(evt) {
				w.@ch.unifr.pai.twice.multipointer.client.WebsocketControl::onMessage(Ljava/lang/String;)(evt.data);
			}
			ws.onopen = function() {
				w.@ch.unifr.pai.twice.multipointer.client.WebsocketControl::onOpen()();
			}
			ws.onclose = function() {
				w.@ch.unifr.pai.twice.multipointer.client.WebsocketControl::onClose()();
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
					boolean isActive = activeCursorsCounter.contains(m);
					if (activeCursorsCounter.size() < maxCursorsOnScreen
							|| isActive) {
						if (!isActive) {
							activeCursorsCounter.add(m);
							if (storage != null) {
								storage.setItem(
										"ch.unifr.pai.mice.multicursor.activeCursor."
												+ m.getFileName(), "true");
							}
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
		if (cursors.size() > 0) {
			// otherwise Get the preferred mouse cursor for this uuid and check
			// if it's still available
			m = preferredMouseCursors.get(uuid);
			// If there is no preferred cursor or if the preferred cursor is
			// already in use, take another one
			if (m == null || !cursors.contains(m)) {
				m = cursors.get(0);
				preferredMouseCursors.put(uuid, m);
				if (storage != null)
					storage.setItem(
							"ch.unifr.pai.mice.multicursor.preferredCursor."
									+ m.getFileName(), uuid);
			}
			m.setUuid(uuid);
			cursors.remove(m);
			assignedMouseCursors.put(uuid, m);
			if (storage != null)
				storage.setItem("ch.unifr.pai.mice.multicursor.assignedCursor."
						+ m.getFileName(), uuid);
			if (opened && websocket != null) {
				send(websocket, UUID.get() + "@c@" + uuid + "@" + m.getColor());
			}
			return m;
		}
		return null;
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
		send(websocket, UUID.get()+"@"+action+"@"+uuid);
	}
	
	
}
