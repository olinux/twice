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

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * The mouse control with Nintendo 3DS is quite different: The device has two screens where one of them is interactive while the other is not. We therefore need
 * a special layout and listen to scroll events instead of touch / cursor events.
 * 
 * @author Oliver Schmid
 * 
 */
public class TouchPadNintendoDS extends TouchPadWidget {

	private final HTML focus = new HTML();
	Label l = new Label();
	long lastScroll = -1;
	long scrollThreshold = 1000;
	private final static int eventsAfterReset = 3;

	int currentX = -1;
	int currentY = -1;
	int left;
	int top;
	int skipEvents = 0;

	Timer updater;
	long lastUpdate = -1;
	long timerThreshold = 200;
	boolean reset;
	boolean dontProcess;

	int currentScreenX;
	int currentScreenY;

	HandlerRegistration windowScrollHandler;

	public TouchPadNintendoDS() {
		super(false);
		add(focus);
		setWidth("2000px");
		setHeight("2000px");
		add(l);
		l.setWidth("200px");
		l.setHeight("200px");
		l.getElement().getStyle().setPosition(Position.ABSOLUTE);
		l.getElement().getStyle().setZIndex(200);
		updater = new Timer() {

			@Override
			public void run() {
				if (skipEvents == 0) {
					left = RootPanel.getBodyElement().getScrollLeft();
					top = RootPanel.getBodyElement().getScrollTop();
					if (currentX == -1 || currentY == -1) {
						currentX = left;
						currentY = top;
					}
					else if (left != currentX || top != currentY) {
						// l.getElement().getStyle().setDisplay(Display.NONE);
						// focus.setVisible(false);
						int dX = currentX - left;
						int dY = currentY - top;
						currentX = left;
						currentY = top;
						updatePos(dX, dY);
						lastUpdate = new Date().getTime();

					}
					// else if (lastUpdate != -1
					// && new Date().getTime() - lastUpdate > timerThreshold) {
					// lastUpdate = -1;
					// currentX = -1;
					// currentY = -1;
					// skipEvents = eventsAfterReset;
					// Window.scrollTo(1000, 1000);
					// l.getElement().getStyle().setDisplay(Display.BLOCK);
					// focus.setVisible(true);
					// }
				}
			}
		};

	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.mousecontrol.client.TouchPadWidget#getX()
	 */
	@Override
	protected int getX() {
		return currentScreenX;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.mousecontrol.client.TouchPadWidget#getY()
	 */
	@Override
	protected int getY() {
		return currentScreenY;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.mousecontrol.client.TouchPadWidget#updateScreenDimensions()
	 */
	@Override
	protected void updateScreenDimensions() {
	}

	private void updatePos(int dX, int dY) {
		if (dX != 0) {
			int changeX = (int) Math.floor((dX * MOVEFACTOR));
			currentScreenX = Math.max(Math.min(currentScreenX + changeX, screenWidth), 0);
		}
		if (dY != 0) {
			int changeY = (int) Math.floor((dY * MOVEFACTOR));
			currentScreenY = Math.max(Math.min(currentScreenY + changeY, screenHeight), 0);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.mousecontrol.client.TouchPadWidget#start()
	 */
	@Override
	public void start() {
		super.start();
		if (windowScrollHandler != null)
			windowScrollHandler.removeHandler();
		windowScrollHandler = Window.addWindowScrollHandler(new Window.ScrollHandler() {

			@Override
			public void onWindowScroll(ScrollEvent event) {
				if (skipEvents > 0)
					skipEvents--;
				l.getElement().getStyle().setLeft(RootPanel.getBodyElement().getScrollLeft(), Unit.PX);
				l.getElement().getStyle().setTop(RootPanel.getBodyElement().getScrollTop(), Unit.PX);
			}
		});
		Timer t2 = new Timer() {

			@Override
			public void run() {
				skipEvents = eventsAfterReset;
				Window.scrollTo(1000, 1000);
				updater.scheduleRepeating(50);
			}
		};
		t2.schedule(1000);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.mousecontrol.client.TouchPadWidget#stop()
	 */
	@Override
	public void stop() {
		super.stop();
		updater.cancel();
		if (windowScrollHandler != null)
			windowScrollHandler.removeHandler();
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.mousecontrol.client.TouchPadWidget#attachToRootPanel()
	 */
	@Override
	public boolean attachToRootPanel() {
		return true;
	}

}
