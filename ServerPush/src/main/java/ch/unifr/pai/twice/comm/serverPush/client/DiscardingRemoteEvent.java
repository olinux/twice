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

/**
 * A discarding remote event. This type of event is very responsive since a newer event always replaces the previous event fully (e.g. mouse pointer position).
 * If such an event is delayed, it can therefore simply be ignored because a newer event has already been processed.
 * 
 * @author Oliver Schmid
 * 
 * @param <H>
 */
public abstract class DiscardingRemoteEvent<H extends RemoteEventHandler<?>> extends RemoteEvent<H> {

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.comm.serverPush.client.RemoteEvent#isBlocking()
	 */
	@Override
	public boolean isBlocking() {
		return false;
	}

	/**
	 * @param id
	 */
	public void setInstanceId(String id) {
		setProperty("instanceid", id);
	}

	/**
	 * @return
	 */
	public String getInstanceId() {
		return getProperty("instanceid");
	}

}
