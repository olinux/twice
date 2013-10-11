package ch.unifr.pai.twice.multipointer.provider.client;

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

import ch.unifr.pai.twice.comm.serverPush.client.CommunicationManager;
import ch.unifr.pai.twice.comm.serverPush.client.RemoteEvent;
import ch.unifr.pai.twice.multipointer.commons.client.events.InformationUpdateEvent;
import ch.unifr.pai.twice.multipointer.commons.client.events.RemoteMouseDownEvent;
import ch.unifr.pai.twice.multipointer.commons.client.events.RemoteMouseMoveEvent;
import ch.unifr.pai.twice.multipointer.commons.client.events.RemoteMouseUpEvent;
import ch.unifr.pai.twice.multipointer.commons.client.rpc.MouseControllerService;
import ch.unifr.pai.twice.multipointer.commons.client.rpc.MouseControllerServiceAsync;
import ch.unifr.pai.twice.multipointer.provider.client.MouseCursorTimeoutEvent.Handler;
import ch.unifr.pai.twice.utils.device.client.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * The currently used controller for multi-cursor device types
 * 
 * @author Oliver Schmid
 * 
 */
public class MultiCursorController extends NoMultiCursorController implements ResizeHandler {

	private final boolean opened = false;

	private JavaScriptObject websocket;

	private final List<MouseCursor> visibleCursors = new ArrayList<MouseCursor>();

	private final Map<String, MouseCursor> assignedMouseCursors = new HashMap<String, MouseCursor>();

	private final Storage storage = Storage.getLocalStorageIfSupported();

	private final List<CursorColor> cursorColors = new ArrayList<CursorColor>();

	/**
	 * A map between the cursor name (related to the .png's in the public folder) and their corresponding HTML color codes.
	 * 
	 * @author Oliver Schmid
	 * 
	 */
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

	/**
	 * Initializes the available cursor colors.
	 */
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

	/**
	 * Create and attach a new mouse pointer representation
	 * 
	 * 
	 * @param cursor
	 * @param color
	 * @return
	 */
	private MouseCursor defineMouseCursor(String cursor, String color) {
		final MouseCursor c = new MouseCursor(cursor, color);
		c.addMouseCursorEventHandler(new Handler() {

			@Override
			public void onMouseCursorTimeout(MouseCursorTimeoutEvent event) {
				visibleCursors.remove(c);
			}
		});
		if (visibleCursors.size() < getMaxCursorsOnScreen()) {
			c.show();
			visibleCursors.add(c);
		}

		RootPanel.get().add(c);
		return c;
	}

	HandlerRegistration r;
	Set<HandlerRegistration> currentRemoteEventHandlers = new HashSet<HandlerRegistration>();

	/**
	 * @return if the component is executed within a frame
	 */
	public native boolean isInIFrame() /*-{
		return $wnd.top.location != $wnd.location;
	}-*/;

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.multipointer.client.MultiCursorController#start()
	 */
	@Override
	public void start() {
		if (!isInIFrame()) {
			initializeCursorList();
			if (r != null)
				r.removeHandler();
			r = Window.addResizeHandler(this);
			EventBus eventBus = CommunicationManager.getBidirectionalEventBus();
			currentRemoteEventHandlers.add(eventBus.addHandler(RemoteMouseMoveEvent.TYPE, new RemoteMouseMoveEvent.Handler() {

				@Override
				public void onEvent(RemoteMouseMoveEvent event) {
					MouseCursor m = getOrCreateCursor(event.getOriginatingDevice());
					if (m != null)
						m.move(event);
				}
			}));
			currentRemoteEventHandlers.add(eventBus.addHandler(RemoteMouseDownEvent.TYPE, new RemoteMouseDownEvent.Handler() {

				@Override
				public void onEvent(RemoteMouseDownEvent event) {
					MouseCursor m = getOrCreateCursor(event.getOriginatingDevice());
					if (m != null)
						m.down(event);
				}
			}));
			currentRemoteEventHandlers.add(eventBus.addHandler(RemoteMouseUpEvent.TYPE, new RemoteMouseUpEvent.Handler() {

				@Override
				public void onEvent(RemoteMouseUpEvent event) {
					MouseCursor m = getOrCreateCursor(event.getOriginatingDevice());
					if (m != null)
						m.up(event);
				}
			}));
			MouseControllerServiceAsync svc = GWT.create(MouseControllerService.class);
			svc.registerAsMPProvider(UUID.get(), new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onSuccess(Void result) {
					Window.alert("Registered");
					// ((ServerPushEventBus) CommunicationManager.getBidirectionalEventBus()).sendPingEvent();
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.multipointer.client.MultiCursorController#stop()
	 */
	@Override
	public void stop() {
		super.stop();
		for (HandlerRegistration remoteEventHandler : currentRemoteEventHandlers) {
			remoteEventHandler.removeHandler();
		}
		currentRemoteEventHandlers.clear();
		for (MouseCursor c : visibleCursors) {
			c.hide();
		}
		visibleCursors.clear();
	}

	private MouseCursor getMouseCursorForEvent(RemoteEvent<?> event) {
		MouseCursor m = getOrCreateCursor(event.getOriginatingDevice());
		if (m != null) {
			boolean isActive = visibleCursors.contains(m);
			if (isActive || visibleCursors.size() < getMaxCursorsOnScreen()) {
				if (!isActive) {
					GWT.log("New cursor. Current visible cursors: " + visibleCursors.size());
					visibleCursors.add(m);
				}
				return m;
			}
		}
		return null;
	}

	/**
	 * Looks up the assigned mouse pointer for a specific device (by the uuid) and returns it. If no mouse pointer is assigned to this UUID, a new cursor is
	 * assigned to this device and returned. This method also informs the web socket server about the new assignment for passing through that information to the
	 * according client.
	 * 
	 * @param uuid
	 * @return
	 */
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
			storage.setItem("ch.unifr.pai.mice.multicursor.assignedCursor." + m.getFileName(), uuid);
		CommunicationManager.getBidirectionalEventBus().fireEvent(
				InformationUpdateEvent.changeColorAndResize(m.getColor(), Window.getClientWidth(), Window.getClientHeight(), uuid));
		return m;
	}

	/**
	 * If the screen of the shared device is resized, the component updates the information on the server side.
	 * 
	 * @see com.google.gwt.event.logical.shared.ResizeHandler#onResize(com.google.gwt.event.logical.shared.ResizeEvent)
	 */
	@Override
	public void onResize(ResizeEvent event) {
		CommunicationManager.getBidirectionalEventBus().fireEvent(InformationUpdateEvent.resize(Window.getClientWidth(), Window.getClientHeight()));
	}

}
