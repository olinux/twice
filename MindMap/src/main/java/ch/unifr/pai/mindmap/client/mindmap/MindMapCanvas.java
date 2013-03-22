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
import java.util.ArrayList;
import java.util.List;

import ch.unifr.pai.mindmap.client.rpc.CreateMindmapNoteEvent;
import ch.unifr.pai.mindmap.client.rpc.UpdateMindmapNoteEvent;
import ch.unifr.pai.twice.dragndrop.client.DragNDrop;
import ch.unifr.pai.twice.dragndrop.client.configuration.DragConfiguration;
import ch.unifr.pai.twice.dragndrop.client.factories.DropTargetHandlerFactory.Priority;
import ch.unifr.pai.twice.dragndrop.client.intf.DragNDropHandler;
import ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler;
import ch.unifr.pai.twice.multipointer.provider.client.NoMultiCursorController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A visualization of the {@link MindMapComponent} on a canvas by arranging the different notes according to their coordinates
 * 
 * @author Oliver Schmid
 * 
 */
public class MindMapCanvas extends MindMapComponent {
	private final FocusPanel canvas = new FocusPanel();
	private final AbsolutePanel panel = new AbsolutePanel();
	private Toolbar toolbar;

	/**
	 * The trash bin, the notes can be dragged at to remove the note
	 */
	private final Image trashBin = new Image(GWT.getModuleBaseURL() + "images/trashbin.png");

	private final Image bgimg = new Image();

	/**
	 * @return the background image for the canvas
	 */
	public Image getBGImage() {
		return bgimg;
	}

