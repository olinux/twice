package ch.unifr.pai.twice.utils.cursorSimulator.client.examples;

/*
 * Copyright 2013 Oliver Schmid
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

import ch.unifr.pai.twice.utils.cursorSimulator.client.PredefinedLoopCursor;
import ch.unifr.pai.twice.utils.cursorSimulator.client.RandomCursor;
import ch.unifr.pai.twice.utils.cursorSimulator.client.utils.MouseMovement;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class CursorSimulation {
	private static int counter = 0;
	private static PredefinedLoopCursor[] loopCursors = new PredefinedLoopCursor[]{
		new PredefinedLoopCursor(0, 300, 300, m(100, 200, 2000), m(600, 500, 2000), m(200, 300, 1000)),
		new PredefinedLoopCursor(1, 200, 100, m(400, 500, 2000), m(100, 200, 2000), m(500, 100, 1000))		
	};
	
	public static void addPredefinedLoopCursor(){
		if(loopCursors.length>counter){
			PredefinedLoopCursor c = loopCursors[counter++];
			RootPanel.get().add(c);
			c.move();			
		}	
	}
	
	public static MouseMovement m(int x, int y, int duration){
		return new MouseMovement(x, y, duration, new Command(){

			@Override
			public void execute() {
				Window.alert("Movement done");
			}});
	}
	
	
	public static void addSimulatedCursor(){
		RandomCursor c = new RandomCursor(counter++, 1500+Random.nextInt(1000), 300, 300);
		RootPanel.get().add(c);
		c.start();
	}
	
}
