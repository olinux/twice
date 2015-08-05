package ch.unifr.pai.twice.multipointer.controller.client;

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

import ch.unifr.pai.twice.comm.serverPush.client.CommunicationManager;
import ch.unifr.pai.twice.module.client.TWICEAnnotations.Configurable;
import ch.unifr.pai.twice.multipointer.commons.client.events.*;
import ch.unifr.pai.twice.multipointer.commons.client.rpc.MouseControllerService;
import ch.unifr.pai.twice.multipointer.commons.client.rpc.MouseControllerServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

import java.util.Date;
import java.util.List;

/**
 * A generic implementation of a remote mouse control component
 * 
 * @author Oliver Schmid
 * 
 */
public abstract class TouchPadWidget extends LayoutPanel {

	/**
	 * The interval in ms of updating messages
	 */
	@Configurable("Movement interval")
	static int MOVEMENTUPDATEINTERVAL = 80;
	/**
	 * The threshold of distance in pixels that has to be exceeded by a mouse
	 * movement to trigger an event
	 */
	@Configurable("Movement threshold")
	static int MOVEMENTTHRESHOLD = 0;
	/**
	 * The factor with which the movement shall be increased
	 */
	@Configurable("Movement factor")
	static double MOVEFACTOR = 1.8;
	/**
	 * The threshold of how long the mouse shall be pressed until the device
	 * switches to drag mode
	 */
	@Configurable("Mouse down threshold")
	static int MOUSEDOWNTHRESHOLD = 300;
	/**
	 * In which interval the client shall try to assign a cursor on the shared
	 * screen
	 */
	@Configurable("Look for cursor interval")
	static int LOOKFORCURSORINTERVAL = 2000;

	private String uuid;
	private String host;
	private Integer port;
	private String currentColor;
	protected int screenWidth;
	protected int screenHeight;
	private boolean active = true;
	private int currentX = -1;
	private int currentY = -1;
	private boolean running;
	private boolean doLog = false;
	private StringBuilder log = new StringBuilder();
	private String header;
	private String[] availableClients;
	private final Label label = new Label();
	protected boolean dragging = false;

	private final static String controlServlet = "mouseManagerXBrowser";

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * Add data to the log
	 * 
	 * @param name
	 * @param attributes
	 * @param content
	 */
	public void addToLog(String name, String attributes, String content) {
		log.append("<").append(name);
		log.append(" time=\"").append(new Date().getTime())
				.append("\" uuid=\"").append(uuid)
				.append("\" color=\"" + getColor() + "\"");
		if (attributes != null) {
			log.append(" ").append(attributes);
		}
		log.append(">");
		if (content != null)
			log.append(content);
		log.append("</").append(name).append(">");
	}

	/**
	 * Clear the log
	 * 
	 * @return the log state before clearance
	 */
	public String flushLog() {
		String result = log.toString();
		log = new StringBuilder();
		return result;
	}

	/**
	 * @return true if the component is writing to the log
	 */
	public boolean isDoLog() {
		return doLog;
	}

	/**
	 * define if the component shall write to the log
	 * 
	 * @param doLog
	 */
	public void setDoLog(boolean doLog) {
		this.doLog = doLog;
	}

	/**
	 * defines if drag is enabled
	 */
	protected boolean dragModeEnabled = true;

	public TouchPadWidget(boolean handleFocus) {
		super();
		add(label);
		// send(null);
	}

	/**
	 * @return true if drag mode is enabled, false otherwise
	 */
	public boolean isDragModeEnabled() {
		return dragModeEnabled;
	}

	/**
	 * set if drag mode is enabled
	 * 
	 * @param dragModeEnabled
	 */
	public void setDragModeEnabled(boolean dragModeEnabled) {
		this.dragModeEnabled = dragModeEnabled;
	}

	/**
	 * timer which sends out movement events
	 */
	private final Timer movement = new Timer() {

		@Override
		public void run() {
			int x = getX();
			int y = getY();
			if (currentX != x || currentY != y) {
				move(x, y);
				currentX = x;
				currentY = y;
			}
		}
	};

	/**
	 * @return the id of the current available shared screen
	 */
	private String getCurrentClient() {
		// TODO the user should select the client if there are multiple. For
		// testing, we take the latest
		return availableClients != null && availableClients.length > 0 ? availableClients[availableClients.length - 1]
				: null;
	}

	/**
	 * If a cursor is assigned, start to fire events
	 */
	private void cursorAssigned() {
		setActive(true);
		running = true;
		String updateInterval = Window.Location.getParameter("update");
		if (updateInterval != null)
			MOVEMENTUPDATEINTERVAL = Integer.parseInt(updateInterval);
		movement.scheduleRepeating(MOVEMENTUPDATEINTERVAL);
		keyboardHandler = Event.addNativePreviewHandler(keyboardPreviewHandler);
	}

