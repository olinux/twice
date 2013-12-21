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

public class TextToType {

	Vector<String> textVector;

	public TextToType() {
		textVector = new Vector<String>();
	}

	public void setTextToType(String text) {
		StringTokenizer st = new StringTokenizer(text);

		while (st.hasMoreTokens()) {
			textVector.add(st.nextToken("."));
		}

		for (String s : textVector) {
			System.out.println(s);
		}
	}

	public String getSentence(int index) {
		return textVector.elementAt(index);
	}

	public int getNumberOfSentences() {
		return textVector.size();
	}

}
