package ch.unifr.pai.ice.client.tracking;

/*
 * Copyright 2013 Pascal Bruegger
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
/***************************************************************************************************
 *  Project ICE 
 *  -----------
 *  PAI research Group - Dept. of Informatics 
 *  University of Fribourg - Switzerland
 *  Author: Pascal Bruegger 
 * 
 *************************************************************************************************/

import java.util.Vector;

import ch.unifr.pai.ice.client.ICEMain;
import ch.unifr.pai.ice.client.RequireInitialisation;
import ch.unifr.pai.ice.client.rpc.EventingService;
import ch.unifr.pai.ice.client.rpc.EventingServiceAsync;
import ch.unifr.pai.ice.shared.ExperimentIdentifier;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;

public class LineTracking2users extends HorizontalPanel implements RequireInitialisation {

	int expFinished = 0;
	int nbUser = 2;
	int timerDelay = 100;
	Image lineImage1 = new Image(GWT.getModuleBaseURL() + "black_line_2.jpg");
	Image lineImage2 = new Image(GWT.getModuleBaseURL() + "red_line_2.jpg");

	Label posLabel1 = new Label("0,0");
	Label posLabel2 = new Label("0,0");

	Vector<CursorXY> cursorPosVector1;
	Vector<CursorXY> cursorPosVector2;

	ToggleButton startBt1 = new ToggleButton("Start Tracking");
	ToggleButton stopBt1 = new ToggleButton("Stop Tracking");
	ToggleButton startBt2 = new ToggleButton("Start Tracking");
	ToggleButton stopBt2 = new ToggleButton("Stop Tracking");

	AbsolutePanel absPanel1 = new AbsolutePanel();
	AbsolutePanel absPanel2 = new AbsolutePanel();

	/*************************************************************************************
	 * Timer 1
	 **************************************************************************************/
	int x1 = 0;
	int y1 = 0;

	// Timers allowing to set a delay between 2 position savings
	Timer timer1 = new Timer() {
		int prevX1;
		int prevY1;

		@Override
		public void run() {
			if (startBt1.isDown() && !stopBt1.isDown() && (x1 != prevX1 || y1 != prevY1)) {
				cursorPosVector1.add(new CursorXY("user1", x1, y1, System.currentTimeMillis()));
				prevX1 = x1;
				prevY1 = y1;
			}
		}
	};

	/*************************************************************************************
	 * Timer 2
	 **************************************************************************************/
	int x2 = 0;
	int y2 = 0;

	Timer timer2 = new Timer() {
		int prevX2;
		int prevY2;

		@Override
		public void run() {
			if (startBt2.isDown() && !stopBt2.isDown() && (x2 != prevX2 || y2 != prevY2)) {
				cursorPosVector2.add(new CursorXY("user2", x2, y2, System.currentTimeMillis()));
				prevX2 = x2;
				prevY2 = y2;
			}
		}
	};

	// ***********************************************************************************

	public LineTracking2users() {
		super();
		this.setSize("100%", "100%");
		absPanel1.setSize("100%", "100%");
		absPanel1.setSize("100%", "100%");
		this.add(absPanel1);
		this.add(absPanel2);
		// this.setBorderWidth(1);
		initAbsPanel1();
		initAbsPanel2();
	}

	@Override
	public void initialise() {

		absPanel1.setWidgetPosition(startBt1, 0, lineImage1.getOffsetHeight());
		absPanel1.setWidgetPosition(stopBt1, lineImage1.getOffsetWidth(), 0);
		absPanel2.setWidgetPosition(startBt2, 0, lineImage2.getOffsetHeight());
		absPanel2.setWidgetPosition(stopBt2, lineImage2.getOffsetWidth(), 0);

	}

	/********************
	 * Init panel 1
	 ********************/
	private void initAbsPanel1() {
		absPanel1.add(lineImage1);
		absPanel1.add(posLabel1);
		absPanel1.add(startBt1);
		absPanel1.add(stopBt1);

		cursorPosVector1 = new Vector<CursorXY>();

		lineImage1.addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent event) {

				if (startBt1.isDown() && !stopBt1.isDown()) {
					x1 = event.getRelativeX(lineImage1.getElement());
					y1 = event.getRelativeY(lineImage1.getElement());
				}
			}
		});

		startBt1.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				timer1.scheduleRepeating(timerDelay);
				cursorPosVector1.clear();
			}
		});

		stopBt1.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				log(cursorPosVector1);
				expFinished++;
				startBt1.setDown(false);
			}
		});

	}

	/********************
	 * Init panel 2
	 ********************/

	private void initAbsPanel2() {
		absPanel2.add(lineImage2);
		absPanel2.add(posLabel2);
		absPanel2.add(startBt2);
		absPanel2.add(stopBt2);

		cursorPosVector2 = new Vector<CursorXY>();

		lineImage2.addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent event) {

				if (startBt2.isDown() && !stopBt2.isDown()) {
					x2 = event.getRelativeX(lineImage2.getElement());
					y2 = event.getRelativeY(lineImage2.getElement());
				}
			}
		});

		startBt2.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				timer2.scheduleRepeating(timerDelay);
				cursorPosVector2.clear();
			}
		});

		stopBt2.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				log(cursorPosVector2);
				expFinished++;
				startBt2.setDown(false);
			}
		});

	}

	public void setLabelText(String text, Label label) {
		label.setText(text);
	}

	/**
	 * log the data and indicate if logged is OK or not
	 */
	private void log(Vector<CursorXY> cursorPosVector) {
		EventingServiceAsync svc = GWT.create(EventingService.class);
		svc.log(ICEMain.identifier, getLoggedResult(cursorPosVector), ExperimentIdentifier.TRACKING, 2, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {

				if (expFinished == nbUser) {
					Window.alert("Successfully logged! Experiment finished");
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error:", caught);
				Window.alert("Not logged - Error: " + caught);
			}
		});
	}

	/**
	 * Format the logged data: comma separated value!
	 * 
	 * @param data
	 * @return Array of string containing the user, x, y time stamps of the click
	 */

	private String[] getLoggedResult(Vector<CursorXY> data) {
		String[] result = new String[data.size()];

		// TODO prepare for proper logging
		for (int i = 0; i < result.length; i++) {
			result[i] = data.get(i).getUser() + " " + data.get(i).getX() + " " + data.get(i).getY() + " " + data.get(i).getTimeStamp();
		}
		return result;
	}

}
