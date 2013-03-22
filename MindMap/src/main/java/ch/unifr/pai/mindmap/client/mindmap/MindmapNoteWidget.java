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
import ch.unifr.pai.twice.multipointer.provider.client.NoMultiCursorController;

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

/**
 * The widget representing an actual note instance in the {@link MindMapCanvas} visualization
 * 
 * @author Oliver Schmid
 * 
 */
public class MindmapNoteWidget extends FocusPanel implements Draggable {

	/**
	 * The event that has lead to the creation of the note
	 */
	private final CreateMindmapNoteEvent note;

	private final HTML view = new HTML();
	/**
	 * Current disclosure state
	 */
	private boolean disclosed = true;

	/**
	 * A timeout in ms after which a note shall be unblocked even if no such event arises (e.g. if the blocking instance does not respond anymore)
	 */
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
				if (NoMultiCursorController.isDefaultCursor(event.getNativeEvent())) {
					toggleDisclosure(!disclosed);
				}
			}
		});
		render();
	}

	/**
	 * The actual rendering of the HTML representation of the note widget. This method resets the previous rendering when called repeatedly.
	 */
	private void render() {

		if (disclosed) {
			StringBuilder sb = new StringBuilder();
			sb.append("<span class=\"noteUserName\">");
			sb.append(SafeHtmlUtils.htmlEscape(note.getUserName()));
			sb.append("</span><span class=\"noteContent\" style=\"visibility:hidden;\">");
			sb.append(SafeHtmlUtils.htmlEscape(note.content).replaceAll("\n", "<br/>"));
			sb.append("</span>");
			view.setHTML(sb.toString());
		}
		else {
			view.setHTML("<span class=\"noteUserName\">" + SafeHtmlUtils.htmlEscape(note.getUserName()) + "</span><span class=\"noteContent\">"
					+ SafeHtmlUtils.htmlEscape(note.content).replaceAll("\n", "<br/>") + "</span>");
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

	/**
	 * Handles the update of the note
	 * 
	 * @param n
	 *            - the update event
	 */
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

	/**
	 * Defines that the note is not draggable while it is blocked.
	 * 
	 * @see ch.unifr.pai.twice.dragndrop.client.intf.Draggable#isDraggable()
	 */
	@Override
	public boolean isDraggable() {
		return note.blocked == null || !note.blocked;
	}

	/**
	 * Toggles the visibility of the note's content
	 * 
	 * @param disclose
	 */
	public void toggleDisclosure(boolean disclose) {
		this.disclosed = disclose;
		render();
	}

	/**
	 * Resets the font-size of the note to the given amount of pixels
	 * 
	 * @param px
	 */
	public void setFontSize(int px) {
		view.getElement().getStyle().setFontSize(px, Unit.PX);
	}

}
