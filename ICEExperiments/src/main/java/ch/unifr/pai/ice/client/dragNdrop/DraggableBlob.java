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

import ch.unifr.pai.twice.dragndrop.client.intf.Draggable;

import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;

public class DraggableBlob extends Image implements Draggable {

	String blobName = "";
	int blobNumber = 0;
	double dragStartTime;
	double dropTime;
	Boolean isStarted = false;

	// -- Constructors ------------------------------------------

	public DraggableBlob(String imageName) {
		super(imageName);
		this.setVisible(true);

	}

	public DraggableBlob(String imageName, int blobNumber) {
		super(imageName);
		this.setVisible(true);
		this.blobNumber = blobNumber;
	}

	// -----------------------------------------------------------

	public Boolean isDragStarted() {
		return isStarted;
	}

	public void setDragStarted(Boolean isStarted) {
		this.isStarted = isStarted;
	}

	public double getDragStartTime() {
		return dragStartTime;
	}

	public void setDragStartTime(double dragStartTime) {
		this.dragStartTime = dragStartTime;
	}

	public double getDropTime() {
		return dropTime;
	}

	public void setDropTime(double dropTime) {
		this.dropTime = dropTime;
	}

	public void setBlobNumber(int blobNumber) {
		this.blobNumber = blobNumber;
	}

	public int getBlobNumber() {
		return blobNumber;
	}

	public String getBlobName() {
		return blobName;
	}

	public void setBlobName(String blobName) {
		this.blobName = blobName;
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		// TODO Auto-generated method stub
		return super.addMouseOverHandler(handler);
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		super.fireEvent(event);
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		// TODO Auto-generated method stub
		return super.addMouseDownHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		// TODO Auto-generated method stub
		return super.addMouseUpHandler(handler);
	}

	@Override
	public boolean isDraggable() {
		// TODO Auto-generated method stub
		return true;
	}

}
