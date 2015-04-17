package ch.unifr.pai.ice.server.linetracking.processing;

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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Vector;

import javax.imageio.ImageIO;

import ch.unifr.pai.ice.client.tracking.CursorXY;

public class ImageProcessor {

	static Vector<CursorXY> vector;
	static BufferedImage bufferedImage;
	int whiteColor = -1;
	static File inputFile;

	public ImageProcessor() {

	}

	public static void setPixelColorFromImage(Vector<CursorXY> xyVector, String imageName) {

		vector = xyVector;
		inputFile = new File(imageName);

		try {
			bufferedImage = ImageIO.read(inputFile);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (CursorXY c : vector) {
			if ((c.getX() >= 0 && c.getX() < bufferedImage.getWidth()) && (c.getY() >= 0 && c.getY() < bufferedImage.getHeight()))
				c.setColor(bufferedImage.getRGB(c.getX(), c.getY()));
			else {
				c.setColor(-1);
				System.out.println("out of line!! ");
			}
		}

	}

	public static void setPixelColorFromImage(Vector<CursorXY> xyVector, URI imageName) {

		vector = xyVector;
		inputFile = new File(imageName);

		try {
			bufferedImage = ImageIO.read(inputFile);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (CursorXY c : vector) {
			if ((c.getX() >= 0 && c.getX() < bufferedImage.getWidth()) && (c.getY() >= 0 && c.getY() < bufferedImage.getHeight()))
				c.setColor(bufferedImage.getRGB(c.getX(), c.getY()));
			else {
				c.setColor(-1);
				System.out.println("out of line!! ");
			}
		}

	}

}
