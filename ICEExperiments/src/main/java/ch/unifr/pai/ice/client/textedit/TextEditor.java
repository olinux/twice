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

import ch.unifr.pai.twice.multipointer.provider.client.widgets.MultiFocusTextBox;
import ch.unifr.pai.twice.multipointer.provider.client.widgets.RemoteTextArea;


import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class TextEditor extends DraggableVerticalPanel implements
		RequireInitialisation {

	HorizontalPanel hPanel = new HorizontalPanel();
	DockLayoutPanel panel = new DockLayoutPanel(Unit.PX);
	TextArea ta = new RemoteTextArea(); //ta is replaced with MultiFocusTextBox
	MultiFocusTextBox MultiFocus = new MultiFocusTextBox();

	// Button addButton = new Button("Add text");
	// Button doneButton = new Button("Done");

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

	int count = 0; 
	int secondcount = 0; 
	boolean isSetFinished = false; 
	boolean isLast = false;
	boolean isSetStarted;
	long setStartTime;

	private Label text;
	String[] traininglist = { "book", "blue", "nose", "key" };
	String[] wordlist = { "car", "baby", "grass", "train", "mouse", "park","boy", "green", "paper", "pencil" };
	String[] secondwordlist = { "bird", "flower", "girl", "bus", "yellow","dog", "red", "blue", "car", "boat" };

	int iteration = 0;


	public TextEditor(TextEntry1Space parentWidget, int userNo, int nbTextEntry) {
		super();
		parent = parentWidget;
		logVector = new Vector<String>();
		this.userNo = userNo;
		this.nbTextEntry = nbTextEntry;
		if (parentWidget instanceof AbsolutePanel)
			isAPanel = true;


		if (nbTextEntry == 1) // if it's training
		{
			wordlist = traininglist;
		}

		text = new Label(wordlist[0]);
		text.getElement().getStyle().setColor("#fff");
		text.getElement().getStyle().setFontSize(20, Unit.PT);
		text.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		hPanel.setWidth("100%");
		hPanel.add(text);
		this.add(hPanel);

		MultiFocus.setVisible(true);
		MultiFocus.setWidth("100%");
		MultiFocus.setHeight("100px");
		MultiFocus.setWidth("250px");

		MultiFocus.getElement().getStyle().setBackgroundColor("#fff");

		// this.add(ta);
		this.add(MultiFocus);
		this.setBorderWidth(1);

		MultiFocus.addDomHandler(new KeyPressHandler() {  //Listen for KeyPress events on MultiFocusTextBox
			@Override
			public void onKeyPress(KeyPressEvent event) {

				if (!isStarted) {
					startTime = System.currentTimeMillis();

					if (!isSetStarted) {
						setStartTime = startTime; // set experiment start time
						isSetStarted = true;
					}

					isStarted = true;
				}
				typedText = typedText + event.getCharCode();

			}
		}, KeyPressEvent.getType());

		MultiFocus.addDomHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {

				if (event.getNativeEvent().getKeyCode() == 13) {

					if (isSetFinished && !isLast) { // for the second set

						log(typedText, ta.getText());
						parent.setLoggedData(logVector, false, true); // done with experiment

						iteration++;
						text.setText(secondwordlist[iteration]);

						typedText = "";
						isStarted = false;
					}

					if (isLast) {
						log(typedText, ta.getText());
						parent.setLoggedData(logVector, true, true); // done with experiment

						text.setText(secondwordlist[0]);
						typedText = "";
						isStarted = false;

						iteration = 0;
						isLast = false;
					}

					if (!isSetFinished) {

						if (!isLast) {
							iteration++; 
							text.setText(wordlist[iteration]); 

							log(typedText, ta.getText()); // log for wordlist[iteration-1]
							typedText = "";
							isStarted = false;

							if ((iteration == (wordlist.length - 1))) { //last element
								isSetFinished = true;
								isLast = true;
							}
						}

					}

				}

				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_BACKSPACE) {

					typedText = typedText + "~";
				}

			}
		}, KeyUpEvent.getType());
	}

	private void addText() {
		if (isAPanel) {
			// contains the draggable text
			DraggableLabel label = new DraggableLabel(ta.getText());
			label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			label.getElement().getStyle().setFontSize(18, Unit.PX);
			DragNDrop.makeDraggable(label,
					DragConfiguration.withProxy(new DragNDropHandler() {

						@Override
						public void onStartDrag(String deviceId,
								Widget draggedWidget) {
						}

						@Override
						public void onEndOfDrop(String deviceId,
								Widget draggedWidget, int dragProxyLeft,
								int dragProxyTop, Event event) {
						}

						@Override
						public boolean onDrop(String deviceId,
								Widget draggedWidget, int dragProxyLeft,
								int dragProxyTop, Event event,
								DropTargetHandler dropTarget, boolean outOfBox) {
							parent.setWidgetPosition(draggedWidget,
									dragProxyLeft - parent.getAbsoluteLeft(),
									dragProxyTop - parent.getAbsoluteTop());
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

		long a = System.currentTimeMillis();

		String actualWord;

		if (count < wordlist.length) {
			actualWord = wordlist[count];
		}

		else {
			actualWord = secondwordlist[secondcount];
			secondcount++;
		}

		if (isLast) { // if it is last element 
			logVector
					.add("User"
							+ userNo+ " ; Text typing started:"+ startTime+ " ; Text typing finished:"+ a
							+ " ; Text Typed: "+ typeText+ " ; Text Submitted: "+ finalText
							+ " ; Actual text: "+ actualWord+ " ; Lenght: "+ (typeText.length() - 1)
							+ "; Time period: "+ (a - startTime)+ '\n'
							+ "---------------------------------------------------------------"
							+ '\n'+ "User"+ userNo+ " ; Start time:"
							+ setStartTime+ " ;  Set finish time:"
							+ a+ " ; Set complition time:"+ (a - setStartTime)+ '\n'
							+ "---------------------------------------------------------------");
		}

		else {

			logVector.add("User" + userNo + " ; Text typing started:"
					+ startTime + " ; Text typing finished:" + a
					+ " ; Text Typed: " + typeText + " ; Text Submitted: "
					+ finalText + " ; Actual text: " + actualWord
					+ " ; Lenght: " + (typeText.length() - 1)
					+ "; Time period: " + (a - startTime) + '\n');
		}

		// PRINTING LOGS in system
		// System.out.println("(LOG INFO) " + "user"+ userNo + "; "
		// + "  Text typing started: " + startTime + "; "
		// + "  Text typing finished: " + a + "; "
		// + "  Text Typed: " + typeText + "; "
		// + "  Text Submited: " + finalText + "; "
		// + "  Actual text: " + actualWord + "; "
		// + "  Lenght: " + (typeText.length() -1) + "; "
		// + "  Time period: " + (a-startTime)
		// ) ;
		count++;

	}

	private int[] setRandomPos(int tEdWidth, int tEdHeight, DraggableLabel l) {

		int[] pos = { 0, 0 };
		// System.out.println("Parent:" + parent.getElement().getClientWidth() + ", label: " + l.getElement().getClientWidth()); 
		pos[0] = randomNum(20, parent.getElement().getClientWidth()
				- l.getElement().getClientWidth());
		pos[1] = randomNum(20, parent.getElement().getClientHeight() - 50);

		while (pos[0] <= tEdWidth || pos[1] <= tEdHeight) {
			pos[0] = randomNum(20, parent.getElement().getClientWidth()
					- l.getElement().getClientWidth());
			pos[1] = randomNum(20, parent.getElement().getClientHeight() - 50);
		}

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
