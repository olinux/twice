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
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TextEntry1Space extends AbsolutePanel implements ICEDataLogger, RequireInitialisation {

	VerticalPanel vPanel = new VerticalPanel(); 
	
	TextEditor textEd;
	Vector<String> resultVector;
	int nbExpFinished = 0;
	int nbUser; // numbers of users
	Vector<String> userLogVector = new Vector<String>();
	String[] loggedData;
	Boolean hasToLogToParent = false;
	ICEDataLogger parent;
	int experimentNo;
	int startingUserNo = 1;


	/****************
	 * Constructors
	 ****************/
	public TextEntry1Space(int nbUser, int nbSentences, int experimentNo) {

		super();
		
		this.nbUser = nbUser;
		this.experimentNo = experimentNo;

		vPanel.setHeight("100%"); 
		
		this.add(vPanel);

		for (int i = 0; i < nbUser; i++) {
			textEd = new TextEditor(this, i + 1, nbSentences);
			vPanel.add(textEd);
		}
		
	}

	/**
	 * Create a TextEntry with the parent logging capabilities (send the logged data to its parent)
	 * 
	 * @param nbUser
	 * @param nbSentences
	 * @param hasToLogToParent
	 * @param parent
	 */
	public TextEntry1Space(int nbUser, int nbSentences, int experimentNo, boolean hasToLogToParent, ICEDataLogger parent) {

		super();
		this.nbUser = nbUser;
		this.hasToLogToParent = hasToLogToParent;
		this.parent = parent;
		this.experimentNo = experimentNo;

		vPanel.setHeight("100%");

		this.add(vPanel);
		for (int i = 0; i < nbUser; i++) {
			textEd = new TextEditor(this, i + 1, nbSentences);
			vPanel.add(textEd);
		}
	}

	/**
	 * Create a TextEntry with the parent logging capabilities (send the logged data to its parent)
	 * 
	 * @param nbUser
	 * @param nbSentences
	 * @param hasToLogToParent
	 * @param parent
	 */
	public TextEntry1Space(int nbUser, int nbSentences, int experimentNo, boolean hasToLogToParent, ICEDataLogger parent, String color) {

		super();
		this.nbUser = nbUser;
		this.hasToLogToParent = hasToLogToParent;
		this.parent = parent;
		this.experimentNo = experimentNo; 

		vPanel.setHeight("100%");

		this.add(vPanel);
		for (int i = 0; i < nbUser; i++) {
			textEd = new TextEditor(this, i + 1, nbSentences);
			textEd.setColor(color);
			vPanel.add(textEd);
		}
	}

	/**
	 * Create a TextEntry with the parent logging capabilities (send the logged data to its parent)
	 * 
	 * @param nbUser
	 *            : number of users
	 * @param startingUserNo
	 *            : user no. to log
	 * @param nbSentences
	 * @param hasToLogToParent
	 * @param parent
	 */
	public TextEntry1Space(int nbOfUser, int startingUserNo, int nbSentences, int experimentNo, boolean hasToLogToParent, ICEDataLogger parent) {

		super();
		this.nbUser = nbOfUser;
		this.hasToLogToParent = hasToLogToParent;
		this.parent = parent;
		this.experimentNo = experimentNo;
		this.startingUserNo = startingUserNo;
	
		vPanel.setHeight("100%");

		this.add(vPanel);
		for (int i = 0; i < nbUser; i++) {
			textEd = new TextEditor(this, startingUserNo++, nbSentences);
			vPanel.add(textEd);
		}
	}

	/**
	 * Create a TextEntry with the parent logging capabilities (send the logged data to its parent)
	 * 
	 * @param nbUser
	 *            : number of users
	 * @param startingUserNo
	 *            : user no. to log
	 * @param nbSentences
	 * @param hasToLogToParent
	 * @param parent
	 */
	public TextEntry1Space(int nbOfUser, int startingUserNo, int nbSentences, int experimentNo, boolean hasToLogToParent, ICEDataLogger parent, String color) {

		super();
		this.nbUser = nbOfUser;
		this.hasToLogToParent = hasToLogToParent;
		this.parent = parent;
		this.experimentNo = experimentNo;
		this.startingUserNo = startingUserNo;
		
		vPanel.setHeight("100%");

		this.add(vPanel);
		for (int i = 0; i < nbUser; i++) {
			textEd = new TextEditor(this, startingUserNo++, nbSentences);
			textEd.setColor(color);
			vPanel.add(textEd);
		}
	}

	// ------------------------------------------------------------------------

	private void log() {
//		if (experimentNo != -1) { 
			EventingServiceAsync svc = GWT.create(EventingService.class);
			svc.log(ICEMain.identifier, loggedData, ExperimentIdentifier.TEXTEDIT, 1, new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					TextEntry1Space.this.clear();
					Window.alert("Successfully logged! Experiment finished");
				}

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Error:", caught);
					Window.alert("Task finished but was not able to log!");
				}
			});

	}

	// ------------------------------------------------------------------------------

	private String[] getLoggedResult(Vector<String> resultVector) {
		String[] result = new String[resultVector.size()];

		for (String s : resultVector) {
			result[resultVector.indexOf(s)] = s;
		}

		return result;
	}

	// ------------------------------------------------------------------------------

	@Override
	public void setLoggedData(Vector<String> textData , boolean finished , boolean check) { 
		if (nbExpFinished < nbUser) {
			
			//if(finished) { 
				userLogVector.addAll(textData);
				
				nbExpFinished++;
			//}
			//else
			//{
			//	userLogVector.add(textData.lastElement()); 
			//	System.out.println("test amacli 1 a:  " + textData.lastElement() ); 
			//}
			
		}

		if (nbExpFinished == nbUser) {
			if (hasToLogToParent && (parent != null)) {

				if(!finished){
					userLogVector.add(textData.lastElement()); 

				}
				parent.setLoggedData(userLogVector, finished , check); 
				
			}
		
			else {
				loggedData = new String[userLogVector.size()];
				userLogVector.copyInto(loggedData);

				log();
			}
		}
	}

	/**
	 * Callback method used by the Blob class to send the vector of data to be logged.
	 * 
	 * @param blobData
	 */
	@Override
	public void setLoggedData(Vector<CursorXY> blobData, boolean finished) {

	}

	@Override
	public void setLoggedData(String[] blobData) {

	}

	@Override
	public void initialise() {
		// TODO Auto-generated method stub

	}
	
	

}

