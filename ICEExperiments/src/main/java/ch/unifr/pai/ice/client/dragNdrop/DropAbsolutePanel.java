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

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class DropAbsolutePanel extends AbsolutePanel {

	int panelID;

	public DropAbsolutePanel() {
		super();

	}

	public DropAbsolutePanel(int panelID) {
		super();
		this.panelID = panelID;

	}

	public DropAbsolutePanel(Element elem) {
		super(elem);
		// TODO Auto-generated constructor stub
	}

	public int getPanelID() {
		return panelID;
	}

	public void setPanelID(int panelID) {
		this.panelID = panelID;
	}

}
