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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;

public class TouchPadScrollWidget extends TouchPadWidget {

	private HTML spacer = new HTML();
	private ScrollPanel scroller = new ScrollPanel();

	Timer updater;
	int currentScreenX;
	int currentScreenY;

	public TouchPadScrollWidget() {
		super(false);
		scroller.setHeight("100%");
		scroller.setWidth("100%");
		add(scroller);
		scroller.add(spacer);
		updater = new Timer() {

			@Override
			public void run() {
				currentScreenX = spacer.getOffsetWidth()-scroller.getHorizontalScrollPosition()-scroller.getOffsetWidth();
				currentScreenY = spacer.getOffsetHeight()-scroller.getVerticalScrollPosition()-scroller.getOffsetHeight();
			}
		};

	}

	@Override
	protected int getX() {
		return currentScreenX;
	}

	@Override
	protected int getY() {
		return currentScreenY;
	}

	@Override
	protected void updateScreenDimensions() {
		spacer.setWidth((scroller.getOffsetWidth() + screenWidth) + "px");
		spacer.setHeight((scroller.getOffsetHeight() + screenHeight) + "px");
	}

	@Override
	public void start() {
		super.start();
		updater.scheduleRepeating(MOVEMENTUPDATEINTERVAL);
	}

	@Override
	public void stop() {
		super.stop();
		updater.cancel();
	}

}
