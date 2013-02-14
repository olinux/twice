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
import java.util.HashSet;
import java.util.Set;

import ch.unifr.pai.twice.dragndrop.client.DragNDrop;
import ch.unifr.pai.twice.dragndrop.client.configuration.DragConfiguration;
import ch.unifr.pai.twice.dragndrop.client.factories.DropHandlerFactory;
import ch.unifr.pai.twice.dragndrop.client.factories.DropTargetHandlerFactory.Priority;
import ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler;
import ch.unifr.pai.twice.module.client.TWICEModuleController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel.Direction;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class MiceLayoutTabPanel extends TabLayoutPanel implements
		HasMouseOverHandlers, HasMouseOutHandlers {

	@Override
	public void selectTab(int index, boolean fireEvents) {
		super.selectTab(index, fireEvents);
		final Widget w = getWidget(index);
		if (w instanceof RequiresResize) {
			((RequiresResize) getWidget(index)).onResize();
		}
		// TWICE modules are wrapped with a simple layout panel. So let's try to
		// start the component.
		if (w instanceof SimpleLayoutPanel
				&& ((SimpleLayoutPanel) w).getWidget() != null) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					TWICEModuleController.start(((SimpleLayoutPanel) w)
							.getWidget());
				}
			});
		}
	}

	private static MiceResourceBundle RESOURCES = GWT
			.create(MiceResourceBundle.class);

	LayoutPanel panel;
	FlowPanel tabBar;
	DeckLayoutPanel deckPanel;
	FocusPanel tabBarFocus;
	boolean fullscreen;
	SimplePanel fullscreenButton = new SimplePanel();
	Image showFullscreenButton;
	Image hideFullscreenButton;

	private Set<MiceTabLabel> labels = new HashSet<MiceTabLabel>();

	private MiceTabLabel hoverGhost = new MiceTabLabel("&nbsp;", new Label());

	private void showHoverGhost(Element dragProxy) {
		hoverGhost.setText(dragProxy.getInnerText());
		tabBarFocus.addStyleName(RESOURCES.miceLayoutStyle().hoverTabBar());
		add(hoverGhost);
	}

	private void hideHoverGhost(Element dragProxy) {
		tabBarFocus.removeStyleName(RESOURCES.miceLayoutStyle().hoverTabBar());
		remove(hoverGhost.getWidget());
		remove(hoverGhost);
	}

	public void add(Widget w) {
		if (w instanceof MiceTabLabel) {
			add((MiceTabLabel) w);
			if (((MiceTabLabel) w).getWidget() instanceof RequiresResize) {
				((RequiresResize) ((MiceTabLabel) w).getWidget()).onResize();
			}
		} else
			add(new MiceTabLabel(w.getTitle() != null
					&& !w.getTitle().isEmpty() ? w.getTitle() : "&nbsp;", w));
	}

	private void add(MiceTabLabel tab) {
		labels.add(tab);
		add(tab.getWidget(), tab);
		tab.setCurrentParent(this);
		if (!tab.isInitializedAsDraggable()) {
			DragNDrop.makeDraggable(tab, DragConfiguration
					.withProxy(DropHandlerFactory.resetWhenNotOnDropArea()));
			tab.initializeAsDraggable();
		}
	}

	public void remove(MiceTabLabel tab) {
		labels.remove(tab);
		if (labels.size() == 0) {
			MiceSplitLayoutPanel parent = getParentSplitLayoutPanel();
			boolean removed = parent.remove(this);
			if (!removed)
				return;
		}
		super.remove(tab.getWidget());
	}

	private static class NewAreaDropHandler implements DropTargetHandler {

		private final Direction direction;
		private final FocusPanel focusPanel;
		private final MiceLayoutTabPanel tabPanel;

		public NewAreaDropHandler(Direction direction, FocusPanel focusPanel,
				MiceLayoutTabPanel tabPanel) {
			this.direction = direction;
			this.focusPanel = focusPanel;
			this.tabPanel = tabPanel;
		}

		private int getSize() {
			switch (direction) {
			case WEST:
			case EAST:
				return tabPanel.getOffsetWidth();
			}
			return tabPanel.getOffsetHeight();

		}

		@Override
		public boolean onDrop(String deviceId, Widget widget,
				Element dragProxy, Event event, Double intersectionPercentage,
				Double intersectionPercentageWithTarget) {
			if (widget instanceof MiceTabLabel) {
				ResizeLayoutPanel parent = (ResizeLayoutPanel) tabPanel
						.getParent();
				int size = getSize() / 2;
				MiceSplitLayoutPanel newPanel = new MiceSplitLayoutPanel();
				parent.setWidget(newPanel);
				final MiceTabLabel tab = (MiceTabLabel) widget;
				MiceLayoutTabPanel newTabPanel = new MiceLayoutTabPanel(20,
						Unit.PX);
				newPanel.insert(newTabPanel, direction, size, null);
				newPanel.add(tabPanel);
				MiceLayoutTabPanel originalParent = tab.getCurrentParent();
				originalParent.remove(tab);
				newTabPanel.add(tab);
				return true;
			}
			return false;
		}

		@Override
		public void onHover(String deviceId, Widget widget, Element dragProxy,
				Event event, Double intersectionPercentage,
				Double intersectionPercentageWithTarget) {
			if (widget instanceof MiceTabLabel)
				focusPanel.setStyleName(RESOURCES.miceLayoutStyle()
						.hoverAddPanel());
		}

		@Override
		public void onHoverEnd(String deviceId, Widget widget,
				Element dragProxy, Event event) {
			if (widget instanceof MiceTabLabel)
				focusPanel.removeStyleName(RESOURCES.miceLayoutStyle()
						.hoverAddPanel());
		}

		@Override
		public Priority getPriority() {
			return Priority.HIGH;
		}

	}

	public MiceLayoutTabPanel(double barHeight, Unit barUnit) {
		super(barHeight, Unit.PX);
		RESOURCES.miceLayoutStyle().ensureInjected();
		hoverGhost.addStyleName(RESOURCES.miceLayoutStyle().hoverGhostTab());
		panel = (LayoutPanel) getWidget();
		tabBar = (FlowPanel) panel.getWidget(0);
		deckPanel = (DeckLayoutPanel) panel.getWidget(1);
		tabBarFocus = new FocusPanel(tabBar);
		panel.add(tabBarFocus);
		panel.setWidgetTopHeight(tabBarFocus, 0, Unit.PX, barHeight + 10,
				Unit.PX);
		panel.setWidgetTopBottom(deckPanel, barHeight + 10, Unit.PX, 0, Unit.PX);

		final FocusPanel southArea = new FocusPanel();
		panel.insert(southArea, 0);
		panel.setWidgetBottomHeight(southArea, 0, Unit.PX, 2, Unit.PX);
		DragNDrop.setDropHandler(southArea, new NewAreaDropHandler(
				Direction.SOUTH, southArea, this), true);

		final FocusPanel westArea = new FocusPanel();
		panel.insert(westArea, 0);
		panel.setWidgetLeftWidth(westArea, 0, Unit.PX, 2, Unit.PX);
		DragNDrop.setDropHandler(westArea, new NewAreaDropHandler(
				Direction.WEST, westArea, this), true);

		final FocusPanel eastArea = new FocusPanel();
		panel.insert(eastArea, 0);
		panel.setWidgetRightWidth(eastArea, 0, Unit.PX, 2, Unit.PX);
		DragNDrop.setDropHandler(eastArea, new NewAreaDropHandler(
				Direction.EAST, eastArea, this), true);

		final FocusPanel northArea = new FocusPanel();
		panel.insert(northArea, 0);
		panel.setWidgetTopHeight(northArea, 0, Unit.PX, 2, Unit.PX);
		DragNDrop.setDropHandler(northArea, new NewAreaDropHandler(
				Direction.NORTH, northArea, this), true);

		DragNDrop.setDropHandler(tabBarFocus, new DropTargetHandler() {

			@Override
			public boolean onDrop(String deviceId, Widget widget,
					Element dragProxy, Event event,
					Double intersectionPercentage,
					Double intersectionPercentageWithTarget) {
				if (widget instanceof MiceTabLabel && !labels.contains(widget)) {
					final MiceTabLabel tab = (MiceTabLabel) widget;
					if (MiceLayoutTabPanel.this != tab.getCurrentParent()) {
						MiceLayoutTabPanel originalParent = tab
								.getCurrentParent();
						add((MiceTabLabel) widget);
						originalParent.remove(tab);
					}
				}
				return false;
			}

			@Override
			public void onHover(String deviceId, Widget widget,
					Element dragProxy, Event event,
					Double intersectionPercentage,
					Double intersectionPercentageWithTarget) {
				if (widget instanceof MiceTabLabel && !labels.contains(widget))
					showHoverGhost(dragProxy);
			}

			@Override
			public void onHoverEnd(String deviceId, Widget widget,
					Element dragProxy, Event event) {
				if (widget instanceof MiceTabLabel && !labels.contains(widget))
					hideHoverGhost(dragProxy);
			}

			@Override
			public Priority getPriority() {
				return Priority.NORMAL;
			}
		}, true);

		showFullscreenButton = new Image(RESOURCES.fullscreenButton());
		showFullscreenButton.setTitle("show fullscreen");
		showFullscreenButton.setStyleName(RESOURCES.miceLayoutStyle()
				.tabPanelButton());
		showFullscreenButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setFullscreen(true);
			}
		});

		hideFullscreenButton = new Image(RESOURCES.nofullscreenButton());
		hideFullscreenButton.setTitle("hide fullscreen");
		hideFullscreenButton.setStyleName(RESOURCES.miceLayoutStyle()
				.tabPanelButton());
		hideFullscreenButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setFullscreen(false);
			}
		});

		Widget tabPanelButtonBar = createTabButtonBar();
		panel.insert(tabPanelButtonBar, 0);
		panel.setWidgetTopHeight(tabPanelButtonBar, 7, Unit.PX, 16, Unit.PX);
		panel.setWidgetRightWidth(tabPanelButtonBar, 5, Unit.PX, 40, Unit.PX);

		addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				for (int i = 0; i < getWidgetCount(); i++) {
					MiceTabLabel w = (MiceTabLabel) getTabWidget(i);
					if (event.getSelectedItem().equals(i)) {
						if (!w.isSelected())
							w.setSelected(true);
					} else {
						if (w.isSelected())
							w.setSelected(false);
					}
				}
			}
		});

	}

	private void setFullscreen(boolean fullscreen) {
		if (fullscreen) {
			fullscreenButton.setWidget(hideFullscreenButton);
			MiceSplitLayoutPanel topPanel = getTopSplitLayoutPanel(getParentSplitLayoutPanel());
			MiceLayout layout = (MiceLayout) topPanel.getParent();
			layout.setFullscreen(MiceLayoutTabPanel.this);
		} else {
			fullscreenButton.setWidget(showFullscreenButton);
			// MiceSplitLayoutPanel topPanel =
			// getTopSplitLayoutPanel(getParentSplitLayoutPanel());
			MiceLayout layout = (MiceLayout) getParent();
			layout.unsetFullscreen();
		}
	}

	private Widget createTabButtonBar() {
		HorizontalPanel p = new HorizontalPanel();
		fullscreenButton.setWidget(showFullscreenButton);
		p.add(fullscreenButton);
		return p;
	}

	private MiceSplitLayoutPanel getTopSplitLayoutPanel(MiceSplitLayoutPanel p) {
		if (p.getParentMiceSplitLayoutPanel() == null)
			return p;
		return getTopSplitLayoutPanel(p.getParentMiceSplitLayoutPanel());
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	private MiceSplitLayoutPanel getParentSplitLayoutPanel() {
		return (MiceSplitLayoutPanel) getParent().getParent();
	}

}
