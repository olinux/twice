package ch.unifr.pai.twice.dragndrop.client.intf;

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

import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.HasTouchEndHandlers;
import com.google.gwt.event.dom.client.HasTouchMoveHandlers;
import com.google.gwt.event.dom.client.HasTouchStartHandlers;

/**
 * An interface to be implemented by widgets which shall be draggable
 * 
 * @author Oliver Schmid
 * 
 */
public interface Draggable extends HasMouseOverHandlers, HasMouseDownHandlers, HasMouseUpHandlers, HasTouchStartHandlers, HasTouchEndHandlers,
		HasTouchMoveHandlers {

	/**
	 * @return true if the widget is draggable, false if it is not
	 */
	boolean isDraggable();
}
