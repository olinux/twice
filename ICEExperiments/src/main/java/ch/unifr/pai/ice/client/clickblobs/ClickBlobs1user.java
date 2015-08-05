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
* Project ICE
* -----------
* PAI research Group - Dept. of Informatics
* University of Fribourg - Switzerland
* Author: Pascal Bruegger
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel; 


public class ClickBlobs1user extends VerticalPanel implements ICEDataLogger, RequireInitialisation { 

		int nbIteration = 10;
		int nbUser = 0;
		int iteration = 0;
		int nbExpFinished = 0;
		long finishTime ; 
		
		String blob1 = GWT.getModuleBaseURL() + "circle_black.png";
		String blob2 = GWT.getModuleBaseURL() + "circle_red.png";
		String blob3 = GWT.getModuleBaseURL() + "circle_blue.png";
		String blob4 = GWT.getModuleBaseURL() + "circle_green.png";
		String blob5 = GWT.getModuleBaseURL() + "circle_magenta.png";
		String blob6 = GWT.getModuleBaseURL() + "circle_violet.png";
		String blob7 = GWT.getModuleBaseURL() + "circle_grey.png";
		String blob8 = GWT.getModuleBaseURL() + "circle.png";

		//AbsolutePanel absPanel; 
		AbsolutePanel user1Panel = new AbsolutePanel(); 
		AbsolutePanel user2Panel = new AbsolutePanel(); 
		AbsolutePanel user3Panel = new AbsolutePanel(); 
		AbsolutePanel user4Panel = new AbsolutePanel();
		
		HorizontalPanel hPanel1 = new HorizontalPanel();
		HorizontalPanel hPanel2 = new HorizontalPanel();  
	
		boolean init = false;
		
		Vector<CursorXY> userLogVector = new Vector<CursorXY>();
		Vector<CursorXY> user1LogVec = new Vector<CursorXY>(); 
		String [] user1result; 
		
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
		* : define the number of blobs
		* @param randomPosition
		* : define if users must be placed randomly or not
		***************************************************************************/
		
