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

	/****************
	 * Constructor
	 ****************/
	public TextEntrySepSpace(int nbUser, int nbSentences, int experimentNo) {

		super();
		this.nbUser = nbUser;
		this.experimentNo = experimentNo;

		this.setSize("100%", "100%");
		this.setBorderWidth(1);

		if (nbUser <= 3) {
			for (int i = 0; i < nbUser; i++) {
				this.add(new TextEntry1Space(1, nbSentences, 0, true, this, colorSet[i]));
				this.getWidget(i).setSize("100%", "100%");
			}
		}
		else {

			vPanel1 = new VerticalPanel();
			vPanel1.setSize("100%", "100%");
			vPanel1.setBorderWidth(1);
			this.add(vPanel1);

			for (int i = 0; i < Math.round(nbUser / 2); i++) {
				vPanel1.add(new TextEntry1Space(1, i + 1, nbSentences, 0, true, this, colorSet[i]));
				vPanel1.getWidget(i).setSize("100%", "100%");
			}

			vPanel2 = new VerticalPanel();
			vPanel2.setSize("100%", "100%");
			vPanel2.setBorderWidth(1);
			this.add(vPanel2);

			nbTEntry = nbUser - Math.round(nbUser / 2);
			int widgetNo = 0;
			for (int i = nbTEntry; i < nbUser; i++) {
				vPanel2.add(new TextEntry1Space(1, i + 1, nbSentences, 0, true, this, colorSet[i]));
				vPanel2.getWidget(widgetNo++).setSize("100%", "100%");
			}
		}
	}

	// ------------------------------------------------------------------------

	private void log() {
		EventingServiceAsync svc = GWT.create(EventingService.class);
		svc.log(ICEMain.identifier, loggedData, ExperimentIdentifier.TEXTEDITSPACES, nbUser, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				Window.alert("Successfully logged");
			}

			@Override
			public void onFailure(Throwable caught) {
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
	public void setLoggedData(Collection<? extends String> textData) {
		if (nbExpFinished < nbUser) {
			userLogVector.addAll(textData);
			nbExpFinished++;
			System.out.println("user: " + nbExpFinished + "; no records:" + userLogVector.size());
		}

		if (nbExpFinished == nbUser) {
			loggedData = new String[userLogVector.size()];
			userLogVector.copyInto(loggedData);
			System.out.println("user: " + nbExpFinished + " - Finished!");
			log();
		}
	}

	/**
	 * Callback method used by the Blob class to send the vector of data to be logged.
	 * 
	 * @param blobData
	 */
	@Override
	public void setLoggedData(Vector<CursorXY> blobData) {

	}

	@Override
	public void setLoggedData(String[] blobData) {

	}

	@Override
	public void initialise() {
		// TODO Auto-generated method stub

	}

}
