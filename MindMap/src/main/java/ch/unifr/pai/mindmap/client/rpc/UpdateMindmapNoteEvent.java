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
import ch.unifr.pai.mindmap.client.rpc.UpdateMindmapNoteEvent.UpdateMindmapNoteHandler;
import ch.unifr.pai.twice.comm.serverPush.client.UndoableRemoteEvent;
import ch.unifr.pai.twice.comm.serverPush.client.UndoableRemoteEventHandler;

import com.google.gwt.core.client.GWT;

/**
 * An application specific remote event which provides the information about an updated note within the mindmap session
 * 
 * @author Oliver Schmid
 * 
 */
public abstract class UpdateMindmapNoteEvent extends UndoableRemoteEvent<UpdateMindmapNoteHandler> {

	public static final Type<UpdateMindmapNoteHandler> TYPE = new Type<UpdateMindmapNoteHandler>();

	public static interface UpdateMindmapNoteHandler extends UndoableRemoteEventHandler<UpdateMindmapNoteEvent> {
	}

	public String uuid;
	public String content;
	public Integer x;
	public Integer y;
	public Boolean blocked;

	/**
	 * Factory method to conveniently create an event for blocking/unblocking a note.
	 * 
	 * @param uuid
	 * @param blocked
	 * @return
	 */
	public static UpdateMindmapNoteEvent block(String uuid, boolean blocked) {
		UpdateMindmapNoteEvent event = GWT.create(UpdateMindmapNoteEvent.class);
		event.uuid = uuid;
		event.blocked = blocked;
		return event;
	}

	/**
	 * Factory method to conveniently create an event for repositioning of a note
	 * 
	 * @param uuid
	 *            - a unique id for the event
	 * @param x
	 * @param y
	 * @return
	 */
	public static UpdateMindmapNoteEvent move(String uuid, int x, int y) {
		UpdateMindmapNoteEvent event = GWT.create(UpdateMindmapNoteEvent.class);
		event.uuid = uuid;
		event.x = x;
		event.y = y;
		return event;
	}

	/**
	 * Factory method to conveniently create an event for a changed value of a note
	 * 
	 * @param uuid
	 *            - a unique id for the event
	 * @param value
	 * @return
	 */
	public static UpdateMindmapNoteEvent changeValue(String uuid, String value) {
		UpdateMindmapNoteEvent event = GWT.create(UpdateMindmapNoteEvent.class);
		event.uuid = uuid;
		event.content = value;
		return event;
	}

	/**
	 * Factory method to conveniently create an event for a changed value and a blocking/unblocking of a note
	 * 
	 * @param uuid
	 *            - a unique id for the event
	 * @param value
	 * @param blocked
	 * @return
	 */
	public static UpdateMindmapNoteEvent createChangeValueAndBlock(String uuid, String value, boolean blocked) {
		UpdateMindmapNoteEvent event = GWT.create(UpdateMindmapNoteEvent.class);
		event.uuid = uuid;
		event.content = value;
		event.blocked = blocked;
		return event;
	}
}
