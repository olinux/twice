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
import ch.unifr.pai.twice.dragndrop.client.intf.Draggable;
import ch.unifr.pai.twice.multipointer.client.MultiCursorController;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;

public class MindmapNoteWidget extends FocusPanel implements Draggable {

	private final CreateMindmapNoteEvent note;
	private final HTML view = new HTML();
	private boolean disclosed = true;

	public static final int BLOCKTIMEOUT = 2000;
	private final Timer blockTimeout = new Timer() {

		@Override
		public void run() {
			note.blocked = false;
			render();
		}
	};

	public MindmapNoteWidget(CreateMindmapNoteEvent note) {
		super();
		this.note = note;
		this.addStyleName("note");
		this.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		this.getElement().getStyle().setPosition(Position.ABSOLUTE);
		view.setStyleName("noteText");
		this.getElement().getStyle().setZIndex(200);
		this.setWidget(view);
		this.getElement().setId(note.uuid);
		this.addDropHandler(new DropHandler() {

			@Override
			public void onDrop(DropEvent event) {
				event.preventDefault();
			}
		});
		this.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (MultiCursorController.isDefaultCursor(event
						.getNativeEvent())) {
					toggleDisclosure(!disclosed);
				}
			}
		});
		render();
	}

	private void render() {

		if (disclosed) {
			StringBuilder sb = new StringBuilder();
			sb.append("<span class=\"noteUserName\">");
			sb.append(SafeHtmlUtils.htmlEscape(note.getUserName()));
			sb.append("</span><span class=\"noteContent\" style=\"visibility:hidden;\">");
			sb.append(SafeHtmlUtils.htmlEscape(note.content).replaceAll("\n",
					"<br/>"));
			sb.append("</span>");
			view.setHTML(sb.toString());
		} else {
			view.setHTML("<span class=\"noteUserName\">"
					+ SafeHtmlUtils.htmlEscape(note.getUserName())
					+ "</span><span class=\"noteContent\">"
					+ SafeHtmlUtils.htmlEscape(note.content).replaceAll("\n",
							"<br/>") + "</span>");
		}
		if (note.blocked != null && note.blocked)
			this.addStyleName("remotelyBlocked");
		else
			this.removeStyleName("remotelyBlocked");
		if (note.x != null)
			getElement().getStyle().setLeft(note.x, Unit.PX);
		if (note.y != null)
			getElement().getStyle().setTop(note.y, Unit.PX);
	}

	public void update(UpdateMindmapNoteEvent n) {
		if (n.blocked != null)
			note.blocked = n.blocked;
		if (n.content != null)
			note.content = n.getUserName() + ": " + n.content;
		if (n.x != null)
			note.x = n.x;
		if (n.y != null)
			note.y = n.y;
		blockTimeout.cancel();
		if (note.blocked != null && note.blocked)
			blockTimeout.schedule(BLOCKTIMEOUT);
		// Add the widget to the bottom to make sure that it is always on the
		// top
		if (getParent() instanceof HasWidgets) {
			((HasWidgets) getParent()).add(this);
			this.getElement().getStyle().setPosition(Position.ABSOLUTE);
		}
		render();
	}

	@Override
	public boolean isDraggable() {
		return note.blocked == null || !note.blocked;
	}

	public void toggleDisclosure(boolean disclose) {
		this.disclosed = disclose;
		render();
	}

	public void setFontSize(int px) {
		view.getElement().getStyle().setFontSize((double) px, Unit.PX);
	}

}
