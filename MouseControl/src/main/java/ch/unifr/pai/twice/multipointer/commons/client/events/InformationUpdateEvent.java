package ch.unifr.pai.twice.multipointer.commons.client.events;

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
import ch.unifr.pai.twice.comm.serverPush.client.DiscardingRemoteEvent;
import ch.unifr.pai.twice.comm.serverPush.client.RemoteEventHandler;
import ch.unifr.pai.twice.multipointer.commons.client.events.InformationUpdateEvent.Handler;

import com.google.gwt.core.client.GWT;

public abstract class InformationUpdateEvent extends DiscardingRemoteEvent<Handler> {

	public static final Type<Handler> TYPE = new Type<Handler>();

	public static interface Handler extends RemoteEventHandler<InformationUpdateEvent> {
	}

	public Integer width;
	public Integer height;
	public String color;
	public String lastAction;

	public static InformationUpdateEvent resize(int width, int height) {
		InformationUpdateEvent event = GWT.create(InformationUpdateEvent.class);
		event.width = width;
		event.height = height;
		return event;
	}

	public static InformationUpdateEvent changeColor(String color, String targetClientUUID) {
		InformationUpdateEvent event = GWT.create(InformationUpdateEvent.class);
		event.color = color;
		event.setReceipients(targetClientUUID);
		return event;
	}

	public static InformationUpdateEvent changeColorAndResize(String color, int width, int height, String targetClientUUID) {
		InformationUpdateEvent event = GWT.create(InformationUpdateEvent.class);
		event.color = color;
		event.width = width;
		event.height = height;
		event.setReceipients(targetClientUUID);
		return event;
	}

	public static InformationUpdateEvent lastAction(String lastAction, String targetClientUUID) {
		InformationUpdateEvent event = GWT.create(InformationUpdateEvent.class);
		event.lastAction = lastAction;
		event.setReceipients(targetClientUUID);
		return event;
	}

}
