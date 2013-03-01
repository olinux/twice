package ch.unifr.pai.mindmap.client.rpc;

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
import ch.unifr.pai.mindmap.client.rpc.CreateMindmapNoteEvent.CreateMindmapNoteHandler;
import ch.unifr.pai.twice.comm.serverPush.client.UndoableRemoteEvent;
import ch.unifr.pai.twice.comm.serverPush.client.UndoableRemoteEventHandler;

import com.google.gwt.core.client.GWT;

/**
 * An application specific remote event which provides the information about a newly created note within the mindmap session
 * 
 * @author Oliver Schmid
 * 
 */
public abstract class CreateMindmapNoteEvent extends UndoableRemoteEvent<CreateMindmapNoteHandler> {

	public static final Type<CreateMindmapNoteHandler> TYPE = new Type<CreateMindmapNoteHandler>();

	public static interface CreateMindmapNoteHandler extends UndoableRemoteEventHandler<CreateMindmapNoteEvent> {
	}

	public String uuid;
	public String content;
	public Integer x;
	public Integer y;
	public Boolean blocked;

	/**
	 * Factory method for convenient creation of a {@link CreateMindmapNoteEvent}
	 * 
	 * @param content
	 * @param x
	 * @param y
	 * @return
	 */
	public static CreateMindmapNoteEvent create(String content, Integer x, Integer y) {
		CreateMindmapNoteEvent event = GWT.create(CreateMindmapNoteEvent.class);
		event.uuid = ch.unifr.pai.twice.utils.device.client.UUID.createNew();
		event.content = content;
		event.x = x;
		event.y = y;
		event.blocked = false;
		return event;
	}

}
