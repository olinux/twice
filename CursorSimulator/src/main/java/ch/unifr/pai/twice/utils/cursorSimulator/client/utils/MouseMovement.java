package ch.unifr.pai.twice.utils.cursorSimulator.client.utils;

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

/**
 * A class defining an atomic mouse movement by the definition of x-coordinate, y-coordinate as well as the duration of the callback. Additionally, a callback
 * can be defined which is fired after the movement has been executed.
 * 
 * @author Oliver Schmid
 * 
 */
public class MouseMovement {

	private final int x;
	private final int y;
	private final int duration;
	private final Command callback;

	/**
	 * @param x
	 *            - the X-coordinate
	 * @param y
	 *            - the Y-coordinate
	 * @param duration
	 *            - the duration of the movementRandomCursor (in ms)
	 */
	public MouseMovement(int x, int y, int duration) {
		this(x, y, duration, null);
	}

	/**
	 * @param x
	 *            - the X-coordinate
	 * @param y
	 *            - the Y-coordinate
	 * @param duration
	 *            - the duration of the movement (in ms)
	 * @param callback
	 *            - the command that shall be executed when the movement has been done.
	 */
	public MouseMovement(int x, int y, int duration, Command callback) {
		super();
		this.x = x;
		this.y = y;
		this.duration = duration;
		this.callback = callback;
	}

	/**
	 * @return the X-coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the Y-coordinate
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return the duration of the movement (in ms)
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * @return the command executed after the movement is done.
	 */
	public Command getCallback() {
		return callback;
	}

}
