package ch.unifr.pai.mindmap.client.components;

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
import ch.unifr.pai.twice.comm.serverPush.client.CommunicationManager;
import ch.unifr.pai.twice.module.client.TWICEModule;
import ch.unifr.pai.twice.utils.device.client.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.event.shared.EventBus;

/**
 * A {@link TWICEModule} that allows to create a new note by providing a textbox for the text entry as well as a submit button to send the note to the shared
 * screen.
 * 
 * Notice: This is a very simple module that doesn't make use of a lot of resources. Therefore, the component implements the interface {@link TWICEModule} by
 * itself. Nevertheless, for more complex modules, this is not a recommended way of implementation since the benefits of lazy loading are evicted.
 * 
 * @author Oliver Schmid
 * 
 */
public class MindmapCreateEditWidget extends VerticalPanel implements TWICEModule<MindmapCreateEditWidget> {

	Button save = new Button("Add");
	TextArea text = new TextArea();
	EventBus eventBus = CommunicationManager.getBidirectionalEventBus();

	public MindmapCreateEditWidget() {
		super();
		add(text);
		add(save);
		save.getElement().getStyle().setWidth(100, Unit.PCT);
		text.setCharacterWidth(160);
		text.getElement().setAttribute("maxlength", "160");
		save.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent evt) {
				if (text.getValue() != null && !text.getValue().trim().isEmpty()) {
					CreateMindmapNoteEvent event = GWT.create(CreateMindmapNoteEvent.class);
					event.content = text.getValue();
					event.uuid = UUID.createNew();
					eventBus.fireEvent(event);
					text.setValue(null);
				}
			}
		});
	}

	@Override
	public void start(MindmapCreateEditWidget instance) {

	}

	@Override
	public void stop(MindmapCreateEditWidget instance) {

	}

	@Override
	public boolean dontShowInMenu(MindmapCreateEditWidget instance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean attachToRootPanel(MindmapCreateEditWidget instance) {
		// TODO Auto-generated method stub
		return false;
	}

}
