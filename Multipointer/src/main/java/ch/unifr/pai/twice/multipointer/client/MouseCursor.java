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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * The represenation and logic of a mouse pointer
 * 
 * @author Oliver Schmid
 * 
 */
public class MouseCursor extends SimplePanel {

	private final static int VISIBILITYTIMEOUT = 2000;
	private final static int DETACHMENTTIMEOUT = 5000;

	private final String color;
	private final String fileName;
	private int x;
	private int y;
	private long lastUpdate;
	private final Image image = new Image();
	private Element oldElement;
	private Element mouseDownElement;
	private String uuid;
	private boolean buttonDown = false;
	private boolean rightButtonDown = false;
	private Element focussedElement;
	private final Storage storage = Storage.getSessionStorageIfSupported();
	private final AbsolutePanel panel = new AbsolutePanel();
	private final Label l = new Label();

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * After the run out of this timer without updates, the mouse pointer is hidden
	 */
	private final Timer timeout = new Timer() {

		@Override
		public void run() {
			MouseCursor.this.hide();
			MouseCursorTimeoutEvent e = new MouseCursorTimeoutEvent();
			e.detached = false;
			fireEvent(e);
		}
	};

	/**
	 * After the run out of this timer without updates, the assignment between the mouse pointer and the client device is released.
	 */
	private final Timer detachtimeout = new Timer() {

		@Override
		public void run() {
			MouseCursor.this.hide();
			MouseCursorTimeoutEvent e = new MouseCursorTimeoutEvent();
			e.detached = true;
			fireEvent(e);
		}
	};

	public MouseCursor(String fileName, String color) {
		super();
		this.addStyleName("multiCursor");
		this.color = color;
		this.fileName = fileName;
		this.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		this.getElement().getStyle().setPosition(Position.FIXED);
		this.getElement().getStyle().setZIndex(2000);
		if (storage != null) {
			String x = storage.getItem("ch.unifr.pai.mice.multicursor.position." + fileName + ".x");
			String y = storage.getItem("ch.unifr.pai.mice.multicursor.position." + fileName + ".y");
			if (x != null && y != null) {
				move(Integer.parseInt(x), Integer.parseInt(y));
				show();
			}

		}

		image.setUrl(GWT.getModuleBaseURL() + "cursors/" + fileName + ".png");
		panel.add(image);
		image.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		panel.add(l);
		panel.getElement().getStyle().setOverflow(Overflow.VISIBLE);
		l.getElement().getStyle().setPosition(Position.ABSOLUTE);
		l.getElement().getStyle().setTop(0, Unit.PX);
		l.getElement().getStyle().setLeft(image.getOffsetWidth(), Unit.PX);
		l.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		l.getElement().getStyle().setFontSize(16, Unit.PX);
		setWidget(panel);
	}

	/**
	 * @return the color of the mouse pointer
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @return the filename of the image that represents the mouse pointer
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Fire a mouse event on the local browser window
	 * 
	 * @param event
	 * @param uuid
	 * @param color
	 * @param element
	 * @param x
	 * @param y
	 */
	private void fireMouseEvent(String event, String uuid, String color, JavaScriptObject element, int x, int y) {
		fireMouseEvent(event, uuid, color, element, x, y, false, false, false, false);
	}

	/**
	 * Fire a keyboard event on the local browser window
	 * 
	 * @param eventType
	 * @param uuid
	 * @param color
	 * @param element
	 * @param keyCode
	 * @param charCode
	 * @param ctrlKey
	 * @param altKey
	 * @param shiftKey
	 * @param metaKey
	 */
	private native void fireKeyboardEvent(String eventType, String uuid, String color, JavaScriptObject element, int keyCode, int charCode, boolean ctrlKey,
			boolean altKey, boolean shiftKey, boolean metaKey)/*-{
		var frameDocument = element.ownerDocument;

		var event = frameDocument.createEvent("Events");
		event.initEvent(eventType, true, true);
		//initialize
		event.view = frameDocument.defaultView;
		event.altKey = false;
		event.ctrlKey = false;
		event.shiftKey = false;
		event.metaKey = false;
		event.keyCode = keyCode;
		event.charCode = charCode;
		event.uuid = uuid;
		event.color = color;
		element.dispatchEvent(event);
	}-*/;

