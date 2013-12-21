package ch.unifr.pai.ice.client.clickblobs;

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

import java.util.Collection;
import java.util.Vector;

import ch.unifr.pai.ice.client.ICEMain;
import ch.unifr.pai.ice.client.RequireInitialisation;
import ch.unifr.pai.ice.client.rpc.EventingService;
import ch.unifr.pai.ice.client.rpc.EventingServiceAsync;
import ch.unifr.pai.ice.client.tracking.CursorXY;
import ch.unifr.pai.ice.client.utils.ICEDataLogger;
import ch.unifr.pai.ice.client.utils.PositioningUtils;
import ch.unifr.pai.ice.shared.ExperimentIdentifier;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;

public class ClickBlobs1user extends AbsolutePanel implements ICEDataLogger, RequireInitialisation {

	int nbIteration = 10;
	int nbUser = 0;
	int iteration = 0;
	int nbExpFinished = 0;

	String blob1 = GWT.getModuleBaseURL() + "circle_black.png";
	String blob2 = GWT.getModuleBaseURL() + "circle_red.png";
	String blob3 = GWT.getModuleBaseURL() + "circle_blue.png";
	String blob4 = GWT.getModuleBaseURL() + "circle_green.png";
	String blob5 = GWT.getModuleBaseURL() + "circle_magenta.png";
	String blob6 = GWT.getModuleBaseURL() + "circle_violet.png";
	String blob7 = GWT.getModuleBaseURL() + "circle_grey.png";
	String blob8 = GWT.getModuleBaseURL() + "circle.png";

	AbsolutePanel absPanel;

	boolean init = false;

	Vector<CursorXY> userLogVector = new Vector<CursorXY>();
	String[][] loggedData;

	Button resetBT = new Button("Reset");
	// Button addUser = new Button("Add user");

	Blob user1;
	Blob user2;
	Blob user3;
	Blob user4;
	Blob user5;
	Blob user6;
	Blob user7;

	int panelWidth;
	int panelHeight;
	int nbBlobs = 0;
	boolean randomPos;

	// statically set the position of the 10 blobs
	int[][] blobCoord;
	boolean doLog;

	/***************************************************************************
	 * Constructor
	 * 
	 * @param nbBoxes
	 *            : define the number of blobs
	 * @param randomPosition
	 *            : define if users must be placed randomly or not
	 ***************************************************************************/

	public ClickBlobs1user(int nbBlobs, boolean randomPosition, boolean doLog) {
		super();
		this.doLog = doLog;
		this.randomPos = randomPosition;
		absPanel = this;
		this.nbBlobs = nbBlobs;
		// this.add(resetBT);
		// this.add(addUser);
		nbUser++;

		// ----- Reset Button --------------------------------------------
		resetBT.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				user1.resetCounter();
				nbExpFinished = 0;
				userLogVector.clear();
			}
		});

	}

	@Override
	public void initialise() {

		if (!init) {

			panelWidth = absPanel.getElement().getOffsetWidth();
			panelHeight = absPanel.getElement().getOffsetHeight();

			// // statically set the position of the 10 blobs
			// blobCoord = new int[][] {
			// { panelWidth - 100, panelHeight - 100 },
			// { (panelWidth / 2)-40, 70 },
			// { 50, panelHeight - 100 },
			// { 100, 70 },
			// { panelWidth - 130, 90 },
			// { panelWidth - 110, panelHeight - 180 },
			// { 140, panelHeight / 2 },
			// { panelWidth / 2, panelHeight - 100 },
			// { (panelWidth / 2) - 50, (panelHeight / 2) - 30 },
			// { 200, 110 },
			// { (panelWidth / 2) + 80, panelHeight - 100 },
			// { (panelWidth / 4) * 3, panelHeight - 100 },
			// { (panelWidth / 4), panelHeight - 120 },
			// { (panelWidth / 4) * 3, panelHeight / 2 },
			// { (panelWidth / 4), panelHeight - 130 },
			// { panelWidth - 110, panelHeight - 180 },
			// { 140, panelHeight / 3 },
			// { panelWidth / 2, panelHeight /3 },
			// { panelWidth - 70, (panelHeight / 3) },
			// { panelWidth - 80, (panelHeight / 3) * 2 },
			// };

			// set the blob position array around a circle
			blobCoord = PositioningUtils.getPositionsInCircle(nbBlobs, panelWidth / 2, panelHeight / 2, (panelHeight / 2) - 50);

			user1 = new Blob(blob1, "User1", nbIteration, absPanel, this, blobCoord, 0, 0, 0, doLog) {

				@Override
				protected void onFinishedInitialIteration() {
					if (!doLog) {
						ClickBlobs1user.this.clear();
						Window.alert("Task finished!");

					}
				}

			};
			absPanel.add(user1);
			absPanel.setWidgetPosition(user1, panelWidth / 2, panelHeight / 2);
			user1.resetCounter();
			init = true;
		}
	}

	/**
	 * Callback method used by the Blob class to send the vector of data to be logged.
	 * 
	 * @param blobData
	 */
	@Override
	public void setLoggedData(Vector<CursorXY> blobData) {

		if (nbExpFinished <= nbUser) {
			userLogVector.addAll(blobData);
			nbExpFinished++;
		}

		if (nbExpFinished == nbUser) {

			loggedData = new String[userLogVector.size()][4];

			for (int i = 0; i < loggedData.length; i++) {
				loggedData[i][0] = userLogVector.get(i).getUser();
				loggedData[i][1] = String.valueOf(userLogVector.get(i).getX());
				loggedData[i][2] = String.valueOf(userLogVector.get(i).getY());
				loggedData[i][3] = String.valueOf(userLogVector.get(i).getTimeStamp());
			}

			log();
		}
	}

	/**
	 * log the data and indicate if logged is OK or not
	 */
	private void log() {
		EventingServiceAsync svc = GWT.create(EventingService.class);
		svc.log(ICEMain.identifier, getLoggedResult(loggedData), ExperimentIdentifier.CLICKBLOB, 1, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				ClickBlobs1user.this.clear();
				Window.alert("Task finished!");
			}

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error:", caught);
				Window.alert("Task finished but was not able to log!");
			}
		});
	}

	/**
	 * Format the logged data: comma separated value!
	 * 
	 * @param data
	 * @return Array of string containing the user, x, y coord, time stamps of the click
	 */

	private String[] getLoggedResult(String[][] data) {
		String[] result = new String[data.length];

		for (int i = 0; i < result.length; i++) {
			result[i] = data[i][0] + ";" + data[i][1] + ";" + data[i][2] + ";" + data[i][3] + ";";
		}
		return result;
	}

	// Random number generator
	private int randomNum(int upperBond) {

		return Random.nextInt(upperBond);
	}

	@Override
	public void setLoggedData(String[] blobData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLoggedData(Collection<? extends String> blobData) {
		// TODO Auto-generated method stub

	}

}
