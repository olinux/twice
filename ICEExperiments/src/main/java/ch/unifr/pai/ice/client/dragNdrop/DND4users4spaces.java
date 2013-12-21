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

import ch.unifr.pai.ice.client.ICEMain;
import ch.unifr.pai.ice.client.RequireInitialisation;
import ch.unifr.pai.ice.client.rpc.EventingService;
import ch.unifr.pai.ice.client.rpc.EventingServiceAsync;
import ch.unifr.pai.ice.client.utils.CoordTransformer;
import ch.unifr.pai.ice.shared.ExperimentIdentifier;
import ch.unifr.pai.twice.dragndrop.client.DragNDrop;
import ch.unifr.pai.twice.dragndrop.client.configuration.DragConfiguration;
import ch.unifr.pai.twice.dragndrop.client.intf.DragNDropHandler;
import ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler;
import ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandlerAdapter;
import ch.unifr.pai.twice.utils.device.client.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DND4users4spaces extends HorizontalPanel implements RequireInitialisation {

	// AbsolutePanel dragPanel = new AbsolutePanel();

	int blobsToDrop = 0;

	FocusPanel dragPanel;
	FocusPanel dropPanel;

	Grid dragGrid = new Grid(4, 10);
	Grid dropGrid = new Grid(4, 10);

	int widht;
	int height;

	int panelWidth = (Window.getClientWidth() / 2);
	int panelHeight = (Window.getClientHeight() - 20);

	// Blob position Array. Allow to chose the blobs order in the drag panel
	int[] blobPosArray = { 8, 5, 0, 2, 9, 3, 4, 7, 6, 1 };

	// define the position of the blobs in the drag panel in order
	// to guaranty equal drag&Drop distance for all users
	int user1RowPosInDragPanel = 2;
	int user2RowPosInDragPanel = 3;
	int user3RowPosInDragPanel = 0;
	int user4RowPosInDragPanel = 1;

	Vector<String> resultVector;

	// statically set the position of the 10 blobs
	int[][] blobCoord = { { panelWidth - (panelWidth / 3), panelHeight - (panelHeight / 3) }, { (panelWidth / 2) - 40, 70 }, { 50, panelHeight - 200 },
			{ 100, 70 }, { panelWidth - 170, 90 }, { panelWidth - 150, panelHeight - 180 }, { 140, panelHeight / 2 }, { panelWidth / 2, panelHeight / 3 },
			{ (panelWidth / 2) - 50, (panelHeight / 2) - 30 }, { 140, 140 }, };

	int[][] blobCoord2 = CoordTransformer.horizontal(blobCoord, panelWidth, panelHeight);
	int[][] blobCoord3 = CoordTransformer.verticalAndHorizontal(blobCoord, panelWidth, panelHeight);
	int[][] blobCoord4 = CoordTransformer.vertical(blobCoord, panelWidth, panelHeight);

	// Black, red, green, blue ->> colors corresponding to the lines and blobs
	String[] colors = { "black", "pink", "PowderBlue", "palegreen" };

	// ----------------------------------------------------------------------------

	public DND4users4spaces(int nbBoxes) {

		super();
		this.setSize("100%", "100%");
		this.setBorderWidth(1);

		dragGrid.setSize("100%", "100%");
		for (int col = 0; col < dropGrid.getColumnCount(); col++) {
			dragGrid.getColumnFormatter().setWidth(col, 100 / dropGrid.getColumnCount() + "%");
		}
		dropGrid.setSize("100%", "100%");
		dropGrid.setBorderWidth(1);

		setDropPanels();
		setBlobsInDragPanel();

		this.add(dragGrid);
		this.add(dropGrid);

		this.setCellWidth(dragGrid, "50%");
		this.setCellWidth(dropGrid, "50%");

		resultVector = new Vector<String>();
	}

	// --------------------------------------------------------------------------------------------

	private void setBlobsInDragPanel() {

		// ---------------------------------------------
		// -- Blobs Statically and equally positioned --
		// ---------------------------------------------

		// user 1 (user number 0 in grid!)
		for (int i = 0; i < 10; i++) {
			addBlob(i, GWT.getModuleBaseURL() + "circle_black.png", "30px", "30px", 0, user1RowPosInDragPanel, blobPosArray[i], "white");
			blobsToDrop++;
		}

		// user 2
		for (int i = 0; i < 10; i++) {
			addBlob(i, GWT.getModuleBaseURL() + "circle_light_red.png", "30px", "30px", 1, user2RowPosInDragPanel, blobPosArray[i], "black");
			blobsToDrop++;
		}

		// user 3
		for (int i = 0; i < 10; i++) {
			addBlob(i, GWT.getModuleBaseURL() + "circle_light_blue.png", "30px", "30px", 2, user3RowPosInDragPanel, blobPosArray[i], "black");
			blobsToDrop++;
		}

		// user 4
		for (int i = 0; i < 10; i++) {
			addBlob(i, GWT.getModuleBaseURL() + "circle_light_green.png", "30px", "30px", 3, user4RowPosInDragPanel, blobPosArray[i], "black");
			blobsToDrop++;
		}

		// blobsToDrop = 3;

	}

	/******************************************
	 * Set the drop panels with the grid
	 ******************************************/

	private void setDropPanels() {

		for (int row = 0; row < 4; ++row) {
			for (int col = 0; col < 10; ++col) {

				dropGrid.getColumnFormatter().setWidth(col, "10%");
				dropPanel = new FocusPanel();
				dropPanel.setSize("100%", "100%");

				Label l = new Label(String.valueOf(col));
				l.getElement().getStyle().setBackgroundColor(colors[row]);
				l.getElement().getStyle().setFontSize(18, Unit.PX);
				l.getElement().getStyle().setFontWeight(FontWeight.BOLD);

				if (colors[row].equals("black"))
					l.getElement().getStyle().setColor("white");

				final DropAbsolutePanel absDropPanel = new DropAbsolutePanel((row * 10) + col);
				absDropPanel.add(l);
				absDropPanel.setTitle(String.valueOf(row) + String.valueOf(col));
				absDropPanel.setSize("100%", "100%");

				dropPanel.add(absDropPanel);
				dropGrid.setWidget(row, col, dropPanel);

				/**************************
				 * Set the drop handler
				 *************************/

				DragNDrop.setDropHandler(dropPanel, new DropTargetHandlerAdapter() {

					@Override
					public boolean onDrop(String deviceId, Widget widget, Element dragProxy, Event event, Double intersectionPercentage,
							Double intersectionPercentageWithTarget) {

						if (((DraggableLabelledBlob) widget).getBlobNumber() == absDropPanel.getPanelID()) {

							absDropPanel.clear();
							absDropPanel.add(widget);

							((DraggableLabelledBlob) widget).setDropTime(System.currentTimeMillis());
							((DraggableLabelledBlob) widget).setDragStarted(false);
							((DraggableLabelledBlob) widget).setDropTime(System.currentTimeMillis());

							resultVector.add(

							new String("user" + widget.getTitle().charAt(0) + ";" + "blob" + ((DraggableLabelledBlob) widget).getBlobName() + ";"
									+ ((DraggableLabelledBlob) widget).getDragNdropTime())

							);

							if (--blobsToDrop == 0) {
								// log the results if all blobs are dropped!!!
								log();
							}
							return true;
						}
						else
							return false;
					}

				}, false);
			}
		}
	}

	/*******************************************************************
	 * Blobs generator with the draggable capabilities - Pseudo randomly position (array of position)
	 *******************************************************************/

	private void addBlob(int xPos, int yPos, String image, String widthSize, String heightSize, int row, int col, String textColor) {

		DraggableLabelledBlob blob = new DraggableLabelledBlob(String.valueOf(col), image);
		blob.setSize(widthSize, heightSize);
		blob.setBlobNumber((row * 10) + col);
		blob.setVisible(true);
		blob.setTitle(String.valueOf((row * 10) + col));
		blob.getElement().getStyle().setColor(textColor);

		DragNDrop.makeDraggable(blob, DragConfiguration.withProxy(new DragNDropHandler() {

			@Override
			public void onStartDrag(String deviceId, Widget draggedWidget) {

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

				return true;
			}
		}

		));

		final FocusPanel panel = new FocusPanel();

		panel.addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent event) {
				String deviceId = UUID.getUUIDForEvent(event.getNativeEvent());
				event.getRelativeX(panel.getElement());
				event.getClientY();
			}
		});

		// dragPanel.add(blob, xPos, yPos);
		dragGrid.setWidget(row, col, blob);

	}

	/************************************************************************
	 * Blobs generator with the draggable capabilities - Equally distributed
	 ************************************************************************/

	private void addBlob(int posInGrid, String image, String widthSize, String heightSize, int userNo, int row, int col, String textColor) {

		DraggableLabelledBlob blob = new DraggableLabelledBlob(String.valueOf(col), image);
		blob.setSize(widthSize, heightSize);
		blob.setBlobNumber((userNo * 10) + col);
		blob.setVisible(true);
		blob.setTitle(String.valueOf((userNo * 10) + col));
		blob.getElement().getStyle().setColor(textColor);

		DragNDrop.makeDraggable(blob, DragConfiguration.withProxy(new DragNDropHandler() {

			@Override
			public void onStartDrag(String deviceId, Widget draggedWidget) {

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

				return true;
			}
		}

		));

		final FocusPanel panel = new FocusPanel();

		panel.addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent event) {
				String deviceId = UUID.getUUIDForEvent(event.getNativeEvent());
				event.getRelativeX(panel.getElement());
				event.getClientY();
			}
		});

		dragGrid.setWidget(row, posInGrid, blob);

	}

	// ------------------------------------------------------------------------

	private void log() {
		EventingServiceAsync svc = GWT.create(EventingService.class);
		svc.log(ICEMain.identifier, getLoggedResult(resultVector), ExperimentIdentifier.DRAGNDROPSPACES, 4, new AsyncCallback<Void>() {

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

	/**
	 * Generate a random number with an upper boundary
	 * 
	 * @param upperBond
	 * @return int
	 */
	private int randomNum(int upperBond) {

		return Random.nextInt(upperBond);
	}

	@Override
	public void initialise() {
		// TODO Auto-generated method stub

	}

}
