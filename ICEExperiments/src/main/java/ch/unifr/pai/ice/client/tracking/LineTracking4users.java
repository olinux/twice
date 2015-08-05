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
import com.google.gwt.user.client.ui.VerticalPanel;

public class LineTracking4users extends VerticalPanel implements RequireInitialisation {

	int expFinished = 0;
	int nbUser = 4;
	int timerDelay = 100;
	Image lineImage1 = new Image(GWT.getModuleBaseURL() + "black_line_3.jpg");
	Image lineImage2 = new Image(GWT.getModuleBaseURL() + "red_line_3.jpg");
	Image lineImage3 = new Image(GWT.getModuleBaseURL() + "blue_line_3.jpg");
	Image lineImage4 = new Image(GWT.getModuleBaseURL() + "green_line_3.jpg");

	Vector<CursorXY> cursorPosVector1;
	Vector<CursorXY> cursorPosVector2;
	Vector<CursorXY> cursorPosVector3;
	Vector<CursorXY> cursorPosVector4;

	ToggleButton startBt1 = new ToggleButton("Start Tracking");
	ToggleButton stopBt1 = new ToggleButton("Stop Tracking");
	ToggleButton startBt2 = new ToggleButton("Start Tracking");
	ToggleButton stopBt2 = new ToggleButton("Stop Tracking");
	ToggleButton startBt3 = new ToggleButton("Start Tracking");
	ToggleButton stopBt3 = new ToggleButton("Stop Tracking");
	ToggleButton startBt4 = new ToggleButton("Start Tracking");
	ToggleButton stopBt4 = new ToggleButton("Stop Tracking");

	AbsolutePanel absPanel1 = new AbsolutePanel();
	AbsolutePanel absPanel2 = new AbsolutePanel();
	AbsolutePanel absPanel3 = new AbsolutePanel();
	AbsolutePanel absPanel4 = new AbsolutePanel();

	HorizontalPanel hPanel1 = new HorizontalPanel();
	HorizontalPanel hPanel2 = new HorizontalPanel();

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
				cursorPosVector1.add(new CursorXY("user1", x1, y1, System.currentTimeMillis() , 0)); 
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
				cursorPosVector2.add(new CursorXY("user2", x2, y2, System.currentTimeMillis() , 0)); 
				prevX2 = x2;
				prevY2 = y2;
			}
		}
	};

	/*************************************************************************************
	 * Timer 3
	 **************************************************************************************/
	int x3 = 0;
	int y3 = 0;

	// Timers allowing to set a delay between 2 position savings
	Timer timer3 = new Timer() {
		int prevX3;
		int prevY3;

		@Override
		public void run() {
			if (startBt3.isDown() && !stopBt3.isDown() && (x3 != prevX3 || y3 != prevY3)) {
				cursorPosVector3.add(new CursorXY("user3", x3, y3, System.currentTimeMillis() , 0)); 
				prevX3 = x3;
				prevY3 = y3;
			}
		}
	};

	/*************************************************************************************
	 * Timer 4
	 **************************************************************************************/
	int x4 = 0;
	int y4 = 0;

	Timer timer4 = new Timer() {
		int prevX4;
		int prevY4;

		@Override
		public void run() {
			if (startBt4.isDown() && !stopBt4.isDown() && (x4 != prevX4 || y4 != prevY4)) {
				cursorPosVector4.add(new CursorXY("user4", x4, y4, System.currentTimeMillis() , 0));
				prevX4 = x4;
				prevY4 = y4;
			}
		}
	};

	// ***********************************************************************************

	public LineTracking4users() {
		super();
		this.setSize("100%", "100%");
		hPanel1.add(absPanel1);
		hPanel1.add(absPanel2);
		hPanel2.add(absPanel3);
		hPanel2.add(absPanel4);

		hPanel1.setSize("100%", "100%");
		hPanel2.setSize("100%", "100%");
		this.add(hPanel1);
		this.add(hPanel2);

		absPanel1.setSize("100%", "100%");
		absPanel2.setSize("100%", "100%");
		absPanel3.setSize("100%", "100%");
		absPanel4.setSize("100%", "100%");

		// this.setBorderWidth(1);
		initAbsPanel1();
		initAbsPanel2();
		initAbsPanel3();
		initAbsPanel4();
	}

	@Override
	public void initialise() {

		absPanel1.setWidgetPosition(startBt1, 0, lineImage1.getOffsetHeight());
		absPanel1.setWidgetPosition(stopBt1, lineImage1.getOffsetWidth(), 0);
		absPanel2.setWidgetPosition(startBt2, 0, lineImage2.getOffsetHeight());
		absPanel2.setWidgetPosition(stopBt2, lineImage2.getOffsetWidth(), 0);
		absPanel3.setWidgetPosition(startBt3, 0, lineImage3.getOffsetHeight());
		absPanel3.setWidgetPosition(stopBt3, lineImage3.getOffsetWidth(), 0);
		absPanel4.setWidgetPosition(startBt4, 0, lineImage4.getOffsetHeight());
		absPanel4.setWidgetPosition(stopBt4, lineImage4.getOffsetWidth(), 0);

	}

	/********************
	 * Init panel 1
	 ********************/
	private void initAbsPanel1() {
		absPanel1.add(lineImage1);
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

	/********************
	 * Init panel 3
	 ********************/
	private void initAbsPanel3() {
		absPanel3.add(lineImage3);
		absPanel3.add(startBt3);
		absPanel3.add(stopBt3);

		cursorPosVector3 = new Vector<CursorXY>();

		lineImage3.addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent event) {

				if (startBt3.isDown() && !stopBt3.isDown()) {
					x3 = event.getRelativeX(lineImage3.getElement());
					y3 = event.getRelativeY(lineImage3.getElement());
				}
			}
		});

		startBt3.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				timer3.scheduleRepeating(timerDelay);
				cursorPosVector3.clear();
			}
		});

		stopBt3.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				log(cursorPosVector3);
				expFinished++;
				startBt3.setDown(false);
			}
		});

	}

	/********************
	 * Init panel 4
	 ********************/

	private void initAbsPanel4() {
		absPanel4.add(lineImage4);
		absPanel4.add(startBt4);
		absPanel4.add(stopBt4);

		cursorPosVector4 = new Vector<CursorXY>();

		lineImage4.addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent event) {

				if (startBt4.isDown() && !stopBt4.isDown()) {
					x4 = event.getRelativeX(lineImage4.getElement());
					y4 = event.getRelativeY(lineImage4.getElement());
				}
			}
		});

		startBt4.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				timer4.scheduleRepeating(timerDelay);
				cursorPosVector4.clear();
			}
		});

		stopBt4.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				log(cursorPosVector4);
				expFinished++;
				startBt4.setDown(false);
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
		svc.log(ICEMain.identifier, getLoggedResult(cursorPosVector), ExperimentIdentifier.TRACKING, 4, new AsyncCallback<Void>() {

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
