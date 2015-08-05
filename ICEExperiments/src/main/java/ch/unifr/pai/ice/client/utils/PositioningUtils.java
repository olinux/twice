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
public class PositioningUtils {
	
	public static int[][] getPositionsInCircle(int nbElements, int xCenter, int yCenter, int radius) {

		int nbPos = (nbElements == 0) ? 2 : nbElements; 
		// check if nbPos is even
		nbPos = (nbPos % 2 == 0) ? nbPos : nbPos + 1; //nbPos is always even

		int[][] blobPos = new int[nbPos][2];
		int dAngle = 360 / nbPos;
		double angle = 0;
		double x = 0;
		double y = 0;

		// set the 2 first pos
		blobPos[0][0] = xCenter;
		blobPos[0][1] = yCenter - radius;

		blobPos[1][0] = xCenter;
		blobPos[1][1] = yCenter + radius;

		for (int i = 2; i < nbPos; i = i + 2) {
			angle = angle + dAngle;  
			if (angle < 90) {

				x = radius * Math.sin(Math.toRadians(angle));
				y = radius * Math.cos(Math.toRadians(angle));

				blobPos[i][0] = (int) (xCenter + x); 
				blobPos[i][1] = (int) (yCenter - y); 
				blobPos[i + 1][0] = (int) (xCenter - x);
				blobPos[i + 1][1] = (int) (yCenter + y);

			}
			else if (angle == 90) {

				blobPos[i][0] = xCenter + radius;
				blobPos[i][1] = (yCenter);
				blobPos[i + 1][0] = xCenter - radius;
				blobPos[i + 1][1] = (yCenter);

			}
			else if (angle > 90) {
				
				x = radius * Math.cos(Math.toRadians(angle - 90));
				y = radius * Math.sin(Math.toRadians(angle - 90));

				blobPos[i][0] = (int) (xCenter + x);
				blobPos[i][1] = (int) (yCenter + y);
				blobPos[i + 1][0] = (int) (xCenter - x);
				blobPos[i + 1][1] = (int) (yCenter - y);
			}
		}

		return blobPos;
	}
	
	
}