	// /**
	// * Handler for keyboard events and invocation of the key events on the
	// shared screen
	// */
	protected NativePreviewHandler keyboardPreviewHandler = new NativePreviewHandler() {

		@Override
		public void onPreviewNativeEvent(NativePreviewEvent event) {
			switch (event.getTypeInt()) {
			case Event.ONKEYDOWN:
				RemoteKeyDownEvent keyDownEvent = GWT
						.create(RemoteKeyDownEvent.class);
				keyDownEvent.keyCode = event.getNativeEvent().getKeyCode();
				CommunicationManager.getBidirectionalEventBus().fireEvent(
						keyDownEvent);
				switch(event.getNativeEvent().getKeyCode()){
					case KeyCodes.KEY_BACKSPACE:
					//case KeyCodes.KEY_ENTER:
						
						event.getNativeEvent().preventDefault();
						event.getNativeEvent().stopPropagation();
						event.cancel();
				}
				break;
			case Event.ONKEYUP:
				RemoteKeyUpEvent keyUpEvent = GWT
						.create(RemoteKeyUpEvent.class);
				keyUpEvent.keyCode = event.getNativeEvent().getKeyCode();
				CommunicationManager.getBidirectionalEventBus().fireEvent(
						keyUpEvent);
				
				switch(event.getNativeEvent().getKeyCode()){
					case KeyCodes.KEY_BACKSPACE:
					//case KeyCodes.KEY_ENTER:
						
						event.getNativeEvent().preventDefault();
						event.getNativeEvent().stopPropagation();
						event.cancel();
				}
				break;
			case Event.ONKEYPRESS:
				RemoteKeyPressEvent keyPressEvent = GWT
						.create(RemoteKeyPressEvent.class);
				keyPressEvent.keyCode = event.getNativeEvent().getKeyCode();
				keyPressEvent.charCode = event.getNativeEvent().getCharCode();
				CommunicationManager.getBidirectionalEventBus().fireEvent(
						keyPressEvent);
				break;
			}
		}
	};

	protected HandlerRegistration keyboardHandler;

	private Timer lookForCursor;

	/**
	 * Invoked if the last action has changed. Forces the stop or the start of
	 * dragging mode based on information originated on the server
	 * 
	 * @param action
	 */
	protected void onActionChanged(String action) {
		if (action != null) {
			if (action.equals("startDrag"))
				dragging = true;
			else if (action.equals("endDrag"))
				stopDragging();
		}
	}

	/**
	 * stops the dragging
	 */
	protected void stopDragging() {
		dragging = false;
	}

	private HandlerRegistration informationUpdateHandler;

	/**
	 * starts the execution of the component
	 */
	public void start() {
		if (!running) {
			informationUpdateHandler = CommunicationManager
					.getBidirectionalEventBus().addHandler(
							InformationUpdateEvent.TYPE,
							new InformationUpdateEvent.Handler() {

								@Override
								public void onEvent(InformationUpdateEvent event) {
									if (event.getOriginatingDevice().equals(
											currentClient)) {
										setScreenDimension(event.width,
												event.height);
										setColor(event.color);
									}
								}
							});
			label.setText("looking for available remote-clients");
			getAvailableClients(new Command() {

				@Override
				public void execute() {
					label.setText((availableClients == null ? "0"
							: availableClients.length) + " clients found");
					currentClient = getCurrentClient();
					if (currentClient != null) {
						cursorAssigned();
					}
				}
			});
		}
	}

	private String currentClient;

	/**
	 * stops the execution of the component (also interrupts the sending of
	 * events).
	 */
	public void stop() {
		if (running) {
			informationUpdateHandler.removeHandler();
			informationUpdateHandler = null;

			running = false;
			movement.cancel();
		}
	}

	/**
	 * Define the cursor id as well as the host and port for the target of the
	 * mouse control events. If not defined, the session id, localhost and the
	 * standard port will be used.
	 * 
	 * @param uuid
	 * @param host
	 * @param port
	 */
	public void initialize(String uuid, String host, Integer port) {
		this.uuid = uuid;
		this.host = host;
		this.port = port;
	}

	/**
	 * send a mouse down event to the mouse control servlet
	 * 
	 * @param leftButton
	 */
	protected void down(boolean leftButton) {
		RemoteMouseDownEvent event = GWT.create(RemoteMouseDownEvent.class);
		event.rightButton = !leftButton;
		CommunicationManager.getBidirectionalEventBus().fireEvent(event);
	}

	/**
	 * send a mouse up event to the mouse control servlet
	 * 
	 * @param leftButton
	 */
	protected void up(boolean leftButton) {
		RemoteMouseUpEvent event = GWT.create(RemoteMouseUpEvent.class);
		event.rightButton = !leftButton;
		CommunicationManager.getBidirectionalEventBus().fireEvent(event);
	}

