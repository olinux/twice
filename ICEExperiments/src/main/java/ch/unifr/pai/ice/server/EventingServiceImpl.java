package ch.unifr.pai.ice.server;

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

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;

import ch.unifr.pai.ice.client.rpc.EventingService;
import ch.unifr.pai.ice.client.tracking.CursorXY;
import ch.unifr.pai.ice.client.utils.ResultAnalyzer;
import ch.unifr.pai.ice.shared.ExperimentIdentifier;
import ch.unifr.pai.twice.multipointer.commons.client.rpc.MouseControllerService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@WebServlet("/icetexteditgwt/logger")

public class EventingServiceImpl extends RemoteServiceServlet implements EventingService {

	private static final long serialVersionUID = 1L;
	Logger experimentLog = Logger.getLogger("experiment");
	
	// Logger log1 = Logger.getLogger("experiment1"); // Text Entry
	// Logger log2 = Logger.getLogger("experiment2"); // Text Entry
	// Logger log3 = Logger.getLogger("experiment3"); // Text Entry
	// Logger log4 = Logger.getLogger("experiment4"); // Check boxes
	// Logger log5 = Logger.getLogger("experiment5"); // Check boxes
	// Logger log6 = Logger.getLogger("experiment6"); // Check boxes
	// Logger log7 = Logger.getLogger("experiment7"); // DragNDrop
	// Logger log71 = Logger.getLogger("experiment71"); // DragNDrop
	// Logger log8 = Logger.getLogger("experiment8"); // DragNDrop
	// Logger log9 = Logger.getLogger("experiment9"); // DragNDrop
	// Logger log10 = Logger.getLogger("experiment10"); // Line Tracking
	// Logger log11 = Logger.getLogger("experiment11"); // Line Tracking
	// Logger log12 = Logger.getLogger("experiment12"); // Line Tracking
	// Logger log20 = Logger.getLogger("experiment20"); // Text Entry
	// Logger log21 = Logger.getLogger("experiment21"); // Text Entry

	TextToType textToType = new TextToType();

	Vector<Vector<String>> vectorOfUserInputVector;
	Vector<String> userInputVector;

	ResultAnalyzer resultAnalyser = new ResultAnalyzer(true);
	String deviceType = "keyboard";

