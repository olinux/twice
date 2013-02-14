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
import ch.unifr.pai.twice.module.client.TWICEModule;
import ch.unifr.pai.twice.utils.device.client.UUID;

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
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.event.shared.HandlerRegistration;

public abstract class TouchPadWidget extends LayoutPanel{
	

	
	@Configurable("Movement interval")
	static int MOVEMENTUPDATEINTERVAL = 80;
	@Configurable("Movement threshold")
	static int MOVEMENTTHRESHOLD = 0;
	@Configurable("Movement factor")
	static double MOVEFACTOR = 1.8;
	@Configurable("Mouse down threshold")
	static int MOUSEDOWNTHRESHOLD = 300;
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
	private Label label = new Label();
//	private TextBox focusTextBox = new TextBox();
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

	public String flushLog() {
		String result = log.toString();
		log = new StringBuilder();
		return result;
	}

	public boolean isDoLog() {
		return doLog;
	}

	public void setDoLog(boolean doLog) {
		this.doLog = doLog;
	}

	protected boolean dragModeEnabled = true;

	private boolean handleFocus = false;

	public TouchPadWidget(boolean handleFocus) {
		super();
		this.handleFocus = handleFocus;
//		add(focusTextBox);
		add(label);
//		focusTextBox.getElement().getStyle().setZIndex(-1);
		// focusTextBox.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		// send(null);
	}

	public boolean isDragModeEnabled() {
		return dragModeEnabled;
	}

	public void setDragModeEnabled(boolean dragModeEnabled) {
		this.dragModeEnabled = dragModeEnabled;
	}

