package ch.unifr.pai.ice.client.dragNdrop;

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

import ch.unifr.pai.ice.client.RequireInitialisation;
import ch.unifr.pai.ice.client.utils.ICEDataLogger;
import ch.unifr.pai.ice.client.utils.PositioningUtils;
import ch.unifr.pai.twice.dragndrop.client.DragNDrop;
import ch.unifr.pai.twice.dragndrop.client.configuration.DragConfiguration;
import ch.unifr.pai.twice.dragndrop.client.intf.DragNDropHandler;
import ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler;
import ch.unifr.pai.twice.utils.device.client.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class DnD1userGeneric extends LayoutPanel implements RequireInitialisation {

	// LayoutPanel dragPanel = new LayoutPanel();
	// LayoutPanel dropPanel = new LayoutPanel();
	AbsolutePanel binPanel = new AbsolutePanel();

	boolean init = false;

	// int dragPanelWidth; // = (Window.getClientWidth()/3);
	// int dragPanelHeight; // = ((Window.getClientHeight()/2) - 20);

	// int dropPanelWidth;
	// int dropPanelHeight;

	Vector<String> resultVector; 

	int blobsToDrop = 0;
	String blobColor = "black";

	// statically set the position of the 20 blobs
	int[][] blobCoord;

	ICEDataLogger logger;
	String userNo;

	// Current image : 100x100 px
	// String imageName = GWT.getModuleBaseURL() + "trash-bin100x100.jpg";

	// Current image : 50x50 px
	String imageName = GWT.getModuleBaseURL() + "trash-bin50x50.jpg";
	
	boolean logcheck = false ; 
	int count = 0; 
	int trialcount = 0; 
	int setcount = 0; 
	boolean isStarted = false; 
	boolean isSetFinished = false;  
	long startTime; 
	long setFinishTime; 
	
	

	/******************************************************************
	 * Constructor
	 * 
	 * @param nbBoxes
	 * @param blobColor
	 ******************************************************************/
	private final int nbBlobs;
	private final int binSize = 50;

	public DnD1userGeneric(String userNo, int nbBlobs, String blobImageURL, ICEDataLogger logger) {
		super();
		this.blobColor = blobImageURL;
		this.logger = logger;
		this.userNo = userNo;
		this.nbBlobs = nbBlobs;
		// this.add(dragPanel);
		// this.add(dropPanel);
		// this.setWidgetTopHeight(dragPanel, 0, Unit.PX, 100, Unit.PCT);
		// this.setWidgetLeftWidth(dragPanel, 0, Unit.PX, 100, Unit.PCT);
		// this.setWidgetTopHeight(dropPanel, 0, Unit.PX, 100, Unit.PCT);
		// this.setWidgetLeftWidth(dropPanel, 0, Unit.PX, 100, Unit.PCT);
		// binPanel.setSize("100px", "100px");
		binPanel.getElement().getStyle().setBackgroundImage("url('" + imageName + "')");
	}
	

	@Override
	public void initialise() {

		if (!init) {
			// dragPanelWidth = dragPanel.getElement().getOffsetWidth();
			// dragPanelHeight = dragPanel.getElement().getOffsetHeight();
			//
			// dropPanelWidth = dropPanel.getElement().getOffsetWidth();
			// dropPanelHeight = dropPanel.getElement().getOffsetHeight();

			add(binPanel);
			setWidgetTopHeight(binPanel, getOffsetHeight() / 2 - binSize / 2, Unit.PX, binSize, Unit.PX);
			setWidgetLeftWidth(binPanel, getOffsetWidth() / 2 - binSize / 2, Unit.PX, binSize, Unit.PX);

			resultVector = new Vector<String>();

			blobCoord = PositioningUtils.getPositionsInCircle(nbBlobs, getOffsetHeight() / 2, getOffsetWidth() / 2,
					(Math.min(getOffsetHeight(), getOffsetWidth()) / 2) - 50);

			// int[][] tmpBlobCoord = new int[][] {
			// { dragPanelWidth - 100, dragPanelHeight - 100 },
			// { (dragPanelWidth / 2) - 40, 70 },
			// { 50, dragPanelHeight - 100 },
			// { 100, 70 },
			// { dragPanelWidth - 130, 90 },
			// { dragPanelWidth - 110, dragPanelHeight - 160 },
			// { 140, dragPanelHeight / 2 },
			// { dragPanelWidth / 2, dragPanelHeight - 100 },
			// { (dragPanelWidth / 2) - 50, (dragPanelHeight / 2) - 30 },
			// { 200, 110 },
			// { (dragPanelWidth / 2) + 80, dragPanelHeight - 80 },
			// { (dragPanelWidth / 4) * 3, dragPanelHeight - 100 },
			// { (dragPanelWidth / 4), dragPanelHeight - 40 },
			// { (dragPanelWidth / 4) * 3, dragPanelHeight / 2 },
			// { (dragPanelWidth / 4), (dragPanelHeight / 2) + 70 },
			// { (dragPanelWidth / 2) + 70, (dragPanelHeight / 2) + 80 },
			// { 140, dragPanelHeight / 3 },
			// { dragPanelWidth / 2, dragPanelHeight / 3 },
			// { dragPanelWidth - 70, (dragPanelHeight / 3) },
			// { (dragPanelWidth / 4) * 3, dragPanelHeight /2 }, };
			//
			// //Reduce to number of blobs
			// if(nbBlobs>=tmpBlobCoord.length)
			// blobCoord = tmpBlobCoord;
			// else{
			// blobCoord = new int[nbBlobs][2];
			// for(int i=0; i<nbBlobs; i++){
			// blobCoord[i][0] = tmpBlobCoord[i][0];
			// blobCoord[i][1] = tmpBlobCoord[i][1];
			// }
			// }

			addBlobs(true); 

			final FocusPanel panel = new FocusPanel();
			panel.addMouseMoveHandler(new MouseMoveHandler() {

				@Override
				public void onMouseMove(MouseMoveEvent event) {
					String deviceId = UUID.getUUIDForEvent(event.getNativeEvent());
					event.getRelativeX(panel.getElement());
					event.getClientY();
				}
			});

			init = true;
		}
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {

			}

		});

	}

	/**************************************************
	 * Blobs generator with the draggable capabilities
	 **************************************************/

	private void addBlobs( boolean check) { 
		logcheck= check;
		final int blobwidth = 30;
		final int blobheight = 30;
		for (int i = 0; i < blobCoord.length; i++) {

			blobsToDrop = blobCoord.length;

			// contains the draggable blob
			DraggableLabelledBlob blob = new DraggableLabelledBlob("", blobColor, i);
			blob.setSize(blobwidth + "px", blobheight + "px");
			// blob.setTitle(String.valueOf(i));
			// blob.setBlobNumber(i);
			// blob.getElement().getStyle().setColor("White");
			blob.setVisible(true);

			DragNDrop.makeDraggable(blob, DragConfiguration.withProxy(new DragNDropHandler() {

				@Override
				public void onStartDrag(String deviceId, Widget draggedWidget) {
					
					if(!isStarted){
						long time = System.currentTimeMillis(); 
						System.out.println(userNo + "  Start Time:" + time ); //to be deleted
						isStarted = true; 
						startTime = time; 
						}
					
					if (!((DraggableLabelledBlob) draggedWidget).isDragStarted()) {

						// set the starting time
						((DraggableLabelledBlob) draggedWidget).setDragStarted(true);
						((DraggableLabelledBlob) draggedWidget).setDragStartTime(System.currentTimeMillis());

						
					}
				}

				@Override
				public void onEndOfDrop(String deviceId, Widget draggedWidget, int dragProxyLeft, int dragProxyTop, Event event) {
				}

				@Override
				public boolean onDrop(String deviceId, Widget draggedWidget, int dragProxyLeft, int dragProxyTop, Event event, DropTargetHandler dropTarget,
						boolean outOfBox) {

					// if (dragProxyLeft <= (dragPanelWidth / 2)) {
					//
					// dragPanel.setWidgetPosition(
					// draggedWidget,
					// dragProxyLeft
					// - dragPanel.getAbsoluteLeft(),
					// dragProxyTop
					// - dragPanel.getAbsoluteTop());
					//
					// } else
					if (isInsideBin(dragProxyLeft, dragProxyTop, binPanel)) {

						//System.out.println(binPanel.getAbsoluteLeft()); 
						// dragProxyLeft > (panelWidth / 2)) {

						remove(draggedWidget);

						// Set the drop time
						((DraggableLabelledBlob) draggedWidget).setDropTime(System.currentTimeMillis());
						((DraggableLabelledBlob) draggedWidget).setDragStarted(false);
						
						((DraggableLabelledBlob) draggedWidget).setFalseDropTime(0); 
						
						//System.out.println("DROP TIME: " + (( (DraggableLabelledBlob) draggedWidget).getDropTime() )) ;
						//System.out.println("DROP check amacli: "+ System.currentTimeMillis() ); //to be deleted
						//System.out.println("ASIL TEST:  " + ((System.currentTimeMillis()) -((( (DraggableLabelledBlob) draggedWidget).getDropTime() )) ) ); //to be deleted
						//System.out.println("belli olan fonksiyon: "+ (((DraggableLabelledBlob) draggedWidget).getDragNdropTime())  ); //to be deleted

						// add the result in the log vector
						resultVector.add(

//						new String(userNo + "; blob" + ((DraggableLabelledBlob) draggedWidget).getBlobNumber() + ";"
//								+ ((DraggableLabelledBlob) draggedWidget).getDragNdropTime()
//								+ ";" + count)); 
						
						new String(userNo + "; blob" + ((DraggableLabelledBlob) draggedWidget).getBlobNumber() + ";"
								+ ((DraggableLabelledBlob) draggedWidget).getDragNdropTime()
								));
						
						//to be deleted//
						System.out.println("(LOG INFO) " + userNo + " ;"
							      	  +    "   BlobNo: " + "blob" + ((DraggableLabelledBlob) draggedWidget).getBlobNumber() + " ;"
									  +    "   Drop time: " 	  + ((DraggableLabelledBlob) draggedWidget).getDragNdropTime()
									  
									  /*+    "   Unsucess blob count:" + count*/);
						
						
						if (! logcheck ) {
							blobsToDrop= blobsToDrop-1;
							log(false);
							
						}
						
						
						if(logcheck){ 
							if (--blobsToDrop == 0 ) { 
							// log the results if all blobs are
							// dropped!!!
							
							log(true);}
						}
					}
					else {
						
						
						((DraggableLabelledBlob) draggedWidget).setFalseDropTime(System.currentTimeMillis());
						((DraggableLabelledBlob) draggedWidget).setDragStarted(false);
						
						((DraggableLabelledBlob) draggedWidget).setDropTime(0);	
						count= count+1;
						
						resultVector.add(  

//								new String(userNo + "; blob" + ((DraggableLabelledBlob) draggedWidget).getBlobNumber() + ";"
//										+ ((DraggableLabelledBlob) draggedWidget).getFalseDragNdropTime()
//										+ ";" + count) + " ; unsuccessful dNd");
						
						new String(userNo + "; blob" + ((DraggableLabelledBlob) draggedWidget).getBlobNumber() + ";"
								+ ((DraggableLabelledBlob) draggedWidget).getFalseDragNdropTime()
								+ ";" + count) + ". unsuccessful dNd");
						
						if (! logcheck ){
							log(false);
							trialcount++; 
						}
						
						if (logcheck ){
							setcount++;
						}
						
						System.out.println(userNo+ ":  " + "Unsuccess Drag&Drop!" + " ; BlobNo:" + ((DraggableLabelledBlob) draggedWidget).getBlobNumber() + " ; Unsuccess Drop Time:" + ((DraggableLabelledBlob) draggedWidget).getFalseDragNdropTime() ); //to be deleted						
						
						return false;
					}
					return true;

				}
			}

			));

			add(blob);
			setWidgetTopHeight(blob, blobCoord[i][0] - blobheight / 2, Unit.PX, blobheight, Unit.PX);
			setWidgetLeftWidth(blob, blobCoord[i][1] - blobwidth / 2, Unit.PX, blobwidth, Unit.PX);
		}
		
	}

	// ------------------------------------------------------------------------

	private void log(boolean doLog) {
		
		if(!isSetFinished){
			long setfinishtime = System.currentTimeMillis(); 
			isSetFinished = true; 
			setFinishTime = setfinishtime; 
			}
		
		logger.setLoggedData(resultVector , doLog , true);
		
		if (blobsToDrop == 0){

		addBlobs(false);} // for user to keep doing task until others finish
		
	}
			

	// ------------------------------------------------------------------------
	/**
	 * Check if the dropped blob is in the bin Panel
	 * 
	 * @param left
	 * @param top
	 * @param p
	 * @return
	 */
	private boolean isInsideBin(int left, int top, Panel p) {
		return ((left > p.getAbsoluteLeft() && left < p.getAbsoluteLeft() + p.getElement().getClientWidth()) && (top > p.getAbsoluteTop() && top < p
				.getAbsoluteTop() + p.getElement().getClientHeight()));
	}

	/**
	 * Generate a random number with an upper boundary
	 * 
	 * @param upperBond
	 * @return int
	 */
	private int randomNum(int upperBond) {

		return Random.nextInt(upperBond);
	}

}