	/**
	 * Fire mouse event on the current browser window (with extended properties such as uuid and color)
	 * 
	 * @param event
	 * @param uuid
	 * @param color
	 * @param element
	 * @param x
	 * @param y
	 * @param ctrlKey
	 * @param altKey
	 * @param shiftKey
	 * @param metaKey
	 */
	private native void fireMouseEvent(String event, String uuid, String color, JavaScriptObject element, int x, int y, boolean ctrlKey, boolean altKey,
			boolean shiftKey, boolean metaKey)/*-{
		var frameDocument = element.ownerDocument;

		var mouseEvent = frameDocument.createEvent("MouseEvent");
		var screenX = ($wnd.screenX + ($wnd.outerWidth - $wnd.innerWidth)) + x;
		var screenY = ($wnd.screenY + ($wnd.outerHeight - $wnd.innerHeight))
				+ y;
		//Add scroll offset to the position.
		x = x + $wnd.pageXOffset;
		y = y + $wnd.pageYOffset;
		mouseEvent.initMouseEvent(event, true, true, frameDocument.defaultView,
				0, screenX, screenY, x, y, false, false, false, false, 0, null);
		mouseEvent.uuid = uuid;
		mouseEvent.color = color;
		element.dispatchEvent(mouseEvent);
	}-*/;

	/**
	 * Fire a mouse down event
	 * 
	 * @param right
	 */
	private void down(boolean right) {
		if (right)
			this.rightButtonDown = true;
		else
			this.buttonDown = true;
		Element e = getElementFromPoint(x, y);
		focussedElement = e;
		fireMouseEvent("mousedown", uuid, color, e, x, y);
		mouseDownElement = e;
	}

	/**
	 * Fire a mouse up event
	 * 
	 * @param right
	 */
	private void up(boolean right) {
		if (right)
			this.rightButtonDown = false;
		else
			this.buttonDown = false;
		Element e = getElementFromPoint(x, y);
		fireMouseEvent("mouseup", uuid, color, e, x, y);
		if (e.equals(mouseDownElement)) {
			fireMouseEvent("click", uuid, color, e, x, y);
			// if (e.getClassName().contains("multiFocusWidget"))
			if (focussedElement != null && !focussedElement.equals(e))
				focussedElement.blur();
			focussedElement = e;
			e.focus();
			// else
			// focussedElement = null;
			// e.focus();
		}
	}

	/**
	 * Fire a key down event
	 * 
	 * @param keyCode
	 * @param charcode
	 */
	private void keyDown(int keyCode, int charcode) {
		Element e = focussedElement;
		if (e == null)
			e = Document.get().getDocumentElement();
		e.focus();
		fireKeyboardEvent("keydown", uuid, color, e, keyCode, charcode, false, false, false, false);
	}

	/**
	 * Fire a key up event
	 * 
	 * @param keyCode
	 * @param charcode
	 */
	private void keyUp(int keyCode, int charcode) {
		Element e = focussedElement;
		if (e == null)
			e = Document.get().getDocumentElement();
		e.focus();
		fireKeyboardEvent("keyup", uuid, color, e, keyCode, charcode, false, false, false, false);
	}

	/**
	 * Fire a key press event
	 * 
	 * @param keyCode
	 * @param charcode
	 */
	private void keyPress(int keyCode, int charcode) {
		Element e = focussedElement;
		if (e == null)
			e = Document.get().getDocumentElement();
		e.focus();
		fireKeyboardEvent("keypress", uuid, color, e, keyCode, charcode, false, false, false, false);
	}

	/**
	 * Get the element at a specific point
	 * 
	 * @param frameDocument
	 * @param x
	 * @param y
	 * @return
	 */
	private native Element elementFromPoint(Document frameDocument, int x, int y)/*-{
		return frameDocument.elementFromPoint(x, y);
	}-*/;

	/**
	 * Get the element at a specific point
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private Element getElementFromPoint(int x, int y) {
		Set<Element> hiddenCursors = new HashSet<Element>();
		Element result = getElementFromPointRec(x, y, hiddenCursors);
		for (Element e : hiddenCursors) {
			e.getStyle().setDisplay(Display.INLINE_BLOCK);
		}
		return result == null ? Document.get().getDocumentElement() : result;
	}

	/**
	 * Get the element within an embedded frame
	 * 
	 * @param e
	 * @param x
	 * @param y
	 * @return
	 */
	private Element getElementInFrame(Element e, int x, int y) {
		if (e != null && e.getTagName() != null && e.getTagName().equalsIgnoreCase("iframe")) {
			FrameElement frame = FrameElement.as(e);
			Document doc = frame.getContentDocument();
			if (doc == null)
				return e;
			x = x - frame.getAbsoluteLeft();
			y = y - frame.getAbsoluteTop();
			e = elementFromPoint(doc, x, y);
			return getElementInFrame(e, x, y);
		}
		else
			return e;
	}

	/**
	 * Recursive method that hides the mouse pointer elements which are below the specific point until the first element is found which is not a mouse pointer.
	 * 
	 * @param x
	 * @param y
	 * @param hiddenCursors
	 *            - a set which is filled with references to all mouse pointers which had to be hidden to find the underlying element.
	 * @return
	 */
	private Element getElementFromPointRec(int x, int y, Set<Element> hiddenCursors) {
		Element e = elementFromPoint(Document.get(), x, y);
		e = getElementInFrame(e, x, y);
		Element tmpE = e;
		if (e != null && e.getTagName().equalsIgnoreCase("img") && Document.get().getDocumentElement() != e)
			tmpE = e.getParentElement().getParentElement();
		if (tmpE != null && tmpE.getClassName().contains("multiCursor")) {
			hiddenCursors.add(tmpE);
			tmpE.getStyle().setDisplay(Display.NONE);
			e = getElementFromPointRec(x, y, hiddenCursors);
		}
		return e;
	}