	/**
	 * send a hide request to the mouse control servlet that lets the mouse
	 * pointer disappear on the shared screen
	 */
	protected void hide() {
		// Scheduler.get().scheduleDeferred(new ScheduledCommand() {
		//
		// @Override
		// public void execute() {
		// send("a=h");
		// }
		// });
	}

	/**
	 * @return the current x coordinate of the mouse pointer on the shared
	 *         screen
	 */
	protected abstract int getX();

	/**
	 * @return the current y coordinate of the mouse pointer on the shared
	 *         screen
	 */
	protected abstract int getY();

	/**
	 * Send a movement request to the mouse control servlet
	 * 
	 * @param x
	 * @param y
	 */
	protected void move(int x, int y) {
		RemoteMouseMoveEvent evt = GWT.create(RemoteMouseMoveEvent.class);
		evt.x = x;
		evt.y = y;
		CommunicationManager.getBidirectionalEventBus().fireEvent(evt);
	}

	/**
	 * sets the mouse control active or inactive
	 * 
	 * @param active
	 */
	private void setActive(boolean active) {
		this.active = active;
		if (!active) {
			this.getElement().getStyle().setBackgroundColor("grey");
			label.setText("Looking for available cursors... not found one so long.");
		} else {
			label.setText("");
		}
	}

	/**
	 * Further actions when the dimensions of the shared screen change
	 */
	protected abstract void updateScreenDimensions();

	/**
	 * Adapts the widget to the given screen dimensions of the shared screen
	 * 
	 * @param dimension
	 */
	private void setScreenDimension(Integer width, Integer height) {
		if (width != null && height != null
				&& (!width.equals(screenWidth) || !height.equals(screenHeight))) {
			screenWidth = width;
			screenHeight = height;
			updateScreenDimensions();
		}
	}

	protected String lastAction = null;

	/**
	 * Sets the color of the font based on the brightness of the background
	 * color either to black or to white
	 * 
	 * @param color
	 */
	protected void setColor(String color) {
		if (color != null) {
			color = color.trim();
			if (currentColor == null || !currentColor.equals(color)) {
				currentColor = color;
				this.getElement().getStyle().setBackgroundColor(color);
				// this.getElement().getStyle().setColor(getInvertedColor(color));
				this.getElement().getStyle()
						.setColor(isColorBright(color) ? "#000000" : "#ffffff");
			}
		}
	}

	/**
	 * @param color
	 * @return true if the color brightness is above the average and false if
	 *         not
	 */
	protected boolean isColorBright(String color) {
		if (color != null) {
			if (color.startsWith("#") && color.length() == 7) {
				try {
					Integer red = Integer.parseInt(color.substring(1, 3)
							.toLowerCase(), 16);
					Integer green = Integer.parseInt(color.substring(3, 5)
							.toLowerCase(), 16);
					Integer blue = Integer.parseInt(color.substring(5, 7)
							.toLowerCase(), 16);
					Double d = (red + green + blue) / 3.0;
					// 128 is the average brightness (16²/2)
					return d > 128;
				} catch (NumberFormatException e) {
					return true;
				}
			}
		}
		return true;
	}

	/**
	 * Request the server for available shared devices
	 * 
	 * @param callback
	 */
	private void getAvailableClients(final Command callback) {
		MouseControllerServiceAsync controller = GWT
				.create(MouseControllerService.class);
		controller.getMPProviders(new AsyncCallback<List<String>>() {

			@Override
			public void onSuccess(List<String> result) {
				if (result != null) {
					availableClients = result.toArray(new String[0]);
				} else {
					availableClients = new String[0];
				}
				if (callback != null)
					callback.execute();

			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("SERVICE NOT FOUND");
			}
		});
	}

	/**
	 * @return the color of the mouse pointer / background
	 */
	public String getColor() {
		return currentColor;
	}

	/**
	 * @param color
	 *            in HTML hexadecimal writing (e.g. "#000000");
	 * @return the inverted color
	 */
	protected String getInvertedColor(String color) {
		if (color != null) {
			if (color.startsWith("#")) {
				String hexString = color.substring(1);
				Integer i;
				Integer maxHex;
				try {
					i = Integer.parseInt(hexString.toLowerCase(), 16);
					maxHex = Integer.parseInt("ffffff", 16);
				} catch (NumberFormatException e) {
					return "#000000";
				}
				String newHexString = Integer.toHexString(maxHex - i);
				return "#" + newHexString;
			}
		}
		return "#000000";
	}

	/**
	 * @return true if the widget shall be attached to the root panel or false
	 *         if it takes care about the attachment by itself
	 */
	public boolean attachToRootPanel() {
		return false;
	}
}
