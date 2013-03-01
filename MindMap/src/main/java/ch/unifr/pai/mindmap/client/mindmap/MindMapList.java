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
import ch.unifr.pai.mindmap.client.rpc.CreateMindmapNoteEvent;
import ch.unifr.pai.mindmap.client.rpc.UpdateMindmapNoteEvent;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A list representation of the mindmap entries (in contrast to the representation on a canvas)
 * 
 * @author Oliver Schmid
 * 
 */
public class MindMapList extends MindMapComponent {

	private final FlowPanel panel = new FlowPanel();

	public MindMapList() {
		super();
		initWidget(panel);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.mindmap.client.mindmap.MindMapComponent#addMindmapNote(ch.unifr.pai.mindmap.client.rpc.CreateMindmapNoteEvent)
	 */
	@Override
	protected void addMindmapNote(CreateMindmapNoteEvent event) {
		ListElement el = new ListElement();
		el.setValue(event.content, event.uuid);
		panel.add(el);
	}

	/**
	 * The representation of a note within the list of notes. This is an actual text box which fires an update note event as soon as it looses focus
	 * 
	 * @author Oliver Schmid
	 * 
	 */
	private class ListElement extends TextBox {
		String id;

		public ListElement() {
			super();
			getElement().getStyle().setDisplay(Display.BLOCK);
			getElement().getStyle().setPadding(10, Unit.PX);
			setWidth("100%");
			addBlurHandler(new BlurHandler() {

				@Override
				public void onBlur(BlurEvent event) {
					eventBus.fireEvent(UpdateMindmapNoteEvent.changeValue(id, getValue()));
				}
			});
		}

		public void setValue(String content, String id) {
			setValue(content);
			this.id = id;
		}

	}

}
