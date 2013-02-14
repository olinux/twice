package ch.unifr.pai.twice.layout.client.eclipseLayout;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import ch.unifr.pai.twice.layout.client.commons.MiceDialogCaption;
import ch.unifr.pai.twice.layout.client.commons.ResizableDecoratorPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class oldMiceSplitLayoutPanel extends SplitLayoutPanel {

	private Map<ResizableDecoratorPanel, Widget> slots = new HashMap<ResizableDecoratorPanel, Widget>();
	private ResizableDecoratorPanel centralDecoratorPanel;
	private boolean isInFullscreenMode = false;

	@Override
	public void insert(Widget child, Direction direction, double size,
			Widget before) {
		ResizableDecoratorPanel slot = new ResizableDecoratorPanel(
				getSplitterSize()+2);
		setWidgetToSlot(slot, child);
		if (direction == Direction.CENTER)
			centralDecoratorPanel = slot;
		super.insert(slot, direction, size, before);
	}

	private void setWidgetToSlot(ResizableDecoratorPanel slot, Widget w) {
		DockLayoutPanel layout = new DockLayoutPanel(Unit.PX);
		layout.addNorth(createHeader(slot, w), 30);
		layout.add(w);
		slot.setWidget(layout);
		slots.put(slot, w);
	}

	private String getCaption(Widget w) {
		return w instanceof HasText ? ((HasText) w).getText() : "unknown";
	}

	private HorizontalPanel createHeader(final ResizableDecoratorPanel slot,
			Widget w) {
		String text = getCaption(w);
		HorizontalPanel header = new HorizontalPanel();
		header.setHeight("100%");
		header.setWidth("100%");
		Label textLabel = new Label(text);
		header.add(textLabel);
		header.setStyleName("miceWidgetCaption");
		header.setCellWidth(textLabel, "100%");

		if (!isInFullscreenMode) {
			Image dialogButton = new Image(GWT.getModuleBaseURL()
					+ "images/dialog.png");
			dialogButton.addStyleName("miceWidgetButton");
			dialogButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					toDialog(slot);
				}
			});
			
			header.add(dialogButton);
			Image fullscreenButton = new Image(GWT.getModuleBaseURL()
					+ "images/fullscreen.png");
			fullscreenButton.addStyleName("miceWidgetButton");
			fullscreenButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					makeFullScreen(slot);
				}
			});
			header.add(fullscreenButton);
		} else {
			Image nofullscreenButton = new Image(GWT.getModuleBaseURL()
					+ "images/nofullscreen.png");
			nofullscreenButton.addStyleName("miceWidgetButton");
			nofullscreenButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					releaseFullScreen();
				}
			});
			header.add(nofullscreenButton);
		}
