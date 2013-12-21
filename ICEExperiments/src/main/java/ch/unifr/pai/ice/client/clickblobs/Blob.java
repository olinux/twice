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

import java.util.Vector;

import ch.unifr.pai.ice.client.tracking.CursorXY;
import ch.unifr.pai.ice.client.utils.ICEDataLogger;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class Blob extends Image {

	Vector<CursorXY> blobClickVector;
	int iteration = 0;
	int nbIter = 0;
	int[] offset = { 0, 0 };
	Blob blob;
	Button resetBT = new Button("Reset");
	Label completed = new Label("Experiment completed!");
	String uNb = "";
	AbsolutePanel absPanel;
	boolean randomized = true;
	int[][] blobPos = null;
	int arrayOffset = 0;
	boolean isLogged = false;
	ICEDataLogger logger;

	/******************************************
	 * Create a new blob with RANDOM position
	 * 
	 * @param String
	 *            image
	 * @param String
	 *            userNb
	 * @param int nbIteration
	 * @param AbsolutePanel
	 *            absolutePanel
	 ******************************************/

	public Blob(String image, String userNb, int nbIteration, AbsolutePanel absolutePanel, ICEDataLogger logger, boolean doLog) {

		super(image);
		isLogged = !doLog;
		this.nbIter = nbIteration;
		blob = this;
		this.absPanel = absolutePanel;
		this.setSize("30px", "30px");
		this.uNb = userNb;
		this.logger = logger;
		blobClickVector = new Vector<CursorXY>();

		this.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {

				if (iteration < nbIter) {
					blobClickVector.add(new CursorXY(uNb, blob.getAbsoluteLeft(), blob.getAbsoluteTop(), System.currentTimeMillis()));
					absPanel.setWidgetPosition(blob, randomNum(absPanel.getOffsetWidth() - 70), randomNum(absPanel.getOffsetHeight() - 70));

					iteration++;
				}
			}
		});
	}

	/***********************************************************************
	 * Create a new blob with an array of position in the absolute panel
	 * 
	 * @param image
	 * @param userNb
	 * @param nbIteration
	 * @param absolutePanel
	 * @param blobPosition
	 ************************************************************************/
	public Blob(String image, String userNb, int nbIteration, AbsolutePanel absolutePanel, ICEDataLogger logger, int[][] blobPosition, int offsetX,
			int offsetY, int startOffset, boolean doLog) {

		super(image);
		this.nbIter = nbIteration;
		blob = this;
		this.absPanel = absolutePanel;
		this.setSize("30px", "30px");
		this.uNb = userNb;
		this.blobPos = blobPosition;
		this.offset[0] = offsetX;
		this.offset[1] = offsetY;
		this.arrayOffset = startOffset;
		this.logger = logger;
		this.isLogged = !doLog;
		blobClickVector = new Vector<CursorXY>();

		setBlobPositionHandler(blobPosition);

	}

	protected void onFinishedInitialIteration() {
	}

	private void setBlobPositionHandler(int[][] blobPosition) {

		if (blobPosition == null) {

			Window.alert("No set of position defined!");

		}
		else {

			this.addMouseDownHandler(new MouseDownHandler() {

				@Override
				public void onMouseDown(MouseDownEvent event) {

					iteration++;
					if (iteration < blobPos.length) {

						if (!isLogged) {
							blobClickVector.add(new CursorXY(uNb, blob.getAbsoluteLeft(), blob.getAbsoluteTop(), System.currentTimeMillis()));
						}

						// Check the pos. in the array and get back to 0 if over array length -> loop
						if ((iteration + arrayOffset) < blobPos.length) {

							absPanel.setWidgetPosition(blob, blobPos[iteration + arrayOffset][0] + offset[0], blobPos[iteration + arrayOffset][1] + offset[1]);

						}
						else {

							absPanel.setWidgetPosition(blob, blobPos[(iteration + arrayOffset) - blobPos.length][0] + offset[0],
									blobPos[(iteration + arrayOffset) - blobPos.length][1] + offset[1]);
						}

					}
					else {

						if (!isLogged) {
							blobClickVector.add(new CursorXY(uNb, blob.getAbsoluteLeft(), blob.getAbsoluteTop(), System.currentTimeMillis()));
							logger.setLoggedData(blobClickVector);
							isLogged = true;

						}

						iteration = 1;

						// continue to move the blob without recording the position
						// this is to let the user continue until the others finished

						// Check the pos. in the array and get back to 0 if over array length -> loop
						if ((iteration + arrayOffset) < blobPos.length) {

							absPanel.setWidgetPosition(blob, blobPos[iteration + arrayOffset][0] + offset[0], blobPos[iteration + arrayOffset][1] + offset[1]);

						}
						else {

							absPanel.setWidgetPosition(blob, blobPos[(iteration + arrayOffset) - blobPos.length][0] + offset[0],
									blobPos[(iteration + arrayOffset) - blobPos.length][1] + offset[1]);
						}

						onFinishedInitialIteration();
						iteration++;
					}
				}
			});
		}
	}

	public Vector<CursorXY> getBlobClickVector() {
		return blobClickVector;
	}

	public void resetCounter() {
		iteration = 0;
		absPanel.remove(completed);
		absPanel.setWidgetPosition(this, absPanel.getElement().getOffsetWidth() / 2 + offset[0], absPanel.getElement().getOffsetHeight() / 2 + offset[1]);
		absPanel.setWidgetPosition(blob, blobPos[iteration + arrayOffset][0] + offset[0], blobPos[iteration + arrayOffset][1] + offset[1]);
		blobClickVector.clear();
		// isLogged = false;

	}

	private int randomNum(int upperBond) {
		return Random.nextInt(upperBond);
	}
}
