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

import ch.unifr.pai.twice.dragndrop.client.intf.DragNDropHandler;
import ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * A factory for {@link DragNDropHandler}
 * 
 * @author Oliver Schmid
 * 
 */
public class DropHandlerFactory {

	private static DragNDropHandler defaultDropHandler;
	private static DragNDropHandler resetDropHandler;
	private static DragNDropHandler resetWhenNotOnDropArea;

	/**
	 * @return a {@link DragNDropHandler} that resets the position of a dragged widget if it is not released on top of a drop target
	 */
	public static DragNDropHandler resetWhenNotOnDropArea() {
		if (resetWhenNotOnDropArea == null)
			resetWhenNotOnDropArea = new DragNDropHandler() {
				@Override
				public boolean onDrop(String deviceId, Widget w, int dragProxyLeft, int dragProxyTop, Event event, DropTargetHandler dropTarget,
						boolean outOfBox) {
					return dropTarget != null;
				}

				@Override
				public void onEndOfDrop(String deviceId, Widget draggedWidget, int dragProxyLeft, int dragProxyTop, Event event) {
				}

				@Override
				public void onStartDrag(String deviceId, Widget draggedWidget) {

				}
			};
		return resetWhenNotOnDropArea;
	}

	/**
	 * @return a {@link DragNDropHandler} which accepts any drops and therefore positions the dragged widget where it has been dropped (unless a drop target
	 *         does not override this function)
	 */
	public static DragNDropHandler defaultHandler() {
		if (defaultDropHandler == null)
			defaultDropHandler = new DragNDropHandler() {
				@Override
				public boolean onDrop(String deviceId, Widget w, int dragProxyLeft, int dragProxyTop, Event event, DropTargetHandler dropTarget,
						boolean outOfBox) {
					return true;
				}

				@Override
				public void onEndOfDrop(String deviceId, Widget draggedWidget, int dragProxyLeft, int dragProxyTop, Event event) {
				}

				@Override
				public void onStartDrag(String deviceId, Widget draggedWidget) {

				}
			};
		return defaultDropHandler;
	}

	/**
	 * @return resets the position if it has been dropped outside of the boundary-box
	 */
	public static DragNDropHandler resetPositionOnOutOfBox() {
		if (resetDropHandler == null)
			resetDropHandler = new DragNDropHandler() {
				@Override
				public boolean onDrop(String deviceId, Widget w, int dragProxyLeft, int dragProxyTop, Event event, DropTargetHandler dropTarget,
						boolean outOfBox) {
					return !outOfBox;
				}

				@Override
				public void onEndOfDrop(String deviceId, Widget draggedWidget, int dragProxyLeft, int dragProxyTop, Event event) {
				}

				@Override
				public void onStartDrag(String deviceId, Widget draggedWidget) {

				}
			};
		return resetDropHandler;
	}
}
