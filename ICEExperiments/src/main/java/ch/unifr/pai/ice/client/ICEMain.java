package ch.unifr.pai.ice.client;

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


import java.util.Date;

import ch.unifr.pai.twice.multipointer.controller.client.TouchPadWidget; 
import ch.unifr.pai.ice.client.clickblobs.ClickBlobs1user;
import ch.unifr.pai.ice.client.clickblobs.ClickBlobs2users; 
import ch.unifr.pai.ice.client.clickblobs.ClickBlobs4users; 
import ch.unifr.pai.ice.client.textedit.TextEntrySepSpace; 
import ch.unifr.pai.ice.client.dragNdrop.DnD2users; 
import ch.unifr.pai.ice.client.dragNdrop.DnD4users; 
import ch.unifr.pai.ice.client.dragNdrop.DND4users4spaces; 

import ch.unifr.pai.ice.client.dragNdrop.DnD1user;
import ch.unifr.pai.ice.client.textedit.TextEntry1Space;
import ch.unifr.pai.ice.client.tracking.LineTracking1user;

import ch.unifr.pai.twice.multipointer.provider.client.MultiCursorController;
import ch.unifr.pai.twice.multipointer.provider.client.NoMultiCursorController;
import ch.unifr.pai.twice.utils.device.client.DeviceType;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import com.google.gwt.dom.client.Style.Unit;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class ICEMain implements EntryPoint {

	public static String identifier;
	int nbSentences = 4;
	int trainingnbSentences = 1;

	//TextEntry1Space trainingTextEntry1Space1User = new TextEntry1Space(1, 2, -1);
	//TextEntry1Space textEntry1Space1User = new TextEntry1Space(1, nbSentences, 1);
	//TextEntry1Space textEntry1Space2Users = new TextEntry1Space(2, nbSentences,2);
	//TextEntry1Space textEntry1Space4Users = new TextEntry1Space(4, nbSentences,3);
	
	TextEntrySepSpace trainingtextEntrySepSpace1Users = new TextEntrySepSpace(1,trainingnbSentences, -1); //-1 is for not logging
	TextEntrySepSpace textEntrySepSpace1Users = new TextEntrySepSpace(1,nbSentences, 19); 
	TextEntrySepSpace textEntrySepSpace2Users = new TextEntrySepSpace(2,nbSentences, 18); 
	TextEntrySepSpace textEntrySepSpace4Users = new TextEntrySepSpace(4,nbSentences, 21); 
	 
	ClickBlobs1user trainingCheckBoxes = new ClickBlobs1user(4, false, true); 
	ClickBlobs1user checkBoxes = new ClickBlobs1user(4, false, true);
	ClickBlobs2users checkB2u = new ClickBlobs2users(4, false);  
	ClickBlobs4users checkB4u = new ClickBlobs4users(4, false);  
	
	DnD1user trainingDNdBoxes = new DnD1user(4, false);
	DnD1user dNdBoxes = new DnD1user(10, true); 
	DnD2users dNdBoxes2S = new DnD2users(); 
	DnD4users dNdBoxes4S = new DnD4users(); 
    //DND4users4spaces dN4dropBoxes = new DND4users4spaces(10); 

	//LineTracking1user trainingLineTracking = new LineTracking1user(false, "trace-training.png");
	//LineTracking1user lineTracking = new LineTracking1user(true, "trace-experiment.png");
	// LineTracking2users lineTracking2users = new LineTracking2users();
	// LineTracking4users lineTracking4users = new LineTracking4users();

	TabLayoutPanel tabPanel = new TabLayoutPanel(40, Unit.PX);
	 	 
	@Override
	public void onModuleLoad() {
		
		if (DeviceType.getDeviceType() == DeviceType.MULTICURSOR) {
			
			/***************************************
			 * Get the size of the browser window.
			***************************************/
			MultiCursorController multiCursor = GWT.create(NoMultiCursorController.class);
			multiCursor.start();
			
			
			identifier = String.valueOf(new Date().getTime() / 1000);
			Window.alert("Start experiment: " + identifier);
			RootLayoutPanel.get().add(tabPanel);
			
			
		/*
		 * tabPanel setup
		 */
		
		//*****************Trainings***************************//
		//tabPanel.add(trainingCheckBoxes, "TA");
		//tabPanel.add(trainingtextEntrySepSpace1Users, "TB");  //tabPanel.add(trainingTextEntry1Space1User, "TB");
		//tabPanel.add(trainingDNdBoxes, "TC");
		//tabPanel.add(trainingLineTracking, "TD");
		
		//*****************Click Blobs***************************//
		//tabPanel.add(checkBoxes, "A"); 
		//tabPanel.add(checkB2u, "Click blobs 2 users"); 
		tabPanel.add(checkB4u, "Click blobs 4 users");
		
		//*****************Text Edit****************************//
		//tabPanel.add(textEntrySepSpace1Users, "B"); 
		//tabPanel.add(textEntrySepSpace2Users, "TE multi Space - 2 users");  
		tabPanel.add(textEntrySepSpace4Users, "TE multi Space - 4 users");
		
		////******************D&D*********************************//
		//tabPanel.add(dNdBoxes, "C");
		//tabPanel.add(dNdBoxes2S, "D & D 2 spaces"); 
		tabPanel.add(dNdBoxes4S, "D & D 4 spaces"); 
		//tabPanel.add(dN4dropBoxes, "D & 4 D boxes"); 
		
		
		//tabPanel.add(textEntry1Space1User, "B");
		//tabPanel.add(textEntry1Space2Users, "TE 1 Space - 2 users"); 
		//tabPanel.add(textEntry1Space4Users, "TE 1 Space - 4 users");
		//tabPanel.add(lineTracking, "D");
		// tabPanel.add(lineTracking2users, "L tracking 2");
		// tabPanel.add(lineTracking4users, "L tracking 4");	
		
		tabPanel.selectTab(0);
		
		if (tabPanel.getWidget(0) instanceof RequireInitialisation) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					((RequireInitialisation) tabPanel.getWidget(0)).initialise(); 
				}
			});
		}

		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent event) {

				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						((RequireInitialisation) tabPanel.getWidget(tabPanel.getSelectedIndex())).initialise();
					}
				});
			}
		});

		/*
		 * re-position of the widgets on the resized window
		 */
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {

			}
		});
			

		}
	
		else { 

			TouchPadWidget widget = GWT.create(TouchPadWidget.class);
			RootLayoutPanel.get().add(widget); 
			//widget.initialize(UUID.get(), null, null);
			widget.start();
		} 
		
	}

	
}
