package ch.unifr.pai.twice.comm.serverPush.client;

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
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;

/**
 * A special type of event which includes additional undo functionality to establish optimistic concurrency control. Do instantiate this class directly through
 * the {@link GWT#create(Class)} method.
 * 
 * @author Oliver Schmid
 * 
 * @param <H>
 */
public abstract class UndoableRemoteEvent<H extends UndoableRemoteEventHandler<?>> extends RemoteEvent<H> {

	private boolean undo;

	private final Map<String, Object> stateStorage = new HashMap<String, Object>();

	public boolean isUndo() {
		return undo;
	}

	public void setUndo(boolean undo) {
		this.undo = undo;
	}

	public <T> void setStorageProperty(String key, T value) {
		stateStorage.put(key, value);
	}

	public <T> T getStorageProperty(String key) {
		return (T) stateStorage.get(key);
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

}
