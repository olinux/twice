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
import ch.unifr.pai.mindmap.client.rpc.DeleteMindmapNoteEvent.DeleteMindmapNoteHandler;
import ch.unifr.pai.twice.comm.serverPush.client.UndoableRemoteEvent;
import ch.unifr.pai.twice.comm.serverPush.client.UndoableRemoteEventHandler;

/**
 * An application specific remote event which provides the information about a removed note within the mindmap session
 * 
 * @author Oliver Schmid
 * 
 */
public abstract class DeleteMindmapNoteEvent extends UndoableRemoteEvent<DeleteMindmapNoteHandler> {

	public static final Type<DeleteMindmapNoteHandler> TYPE = new Type<DeleteMindmapNoteHandler>();

	public static interface DeleteMindmapNoteHandler extends UndoableRemoteEventHandler<DeleteMindmapNoteEvent> {
	}

	public String uuid;

}