		public ClickBlobs1user(int nbBlobs, boolean randomPosition, boolean doLog) {
				super();
				this.setSize("100%", "100%");
				this.doLog = doLog;
				this.randomPos = randomPosition;
				//absPanel = this; 
				this.nbBlobs = nbBlobs;
				// this.add(resetBT);
				// this.add(addUser);
				nbUser++;
				
				hPanel1.setSize("100%", "100%"); 
				hPanel2.setSize("100%", "100%"); 
				
				user1Panel.setSize("100%", "100%"); 
				user2Panel.setSize("100%", "100%"); 
				user3Panel.setSize("100%", "100%"); 
				user4Panel.setSize("100%", "100%");
				
				hPanel1.add(user1Panel); 
				hPanel1.setCellWidth(user1Panel, "50%"); 
				hPanel1.setCellHeight(user1Panel, "50%"); 
				hPanel1.add(user3Panel); 
				
				hPanel2.add(user4Panel); 
				hPanel2.setCellWidth(user4Panel, "50%"); 
				hPanel2.setCellHeight(user4Panel, "50%"); 
				hPanel2.add(user2Panel); 
				
				this.add(hPanel1);
				this.add(hPanel2);
				
				this.setBorderWidth(1);
				
				hPanel1.setBorderWidth(1);
				hPanel2.setBorderWidth(1);

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
		panelWidth = user1Panel.getElement().getOffsetWidth();  
		panelHeight = user1Panel.getElement().getOffsetHeight(); 
		
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
		
		user1 = new Blob(blob1, "User 1", nbIteration, user1Panel, this, blobCoord, 0, 0, 0, doLog) {
		
			@Override
			protected void onFinishedInitialIteration() {
				
			if (!doLog) {   // for training
			ClickBlobs1user.this.clear();
			Window.alert("Task finished!");
			}
			}
		
		};
				
		user1Panel.add(user1);	
		user1Panel.setWidgetPosition(user1, blobCoord[0][0], blobCoord[0][1] ); 
		user1.setBlobNumber(0);
		

		//PRINTING LOGS
		System.out.println("Experiment Identifier: " + ICEMain.identifier);
		System.out.println("Name of Experiment Task: " + ExperimentIdentifier.CLICKBLOB);
		System.out.println("User Number: " + nbUser );
		System.out.println(); 
	
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
		public void setLoggedData(Vector<CursorXY> blobData , boolean finished) { 
		
		if (nbExpFinished <= nbUser) {	
			
		userLogVector.addAll(blobData);
		nbExpFinished++; //when one user finished
		
		}
		
		if (nbExpFinished == nbUser) {
			
		
		finishTime = user1.setFinishTime; 	//experiment finish time is also set finish time
		loggedData = new String[userLogVector.size()][5];
		
		for (int i = 0; i < loggedData.length; i++) {
		loggedData[i][0] = userLogVector.get(i).getUser();
		loggedData[i][1] = String.valueOf(userLogVector.get(i).getX());
		loggedData[i][2] = String.valueOf(userLogVector.get(i).getY());
		loggedData[i][3] = String.valueOf(userLogVector.get(i).getTimeStamp());
		loggedData[i][4] = String.valueOf(userLogVector.get(i).getblobNumber());
		
		}

		System.out.println("-------------------------------------------------------------------");
		System.out.println("******TASK FINISHED******");
	
		for (int i = 0; i < loggedData.length; i++) {   //PRINTING LOGS 
			System.out.println( i + ".  user:" + loggedData[i][0] + ";  " + 
			"x:"+ loggedData[i][1] + ";  " +
			"y:"+loggedData[i][2]  + ";  " +
			"time:" +loggedData[i][3] + ";  " +
			"blobNo:" +loggedData[i][4]);
			}
		
		System.out.println("");
		System.out.println("-------------------------------------------------------------------");
		System.out.println("User1; " + user1.count + " times out of blob");
		System.out.println("-------------------------------------------------------------------");
		System.out.println("User1; Start Time:"+ user1.startTime + " ;  Finish Time:"+ finishTime);		
		System.out.println("User1; Experiment Completion Time: " + ((finishTime)-user1.startTime) ); 
		System.out.println("-------------------------------------------------------------------");
		
		//********************************************************
		//FORMING USER1 LOG VECTOR
		for(int i=0; i< userLogVector.size() ; i++) {
			
			if(userLogVector.get(i).getUser().equals("User 1") && userLogVector.get(i).getblobNumber() != -5){
				user1LogVec.add(userLogVector.get(i));
			}	
		}
		
		user1result = new String[user1LogVec.size() / 2];
		
		//********************************************************
		//DIAMETER BLOBS FOR USER1
		int j=0;
		for(int i=0 ; i< user1LogVec.size() ; i++){
			
			if( (user1LogVec.get(i).getblobNumber() %2 == 0) && ((i+1) < user1LogVec.size()) ){ //if blobNo is even && next element exists
				
				if(user1LogVec.get(i+1).getblobNumber() == (user1LogVec.get(i).getblobNumber() + 1) )
				{
					
					user1result[j]= user1LogVec.get(i).getUser()+"; Time passed between blob" +user1LogVec.get(i).getblobNumber() + " and blob" + user1LogVec.get(i+1).getblobNumber() + ":  " +
							(user1LogVec.get(i+1).getTimeStamp() - user1LogVec.get(i).getTimeStamp()) + '\n' ;  
					
					
					System.out.println( user1LogVec.get(i).getUser()+ ";Time passed between blob" +user1LogVec.get(i).getblobNumber() + " and blob" + user1LogVec.get(i+1).getblobNumber() + ":  " +
							(user1LogVec.get(i+1).getTimeStamp() - user1LogVec.get(i).getTimeStamp())  );
					j++;

				}
			}	
		}
		System.out.println("-------------------------------------------------------------------");

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
			Window.alert("Successfully logged! Experiment finished");
		}
		
		@Override
		public void onFailure(Throwable caught) {
			GWT.log("Error:", caught);
			ClickBlobs1user.this.clear();
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
			
			String[] result = new String[data.length + 1 ];
			String messageU1 = "";
		
		for (int i = 0; i < data.length; i++) {
			result[i] = data[i][0] + "; " + data[i][1] + "; " + data[i][2] + "; " + data[i][3] + "; " + data[i][4] + "; " ;
		}
		
		for(int j = 0; j< user1result.length ; j++){

		    messageU1 = messageU1+ user1result[j];
		}
		
		result[data.length] = '\n' + "---------------------------------------------------------------"+ '\n' +
							  "User1; Start Time:"+ user1.startTime + " ; Finish Time:"+ finishTime +
							  "; Experiment Completion Time: " + (finishTime-user1.startTime)  + '\n' +
							  "---------------------------------------------------------------"+ '\n'+
							  "User1 ; "+ user1.count + " times out of blob" + '\n'+
							  "---------------------------------------------------------------"+ '\n'+
							  messageU1 ; 
		
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
		public void setLoggedData(Vector<String> blobData , boolean finished , boolean check) {
			// TODO Auto-generated method stub
		
		}
		
		
		}