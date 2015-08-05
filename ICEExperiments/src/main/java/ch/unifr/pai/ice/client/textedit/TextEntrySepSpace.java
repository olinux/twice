package ch.unifr.pai.ice.client.textedit;

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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.google.gwt.user.client.ui.AbsolutePanel; 

public class TextEntrySepSpace extends HorizontalPanel implements ICEDataLogger, RequireInitialisation { 

	Vector<String> resultVector;
	int nbExpFinished = 0;
	int nbUser;
	Vector<String> userLogVector = new Vector<String>();
	String[] loggedData;
	VerticalPanel vPanel1;
	VerticalPanel vPanel2;
	int nbTEntry;
	int experimentNo;
	String[] colorSet = { "black", "red", "blue", "green" };
	
	Vector<String> user1LogVec = new Vector<String>();  
	Vector<String> user2LogVec = new Vector<String>();  
	int size = 10; 
	
	String key;
	
	AbsolutePanel user1Panel = new AbsolutePanel(); 
	AbsolutePanel user2Panel = new AbsolutePanel(); 
	AbsolutePanel user3Panel = new AbsolutePanel(); 
	//AbsolutePanel user4Panel = new AbsolutePanel(); 
	
	long finishTime ; 

	/****************
	 * Constructor
	 ****************/
	public TextEntrySepSpace(int nbUser, int nbSentences, int experimentNo) {
		
		super();
		this.nbUser = nbUser;
		this.experimentNo = experimentNo;

		this.setSize("100%", "100%"); 
		this.setBorderWidth(1); 
		
		/*if (nbUser <= 3) {
			for (int i = 0; i < nbUser; i++) {
				this.add(new TextEntry1Space(1, nbSentences, 0, true, this, colorSet[i]));
				this.getWidget(i).setSize("100%", "100%");
				}
		} */

		
		if (nbUser == 1) {	
			//user1Panel = new AbsolutePanel();
			//user2Panel = new AbsolutePanel();
			//user3Panel = new AbsolutePanel();
			user1Panel.setSize("100%", "100%");
			
			user3Panel.setSize("100%", "100%");
			
			//RemoteTextArea ta =new RemoteTextArea();
			
			vPanel1 = new VerticalPanel();
			vPanel1.setSize("100%", "100%");
			vPanel2 = new VerticalPanel();
			vPanel2.setSize("100%", "100%");
			
			
			this.add(vPanel1);
			
			vPanel1.add(new TextEntry1Space(1,1, nbSentences, 0, true, this, colorSet[0])); //for user1
			vPanel1.getWidget(0).setSize("100%", "100%");
			vPanel1.add(user1Panel);
			vPanel1.setCellHeight(user1Panel, "50%");
			vPanel1.setCellWidth(user1Panel, "50%");
			vPanel1.setBorderWidth(1);
			
			this.add(vPanel2);
			user2Panel.setSize("100%", "100%");
			vPanel2.add(user2Panel); 
			user2Panel.setWidth("314px");
			user2Panel.setHeight("85px");
			
			vPanel2.setCellHeight(user2Panel, "50%");
			vPanel2.setCellWidth(user2Panel, "50%");
			//vPanel2.add(new TextEntry1Space(0,-18,1));
			//vPanel2.getWidget(0).setSize("100%", "100%");
			vPanel2.add(user3Panel);
			//vPanel2.getWidget(1).setSize("100%", "100%");
			vPanel2.setBorderWidth(1);
	}
		
		if (nbUser == 2) {
			
			//user1Panel = new AbsolutePanel();
			//user2Panel = new AbsolutePanel();
			user1Panel.setSize("100%", "100%");
			user2Panel.setSize("100%", "100%");
			
			vPanel1 = new VerticalPanel();
			vPanel1.setSize("100%", "100%");
			vPanel1.setBorderWidth(1);
			this.add(vPanel1);
			
			vPanel1.add(new TextEntry1Space(1,1, nbSentences, 0, true, this, colorSet[0])); //for user1
			vPanel1.getWidget(0).setSize("100%", "100%");
			vPanel1.add(user1Panel);
			vPanel1.setCellWidth(user1Panel, "50%");
			vPanel1.setCellHeight(user1Panel, "50%");
			
			vPanel2 = new VerticalPanel();
			vPanel2.setSize("100%", "100%");
			vPanel2.setBorderWidth(1);
			this.add(vPanel2);
			
			vPanel2.add(user2Panel);
			vPanel2.setCellWidth(user2Panel, "50%");
			vPanel2.setCellHeight(user2Panel, "50%");
			vPanel2.add(new TextEntry1Space(1,2, nbSentences, 0, true, this, colorSet[1])); //for user2
			vPanel2.getWidget(1).setSize("100%", "100%");
	}
		
		if(nbUser == 4) {
			
			/*  else{  vPanel1 = new VerticalPanel();
			vPanel1.setSize("100%", "100%");
			vPanel1.setBorderWidth(1);
			this.add(vPanel1);

			for (int i = 0; i < Math.round(nbUser / 2); i++) {
				vPanel1.add(new TextEntry1Space(1, i + 1, nbSentences, 0, true, this, colorSet[i]));
				vPanel1.getWidget(i).setSize("100%", "100%");}

			vPanel2 = new VerticalPanel();
			vPanel2.setSize("100%", "100%");
			vPanel2.setBorderWidth(1);
			this.add(vPanel2);

			nbTEntry = nbUser - Math.round(nbUser / 2);
			int widgetNo = 0;
			
			for (int i = nbTEntry; i < nbUser; i++) {
				vPanel2.add(new TextEntry1Space(1, i + 1 , nbSentences, 0, true, this, colorSet[i]));
				vPanel2.getWidget(widgetNo++).setSize("100%", "100%");} */
			
			vPanel1 = new VerticalPanel();
			vPanel1.setSize("100%", "100%");
			vPanel1.setBorderWidth(1);
			this.add(vPanel1);
			
			vPanel1.add(new TextEntry1Space(1,1, nbSentences, 0, true, this, colorSet[0])); //for user1
			vPanel1.getWidget(0).setSize("100%", "100%");
			vPanel1.add(new TextEntry1Space(1,4, nbSentences, 0, true, this, colorSet[3])); //for user3
			vPanel1.getWidget(1).setSize("100%", "100%");
			
			vPanel2 = new VerticalPanel();
			vPanel2.setSize("100%", "100%");
			vPanel2.setBorderWidth(1);
			this.add(vPanel2);
			
			vPanel2.add(new TextEntry1Space(1,3, nbSentences, 0, true, this, colorSet[2])); //for user4
			vPanel2.getWidget(0).setSize("100%", "100%");
			vPanel2.add(new TextEntry1Space(1,2, nbSentences, 0, true, this, colorSet[1])); //for user2
			vPanel2.getWidget(1).setSize("100%", "100%");

			 }
		}

