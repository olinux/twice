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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;

public class Cursor extends FlowPanel{

	public Cursor(int index, int startX, int startY){
			setStyleName("fakedCursor");
			getElement().setId("cursor" + index);
			getElement().getStyle().setLeft(startX, Unit.PX);
			getElement().getStyle().setTop(startY, Unit.PX);
	}
	
	public void move(int nextX, int nextY, int nextDuration, Command callback) {
		new MoveAnimation(Cursor.this.getElement(), nextX, nextY, callback).run(nextDuration);
	}
	
	public class MoveAnimation extends Animation {
		private final Element element;
		private int startX;
		private int startY;
		private int finalX;
		private int finalY;
		private Command callback;

		public MoveAnimation(Element element, int finalX, int finalY, Command callback) {
			this.element = element;
			this.callback = callback;
			this.startX = element.getAbsoluteLeft();
			this.startY = element.getAbsoluteTop();
			this.finalX = finalX;
			this.finalY = finalY;
		}

		@Override
		protected void onUpdate(double progress) {
			double positionX = startX + (progress * (this.finalX - startX));
			double positionY = startY + (progress * (this.finalY - startY));

			this.element.getStyle().setLeft(positionX, Style.Unit.PX);
			this.element.getStyle().setTop(positionY, Style.Unit.PX);
		}

		@Override
		protected void onComplete() {
			super.onComplete();
			this.element.getStyle().setLeft(this.finalX, Style.Unit.PX);
			this.element.getStyle().setTop(this.finalY, Style.Unit.PX);
			if(callback!=null)
				callback.execute();
		}
	}
	
}
