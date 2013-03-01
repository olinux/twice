package ch.unifr.pai.twice.mousecontrol.client;

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
import java.util.Date;

import ch.unifr.pai.twice.authentication.client.Authentication;
import ch.unifr.pai.twice.module.client.TWICEAnnotations.Configurable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

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
	 * The threshold of distance in pixels that has to be exceeded by a mouse movement to trigger an event
	 */
	@Configurable("Movement threshold")
	static int MOVEMENTTHRESHOLD = 0;
	/**
	 * The factor with which the movement shall be increased
	 */
	@Configurable("Movement factor")
	static double MOVEFACTOR = 1.8;
	/**
	 * The threshold of how long the mouse shall be pressed until the device switches to drag mode
	 */
	@Configurable("Mouse down threshold")
	static int MOUSEDOWNTHRESHOLD = 300;
	/**
	 * In which interval the client shall try to assign a cursor on the shared screen
	 */
	@Configurable("Look for cursor interval")
	static int LOOKFORCURSORINTERVAL = 2000;

	private String uuid;
	private String host;
	private Integer port;
	private String currentColor;
	private String screenDimension;
	protected int screenWidth;
	protected int screenHeight;
	private boolean active = true;
	private int currentX = -1;
	private int currentY = -1;
	private boolean running;
	private boolean downLastAction = false;
	private boolean doLog = false;
	private StringBuilder log = new StringBuilder();
	private String header;
	private String[] availableClients;
	private final Label label = new Label();
	// private TextBox focusTextBox = new TextBox();
	protected boolean dragging = false;

	// private final static String controlServlet = "mouseManager";
	private final static String controlServlet = "mouseManagerXBrowser";

	// private final static String controlServlet = "mouseManagerXBrowserWS";

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
		log.append(" time=\"").append(new Date().getTime()).append("\" uuid=\"").append(uuid).append("\" color=\"" + getColor() + "\"");
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
		// add(focusTextBox);
		add(label);
		// focusTextBox.getElement().getStyle().setZIndex(-1);
		// focusTextBox.getElement().getStyle().setVisibility(Visibility.HIDDEN);
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
		return availableClients != null && availableClients.length > 0 ? availableClients[availableClients.length - 1] : null;
	}

	/**
	 * if no cursor is available on the shared screen, try to gather one with the given interval
	 */
	private void noCursorAvailable() {
		setActive(false);
		if (lookForCursor != null)
			lookForCursor.schedule(LOOKFORCURSORINTERVAL);
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

	/**
	 * Handler for keyboard events and invocation of the key events on the shared screen
	 */
	protected NativePreviewHandler keyboardPreviewHandler = new NativePreviewHandler() {

		@Override
		public void onPreviewNativeEvent(NativePreviewEvent event) {
			switch (event.getTypeInt()) {
				case Event.ONKEYDOWN:
					// if (handleFocus)
					// focusTextBox.setFocus(true);
					send("a=kd&kc=" + event.getNativeEvent().getKeyCode() + "&cc=" + event.getNativeEvent().getCharCode());
					break;
				case Event.ONKEYUP:
					// if (handleFocus)
					// focusTextBox.setFocus(true);
					send("a=ku&kc=" + event.getNativeEvent().getKeyCode() + "&cc=" + event.getNativeEvent().getCharCode());
					break;
				case Event.ONKEYPRESS:
					// if (handleFocus)
					// focusTextBox.setFocus(true);
					send("a=kp&kc=" + event.getNativeEvent().getKeyCode() + "&cc=" + event.getNativeEvent().getCharCode());
					break;
			}
		}
	};

	protected HandlerRegistration keyboardHandler;

	private Timer lookForCursor;

	/**
	 * Invoked if the last action has changed. Forces the stop or the start of dragging mode based on information originated on the server
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

	/**
	 * starts the execution of the component
	 */
	public void start() {
		if (!running) {
			label.setText("looking for available remote-clients");
			getAvailableClients(new Command() {

				@Override
				public void execute() {
					label.setText((availableClients == null ? "0" : availableClients.length) + " clients found");
					if (getCurrentClient() != null) {
						label.setText("looking for cursor on client " + getCurrentClient());
						lookForCursor = new Timer() {

							@Override
							public void run() {
								try {
									new RequestBuilder(RequestBuilder.GET, GWT.getHostPageBaseURL() + controlServlet + "?a=x"
											+ (getCurrentClient() != null ? "&targetUUID=" + getCurrentClient() : "") + (uuid != null ? "&uuid=" + uuid : "")
											+ (host != null ? "&host=" + host : "") + (port != null ? "&port=" + port : "")).sendRequest(null,
											new RequestCallback() {

												@Override
												public void onResponseReceived(Request request, Response response) {
													if (response.getStatusCode() > 400)
														onError(request, null);
													label.setText("GOT DATA: " + response.getText());
													String color = extractColor(response);
													if (color == null || color.isEmpty() || color.equals("#null"))
														color = null;
													extractLastAction(response);

													setScreenDimension(extractScreenDimensions(response));
													if (color != null) {
														setColor(color);
														cursorAssigned();
													}
													else {
														noCursorAvailable();
													}
												}

												@Override
												public void onError(Request request, Throwable exception) {
													noCursorAvailable();
												}
											});
								}
								catch (RequestException e) {
									noCursorAvailable();
								}
							}
						};
						lookForCursor.run();
					}
				}
			});
		}
	}

	/**
	 * stops the execution of the component (also interrupts the sending of events).
	 */
	public void stop() {
		if (running) {
			running = false;
			movement.cancel();
		}
	}

	/**
	 * Define the cursor id as well as the host and port for the target of the mouse control events. If not defined, the session id, localhost and the standard
	 * port will be used.
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
		send("a=d&b=" + (leftButton ? "l" : "r"));
		downLastAction = true;
	}

	/**
	 * send a mouse up event to the mouse control servlet
	 * 
	 * @param leftButton
	 */
	protected void up(boolean leftButton) {
		send("a=u&b=" + (leftButton ? "l" : "r"));
		downLastAction = false;
	}

	/**
	 * send a hide request to the mouse control servlet that lets the mouse pointer disappear on the shared screen
	 */
	protected void hide() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				send("a=h");
			}
		});
	}

	/**
	 * @return the current x coordinate of the mouse pointer on the shared screen
	 */
	protected abstract int getX();

	/**
	 * @return the current y coordinate of the mouse pointer on the shared screen
	 */
	protected abstract int getY();

	/**
	 * Send a movement request to the mouse control servlet
	 * 
	 * @param x
	 * @param y
	 */
	protected void move(int x, int y) {
		if (screenDimension == null)
			send(null);
		else
			send("a=m&x=" + x + "&y=" + y);
	}

	/**
	 * Delegator method to send a query to the mouse control servlet without callback
	 * 
	 * @param query
	 */
	protected void send(String query) {
		send(query, null);
	}

	/**
	 * Sends a request to the server to get the current status
	 * 
	 * @param callback
	 */
	protected void getStatus(Command callback) {
		send(null, callback);
	}

	private boolean noConnection;

	/**
	 * Sends the given query to the mouse pointer controller servlet
	 * 
	 * @param query
	 * @param callback
	 */
	protected void send(String query, final Command callback) {
		try {
			if (active) {

				new RequestBuilder(RequestBuilder.GET, GWT.getHostPageBaseURL() + controlServlet + "?" + (query != null ? query : "a=x")
						+ (getCurrentClient() != null ? "&targetUUID=" + getCurrentClient() : "") + (uuid != null ? "&uuid=" + uuid : "")
						+ (host != null ? "&host=" + host : "") + (port != null ? "&port=" + port : "") + ("&user=" + Authentication.getUserName()))
						.sendRequest(null, new RequestCallback() {

							@Override
							public void onResponseReceived(Request request, Response response) {
								if (response.getStatusCode() > 400)
									onError(request, null);
								String color = extractColor(response);
								if (response.getText().trim().isEmpty()) {
									label.setText("No connection available");
									noConnection = true;
								}
								else {
									if (noConnection) {
										label.setText("");
										noConnection = false;
									}

									if (color == null || color.isEmpty() || color.equals("#null"))
										color = null;
									extractLastAction(response);
									setColor(color);
									setScreenDimension(extractScreenDimensions(response));
									if (callback != null)
										callback.execute();
								}
							}

							@Override
							public void onError(Request request, Throwable exception) {
								setActive(false);
							}
						});
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
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
		}
		else {
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
	private void setScreenDimension(String dimension) {
		if (dimension != null && (screenDimension == null || !screenDimension.equals(dimension))) {
			String[] values = dimension.split("x");
			if (values.length == 2) {
				screenWidth = Integer.parseInt(values[0]);
				screenHeight = Integer.parseInt(values[1]);
				updateScreenDimensions();
				screenDimension = dimension;
			}
		}
	}

	/**
	 * @param resp
	 * @return the screen dimensions of the shared screen separated by a "x"
	 */
	private static String extractScreenDimensions(Response resp) {
		return extractByRegex(resp, "[0-9]*x[0-9]*");
	}

	/**
	 * @param resp
	 * @return the color of the assigned the mouse pointer
	 */
	private static String extractColor(Response resp) {
		return extractByRegex(resp, "#.{6}");
	}

	protected String lastAction = null;

	/**
	 * @param response
	 *            - the last action that has been applied
	 */
	private void extractLastAction(Response response) {
		String[] split = response.getText().split("@");
		if (split.length > 2) {
			if (!split[2].equals(lastAction)) {
				lastAction = split[2];
				onActionChanged(lastAction);
				GWT.log("Action changed: " + lastAction);
			}
		}
	}

	/**
	 * Helper method to extract values from the HTTP-response by the given regular expression
	 * 
	 * @param response
	 * @param regex
	 * @return
	 */
	private static String extractByRegex(Response response, String regex) {
		RegExp re = RegExp.compile(regex);
		MatchResult result = re.exec(response.getText());
		return result.getGroup(0);
	}

	/**
	 * Sets the color of the font based on the brightness of the background color either to black or to white
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
				this.getElement().getStyle().setColor(isColorBright(color) ? "#000000" : "#ffffff");
			}
		}
	}

	/**
	 * @param color
	 * @return true if the color brightness is above the average and false if not
	 */
	protected boolean isColorBright(String color) {
		if (color != null) {
			if (color.startsWith("#") && color.length() == 7) {
				try {
					Integer red = Integer.parseInt(color.substring(1, 3).toLowerCase(), 16);
					Integer green = Integer.parseInt(color.substring(3, 5).toLowerCase(), 16);
					Integer blue = Integer.parseInt(color.substring(5, 7).toLowerCase(), 16);
					Double d = (red + green + blue) / 3.0;
					// 128 is the average brightness (16²/2)
					return d > 128;
				}
				catch (NumberFormatException e) {
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
		try {
			new RequestBuilder(RequestBuilder.GET, GWT.getHostPageBaseURL() + controlServlet + "?a=g").sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (response.getText() != null && !response.getText().isEmpty()) {
						availableClients = response.getText().split("\n");
					}
					if (callback != null)
						callback.execute();
				}

				@Override
				public void onError(Request request, Throwable exception) {
					GWT.log("Available clients request", exception);
					if (callback != null)
						callback.execute();
				}
			});
		}
		catch (RequestException e) {
			GWT.log("Request Exception", e);
			if (callback != null)
				callback.execute();

		}
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
				}
				catch (NumberFormatException e) {
					return "#000000";
				}
				String newHexString = Integer.toHexString(maxHex - i);
				return "#" + newHexString;
			}
		}
		return "#000000";
	}

	/**
	 * @return true if the widget shall be attached to the root panel or false if it takes care about the attachment by itself
	 */
	public boolean attachToRootPanel() {
		return false;
	}
}
