package ch.unifr.pai.mindmap.client.mindmap;
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

import ch.unifr.pai.mindmap.client.rpc.CreateMindmapNoteEvent;
import ch.unifr.pai.mindmap.client.rpc.DeleteMindmapNoteEvent;
import ch.unifr.pai.mindmap.client.rpc.UpdateMindmapNoteEvent;
import ch.unifr.pai.twice.comm.serverPush.client.CommunicationManager;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public abstract class MindMapComponent extends Composite {
	protected String mindmapId;
	protected static Map<String, Widget> mindmapNoteWidgets = new HashMap<String, Widget>();
	protected Map<String, String> selectedElements = new HashMap<String, String>();

	EventBus eventBus;
	
	public void toggleDisclosureOfNotes(boolean disclose){
		for(Widget w : mindmapNoteWidgets.values()){
			if(w instanceof MindmapNoteWidget){
				((MindmapNoteWidget)w).toggleDisclosure(disclose);
			}
		}
	}
	
	public void setFontSizeOfNotes(int px){
		for(Widget w : mindmapNoteWidgets.values()){
			if(w instanceof MindmapNoteWidget){
				((MindmapNoteWidget)w).setFontSize(px);
			}
		}
	}

	public void registerNoteWidget(String id, Widget widget) {
		mindmapNoteWidgets.put(id, widget);
	}

	public void removeNoteWidget(Widget w){
		for(String id : mindmapNoteWidgets.keySet()){
			if(mindmapNoteWidgets.get(id) == w){
				unregisterNoteWidget(id);
				return;
			}
		}
	}
	
	public Widget unregisterNoteWidget(String id) {
		return mindmapNoteWidgets.remove(id);
	}

	public Widget getNoteWidgetById(String id) {
		return mindmapNoteWidgets.get(id);
	}

	public void initialize(String mindmapId) {
		this.mindmapId = mindmapId;
		this.eventBus = CommunicationManager.getBidirectionalEventBus();
		eventBus.addHandler(CreateMindmapNoteEvent.TYPE,
				new CreateMindmapNoteEvent.CreateMindmapNoteHandler() {

					@Override
					public void onEvent(CreateMindmapNoteEvent event) {
						if (getNoteWidgetById(event.uuid) == null) {
							addMindmapNote(event);
						}
					}

					@Override
					public void undo(CreateMindmapNoteEvent event) {
						MindmapNoteWidget w = (MindmapNoteWidget) unregisterNoteWidget(event.uuid);
						if (w != null)
							w.removeFromParent();							
					}

					@Override
					public void saveState(CreateMindmapNoteEvent event) {
						// TODO Auto-generated method stub
						
					}
				});
		eventBus.addHandler(DeleteMindmapNoteEvent.TYPE,
				new DeleteMindmapNoteEvent.DeleteMindmapNoteHandler() {
					
					@Override
					public void onEvent(DeleteMindmapNoteEvent event) {
						MindmapNoteWidget w = (MindmapNoteWidget) unregisterNoteWidget(event.uuid);
						if (w != null)
							w.removeFromParent();
					}

					@Override
					public void undo(DeleteMindmapNoteEvent event) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void saveState(DeleteMindmapNoteEvent event) {
						// TODO Auto-generated method stub
						
					}
				});
		eventBus.addHandler(UpdateMindmapNoteEvent.TYPE, new UpdateMindmapNoteEvent.UpdateMindmapNoteHandler() {
			
			@Override
			public void onEvent(UpdateMindmapNoteEvent event) {
				MindmapNoteWidget w = (MindmapNoteWidget) getNoteWidgetById(event.uuid);
				if (w != null) {
					w.update(event);
				}
			}

			@Override
			public void undo(UpdateMindmapNoteEvent event) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void saveState(UpdateMindmapNoteEvent event) {
				// TODO Auto-generated method stub
				
			}
		});

	}

	protected abstract void addMindmapNote(CreateMindmapNoteEvent event);

}
