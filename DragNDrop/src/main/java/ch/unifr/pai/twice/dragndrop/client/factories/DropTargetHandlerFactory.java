package ch.unifr.pai.twice.dragndrop.client.factories;

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

import ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler;
import ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandlerAdapter;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * A factory providing typical drop target configurations
 * 
 * @author Oliver Schmid
 * 
 */
public class DropTargetHandlerFactory {

	/**
	 * Priorities of drop target (if two drop targets are intersected, the higher target will be marked as the drop target even if the percentage of
	 * intersection is smaller than with the lower priority drop target)
	 * 
	 * @author Oliver Schmid
	 * 
	 */
	public enum Priority {
		LOW(1), NORMAL(2), HIGH(3);

		private final int value;

		private Priority(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	/**
	 * The threshold percentage which is regarded as a "complete intersection"
	 */
	public final static int THRESHOLD_PERCENTAGE = 99;

	/**
	 * @param com
	 *            - the {@link Command} which is executed only if the dragged widget is intersecting fully with the drop target
	 * @param resetPosition
	 * @return a {@link DropTargetHandler}
	 */
	public static DropTargetHandler completeIntersection(final Command com, final boolean resetPosition) {
		return new DropTargetHandlerAdapter() {
			@Override
			public boolean onDrop(String deviceId, Widget widget, Element dragProxy, Event event, Double intersectionPercentage,
					Double intersectionPercentageWithTarget) {
				if (intersectionPercentage > THRESHOLD_PERCENTAGE)
					com.execute();
				return !resetPosition;
			}
		};
	}

	/**
	 * A {@link DropTargetHandler} that executes the given command even if it is intersected only partially by the dragged widget
	 * 
	 * @param com
	 * @param resetPosition
	 * @return
	 */
	public static DropTargetHandler incompleteIntersection(final Command com, final boolean resetPosition) {
		return new DropTargetHandlerAdapter() {
			@Override
			public boolean onDrop(String deviceId, Widget widget, Element dragProxy, Event event, Double intersectionPercentage,
					Double intersectionPercentageWithTarget) {
				com.execute();
				return !resetPosition;
			}
		};
	}

	/**
	 * @return a {@link DropTargetHandler} that rejects the drop if the dragged widget is not fully intersecting with the drop target
	 */
	public static DropTargetHandler rejectIfNotFullyIntersecting() {
		return new DropTargetHandlerAdapter() {

			@Override
			public boolean onDrop(String deviceId, Widget widget, Element dragProxy, Event event, Double intersectionPercentage,
					Double intersectionPercentageWithTarget) {
				return intersectionPercentage > THRESHOLD_PERCENTAGE;
			}

		};

	}
}