	/**
	 * Reposition the mouse pointer to x and y coordinates
	 * 
	 * @param x
	 * @param y
	 */
	private void move(int x, int y) {
		this.x = x;
		this.y = y;
		Element e = getElementFromPoint(x, y);
		int shrinkX = x + image.getOffsetWidth() - Window.getClientWidth() - Window.getScrollLeft();
		int shrinkY = y + image.getOffsetHeight() - Window.getClientHeight() - Window.getScrollTop();
		setWidth(Math.max(0, (image.getOffsetWidth() - (shrinkX > 0 ? shrinkX : 0))) + "px");
		setHeight(Math.max(0, (image.getOffsetHeight() - (shrinkY > 0 ? shrinkY : 0))) + "px");
		int clientX = x + Window.getScrollLeft();
		int clientY = y + Window.getScrollTop();
		if (!e.equals(oldElement)) {
			if (oldElement != null)
				fireMouseEvent("mouseout", uuid, color, oldElement, clientX, clientY);
			fireMouseEvent("mouseover", uuid, color, e, clientX, clientY);
		}
		fireMouseEvent("mousemove", uuid, color, e, clientX, clientY);
		oldElement = e;
		getElement().getStyle().setLeft(x, Unit.PX);
		getElement().getStyle().setTop(y, Unit.PX);
		if (storage != null) {
			storage.setItem("ch.unifr.pai.mice.multicursor.position." + fileName + ".x", String.valueOf(x));
			storage.setItem("ch.unifr.pai.mice.multicursor.position." + fileName + ".y", String.valueOf(y));
		}
	}

	/**
	 * @return the timestamp in ms of the last update
	 */
	public long getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * sets the timestamp of the last update
	 * 
	 * @param lastUpdate
	 */
	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * Interpret the message of an event and trigger the appropriate methods.
	 * 
	 * @param action
	 * @param params
	 */
	public void interpretMessage(String action, Map<String, String> params) {
		timeout.cancel();
		detachtimeout.cancel();
		l.getElement().getStyle().setLeft(image.getOffsetWidth(), Unit.PX);
		l.getElement().getStyle().setTop(image.getOffsetHeight() / 2, Unit.PX);
		String user = params.get("user");
		if (user != null) {
			l.setText((user.length() > 1 ? user.substring(0, 2) : user).toUpperCase());
		}
		else
			l.setText("");
		if (action.equals("m")) {
			move(Integer.parseInt(params.get("x")), Integer.parseInt(params.get("y")));
			show();
		}
		else if (action.equals("d")) {
			down(params.get("b") != null && params.get("b").equals("r"));
			show();
		}
		else if (action.equals("u")) {
			up(params.get("b") != null && params.get("b").equals("r"));
			show();
		}
		else if (action.equals("kd")) {
			keyDown(Integer.parseInt(params.get("kc")), Integer.parseInt(params.get("cc")));
		}
		else if (action.equals("ku")) {
			keyUp(Integer.parseInt(params.get("kc")), Integer.parseInt(params.get("cc")));
		}
		else if (action.equals("kp")) {
			keyPress(Integer.parseInt(params.get("kc")), Integer.parseInt(params.get("cc")));
		}
	}

	/**
	 * Show the mouse pointer on the screen and fire a mouseover event
	 */
	public void show() {
		Element e = getElementFromPoint(x, y);
		fireMouseEvent("mouseover", uuid, color, e, x, y);
		getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		getElement().getStyle().setPosition(Position.FIXED);
		timeout.schedule(VISIBILITYTIMEOUT);
		detachtimeout.schedule(DETACHMENTTIMEOUT);
	}

	/**
	 * Hide the mouse pointer on the screen and fire a mouseout event
	 */
	public void hide() {
		if (oldElement != null) {
			fireMouseEvent("mouseout", uuid, color, oldElement, x, y);
			if (buttonDown)
				up(false);
			if (rightButtonDown)
				up(true);
		}
		getElement().getStyle().setDisplay(Display.NONE);
		oldElement = null;
	}

	/**
	 * Adds a handler for a {@link MouseCursorTimeoutEvent}
	 * 
	 * @param handler
	 * @return
	 */
	public HandlerRegistration addMouseCursorEventHandler(MouseCursorTimeoutEvent.Handler handler) {
		return addHandler(handler, MouseCursorTimeoutEvent.TYPE);
	}

}
