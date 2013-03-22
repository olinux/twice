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
import java.util.Date;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * The touchpad for mobile devices (based on scrolling for better performance).
 * 
 * @author oli
 * 
 */
public class TouchPadMobileWidget extends TouchPadWidget implements TouchStartHandler, TouchEndHandler, TouchMoveHandler {

	private int x;
	private int y;
	private boolean move;
	private boolean dragging = false;

	/**
	 * Timer for recognizing a switch to drag mode
	 */
	private final Timer mouseDownTimer = new Timer() {
		@Override
		public void run() {
			down(true);
			downSent = true;
			dragging = true;
			if (isDoLog())
				addToLog("startDrag", "cursorX=\"" + x + "\" cursorY=\"" + y + "\"", null);
			widget.getElement().setInnerHTML("<p>Your device is in dragging mode.</p><p> Tap on the screen to release.</p>");
		}
	};

	/**
	 * Transform relative movements to absolute x- and y-coordinates
	 * 
	 * @param dX
	 * @param dY
	 */
	private void updatePos(int dX, int dY) {
		if (dX != 0) {
			int changeX = (int) Math.floor((dX * MOVEFACTOR));
			x = Math.max(Math.min(x + changeX, screenWidth), 0);
		}
		if (dY != 0) {
			int changeY = (int) Math.floor((dY * MOVEFACTOR));
			y = Math.max(Math.min(y + changeY, screenHeight), 0);
		}

	}

	private int lastX;
	private int lastY;

	private long fingerDownAt;
	private boolean downSent;

	DockLayoutPanel p = new DockLayoutPanel(Unit.PCT);
	// private MobileKeyboard keyboardButton = new MobileKeyboard("Keyboard", "Done");
	HTML widget = new HTML();

	public TouchPadMobileWidget() {
		super(false);
		widget.getElement().getStyle().setProperty("userSelect", "none");
		widget.addTouchStartHandler(this);
		widget.addTouchEndHandler(this);
		widget.addTouchMoveHandler(this);
		widget.getElement().getStyle().setFontSize(20, Unit.PX);
		add(p);
		widget.setHeight("100%");
		// p.addSouth(keyboardButton, 10);
		p.add(widget);
		// setWidgetTopBottom(widget, 0, Unit.PX, 0, Unit.PX);
		// setWidgetLeftRight(widget, 0, Unit.PX, 0, Unit.PX);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.mousecontrol.client.TouchPadWidget#updateScreenDimensions()
	 */
	@Override
	protected void updateScreenDimensions() {
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.mousecontrol.client.TouchPadWidget#getX()
	 */
	@Override
	protected int getX() {
		return x;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.mousecontrol.client.TouchPadWidget#getY()
	 */
	@Override
	protected int getY() {
		return y;
	}

	/**
	 * On movement, calculate the new position of the mouse pointer on the shared screen
	 * 
	 * @see com.google.gwt.event.dom.client.TouchMoveHandler#onTouchMove(com.google.gwt.event.dom.client.TouchMoveEvent)
	 */
	@Override
	public void onTouchMove(TouchMoveEvent event) {
		event.preventDefault();
		event.stopPropagation();
		if (event.getTouches().length() > 0) {
			Touch t = event.getTouches().get(0);
			int x = t.getClientX();
			int y = t.getClientY();
			int dX = x - lastX;
			int dY = y - lastY;
			if (Math.abs(dX) > MOVEMENTTHRESHOLD || Math.abs(dY) > MOVEMENTTHRESHOLD) {
				mouseDownTimer.cancel();
				lastX = x;
				lastY = y;
				updatePos(dX, dY);
				move = true;
			}
		}
	}

	/**
	 * On touch end - stop dragging
	 * 
	 * @see com.google.gwt.event.dom.client.TouchEndHandler#onTouchEnd(com.google.gwt.event.dom.client.TouchEndEvent)
	 */
	@Override
	public void onTouchEnd(TouchEndEvent event) {
		event.preventDefault();
		event.stopPropagation();
		if (isDoLog())
			addToLog("endTouch", "cursorX=\"" + x + "\" cursorY=\"" + y + "\"", null);
		if (fingerDownAt != -1) {
			if (!move) {
				stopDragging();
			}
		}
		fingerDownAt = -1;
		move = false;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.mousecontrol.client.TouchPadWidget#stopDragging()
	 */
	@Override
	protected void stopDragging() {
		if (!downSent) {
			down(true);
			mouseDownTimer.cancel();
		}
		up(true);
		if (isDoLog())
			addToLog("stopDrag", "cursorX=\"" + x + "\" cursorY=\"" + y + "\"", null);
		super.stopDragging();
		widget.getElement().setInnerHTML("");
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.mousecontrol.client.TouchPadWidget#stop()
	 */
	@Override
	public void stop() {
		if (dragging) {
			stopDragging();
		}
		super.stop();
	}

	/**
	 * On touch start, check if it is the begin of a drag.
	 * 
	 * @see com.google.gwt.event.dom.client.TouchStartHandler#onTouchStart(com.google.gwt.event.dom.client.TouchStartEvent)
	 */
	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.preventDefault();
		event.stopPropagation();
		if (event.getTouches().length() > 0) {
			Touch t = event.getTouches().get(0);
			lastX = t.getClientX();
			lastY = t.getClientY();
			fingerDownAt = new Date().getTime();
			if (isDoLog())
				addToLog("startTouch", "cursorX=\"" + x + "\" cursorY=\"" + y + "\"", null);
			if (!move) {
				downSent = false;
				if (dragModeEnabled)
					mouseDownTimer.schedule(MOUSEDOWNTHRESHOLD);
			}

		}
	}

}
