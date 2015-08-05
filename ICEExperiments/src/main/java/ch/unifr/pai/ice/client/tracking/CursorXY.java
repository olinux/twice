package ch.unifr.pai.ice.client.tracking;

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
 * Project ICE ----------- PAI research Group - Dept. of Informatics University of Fribourg - Switzerland Author: Pascal Bruegger
 * 
 *************************************************************************************************/

public class CursorXY {

	int x;
	int y;
	long timeStamp;
	String user;
	int color;
	
	int blobNumber ; 

	public CursorXY() {

	}

	public CursorXY(String user, int x, int y, long timeStamp, int blobNumber) {
		this.user = user;
		this.x = x;
		this.y = y;
		this.timeStamp = timeStamp;
		this.blobNumber = blobNumber; 
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public int getblobNumber() {  
		return blobNumber;}
	
	public void setblobNumber(int num) {  
		this.blobNumber = num;
	}
	
	
	

}