	/**
	 * The set up for the canvas with its base structure and the registration of the drop handler for the trash bin
	 */
	public MindMapCanvas() {
		super();
		initWidget(panel);
		toolbar = new Toolbar(this);
		bgimg.getElement().getStyle().setProperty("maxHeight", "100%");
		bgimg.getElement().getStyle().setProperty("maxWidth", "100%");
		SimplePanel bgpanel = new SimplePanel();
		bgpanel.getElement().getStyle().setProperty("textAlign", "center");

		canvas.getElement().setId("mindmapCanvas");
		canvas.getElement().getStyle().setPosition(Position.ABSOLUTE);
		canvas.getElement().getStyle().setTop(0, Unit.PX);
		canvas.getElement().getStyle().setLeft(0, Unit.PX);
		canvas.getElement().getStyle().setRight(0, Unit.PX);
		canvas.getElement().getStyle().setBottom(0, Unit.PX);
		bgpanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		bgpanel.getElement().getStyle().setTop(0, Unit.PX);
		bgpanel.getElement().getStyle().setLeft(0, Unit.PX);
		bgpanel.getElement().getStyle().setRight(0, Unit.PX);
		bgpanel.getElement().getStyle().setBottom(0, Unit.PX);
		bgpanel.setWidget(bgimg);
		toolbar.getElement().getStyle().setPosition(Position.ABSOLUTE);
		toolbar.getElement().getStyle().setLeft(0, Unit.PX);
		toolbar.getElement().getStyle().setRight(0, Unit.PX);
		toolbar.getElement().getStyle().setBottom(0, Unit.PX);
		panel.add(bgpanel);
		panel.add(canvas);
		panel.add(toolbar);
		panel.add(trashBin);
		DragNDrop.setDropHandler(trashBin, new DropTargetHandler() {

			@Override
			public void onHoverEnd(String deviceId, Widget widget, Element dragProxy, Event event) {
				trashBin.setUrl(GWT.getModuleBaseURL() + "images/trashbin.png");
			}

			@Override
			public void onHover(String deviceId, Widget widget, Element dragProxy, Event event, Double intersectionPercentage,
					Double intersectionPercentageWithTarget) {
				trashBin.setUrl(GWT.getModuleBaseURL() + "images/trashbinhover.png");

			}

			@Override
			public boolean onDrop(String deviceId, Widget widget, Element dragProxy, Event event, Double intersectionPercentage,
					Double intersectionPercentageWithTarget) {
				removeNoteWidget(widget);
				widget.removeFromParent();
				return false;
			}

			@Override
			public Priority getPriority() {
				return Priority.NORMAL;
			}
		}, true);
		trashBin.getElement().getStyle().setPosition(Position.ABSOLUTE);
		trashBin.getElement().getStyle().setRight(0, Unit.PX);
		trashBin.getElement().getStyle().setBottom(00, Unit.PX);
		getElement().getStyle().setOverflow(Overflow.VISIBLE);
		canvas.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String deviceId = NoMultiCursorController.getUUID(event.getNativeEvent());
				String selected = selectedElements.get(deviceId);
				if (selectedElements.get(deviceId) != null) {
					eventBus.fireEvent(UpdateMindmapNoteEvent.block(selected, false));
				}
			}
		});
	}

	/**
	 * Attach the note widget to the canvas panel
	 * 
	 * @param w
	 */
	public void addWidgetToCanvas(Widget w) {
		panel.add(w);
	}

	/**
	 * Get the left position of a dragged widget relative to the canvas
	 * 
	 * @param draggedWidget
	 * @param proxyLeft
	 * @return
	 */
	protected int getLeft(Widget draggedWidget, int proxyLeft) {
		return proxyLeft - draggedWidget.getElement().getOffsetParent().getAbsoluteLeft();
	}

	/**
	 * Get the left position of a dragged widget relative to the canvas
	 * 
	 * @param draggedWidget
	 * @param proxyTop
	 * @return
	 */
	protected int getTop(Widget draggedWidget, int proxyTop) {
		return proxyTop - draggedWidget.getElement().getOffsetParent().getAbsoluteTop();
	}

	/**
	 * A list holding the user names of those users that have added notes already
	 */
	private final List<String> noteStack = new ArrayList<String>();

	/**
	 * The vertical offset for the stack of a specific user (mapped through the index between this list and {@link MindMapCanvas#noteStack}
	 */
	private final List<Integer> offset = new ArrayList<Integer>();

	/**
	 * Adds the note to the canvas. makes it draggable and registers it in the component. If the event does not provide initial coordinates, this method
	 * arranges them in a grid while stacking notes originated by the same users.
	 * 
	 * @see ch.unifr.pai.mindmap.client.mindmap.MindMapComponent#addMindmapNote(ch.unifr.pai.mindmap.client.rpc.CreateMindmapNoteEvent)
	 */
	@Override
	protected void addMindmapNote(CreateMindmapNoteEvent event) {
		if (toolbar.isTextInputEnabled()) {
			int index = noteStack.indexOf(event.getUserName());
			if (index == -1) {
				index = noteStack.size();
				noteStack.add(event.getUserName());
				offset.add(0);
			}
			Integer o = offset.get(index);
			o++;
			offset.set(index, o);
			int numberOfElementsPerRow = canvas.getOffsetWidth() / 170;
			int row = index / numberOfElementsPerRow;
			int col = index - (row * numberOfElementsPerRow);
			if (event.x == null) {
				event.x = col * 170 + (o * 2);
				// event.x = (int) ((canvas.getOffsetWidth() - 100) *
				// Math.random());
			}
			if (event.y == null) {
				event.y = row * 200 + ((o * 4));
			}
			final MindmapNoteWidget note = new MindmapNoteWidget(event);
			note.toggleDisclosure(toolbar.isDiscloseNotes());
			note.setFontSize(toolbar.getFontSize());
			addWidgetToCanvas(note);
			DragNDrop.makeDraggable(note, DragConfiguration.withProxy(new DragNDropHandler() {
				@Override
				public boolean onDrop(String deviceId, Widget draggedWidget, int proxyLeft, int proxyTop, Event event, DropTargetHandler dropTarget,
						boolean outOfBox) {
					eventBus.fireEvent(UpdateMindmapNoteEvent.move(draggedWidget.getElement().getId(), getLeft(draggedWidget, proxyLeft),
							getTop(draggedWidget, proxyTop)));
					eventBus.fireEvent(UpdateMindmapNoteEvent.block(draggedWidget.getElement().getId(), false));
					return true;
				}

				@Override
				public void onEndOfDrop(String deviceId, Widget draggedWidget, int proxyLeft, int proxyTop, Event event) {
				}

				@Override
				public void onStartDrag(String deviceId, Widget draggedWidget) {
					eventBus.fireEvent(UpdateMindmapNoteEvent.block(draggedWidget.getElement().getId(), true));
				}
			}, canvas));
			// note.addClickHandler(new ClickHandler() {
			//
			// @Override
			// public void onClick(ClickEvent event) {
			// String deviceId =
			// MultiCursorController.getUUID(event.getNativeEvent());
			// if (selectedElements.get(deviceId) != null)
			// eventBus.fireEvent(UpdateMindmapNoteEvent.block(
			// selectedElements.get(deviceId), false));
			// eventBus.fireEvent(UpdateMindmapNoteEvent.block(note
			// .getElement().getId(), true));
			// selectedElements.put(deviceId, note.getElement().getId());
			// }
			// });

			registerNoteWidget(note.getElement().getId(), note);
		}
	}

}