//
//		Image closeButton = new Image(GWT.getModuleBaseURL()
//				+ "images/close_hover.png");
//		closeButton.addStyleName("miceWidgetButton");
//		header.add(closeButton);

		return header;
	}

	private Widget currentCenterWidget;
	private ResizableDecoratorPanel originSlotOfCurrentFullscreenWidget;
	private Map<Widget, Integer> sizeBeforeFullscreen = new HashMap<Widget, Integer>();

	private void releaseFullScreen() {
		if (isInFullscreenMode) {
			isInFullscreenMode = false;
			Widget minimizeWidget = slots.get(getCenter());
			setWidgetToSlot(originSlotOfCurrentFullscreenWidget, minimizeWidget);
			setWidgetToSlot((ResizableDecoratorPanel) getCenter(),
					currentCenterWidget);
			for (Widget widget : getChildren()) {
				if (getWidgetDirection(widget) != Direction.CENTER)
					setWidgetSize(widget, sizeBeforeFullscreen.get(widget));
			}
			onResize();
			currentCenterWidget = null;
			originSlotOfCurrentFullscreenWidget = null;
			sizeBeforeFullscreen.clear();
		}

	}

	private void makeFullScreen(ResizableDecoratorPanel w) {
		if (!isInFullscreenMode) {
			isInFullscreenMode = true;
			currentCenterWidget = slots.get(getCenter());
			for (Widget widget : getChildren()) {
				sizeBeforeFullscreen.put(widget, getSizeForWidget(widget));
			}
			setWidgetToSlot(centralDecoratorPanel, slots.get(w));
			originSlotOfCurrentFullscreenWidget = w;
			for (Widget widget : getChildren()) {
				if (this.getWidgetDirection(widget) != Direction.CENTER)
					this.setWidgetSize(widget, 0);
			}
			centralDecoratorPanel.onResize();
		}
	}

	private Map<ResizableDecoratorPanel, Integer> widthOfDialogsOriginalSlots = new HashMap<ResizableDecoratorPanel, Integer>();
	private Map<DialogBox, ResizableDecoratorPanel> dialogs = new HashMap<DialogBox, ResizableDecoratorPanel>();
	private Map<ResizableDecoratorPanel, Integer> originsOfCenterReplacements = new LinkedHashMap<ResizableDecoratorPanel, Integer>();

	private void closeDialog(DialogBox dialog) {
		ResizableDecoratorPanel panel = dialogs.remove(dialog);
		if (getWidgetDirection(panel) == Direction.CENTER) {
			if (originsOfCenterReplacements.size() > 0) {
				ResizableDecoratorPanel lastReplacement = null;
				Iterator<ResizableDecoratorPanel> i = originsOfCenterReplacements.keySet().iterator();
				while(i.hasNext())
					lastReplacement=i.next();
				if(lastReplacement!=null){
					setWidgetToSlot(lastReplacement, slots.get(getCenter()));
					setWidgetSize(lastReplacement, originsOfCenterReplacements.get(lastReplacement));
				}
			}
		}
		setWidgetToSlot(panel, dialog.getWidget());
		if (getWidgetDirection(panel) != Direction.CENTER) {
			setWidgetSize(panel, widthOfDialogsOriginalSlots.remove(panel));
		}
		dialog.hide();
		onResize();
	}

	private ResizableDecoratorPanel getLastAddedNonDialogSlot() {
		for (int i = getChildren().size(); i > 0; i--) {
			Widget w = getChildren().get(i - 1);
			if (w instanceof ResizableDecoratorPanel) {
				if (!dialogs.containsValue(w)) {
					return ((ResizableDecoratorPanel) w);
				}
			}
		}
		return null;
	}

	private void toDialog(ResizableDecoratorPanel w) {
		if (isInFullscreenMode) {
			w = originSlotOfCurrentFullscreenWidget;
			releaseFullScreen();
		}
		final ResizableDecoratorPanel finalSlot = w;
		MiceDialogCaption caption;
		final DialogBox dbox = new DialogBox(false, false,
				(caption = new MiceDialogCaption()));
		caption.setHandlers(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				closeDialog(dbox);
				makeFullScreen(finalSlot);
			}
		}, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				closeDialog(dbox);
			}
		});
		dialogs.put(dbox, w);
		dbox.setPopupPosition(w.getAbsoluteLeft(), w.getAbsoluteTop());
		Widget element = slots.get(w);
		dbox.setText(getCaption(element));
		dbox.setWidget(element);
		widthOfDialogsOriginalSlots.put(w, getSizeForWidget(w));
		dbox.show();
		if (getWidgetDirection(w) == Direction.CENTER) {
			ResizableDecoratorPanel lastAddedDecoratorPanel = getLastAddedNonDialogSlot();
			if (lastAddedDecoratorPanel != null) {
				setWidgetToSlot((ResizableDecoratorPanel) getCenter(),
						slots.get(lastAddedDecoratorPanel));
				setWidgetSize(lastAddedDecoratorPanel, 0);
				originsOfCenterReplacements.put(lastAddedDecoratorPanel, getSizeForWidget(lastAddedDecoratorPanel));
			}
		} else {
			setWidgetSize(w, 0);
		}
		onResize();
	}

	private Integer getSizeForWidget(Widget w) {
		switch (this.getWidgetDirection(w)) {
		case EAST:
		case WEST:
			return w.getOffsetWidth();
		}
		return w.getOffsetHeight();

	}
}
