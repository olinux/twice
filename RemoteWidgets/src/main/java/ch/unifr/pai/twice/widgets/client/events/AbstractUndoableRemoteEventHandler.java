package ch.unifr.pai.twice.widgets.client.events;
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
import ch.unifr.pai.twice.comm.serverPush.client.UndoableRemoteEvent;
import ch.unifr.pai.twice.comm.serverPush.client.UndoableRemoteEventHandler;

import com.google.gwt.user.client.ui.HasValue;

public abstract class AbstractUndoableRemoteEventHandler<E extends UndoableRemoteEvent<?>>
		implements UndoableRemoteEventHandler<E> {
	private Object source;

	public AbstractUndoableRemoteEventHandler(Object source) {
		this.source = source;
	}

	@Override
	public void undo(E event) {
		if(source instanceof HasValue)
			((HasValue<Object>)source).setValue(event.getStorageProperty("oldvalue"));
	}

	@Override
	public void saveState(E event) {
		if(source instanceof HasValue)
			event.setStorageProperty("oldvalue", ((HasValue<Object>)source).getValue());
	}

}
