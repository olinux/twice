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
import ch.unifr.pai.ice.shared.ExperimentIdentifier;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class ClickBlobs2users extends HorizontalPanel implements ICEDataLogger, RequireInitialisation {

	int nbIteration = 20;
	int nbUser = 0;
	int iteration = 0;
	int nbExpFinished = 0; 
	boolean init = false;
	
	String blob1 = GWT.getModuleBaseURL() + "circle_black.png";
	String blob2 = GWT.getModuleBaseURL() + "circle_red.png";
	String blob3 = GWT.getModuleBaseURL() + "circle_blue.png";
	String blob4 = GWT.getModuleBaseURL() + "circle_green.png";
	String blob5 = GWT.getModuleBaseURL() + "circle_magenta.png";
	String blob6 = GWT.getModuleBaseURL() + "circle_violet.png";
	String blob7 = GWT.getModuleBaseURL() + "circle_grey.png";
	String blob8 = GWT.getModuleBaseURL() + "circle.png";

	AbsolutePanel absPanel;
	
	Vector<CursorXY> userLogVector = new Vector<CursorXY>();
	String[][] loggedData;
	
	Blob user1;
	Blob user2;

	int panelWidth;
	int panelHeight;
	int nbBlobs;
	boolean randomPos;

	// statically set the position of the blobs
	int[][] blobCoord; 
	
	AbsolutePanel user1Panel = new AbsolutePanel();
	AbsolutePanel user2Panel = new AbsolutePanel();
	
	/***************************************************************************
	 * Constructor
	 * @param nbBoxes : define the number of blobs
	 * @param randomPosition : define if users must be placed randomly or not
	 ***************************************************************************/
	public ClickBlobs2users(int nbBlobs, boolean randomPosition) {
		super();
		this.setSize("100%", "100%");
		this.nbBlobs = nbBlobs;
		user1Panel.setSize("100%", "100%");
		user2Panel.setSize("100%", "100%");
		
		this.add(user1Panel);
		this.add(user2Panel);
		// forces to maintain the size of the cell
		this.setCellHeight(user1Panel, "50%");
		this.setBorderWidth(1);
	}

	
	@Override
	public void initialise() {

		if (!init){
		
			panelWidth = user1Panel.getElement().getOffsetWidth();
			panelHeight = user1Panel.getElement().getOffsetHeight();

//		// statically set the position of the 10 blobs
//			blobCoord = new int[][] { 
//					{ panelWidth - 100, panelHeight - 100 },
//					{ (panelWidth / 2)-40, 70 }, 
//					{ 50, panelHeight - 100 }, 
//					{ 100, 70 },
//					{ panelWidth - 130, 90 }, 
//					{ panelWidth - 110, panelHeight - 180 },
//					{ 140, panelHeight / 2 }, 
//					{ panelWidth / 2, panelHeight - 100 },
//					{ (panelWidth / 2) - 50, (panelHeight / 2) - 30 }, 
//					{ 200, 110 },			
//					{ (panelWidth / 2) + 80, panelHeight - 100 },
//					{ (panelWidth / 4) * 3, panelHeight - 100 }, 
//					{ (panelWidth / 4), panelHeight - 120 }, 
//					{ (panelWidth / 4) * 3, panelHeight / 2 }, 
//					{ (panelWidth / 4),  panelHeight - 130 }, 
//					{ panelWidth - 110, panelHeight - 180 },
//					{ 140, panelHeight / 3 }, 
//					{ panelWidth / 2, panelHeight /3 },
//					{ panelWidth  - 70, (panelHeight / 3) }, 
//					{ panelWidth  - 80, (panelHeight / 3) * 2 },
//			};
			
			// set the blob position array around a circle
			blobCoord = initBlobPosInCircle(nbBlobs, panelWidth / 2, panelHeight / 2, (panelHeight/ 2)-50);
			setUsers();
			init = true;
		}
	}
	
	private void setUsersRandom() {

		user1 = new Blob(blob1, "User 1", iteration, user1Panel, this, true);
		user1Panel.add(user1);
		user1Panel.setWidgetPosition(user1, randomNum(0, panelWidth - 70),
				randomNum(0, panelHeight - 70));

		user2 = new Blob(blob2, "User 2", iteration, user2Panel, this, true);
		user2Panel.add(user2);
		user2Panel.setWidgetPosition(user2, randomNum(0, panelWidth - 70),
				randomNum(0, panelHeight - 70));
		
		nbUser = 2;
	}

	private void setUsers() {
	
		user1 = new Blob(blob1, "User 1", iteration, user1Panel, this, blobCoord, 0,0, 0, true);
		user1Panel.add(user1);
		user1Panel.setWidgetPosition(user1, panelWidth / 2, panelHeight / 2);

		
		user2 = new Blob(blob2, "User 2", iteration, user2Panel, this, blobCoord, 0,0, 1, true);
		user2Panel.add(user2);
		user2Panel.setWidgetPosition(user2, panelWidth / 2, panelHeight / 2);
		
		nbUser = 2;
	}

	
	/**
	 * Callback method used by the Blob class to send the vector of data to be logged.
	 * @param blobData
	 */
	public void setLoggedData(Vector<CursorXY> blobData){
		
		if (nbExpFinished <= nbUser){
			userLogVector.addAll(blobData);
			nbExpFinished++;
		}
		
		if (nbExpFinished == nbUser){
			
			loggedData = new String[userLogVector.size()][4];
			
			for (int i = 0; i < loggedData.length; i++){
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
		svc.log(ICEMain.identifier, getLoggedResult(loggedData), ExperimentIdentifier.CLICKBLOB, 2, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				Window.alert("Successfully logged! Experiment finished");
			}

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error:", caught);
				Window.alert("Not logged");
			}
		});
	}

	
	/**
	 * Format the logged data: comma separated value!
	 * @param data
	 * @return Array of string containing the user, x, y coord, time stamps of the click
	 */
	
	private String[] getLoggedResult(String[][] data) {
		String[] result = new String[data.length];

		for (int i=1; i < result.length; i++){
			result[i] = data[i][0] + ";" + data[i][1] + ";" + data[i][2] + ";" + data[i][3]  + " \n";
		}
		return result;
	}
	
	
	/**
	 * Generate a random number with an upper boundary
	 * 
	 * @param upperBond
	 * @return int
	 */
	private int randomNum(int lowerBond, int upperBond) {

		int num = Random.nextInt(upperBond);

		while (num <= lowerBond) {
			num = Random.nextInt(upperBond);
		}

		return num;
	}

	@Override
	public void setLoggedData(String[] blobData) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setLoggedData(Collection<? extends String> blobData) {
		// TODO Auto-generated method stub
		
	}
	
	private int[][] initBlobPosInCircle(int nbBlob, int xCenter, int yCenter, int radius) {
		
		
		int nbPos = (nbBlob == 0) ? 2 : nbBlob;	
		//check if nbPos is even
		nbPos = (nbPos % 2 == 0) ? nbPos : nbPos + 1;
		
		int [][] blobPos = new int[nbPos][2]; 			
		int dAngle = 360 / nbPos;
		double angle = 0;
		double x = 0; 
		double y = 0;
		
		// set the 2 first pos
		blobPos[0][0] = xCenter;
		blobPos[0][1] = yCenter - radius;

		blobPos[1][0] = xCenter;
		blobPos[1][1] = yCenter + radius;
		
		System.out.println("Y pos 0: " + blobPos[0][1] + "; Y pos 1: " + blobPos[1][1] + " radius: "+ radius + "dAngle: " + dAngle);
		
		for (int i = 2; i < nbPos ; i = i + 2){
			angle = angle + dAngle;
						
			if (angle < 90) {
				
				x = radius * Math.sin(Math.toRadians(angle));
				y = radius * Math.cos(Math.toRadians(angle));
				
				blobPos[i][0] = (int) (xCenter + x);
				blobPos[i][1] = (int) (yCenter - y);
				blobPos[i+1][0] = (int) (xCenter - x);
				blobPos[i+1][1] = (int) (yCenter + y);
			
			} else if (angle == 90) {
				blobPos[i][0] = (int) (xCenter + radius);
				blobPos[i][1] = (int) (yCenter);
				blobPos[i+1][0] = (int) (xCenter - radius);
				blobPos[i+1][1] = (int) (yCenter);
			
			} else if (angle > 90) {
				
				x = radius * Math.cos(Math.toRadians(angle-90));
				y = radius * Math.sin(Math.toRadians(angle-90));
				
				blobPos[i][0] = (int) (xCenter + x);
				blobPos[i][1] = (int) (yCenter + y);
				blobPos[i+1][0] = (int) (xCenter - x);
				blobPos[i+1][1] = (int) (yCenter - y);				
			}
			System.out.println("Y pos " + i + ": " + blobPos[i][1] + "; Y pos "+ (i+1) + ": " + blobPos[i+1][1]  + "; Angle: " + angle);
		}
		
		return blobPos;
	}

}