	@Override
	public void log(String experimentId, String[] message, ExperimentIdentifier experimentName, int numberOfUsers) {
		// initText(text);
		for (String m : message) {
			experimentLog.info(experimentId + "; " + experimentName.name() + "; " + numberOfUsers + "; " + m); 
			
		}

		// // process the text input received from the user
		// // print the results
		// if (experimentName.startsWith("TextEntry")) {
		// for (String r : processTextInput(userInputVector, textToType)) {
		// experimentLog.info(experimentId+"; "+experimentName+"; "+r);
		// }
		// }
		// else if (experimentName.startsWith("LineTracking")){
		// Vector<CursorXY> vector1 = ArrayToVector(message);
		// ImageProcessor.setPixelColorFromImage(vector1, getServletContext()
		// .getRealPath("/")
		// + "icetexteditgwt"
		// + File.separator
		// + "experimentLineTracking.jpg");
		// int nWhite = 0;
		//
		// // calculation of the number of points out of the line
		// for (CursorXY c : vector1) {
		// experimentLog.info(experimentId+"; "+experimentName+"; "+c.getUser() + ";" + c.getX() + ";" + c.getY() + ";"
		// + c.getColor() + ";" + c.getTimeStamp());
		// if (c.getColor() == -1) {
		// nWhite++;
		// }
		// }
		//
		// experimentLog.info(experimentId+"; "+experimentName+"; errorRateInPct; "+String.valueOf(nWhite * 100 / vector1.size())
		// + "; "
		// + vector1.get(0).getUser());
		//
		// }

		// switch (experimentNumber) {
		// case 1:
		// System.out.println(getThreadLocalRequest().getServletPath());
		// log1.info("");
		// log1.info("Text Entry - 1 space - 1 users");
		// log1.info("Session of " + new Date());
		// log1.info("Device: " + deviceType);
		// log1.info("user; start time; stop time (ms); typed text; final text");
		// log1.info("-------------------------------------------------");
		//
		// for (String s : message) {
		// log1.info(s);
		// }
		// log1.info("-------------------------------------------------");
		//
		// vectorOfUserInputVector = separateUpTo4UserInput(message);
		// userInputVector = vectorOfUserInputVector.get(0);
		//
		// // process the text input received from the user
		// // print the results
		//
		// for (String r : processTextInput(userInputVector, textToType)) {
		// log1.info(r);
		// }
		// log1.info("-------------------------------------------------");
		// log1.info(" ");
		//
		// break;
		// case 2:
		// log2.info("");
		// log2.info("Text Entry - 1 space - 2 users");
		// log2.info("Session of " + new Date());
		// log2.info("Device: " + deviceType);
		// log2.info("user; start time; stop time (ms); typed text; final text");
		// log2.info("-------------------------------------------------");
		//
		// for (String s : message) {
		// log2.info(s);
		// }
		// log2.info("-------------------------------------------------");
		//
		// vectorOfUserInputVector = separateUpTo4UserInput(message);
		//
		// for (Vector<String> vInput : vectorOfUserInputVector) {
		// // process the text input received from the user
		// // print the results
		// log2.info("Result for "
		// + new StringTokenizer(vInput.elementAt(0))
		// .nextToken(";"));
		// for (String r : processTextInput(vInput, textToType)) {
		// log2.info(r);
		// }
		// log2.info("-------------------------------------------------");
		// log2.info(" ");
		// }
		// break;
		// case 3:
		// log3.info("");
		// log3.info("Text Entry - 1 space - 4 users");
		// log3.info("Session of " + new Date());
		// log3.info("Device: " + deviceType);
		// log3.info("user; start time; stop time (ms); typed text; final text");
		// log3.info("-------------------------------------------------");
		//
		// for (String s : message) {
		// log3.info(s);
		// }
		// log3.info("-------------------------------------------------");
		//
		// vectorOfUserInputVector = separateUpTo4UserInput(message);
		// for (Vector<String> vInput : vectorOfUserInputVector) {
		// // process the text input received from the user
		// // print the results
		// log3.info("Result for "
		// + new StringTokenizer(vInput.elementAt(0))
		// .nextToken(";"));
		// for (String r : processTextInput(vInput, textToType)) {
		// log3.info(r);
		// }
		// log3.info("-------------------------------------------------");
		// log3.info(" ");
		// }
		// break;
		//
		// case 4: {
//		 log4.info("Click blobs 1 users");
//		 log4.info("Session of " + new Date());
//		 log4.info("user; Xcoord; Ycoord; TimeStamp (ms)");
//		 log4.info("-------------------------------------------------");
//		
//		 for (String s : message) {
//		 log4.info(s);
//		 }
//		 log4.info("-------------------------------------------------");
		//
		// break;
		// }
		//
		// case 5:
//		 log5.info("Click blobs 2 users");
//		 log5.info("Session of " + new Date());
//		 log5.info("user; Xcoord; Ycoord; TimeStamp (ms)");
//		 log5.info("-------------------------------------------------");
//		
//		 for (String s : message) {
//		 log5.info(s);
//		 }
//		 log5.info("-------------------------------------------------");
//		
		// break;
		//
		// case 6:
		// log6.info("Click blobs 4 users");
		// log6.info("Session of " + new Date());
		// log6.info("user; Xcoord; Ycoord; TimeStamp (ms)");
		// log6.info("-------------------------------------------------");
		//
		// for (String s : message) {
		// log6.info(s);
		// }
		// log6.info("-------------------------------------------------");
		//
		// break;
		//
		// case 7:
		// log7.info("Drag N Drop 1 user - 1 space");
		// log7.info("Session of " + new Date());
		// log7.info("user; blob num; Time to drag and drop (ms)");
		// log7.info("-------------------------------------------------");
		//
		// for (String s : message) {
		// log7.info(s);
		// }
		// log7.info("-------------------------------------------------");
		// break;
		//
		// case 71:
		// log7.info("Drag N Drop 2 user2 - separated spaces");
		// log7.info("Session of " + new Date());
		// log7.info("user; blob num; Time to drag and drop (ms)");
		// log7.info("-------------------------------------------------");
		//
		// for (String s : message) {
		// log71.info(s);
		// }
		// log7.info("-------------------------------------------------");
		// break;
		//
		// case 8:
		// log8.info("Drag N Drop 4 users - separated spaces");
		// log8.info("Session of " + new Date());
		// log8.info("user; blob num; Time to drag and drop (ms)");
		// log8.info("-------------------------------------------------");
		//
		// for (String s : message) {
		// log8.info(s);
		// }
		// log8.info("-------------------------------------------------");
		// break;
		//
		// case 9:
		// log9.info("Drag N Drop 4 users - 1 drag space - 4 drop spaces");
		// log9.info("Session of " + new Date());
		// log9.info("user; Xcoord; Ycoord; TimeStamp (ms)");
		// log9.info("-------------------------------------------------");
		//
		// for (String s : message) {
		// log9.info(s);
		// }
		// log9.info("-------------------------------------------------");
		//
		// break;
		//
		// case 10:
		// Vector<CursorXY> vector1 = ArrayToVector(message);
		//
		// log10.info("Line tracking - 1 user");
		// log10.info("Session of " + new Date());
		// log10.info("Xcoord; Ycoord; color; TimeStamp (ms)");
		// log10.info("-------------------------------------------------");
		//
		// for (String s : message) {
		// log10.info(s);
		// }
		//
		// log10.info("-------------------------------------------------");
		//
		// ImageProcessor.setPixelColorFromImage(vector1, getServletContext()
		// .getRealPath("/")
		// + "icetexteditgwt"
		// + File.separator
		// + "black_line_2.jpg");
		// int nWhite = 0;
		//
		// // calculation of the number of points out of the line
		// for (CursorXY c : vector1) {
		// log11.info(c.getUser() + ";" + c.getX() + ";" + c.getY() + ";"
		// + c.getColor() + ";" + c.getTimeStamp());
		// if (c.getColor() == -1) {
		// nWhite++;
		// }
		// }
		//
		// log10.info("-------------------------------------------------");
		// log10.info(String.valueOf(nWhite * 100 / vector1.size())
		// + "% of error - point out of the line for "
		// + vector1.get(0).getUser());
		// log10.info("-------------------------------------------------");
		//
		// break;
		//
		// case 11:
		//
		// Vector<CursorXY> vector2 = ArrayToVector(message);
		//
		// log11.info("Line tracking - 2 users");
		// log11.info("Session of " + new Date());
		// log11.info("user; Xcoord; Ycoord; color, TimeStamp");
		// log11.info("-------------------------------------------------");
		//
		// ImageProcessor.setPixelColorFromImage(vector2, imagePath2);
		//
		// int nWhite2 = 0;
		//
		// // calculation of the number of points out of the line
		// for (CursorXY c : vector2) {
		// log11.info(c.getUser() + ";" + c.getX() + ";" + c.getY() + ";"
		// + c.getColor() + ";" + c.getTimeStamp());
		// if (c.getColor() == -1) {
		// nWhite2++;
		// }
		// }
		//
		// log11.info("-------------------------------------------------");
		// log11.info(String.valueOf(nWhite2 * 100 / vector2.size())
		// + "% of error - point out of the line for "
		// + vector2.get(0).getUser());
		// log11.info("-------------------------------------------------");
		//
		// break;
		//
		// case 12:
		//
		// Vector<CursorXY> vector3 = ArrayToVector(message);
		//
		// log12.info("Line tracking - 4 users");
		// log12.info("Session of " + new Date());
		// log12.info("user; Xcoord; Ycoord; TimeStamp");
		// log12.info("-------------------------------------------------");
		//
		// ImageProcessor.setPixelColorFromImage(vector3, imagePath3);
		//
		// int nWhite3 = 0;
		//
		// // calculation of the number of points out of the line
		// for (CursorXY c : vector3) {
		// log12.info(c.getUser() + ";" + c.getX() + ";" + c.getY() + ";"
		// + c.getColor() + ";" + c.getTimeStamp());
		// if (c.getColor() == -1) {
		// nWhite3++;
		// }
		// }
		//
		// log12.info("-------------------------------------------------");
		// log12.info(String.valueOf(nWhite3 * 100 / vector3.size())
		// + "% of error - point out of the line for "
		// + vector3.get(0).getUser());
		// log12.info("-------------------------------------------------");
		//
		// break;
		//
		// case 20:
		//
		// log20.info("");
		// log20.info("Text Entry - sep space - 2 users");
		// log20.info("Session of " + new Date());
		// log20.info("Device: " + deviceType);
		// log20.info("user; start time; stop time (ms); typed text; final text");
		// log20.info("-------------------------------------------------");
		//
		// for (String s : message) {
		// log20.info(s);
		// }
		// log20.info("-------------------------------------------------");
		//
		// vectorOfUserInputVector = separateUpTo4UserInput(message);
		// for (Vector<String> vInput : vectorOfUserInputVector) {
		// // process the text input received from the user
		// // print the results
		// log20.info("Result for "
		// + new StringTokenizer(vInput.elementAt(0))
		// .nextToken(";"));
		// for (String r : processTextInput(vInput, textToType)) {
		// log20.info(r);
		// }
		// log20.info("-------------------------------------------------");
		// log20.info(" ");
		// }
		// break;
		//
		// case 21:
		// log21.info("");
		// log21.info("Text Entry - sep space - 4 users");
		// log21.info("Session of " + new Date());
		// log21.info("Device: " + deviceType);
		// log21.info("user; start time; stop time (ms); typed text; final text");
		// log21.info("-------------------------------------------------");
		//
		// for (String s : message) {
		// log21.info(s);
		// }
		// log21.info("-------------------------------------------------");
		//
		// vectorOfUserInputVector = separateUpTo4UserInput(message);
		// for (Vector<String> vInput : vectorOfUserInputVector) {
		// // process the text input received from the user
		// // print the results
		// log21.info("Result for "
		// + new StringTokenizer(vInput.elementAt(0))
		// .nextToken(";"));
		// for (String r : processTextInput(vInput, textToType)) {
		// log21.info(r);
		// }
		// log21.info("-------------------------------------------------");
		// log21.info(" ");
		// }
		// break;
		//
		// default:
		//
		// }
	}

