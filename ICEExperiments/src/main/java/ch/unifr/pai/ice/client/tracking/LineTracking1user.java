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

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class LineTracking1user extends AbsolutePanel implements RequireInitialisation {

	int timerDelay = 100;

	private final int lineOffset = 50;

	Image lineImage;
	Label posLabel = new Label("0,0");
	Vector<CursorXY> cursorPosVector;
	Button startBt = new Button("Start Tracking");
	Button stopBt = new Button("Stop Tracking");
	Canvas canvas = Canvas.createIfSupported();
	boolean tracking;

	/*************************************************************************************
	 * Timer
	 **************************************************************************************/
	int x = -1;
	int y = -1;

	// Timer allowing to set a delay between 2 position savings
	Timer timer = new Timer() {
		int prevX;
		int prevY;

		@Override
		public void run() {
			if (tracking && (x != prevX || y != prevY)) {
				cursorPosVector.add(new CursorXY("user1", x, y, System.currentTimeMillis()));
				prevX = x;
				prevY = y;
			}
		}
	};

	// ***********************************************************************************
	private final boolean doLog;
	private final Context2d context;
	private HandlerRegistration eventPreview;

	public LineTracking1user(boolean doLog, String image) {
		super();

		this.doLog = doLog;
		this.lineImage = new Image(GWT.getModuleBaseURL() + image);
		context = canvas.getContext2d();

		canvas.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				event.preventDefault();
			}
		});
		this.add(lineImage);
		this.add(canvas);
		this.setWidgetPosition(canvas, 0, 0);
		this.setWidgetPosition(lineImage, lineOffset, lineOffset);
		// this.add(posLabel);
		this.add(startBt);
		this.add(stopBt);
		cursorPosVector = new Vector<CursorXY>();

		canvas.addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent event) {

				if (tracking) {
					if (x != -1 && y != -1) {
						context.beginPath();
						context.moveTo(x, y);
						context.lineTo(event.getRelativeX(canvas.getCanvasElement()), event.getRelativeY(canvas.getCanvasElement()));
						context.stroke();
					}
					x = event.getRelativeX(canvas.getCanvasElement());
					y = event.getRelativeY(canvas.getCanvasElement());
					posLabel.setText(x + "," + y);

				}

			}
		});
		canvas.addTouchMoveHandler(new TouchMoveHandler() {

			@Override
			public void onTouchMove(TouchMoveEvent event) {

				if (tracking && event.getTouches().length() > 0) {
					event.preventDefault();
					if (x != -1 && y != -1) {
						context.beginPath();
						context.moveTo(x, y);
						context.lineTo(event.getTouches().get(0).getRelativeX(canvas.getCanvasElement()),
								event.getTouches().get(0).getRelativeY(canvas.getCanvasElement()));
						context.stroke();
					}
					x = event.getTouches().get(0).getRelativeX(canvas.getCanvasElement());
					y = event.getTouches().get(0).getRelativeY(canvas.getCanvasElement());
					posLabel.setText(x + "," + y);

				}

			}
		});

		startBt.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				tracking = true;
				timer.scheduleRepeating(timerDelay);
				cursorPosVector.clear();
				startBt.removeFromParent();
			}
		});

		stopBt.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				log();
				tracking = false;
				stopBt.removeFromParent();
				timer.cancel();
			}
		});

	}

	@Override
	public void initialise() {

		this.setWidgetPosition(startBt, 0, lineImage.getOffsetHeight() + lineOffset);
		this.setWidgetPosition(stopBt, lineImage.getOffsetWidth(), lineImage.getOffsetHeight() + lineOffset);
		canvas.setCoordinateSpaceWidth(getOffsetWidth());
		canvas.setCoordinateSpaceHeight(getOffsetHeight());
		context.setStrokeStyle("black");
		context.setLineWidth(4);
	}

	public void setLabelText(String text) {
		posLabel.setText(text);
		System.out.println(text);
	}

	/**
	 * log the data and indicate if logged is OK or not
	 */
	private void log() {
		if (doLog) {
			EventingServiceAsync svc = GWT.create(EventingService.class);
			svc.log(ICEMain.identifier, getLoggedResult(cursorPosVector), ExperimentIdentifier.TRACKING, 1, new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					LineTracking1user.this.clear();
					Window.alert("Task finished!");
				}

				@Override
				public void onFailure(Throwable caught) {

					LineTracking1user.this.clear();
					GWT.log("Error:", caught);
					Window.alert("Task finished but was not able to log!");
				}
			});
		}
		else {
			clear();
			Window.alert("Task finished");
		}
	}

	/**
	 * Format the logged data: comma separated value!
	 * 
	 * @param data
	 * @return Array of string containing the user, x, y time stamps of the click
	 */

	private String[] getLoggedResult(Vector<CursorXY> data) {
		String[] result = new String[data.size()];

		for (int i = 0; i < result.length; i++) {
			result[i] = lineImage.getUrl() + "; " + data.get(i).getUser() + "; " + data.get(i).getX() + "; " + data.get(i).getY() + "; "
					+ data.get(i).getTimeStamp();
		}
		return result;
	}

}
