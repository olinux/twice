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

/**
 * An example of the functionality for the simulation of multiple mouse pointers following predefined paths on the screen. This is useful for experimenting e.g.
 * with the influence of distraction which is introduced by multiple cursors on the usability of the overall system.
 * 
 * @author Oliver Schmid
 * 
 */
public class CursorSimulation {
	/**
	 * The counter of the currently added mouse pointers
	 */
	private static int counter = 0;
	/**
	 * Predefined set of points to which the simulated mouse pointers shall be moved to
	 */
	private static PredefinedLoopCursor[] loopCursors = new PredefinedLoopCursor[] {
			new PredefinedLoopCursor(0, 300, 300, m(100, 200, 2000), m(600, 500, 2000), m(200, 300, 1000)),
			new PredefinedLoopCursor(1, 200, 100, m(400, 500, 2000), m(100, 200, 2000), m(500, 100, 1000)) };

	/**
	 * Add the next mouse pointers to the application (as long as the limit of the {@link CursorSimulation#loopCursors}. The mouse pointer will immediately
	 * start to move and will move to the first coordinates as soon as it has reached the end of the predefined coordinates (looping)
	 */
	public static void addPredefinedLoopCursor() {
		if (loopCursors.length > counter) {
			PredefinedLoopCursor c = loopCursors[counter++];
			RootPanel.get().add(c);
			c.move();
		}
	}

	/**
	 * Factory method for a mouse movement
	 * 
	 * @param x
	 *            destination X-coordinate
	 * @param y
	 *            destination Y-coordinate
	 * @param duration
	 *            of the movement
	 * @return MouseMovement object
	 */
	public static MouseMovement m(int x, int y, int duration) {
		return new MouseMovement(x, y, duration, new Command() {

			@Override
			public void execute() {
				Window.alert("Movement done");
			}
		});
	}

	/**
	 * Adds a cursor that follows a random path on the screen and start the movement immediately.
	 */
	public static void addSimulatedCursor() {
		RandomCursor c = new RandomCursor(counter++, 1500 + Random.nextInt(1000), 300, 300);
		RootPanel.get().add(c);
		c.start();
	}

}
