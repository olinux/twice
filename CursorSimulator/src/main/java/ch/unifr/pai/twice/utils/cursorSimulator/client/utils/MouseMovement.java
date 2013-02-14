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

public class MouseMovement {

	private final int x;
	private final int y;
	private final int duration;
	private final Command callback;
	
	public MouseMovement(int x, int y, int duration){
		this(x, y, duration, null);
	}
	
	
	public MouseMovement(int x, int y, int duration, Command callback) {
		super();
		this.x = x;
		this.y = y;
		this.duration = duration;
		this.callback = callback;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getDuration() {
		return duration;
	}

	public Command getCallback() {
		return callback;
	}
	
	
	
}
