package ch.unifr.pai.twice.multipointer.provider.client;

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

import ch.unifr.pai.twice.multipointer.provider.client.MouseCursorTimeoutEvent.Handler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An event notifying about timed out mouse pointers
 * 
 * @author Oliver Schmid
 * 
 */
public class MouseCursorTimeoutEvent extends GwtEvent<Handler> {

	boolean detached;

	/**
	 * @return true if the cursor is detached from a uuid and therefore free to be used by another user or false if only the visibility timed out and the cursor
	 *         is still assigned to the user.
	 */
	public boolean isDetached() {
		return detached;
	}

	public static interface Handler extends EventHandler {
		public void onMouseCursorTimeout(MouseCursorTimeoutEvent event);
	}

	public static final Type<Handler> TYPE = new Type<Handler>();

	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onMouseCursorTimeout(this);
	}
}