	/********************************************************************************************************
	 * Util Methods to process the diff. logs
	 ********************************************************************************************************/

	/**************************************************************************************
	 * Array to Vector : convert Array sent from the client to vector of cursor position
	 **************************************************************************************/
	private Vector<CursorXY> ArrayToVector(String[] array) {

		Vector<CursorXY> vector = new Vector<CursorXY>();
		CursorXY c;

		StringTokenizer st;

		for (String s : array) {
			System.out.println(s);

			st = new StringTokenizer(s);
			c = new CursorXY();

			c.setUser(st.nextToken());
			System.out.println(c.getUser());

			c.setX(Integer.parseInt(st.nextToken()));
			System.out.println(c.getX());

			c.setY(Integer.parseInt(st.nextToken()));
			System.out.println(c.getY());

			c.setTimeStamp(Long.parseLong(st.nextToken()));
			System.out.println(c.getTimeStamp());

			vector.add(c);

		}
		return vector;
	}

	private void initText(String text) {
		textToType.setTextToType(text);
	}

	private Vector<Vector<String>> separateUpTo4UserInput(String[] a) {

		StringTokenizer st;
		Vector<Vector<String>> vectorOfUserInput = new Vector<Vector<String>>();
		Vector<String> stVector = null;
		boolean firstRecord = true;
		String token;
		String previousToken = "user1";

		for (String s : a) {

			st = new StringTokenizer(s);
			token = st.nextToken(";");

			if (token.equals("user1")) {
				if (firstRecord) {
					stVector = new Vector<String>();
					vectorOfUserInput.add(stVector);
					firstRecord = false;
				}
				stVector.add(s);
				previousToken = token;

			}
			else if (token.equals("user2")) {

				if (!previousToken.equals(token)) {
					firstRecord = true;
				}

				if (firstRecord) {
					stVector = new Vector<String>();
					vectorOfUserInput.add(stVector);
					firstRecord = false;
				}
				stVector.add(s);
				previousToken = token;

			}
			else if (token.equals("user3")) {
				if (!previousToken.equals(token)) {
					firstRecord = true;
				}

				if (firstRecord) {
					stVector = new Vector<String>();
					vectorOfUserInput.add(stVector);
					firstRecord = false;
				}
				stVector.add(s);
				previousToken = token;

			}
			else if (token.equals("user4")) {
				if (!previousToken.equals(token)) {
					firstRecord = true;
				}

				if (firstRecord) {
					stVector = new Vector<String>();
					vectorOfUserInput.add(stVector);
					firstRecord = false;
				}
				stVector.add(s);
				previousToken = token;

			}

		}

		return vectorOfUserInput;
	}

	/**
	 * Process the text typed by the user
	 * 
	 * @param array
	 *            : array of typed sentences
	 * @param t
	 *            : Ref. text
	 * @return String[] of results (% accuracy, etc...)
	 */
	private String[] processTextInput(Vector<String> vector, TextToType t) {

		String tTyped = "";
		String sentence = "";
		StringTokenizer st;
		String[] result;

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < vector.size(); i++) {
			sentence = t.getSentence(i);

			tTyped = vector.get(i);
			st = new StringTokenizer(tTyped);
			// dirty!!!!
			st.nextToken(";"); // get user num.
			st.nextToken(";"); // get startTime.
			st.nextToken(";"); // get stopTime.
			sb.append(st.nextToken());

			// resultAnalyser.analyze(sentence, st.nextToken());
		}
		resultAnalyser.analyze(sentence, sb.toString());
		result = resultAnalyser.showResults();
		resultAnalyser.reset();
		return result;
	}
}
