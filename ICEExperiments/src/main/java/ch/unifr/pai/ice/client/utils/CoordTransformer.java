package ch.unifr.pai.ice.client.utils;
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

public final class CoordTransformer {

	static int[][] coordA;

	private CoordTransformer() {

	}

	/**
	 * Vertically Transpose the coordinates of an array of positions 
	 * @param coordArray
	 * @param maxX
	 * @param maxY
	 * @return array[][] of coordinates 
	 */
	public static int[][] vertical(int[][] coordArray, int maxX, int maxY) {

		int[][] coordA = new int[10][2];


		for (int i = 0; i < coordA.length; i++) {
			coordA[i][0] = coordArray[i][0];
			coordA[i][1] = maxY - coordArray[i][1];
		}

		return coordA;
	}

	
	/**
	 * Horizontally transpose the coordinates of an array of positions
	 * @param coordArray
	 * @param maxX
	 * @param maxY
	 * @return array[][] of coordinates 
	 */
	public static int[][] horizontal(int[][] coordArray, int maxX, int maxY) {

		int[][] coordA = new int[10][2];


		for (int i = 0; i < coordA.length; i++) {
			coordA[i][0] = maxX - coordArray[i][0];
			coordA[i][1] = coordArray[i][1];
		}

		return coordA;
	}

	
	/**
	 * Vertically and Horizontally transpose the coordinates of an array of positions
	 * @param coordArray
	 * @param maxX
	 * @param maxY
	 * @return array[][] of coordinates 
	 */
	public static int[][] verticalAndHorizontal(int[][] coordArray, int maxX, int maxY) {

		int[][] coordA = new int[10][2];


		for (int i = 0; i < coordA.length; i++) {
			coordA[i][0] = maxX - coordArray[i][0];
			coordA[i][1] = maxY - coordArray[i][1];
		}

		return coordA;
	}

	public static int[][] horizontalRight(int[][] coordArray, int maxX, int maxY) {

		return null;
	}

}