	private Timer movement = new Timer() {

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

	private String getCurrentClient() {
		// TODO the user should select the client if there are multiple. For
		// testing, we take the latest
		return availableClients != null && availableClients.length > 0 ? availableClients[availableClients.length - 1]
				: null;
	}

	private void noCursorAvailable() {
		setActive(false);
		if (lookForCursor != null)
			lookForCursor.schedule(LOOKFORCURSORINTERVAL);
	}

	private void cursorAssigned() {
		setActive(true);
		running = true;
		String updateInterval = Window.Location.getParameter("update");
		if (updateInterval != null)
			MOVEMENTUPDATEINTERVAL = Integer.parseInt(updateInterval);
		movement.scheduleRepeating(MOVEMENTUPDATEINTERVAL);		
		keyboardHandler = Event.addNativePreviewHandler(keyboardPreviewHandler);
	}
	
	protected NativePreviewHandler keyboardPreviewHandler= new NativePreviewHandler() {

		@Override
		public void onPreviewNativeEvent(NativePreviewEvent event) {
			switch (event.getTypeInt()) {
			case Event.ONKEYDOWN:
//				if (handleFocus)
//					focusTextBox.setFocus(true);
				send("a=kd&kc="
						+ event.getNativeEvent().getKeyCode()
						+ "&cc="
						+ event.getNativeEvent().getCharCode());
				break;
			case Event.ONKEYUP:
//				if (handleFocus)
//					focusTextBox.setFocus(true);
				send("a=ku&kc="
						+ event.getNativeEvent().getKeyCode()
						+ "&cc="
						+ event.getNativeEvent().getCharCode());
				break;
			case Event.ONKEYPRESS:
//				if (handleFocus)
//					focusTextBox.setFocus(true);
				send("a=kp&kc="
						+ event.getNativeEvent().getKeyCode()
						+ "&cc="
						+ event.getNativeEvent().getCharCode());
				break;
			}
		}
	};

	protected HandlerRegistration keyboardHandler;

	private Timer lookForCursor;

	protected void onActionChanged(String action) {
		if (action != null) {
			if (action.equals("startDrag"))
				dragging = true;
			else if (action.equals("endDrag"))
				stopDragging();
		}
	}

	protected void stopDragging() {
		dragging = false;
	}

	public void start() {
		if (!running) {
			label.setText("looking for available remote-clients");
			getAvailableClients(new Command() {

				@Override
				public void execute() {
					label.setText((availableClients == null ? "0"
							: availableClients.length) + " clients found");
					if (getCurrentClient() != null) {
						label.setText("looking for cursor on client "
								+ getCurrentClient());
						lookForCursor = new Timer() {

							@Override
							public void run() {
								try {
									new RequestBuilder(
											RequestBuilder.GET,
											GWT.getHostPageBaseURL()
													+ controlServlet
													+ "?a=x"
													+ (getCurrentClient() != null ? "&targetUUID="
															+ getCurrentClient()
															: "")
													+ (uuid != null ? "&uuid="
															+ uuid : "")
													+ (host != null ? "&host="
															+ host : "")
													+ (port != null ? "&port="
															+ port : ""))
											.sendRequest(null,
													new RequestCallback() {

														@Override
														public void onResponseReceived(
																Request request,
																Response response) {
															if (response
																	.getStatusCode() > 400)
																onError(request,
																		null);
															label.setText("GOT DATA: "
																	+ response
																			.getText());
															String color = extractColor(response);
															if (color == null
																	|| color.isEmpty()
																	|| color.equals("#null"))
																color = null;
															extractLastAction(response);

															setScreenDimension(extractScreenDimensions(response));
															if (color != null) {
																setColor(color);
																cursorAssigned();
															} else {
																noCursorAvailable();
															}
														}

														@Override
														public void onError(
																Request request,
																Throwable exception) {
															noCursorAvailable();
														}
													});
								} catch (RequestException e) {
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

	public void stop() {
		if (running) {
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

	protected void down(boolean leftButton) {
		send("a=d&b=" + (leftButton ? "l" : "r"));
		downLastAction = true;
	}

	protected void up(boolean leftButton) {
		send("a=u&b=" + (leftButton ? "l" : "r"));
		downLastAction = false;
	}

	protected void hide() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				send("a=h");
			}
		});
	}

	protected abstract int getX();

	protected abstract int getY();

	protected void move(int x, int y) {
		if (screenDimension == null)
			send(null);
		else
			send("a=m&x=" + x + "&y=" + y);
	}

	protected void send(String query) {
		send(query, null);
	}

	protected void getStatus(Command callback) {
		send(null, callback);
	}

	private boolean noConnection;
	
	protected void send(String query, final Command callback) {
		try {
			if (active) {

				new RequestBuilder(RequestBuilder.GET, GWT.getHostPageBaseURL()
						+ controlServlet
						+ "?"
						+ (query != null ? query : "a=x")
						+ (getCurrentClient() != null ? "&targetUUID="
								+ getCurrentClient() : "")
						+ (uuid != null ? "&uuid=" + uuid : "")
						+ (host != null ? "&host=" + host : "")
						+ (port != null ? "&port=" + port : "")
						+ ("&user="+Authentication.getUserName())).sendRequest(
						null, new RequestCallback() {

							@Override
							public void onResponseReceived(Request request,
									Response response) {
								if (response.getStatusCode() > 400)
									onError(request, null);
								String color = extractColor(response);
								if(response.getText().trim().isEmpty()){
									label.setText("No connection available");
									noConnection = true;
								}
								else{
									if(noConnection){
										label.setText("");
										noConnection = false;										
									}
									
								if (color == null || color.isEmpty()
										|| color.equals("#null"))
									color = null;
								extractLastAction(response);
								setColor(color);
								setScreenDimension(extractScreenDimensions(response));
								if (callback != null)
									callback.execute();
								}
							}

							@Override
							public void onError(Request request,
									Throwable exception) {
								setActive(false);
							}
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setActive(boolean active) {
		this.active = active;
		if (!active) {
			this.getElement().getStyle().setBackgroundColor("grey");
			label.setText("Looking for available cursors... not found one so long.");
		} else {
			label.setText("");
		}
	}

	protected abstract void updateScreenDimensions();

	private void setScreenDimension(String dimension) {
		if (dimension != null
				&& (screenDimension == null || !screenDimension
						.equals(dimension))) {
			String[] values = dimension.split("x");
			if (values.length == 2) {
				screenWidth = Integer.parseInt(values[0]);
				screenHeight = Integer.parseInt(values[1]);
				updateScreenDimensions();
				screenDimension = dimension;
			}
		}
	}

	private static String extractScreenDimensions(Response resp) {
		return extractByRegex(resp, "[0-9]*x[0-9]*");
	}

	private static String extractColor(Response resp) {
		return extractByRegex(resp, "#.{6}");
	}

	protected String lastAction = null;

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

	private static String extractByRegex(Response response, String regex) {
		RegExp re = RegExp.compile(regex);
		MatchResult result = re.exec(response.getText());
		return result.getGroup(0);
	}

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
					Double d = ((double) (red + green + blue)) / 3.0;
					// 128 is the average brightness (16²/2)
					return d > 128;
				} catch (NumberFormatException e) {
					return true;
				}
			}
		}
		return true;
	}

	private void getAvailableClients(final Command callback) {
		try {
			new RequestBuilder(RequestBuilder.GET, GWT.getHostPageBaseURL()
					+ controlServlet + "?a=g").sendRequest(null,
					new RequestCallback() {

						@Override
						public void onResponseReceived(Request request,
								Response response) {
							if (response.getText() != null
									&& !response.getText().isEmpty()) {
								availableClients = response.getText().split(
										"\n");
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
		} catch (RequestException e) {
			GWT.log("Request Exception", e);
			if (callback != null)
				callback.execute();

		}
	}

	public String getColor() {
		return currentColor;
	}

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
	
	public boolean attachToRootPanel(){
		return false;
	}
}
