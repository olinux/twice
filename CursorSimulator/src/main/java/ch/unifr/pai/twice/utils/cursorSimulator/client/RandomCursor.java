package ch.unifr.pai.twice.utils.cursorSimulator.client;

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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;

/**
 * This cursor moves by completely random values within the current screen size.
 * 
 * @author oli
 * 
 */
public class RandomCursor extends Cursor {

	/**
	 * The interval in ms in which a new random position shall be reached
	 */
	private final int interval;
	/**
	 * A boolean flag defining if the movement is currently stopped
	 */
	private boolean stopped;

	/**
	 * @param index
	 *            - @see {@link Cursor#Cursor(int, int, int)}
	 * @param interval
	 *            - the interval in ms in which a new random position shall be reached
	 * @param startX
	 *            - @see {@link Cursor#Cursor(int, int, int)}
	 * @param startY
	 *            - @see {@link Cursor#Cursor(int, int, int)}
	 */
	public RandomCursor(int index, int interval, int startX, int startY) {
		super(index, startX, startY);
		this.interval = interval;
	}

	/**
	 * Move the mouse pointer randomly (the mouse pointer does not move every time but decides on if to move within this interval depending on a random boolean
	 * choice as well.
	 */
	private void move() {
		int newX;
		int newY;
		if (Random.nextBoolean()) {
			newX = Random.nextInt(Window.getClientWidth() - RandomCursor.this.getOffsetWidth());
			newY = Random.nextInt(Window.getClientHeight() - RandomCursor.this.getOffsetHeight());
		}
		else {
			newX = RandomCursor.this.getAbsoluteLeft();
			newY = RandomCursor.this.getAbsoluteTop();
		}
		move(newX, newY, interval, moveCallback);
	}

	/**
	 * Lets the movement stop
	 */
	public void stop() {
		stopped = true;
	}

	/**
	 * Start the movement
	 */
	public void start() {
		stopped = false;
		move();
	}

	/**
	 * The callback executed after every movement letting the mouse pointer move on as long as it is not stopped
	 */
	private final Command moveCallback = new Command() {
		@Override
		public void execute() {
			if (!stopped) {
				int newX = Random.nextInt(Window.getClientWidth() - RandomCursor.this.getOffsetWidth());
				int newY = Random.nextInt(Window.getClientHeight() - RandomCursor.this.getOffsetHeight());

				if (Random.nextBoolean())
					move(newX, newY, interval, moveCallback);
				else
					moveCallback.execute();
			}
		}
	};

}
