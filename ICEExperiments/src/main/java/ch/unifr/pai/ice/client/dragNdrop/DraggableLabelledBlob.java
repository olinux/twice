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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HTML;

public class DraggableLabelledBlob extends HTML implements Draggable {

	int blobNumber = 0;
	double dragStartTime;
	double dropTime;
	double dragNdropTime;
	Boolean isStarted = false;
	
	double false_dropTime;  
	double false_dragNdropTime; 
	
	

	// -- Constructors ------------------------------------------

	public DraggableLabelledBlob(String name, String imageName) {
		super(name);
		this.setVisible(true);
		this.setHeight("30px");
		this.getElement().getStyle().setBackgroundImage("url('" + imageName + "')");
		this.setHorizontalAlignment(ALIGN_CENTER);
		this.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		this.getElement().getStyle().setFontSize(16, Unit.PX);
		this.getElement().getStyle().setFontWeight(FontWeight.BOLD);

	}

	public DraggableLabelledBlob(String name, String imageName, int blobNumber) {
		super(name);
		this.setVisible(true);
		this.blobNumber = blobNumber;
		this.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		this.getElement().getStyle().setBackgroundImage("url('" + imageName + "')");
		this.getElement().getStyle().setFontSize(16, Unit.PX);
		this.getElement().getStyle().setFontWeight(FontWeight.BOLD);
	}

	@Override
	protected void onAttach() {
		// TODO Auto-generated method stub
		super.onAttach();

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
		this.dragNdropTime = dropTime - dragStartTime;
	}
	
	public void setFalseDropTime(double false_dropTime) {   //for unsuccessful DnDs
		this.false_dropTime = false_dropTime; 
		this.false_dragNdropTime = false_dropTime - dragStartTime;
	}
	

	/**
	 * Get the time needed to drag and drop the blob (in ms)
	 * 
	 * @return dNdrop total time
	 */
	public double getDragNdropTime() {
		return dragNdropTime;
	}
	
	public double getFalseDragNdropTime() { 
		return false_dragNdropTime ;
	}

	/**
	 * Set the time to drag and drop the blob (in ms)
	 * 
	 * @param dragNdropTime
	 */
	public void setDragNdropTime(double dragNdropTime) {
		this.dragNdropTime = dragNdropTime;
	}
	
	public void setFalseDragNdropTime(double false_dragNdropTime) {  
		this.false_dragNdropTime = false_dragNdropTime;
	}

	public void setBlobNumber(int blobNumber) {
		this.blobNumber = blobNumber;
	}

	public int getBlobNumber() {
		return blobNumber;
	}

	public String getBlobName() {
		return this.getHTML();
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
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