	// ------------------------------------------------------------------------

	private void log() {
//		if (experimentNo != -1) {  //COMMENTED FOR NOW
		EventingServiceAsync svc = GWT.create(EventingService.class);
		svc.log(ICEMain.identifier, loggedData, ExperimentIdentifier.TEXTEDITSPACES, nbUser, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				TextEntrySepSpace.this.clear(); 
				Window.alert("Successfully logged! Experiment finished");
			}

			@Override
			public void onFailure(Throwable caught) {
				TextEntrySepSpace.this.clear(); 
				GWT.log("Error:", caught);
				Window.alert("Not logged");
			}
		});

	}

	private String[] getLoggedResult(Vector<String> resultVector) {
		String[] result = new String[resultVector.size()];

		for (String s : resultVector) {
			result[resultVector.indexOf(s)] = s;
		}

		return result;
	}

	@Override
	public void setLoggedData(Vector<String> textData , boolean finished , boolean check) {
		
		if (nbExpFinished < nbUser) {
			
			if(finished) { 
			
			userLogVector.addAll(textData);
			nbExpFinished++;
			}
			
			else
			{
				userLogVector.add(textData.lastElement()); 
			}
			
		}

		if (nbExpFinished == nbUser) {
			
			finishTime = System.currentTimeMillis();
			userLogVector.add("Experiment Finish Time: " + finishTime 
							  + '\n' + "---------------------------------------------------------------");
			loggedData = new String[userLogVector.size()];
	
			
			userLogVector.copyInto(loggedData);
			
			System.out.println("-------------------------------------------------------------------");
			System.out.println("***TASK FINISHED***");
		
			for(int i = 0 ; i< userLogVector.size() ; i++ ){
				System.out.println(i+ ". "+loggedData[i]);
			}
			System.out.println("-------------------------------------------------------------------");
			log();
		}
	}

	/**
	 * Callback method used by the Blob class to send the vector of data to be logged.
	 * 
	 * @param blobData
	 */
	@Override
	public void setLoggedData(Vector<CursorXY> blobData , boolean finished) {

	}

	@Override
	public void setLoggedData(String[] blobData) {

	}

	@Override
	public void initialise() {
		// TODO Auto-generated method stub

	}

}

