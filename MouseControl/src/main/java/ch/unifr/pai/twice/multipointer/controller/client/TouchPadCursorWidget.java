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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.HTML;

/**
 * The touchpad widget for cursor based devices. Here, the relative position of the mouse is captured (in ppm) and sent to the server. The server calculates the
 * corresponding coordinates for the cursor based on the screen dimensions.
 * 
 * @author oli
 * 
 */
public class TouchPadCursorWidget extends TouchPadWidget {

	private int x;
	private int y;

	private int dragOffsetWidth;
	private int dragOffsetHeight;
	private final int borderSize = 40;

	private final HTML dragArea = new HTML();

	public TouchPadCursorWidget() {
		super(true);
		dragArea.getElement().setAttribute("oncontextmenu", "return false;");
		dragArea.setStyleName("dragArea");
		add(dragArea);
		dragArea.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				event.preventDefault();
				down(NativeEvent.BUTTON_RIGHT != event.getNativeButton());
			}
		});
		dragArea.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				event.preventDefault();
				up(NativeEvent.BUTTON_RIGHT != event.getNativeButton());
			}
		});
		dragArea.addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent event) {
				// event.preventDefault();
				x = event.getRelativeX(dragArea.getElement());
				y = event.getRelativeY(dragArea.getElement());
			}
		});

		// dragArea.addMouseOverHandler(new MouseOverHandler() {
		//
		// @Override
		// public void onMouseOver(MouseOverEvent event) {
		// if (keyboardHandler == null) {
		//
		// GWT.log("ADD KEYBOARD HANDLER");
		// keyboardHandler = Event.addNativePreviewHandler(keyboardPreviewHandler);
		// }
		// }
		// });
		//
		// dragArea.addMouseOutHandler(new MouseOutHandler() {
		//
		// @Override
		// public void onMouseOut(MouseOutEvent event) {
		// if (keyboardHandler != null) {
		// GWT.log("REMOVE KEYBOARD HANDLER");
		// keyboardHandler.removeHandler();
		// keyboardHandler = null;
		// }
		// }
		// });
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.mousecontrol.client.TouchPadWidget#updateScreenDimensions()
	 */
	@Override
	protected void updateScreenDimensions() {
		// Do nothing special since we're calculating relative values
	}

	/**
	 * 
	 */
	private void updateWidgetSize() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				dragOffsetWidth = dragArea.getOffsetWidth() - 2 * borderSize;
				dragOffsetHeight = dragArea.getOffsetHeight() - 2 * borderSize;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.Widget#onAttach()
	 */
	@Override
	protected void onAttach() {
		updateWidgetSize();
		super.onAttach();
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.LayoutPanel#onResize()
	 */
	@Override
	public void onResize() {
		super.onResize();
		updateWidgetSize();

	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.mousecontrol.client.TouchPadWidget#getX()
	 */
	@Override
	protected int getX() {
		double percent = 100.0 / dragOffsetWidth * Math.min(Math.max(0, x - borderSize), dragOffsetWidth);
		return (int) (screenWidth / 100.0 * percent);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.mousecontrol.client.TouchPadWidget#getY()
	 */
	@Override
	protected int getY() {
		double percent = 100.0 / dragOffsetHeight * Math.min(Math.max(0, y - borderSize), dragOffsetHeight);
		return (int) (screenHeight / 100.0 * percent);
	}

}
