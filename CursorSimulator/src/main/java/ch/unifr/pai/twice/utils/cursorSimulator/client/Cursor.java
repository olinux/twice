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

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * The representational widget of a MousePointer provided as a {@link FlowPanel} resulting in an absolutely positioned "div" element including appropriate style
 * sheets
 * 
 * 
 * @author Oliver Schmid
 * 
 */
public class Cursor extends FlowPanel {

	/**
	 * @param index
	 *            the identifier of the mouse pointer (unique if multiple pointers are used)
	 * @param startX
	 *            the initial X-coordinate
	 * @param startY
	 *            the initial Y-coordinate
	 */
	public Cursor(int index, int startX, int startY) {
		setStyleName("fakedCursor");
		getElement().setId("cursor" + index);
		getElement().getStyle().setLeft(startX, Unit.PX);
		getElement().getStyle().setTop(startY, Unit.PX);
	}

	/**
	 * Moves the mouse pointer to a new position
	 * 
	 * @param nextX
	 *            - the target X-coordinate
	 * @param nextY
	 *            - the target Y-coordinate
	 * @param nextDuration
	 *            - the duration to be used to move to the new position
	 * @param callback
	 *            - the command which shall be executed after the movement is done
	 */
	public void move(int nextX, int nextY, int nextDuration, Command callback) {
		new MoveAnimation(Cursor.this.getElement(), nextX, nextY, callback).run(nextDuration);
	}

	/**
	 * The actual move animation which allows the pointer to move continuously to its new position
	 * 
	 * @author Oliver Schmid
	 * 
	 */
	public class MoveAnimation extends Animation {
		/**
		 * The HTML element of the mouse pointer (the "div" element)
		 */
		private final Element element;
		/**
		 * The X-coordinate from where the animation starts
		 */
		private final int startX;
		/**
		 * The Y-coordinate from where the animation starts
		 */
		private final int startY;
		/**
		 * The X-coordinate where the animation ends.
		 */
		private final int finalX;
		/**
		 * The Y-coordinate where the animation ends
		 */
		private final int finalY;
		/**
		 * The command that shall be executed when the movement is done.
		 */
		private final Command callback;

		/**
		 * A move animation
		 * 
		 * @param element
		 *            - the "div" HTML-element of the mouse pointer that shall be moved
		 * @param finalX
		 *            - the X-coordinate where the animation ends
		 * @param finalY
		 *            - the Y-coordinate where the animation ends
		 * @param callback
		 *            - the command that shall be executed when the movement is done.
		 */
		public MoveAnimation(Element element, int finalX, int finalY, Command callback) {
			this.element = element;
			this.callback = callback;
			this.startX = element.getAbsoluteLeft();
			this.startY = element.getAbsoluteTop();
			this.finalX = finalX;
			this.finalY = finalY;
		}

		/**
		 * Updates the position of the mouse pointer "div"-HTML element
		 * 
		 * @see com.google.gwt.animation.client.Animation#onUpdate(double)
		 */
		@Override
		protected void onUpdate(double progress) {
			double positionX = startX + (progress * (this.finalX - startX));
			double positionY = startY + (progress * (this.finalY - startY));

			this.element.getStyle().setLeft(positionX, Style.Unit.PX);
			this.element.getStyle().setTop(positionY, Style.Unit.PX);
		}

		/**
		 * Positions the mouse pointer at its final coordinates and executes the callback command if defined.
		 * 
		 * @see com.google.gwt.animation.client.Animation#onComplete()
		 */
		@Override
		protected void onComplete() {
			super.onComplete();
			this.element.getStyle().setLeft(this.finalX, Style.Unit.PX);
			this.element.getStyle().setTop(this.finalY, Style.Unit.PX);
			if (callback != null)
				callback.execute();
		}
	}

}
