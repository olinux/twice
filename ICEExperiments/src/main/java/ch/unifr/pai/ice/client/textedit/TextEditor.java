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

import java.util.Vector;

import ch.unifr.pai.ice.client.DraggableLabel;
import ch.unifr.pai.ice.client.DraggableVerticalPanel;
import ch.unifr.pai.ice.client.RequireInitialisation;
import ch.unifr.pai.twice.dragndrop.client.DragNDrop;
import ch.unifr.pai.twice.dragndrop.client.configuration.DragConfiguration;
import ch.unifr.pai.twice.dragndrop.client.intf.DragNDropHandler;
import ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler;
import ch.unifr.pai.twice.multipointer.provider.client.widgets.RemoteTextArea;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class TextEditor extends DraggableVerticalPanel implements RequireInitialisation {

	TextArea ta = new RemoteTextArea();
	Button addButton = new Button("Add text");
	Button doneButton = new Button("Done");

	TextEntry1Space parent;
	String bckGColor = "white";
	boolean isAPanel = false;
	int[] textPos = { 0, 0 };
	int width = 300;
	int height = 85;
	Vector<String> logVector;
	long startTime;
	int userNo;
	int nbTextEntry;
	String typedText = "";
	boolean isStarted = false;

	public TextEditor(TextEntry1Space parentWidget, int userNo, int nbTextEntry) {
		super();
		parent = parentWidget;
		logVector = new Vector<String>();
		this.userNo = userNo;
		this.nbTextEntry = nbTextEntry;
		if (parentWidget instanceof AbsolutePanel)
			isAPanel = true;

		ta.setCharacterWidth(80);
		ta.setVisibleLines(50);
		ta.setHeight(String.valueOf(height) + "px");
		ta.setWidth(String.valueOf(width) + "px");
		ta.getElement().getStyle().setFontSize(18, Unit.PX);

		this.add(ta);
		FlowPanel fp = new FlowPanel();
		fp.add(addButton);
		fp.add(doneButton);
		doneButton.getElement().getStyle().setFloat(Float.RIGHT);

		this.add(fp);
		// this.add(doneButton);
		this.setBorderWidth(1);

		// Listen for mouse events on the Add button.
		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				log(typedText, ta.getText());
				typedText = "";
				isStarted = false;
				addText();
				ta.setText("");
				ta.setFocus(true);
			}
		});

		doneButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				parent.setLoggedData(logVector);
			}
		});

		ta.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (!isStarted) {
					startTime = System.currentTimeMillis();
					isStarted = true;
				}
				typedText = typedText + event.getCharCode();
			}
		});

	}

	private void addText() {
		if (isAPanel) {
			// contains the draggable text
			DraggableLabel label = new DraggableLabel(ta.getText());
			label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			label.getElement().getStyle().setFontSize(18, Unit.PX);
			DragNDrop.makeDraggable(label, DragConfiguration.withProxy(new DragNDropHandler() {

				@Override
				public void onStartDrag(String deviceId, Widget draggedWidget) {
				}

				@Override
				public void onEndOfDrop(String deviceId, Widget draggedWidget, int dragProxyLeft, int dragProxyTop, Event event) {
				}

				@Override
				public boolean onDrop(String deviceId, Widget draggedWidget, int dragProxyLeft, int dragProxyTop, Event event, DropTargetHandler dropTarget,
						boolean outOfBox) {
					parent.setWidgetPosition(draggedWidget, dragProxyLeft - parent.getAbsoluteLeft(), dragProxyTop - parent.getAbsoluteTop());
					return true;
				}
			}

			));

			/*
			 * add the new text in the abs panel
			 */

			parent.add(label);
			textPos = setRandomPos(width, height, label);
			parent.setWidgetPosition(label, textPos[0], textPos[1]);
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setColor(String bckGColor) {

		this.bckGColor = bckGColor;
		this.getElement().getStyle().setBackgroundColor(bckGColor);

	}

	private void log(String typeText, String finalText) {
		logVector.add("user" + userNo + ";" + startTime + ";" + System.currentTimeMillis() + ";" + typeText + ";" + finalText);
	}

	private int[] setRandomPos(int tEdWidth, int tEdHeight, DraggableLabel l) {

		int[] pos = { 0, 0 };
		System.out.println("Parent:" + parent.getElement().getClientWidth() + ", label: " + l.getElement().getClientWidth());

		pos[0] = randomNum(20, parent.getElement().getClientWidth() - l.getElement().getClientWidth());
		pos[1] = randomNum(20, parent.getElement().getClientHeight() - 50);

		while (pos[0] <= tEdWidth || pos[1] <= tEdHeight) {
			pos[0] = randomNum(20, parent.getElement().getClientWidth() - l.getElement().getClientWidth());
			pos[1] = randomNum(20, parent.getElement().getClientHeight() - 50);
		}

		// parent.
		return pos;
	}

	/**
	 * Generate a random number with an upper boundary
	 * 
	 * @param upperBond
	 * @return int
	 */
	private int randomNum(int lowerBond, int upperBond) {

		int num = Random.nextInt(upperBond);

		while (num <= lowerBond) {
			num = Random.nextInt(upperBond);
		}

		return num;
	}

	private boolean checkOverlap(Panel p, Label l) {

		return true;

	}

	@Override
	public void initialise() {
		// TODO Auto-generated method stub

	}

}
