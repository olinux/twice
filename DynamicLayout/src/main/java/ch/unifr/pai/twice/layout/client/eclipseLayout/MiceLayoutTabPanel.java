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
import ch.unifr.pai.twice.module.client.TWICEModule;
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

/**
 * The {@link MiceLayoutTabPanel} is a panel which provides tabs that can contain multiple widget components. The labels of the tab are draggable and can be *
 * rearranged as it is known from the eclipse user interface. Additionally, the screen can be split if a tab is dragged to a border of the tab panel.
 * 
 * @author Oliver Schmid
 * 
 */
public class MiceLayoutTabPanel extends TabLayoutPanel implements HasMouseOverHandlers, HasMouseOutHandlers {

	/**
	 * If a tab is selected (becoming active and the underlying component is a {@link TWICEModule}, the component is started through th
	 * {@link TWICEModuleController}
	 * 
	 * @see com.google.gwt.user.client.ui.TabLayoutPanel#selectTab(int, boolean)
	 */
	@Override
	public void selectTab(int index, boolean fireEvents) {
		super.selectTab(index, fireEvents);
		final Widget w = getWidget(index);
		if (w instanceof RequiresResize) {
			((RequiresResize) getWidget(index)).onResize();
		}
		// TWICE modules are wrapped with a simple layout panel. So let's try to
		// start the component.
		if (w instanceof SimpleLayoutPanel && ((SimpleLayoutPanel) w).getWidget() != null) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					TWICEModuleController.start(((SimpleLayoutPanel) w).getWidget());
				}
			});
		}
	}

	/**
	 * The resource bundle
	 */
	private static MiceResourceBundle RESOURCES = GWT.create(MiceResourceBundle.class);

	/**
	 * the root panel
	 */
	LayoutPanel panel;
	/**
	 * the bar on the top holding the different tab segments
	 */
	FlowPanel tabBar;
	DeckLayoutPanel deckPanel;
	FocusPanel tabBarFocus;
	boolean fullscreen;
	SimplePanel fullscreenButton = new SimplePanel();
	Image showFullscreenButton;
	Image hideFullscreenButton;

	private final Set<MiceTabLabel> labels = new HashSet<MiceTabLabel>();

	private final MiceTabLabel hoverGhost = new MiceTabLabel("&nbsp;", new Label());

	/**
	 * Shows a temporary, semi-transparent representation of the dragged tab label which is currently hovering the tab bar. This represents the position, where
	 * the component would be placed at if it would be dropped on its current position
	 * 
	 * @param dragProxy
	 */
	private void showHoverGhost(Element dragProxy) {
		hoverGhost.setText(dragProxy.getInnerText());
		tabBarFocus.addStyleName(RESOURCES.miceLayoutStyle().hoverTabBar());
		add(hoverGhost);
	}

	/**
	 * Hides the temporary, semi-transparent representation of the dragged tab label since it has left the area of the tab bar.
	 * 
	 * @param dragProxy
	 */
	private void hideHoverGhost(Element dragProxy) {
		tabBarFocus.removeStyleName(RESOURCES.miceLayoutStyle().hoverTabBar());
		remove(hoverGhost.getWidget());
		remove(hoverGhost);
	}

	/**
	 * Adds a component to the tab panel. If it the widget is an instance of {@link MiceTabLabel}, it takes the predefined label value for the labelling of the
	 * tab. Otherwise it looks after a title attribute of the widget and labels the tab after this value. If no such value exists, its tab will get an empty
	 * label.
	 * 
	 * @see com.google.gwt.user.client.ui.TabLayoutPanel#add(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public void add(Widget w) {
		if (w instanceof MiceTabLabel) {
			add((MiceTabLabel) w);
			if (((MiceTabLabel) w).getWidget() instanceof RequiresResize) {
				((RequiresResize) ((MiceTabLabel) w).getWidget()).onResize();
			}
		}
		else
			add(new MiceTabLabel(w.getTitle() != null && !w.getTitle().isEmpty() ? w.getTitle() : "&nbsp;", w));
	}

	/**
	 * Actually adds the {@link MiceTabLabel} to the tab layout while making it draggable (for the repositioning)
	 * 
	 * @param tab
	 */
	private void add(MiceTabLabel tab) {
		labels.add(tab);
		add(tab.getWidget(), tab);
		tab.setCurrentParent(this);
		if (!tab.isInitializedAsDraggable()) {
			DragNDrop.makeDraggable(tab, DragConfiguration.withProxy(DropHandlerFactory.resetWhenNotOnDropArea()));
			tab.initializeAsDraggable();
		}
	}

	/**
	 * Removes a {@link MiceTabLabel} from the tab layout. If this was the last label of the tab panel, the tab panel removes itself from its parent split
	 * layout panel.
	 * 
	 * @param tab
	 */
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

	/**
	 * The drop handler which handles drops of {@link MiceTabLabel}s on the borders of existing {@link MiceLayoutTabPanel}s.
	 * 
	 * @author Oliver Schmid
	 * 
	 */
	private static class NewAreaDropHandler implements DropTargetHandler {

		private final Direction direction;
		private final FocusPanel focusPanel;
		private final MiceLayoutTabPanel tabPanel;

		/**
		 * @param direction
		 *            - the direction of the new area handler (relative to the parent layout {@link MiceLayoutTabPanel})
		 * @param focusPanel
		 *            - the widget whose drag events actually represent the drop area (these are widgets which are attached at the different borders of the
		 *            {@link MiceLayoutTabPanel}).
		 * @param tabPanel
		 *            - the tab panel, the dragged tab originates from
		 */
		public NewAreaDropHandler(Direction direction, FocusPanel focusPanel, MiceLayoutTabPanel tabPanel) {
			this.direction = direction;
			this.focusPanel = focusPanel;
			this.tabPanel = tabPanel;
		}

		/**
		 * @return the size of a widget in pixels dependent on its scaling direction (width for horizontally scaled, height for vertically scaled)
		 */
		private int getSize() {
			switch (direction) {
				case WEST:
				case EAST:
					return tabPanel.getOffsetWidth();
			}
			return tabPanel.getOffsetHeight();

		}

		/**
		 * The actual drop handler. It only accepts drops of {@link MiceTabLabel}s and handles the creation of a new {@link MiceSplitLayoutPanel} to which the
		 * dropped widget is attached to.
		 * 
		 * @see ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler#onDrop(java.lang.String, com.google.gwt.user.client.ui.Widget,
		 *      com.google.gwt.dom.client.Element, com.google.gwt.user.client.Event, java.lang.Double, java.lang.Double)
		 */
		@Override
		public boolean onDrop(String deviceId, Widget widget, Element dragProxy, Event event, Double intersectionPercentage,
				Double intersectionPercentageWithTarget) {
			if (widget instanceof MiceTabLabel) {
				ResizeLayoutPanel parent = (ResizeLayoutPanel) tabPanel.getParent();
				int size = getSize() / 2;
				MiceSplitLayoutPanel newPanel = new MiceSplitLayoutPanel();
				parent.setWidget(newPanel);
				final MiceTabLabel tab = (MiceTabLabel) widget;
				MiceLayoutTabPanel newTabPanel = new MiceLayoutTabPanel(20);
				newPanel.insert(newTabPanel, direction, size, null);
				newPanel.add(tabPanel);
				MiceLayoutTabPanel originalParent = tab.getCurrentParent();
				originalParent.remove(tab);
				newTabPanel.add(tab);
				return true;
			}
			return false;
		}

		/**
		 * Highlights the area if a {@link MiceTabLabel} is hovering the focus panel
		 * 
		 * @see ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler#onHover(java.lang.String, com.google.gwt.user.client.ui.Widget,
		 *      com.google.gwt.dom.client.Element, com.google.gwt.user.client.Event, java.lang.Double, java.lang.Double)
		 */
		@Override
		public void onHover(String deviceId, Widget widget, Element dragProxy, Event event, Double intersectionPercentage,
				Double intersectionPercentageWithTarget) {
			if (widget instanceof MiceTabLabel)
				focusPanel.setStyleName(RESOURCES.miceLayoutStyle().hoverAddPanel());
		}

		/**
		 * Removes the highlighting of the focus panel if the {@link MiceTabLabel} leaves its area
		 * 
		 * @see ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler#onHoverEnd(java.lang.String, com.google.gwt.user.client.ui.Widget,
		 *      com.google.gwt.dom.client.Element, com.google.gwt.user.client.Event)
		 */
		@Override
		public void onHoverEnd(String deviceId, Widget widget, Element dragProxy, Event event) {
			if (widget instanceof MiceTabLabel)
				focusPanel.removeStyleName(RESOURCES.miceLayoutStyle().hoverAddPanel());
		}

		/*
		 * (non-Javadoc)
		 * @see ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler#getPriority()
		 */
		@Override
		public Priority getPriority() {
			return Priority.HIGH;
		}

	}

	/**
	 * The constructor creates the panel and adds the necessary drop handlers.
	 * 
	 * @param barHeight
	 *            - the height of the tab bar in pixels
	 */
	public MiceLayoutTabPanel(double barHeight) {
		super(barHeight, Unit.PX);
		RESOURCES.miceLayoutStyle().ensureInjected();
		hoverGhost.addStyleName(RESOURCES.miceLayoutStyle().hoverGhostTab());
		panel = (LayoutPanel) getWidget();
		tabBar = (FlowPanel) panel.getWidget(0);
		deckPanel = (DeckLayoutPanel) panel.getWidget(1);
		tabBarFocus = new FocusPanel(tabBar);
		panel.add(tabBarFocus);
		panel.setWidgetTopHeight(tabBarFocus, 0, Unit.PX, barHeight + 10, Unit.PX);
		panel.setWidgetTopBottom(deckPanel, barHeight + 10, Unit.PX, 0, Unit.PX);

		final FocusPanel southArea = new FocusPanel();
		panel.insert(southArea, 0);
		panel.setWidgetBottomHeight(southArea, 0, Unit.PX, 2, Unit.PX);
		DragNDrop.setDropHandler(southArea, new NewAreaDropHandler(Direction.SOUTH, southArea, this), true);

		final FocusPanel westArea = new FocusPanel();
		panel.insert(westArea, 0);
		panel.setWidgetLeftWidth(westArea, 0, Unit.PX, 2, Unit.PX);
		DragNDrop.setDropHandler(westArea, new NewAreaDropHandler(Direction.WEST, westArea, this), true);

		final FocusPanel eastArea = new FocusPanel();
		panel.insert(eastArea, 0);
		panel.setWidgetRightWidth(eastArea, 0, Unit.PX, 2, Unit.PX);
		DragNDrop.setDropHandler(eastArea, new NewAreaDropHandler(Direction.EAST, eastArea, this), true);

		final FocusPanel northArea = new FocusPanel();
		panel.insert(northArea, 0);
		panel.setWidgetTopHeight(northArea, 0, Unit.PX, 2, Unit.PX);
		DragNDrop.setDropHandler(northArea, new NewAreaDropHandler(Direction.NORTH, northArea, this), true);

		DragNDrop.setDropHandler(tabBarFocus, new DropTargetHandler() {

			/**
			 * Handles the drop of a dragged {@link MiceTabLabel} hovering the tab bar
			 * 
			 * @see ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler#onDrop(java.lang.String, com.google.gwt.user.client.ui.Widget,
			 *      com.google.gwt.dom.client.Element, com.google.gwt.user.client.Event, java.lang.Double, java.lang.Double)
			 */
			@Override
			public boolean onDrop(String deviceId, Widget widget, Element dragProxy, Event event, Double intersectionPercentage,
					Double intersectionPercentageWithTarget) {
				if (widget instanceof MiceTabLabel && !labels.contains(widget)) {
					final MiceTabLabel tab = (MiceTabLabel) widget;
					if (MiceLayoutTabPanel.this != tab.getCurrentParent()) {
						MiceLayoutTabPanel originalParent = tab.getCurrentParent();
						add((MiceTabLabel) widget);
						originalParent.remove(tab);
					}
				}
				return false;
			}

			/**
			 * Shows the "ghost" of the dragged {@link MiceTabLabel} when hovering the tab bar
			 * 
			 * @see ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler#onHover(java.lang.String, com.google.gwt.user.client.ui.Widget,
			 *      com.google.gwt.dom.client.Element, com.google.gwt.user.client.Event, java.lang.Double, java.lang.Double)
			 */
			@Override
			public void onHover(String deviceId, Widget widget, Element dragProxy, Event event, Double intersectionPercentage,
					Double intersectionPercentageWithTarget) {
				if (widget instanceof MiceTabLabel && !labels.contains(widget))
					showHoverGhost(dragProxy);
			}

			/**
			 * Hides the "ghost" of the dragged {@link MiceTabLabel} when leaving the area of the tab bar
			 * 
			 * @see ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler#onHoverEnd(java.lang.String, com.google.gwt.user.client.ui.Widget,
			 *      com.google.gwt.dom.client.Element, com.google.gwt.user.client.Event)
			 */
			@Override
			public void onHoverEnd(String deviceId, Widget widget, Element dragProxy, Event event) {
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
		showFullscreenButton.setStyleName(RESOURCES.miceLayoutStyle().tabPanelButton());
		showFullscreenButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setFullscreen(true);
			}
		});

		hideFullscreenButton = new Image(RESOURCES.nofullscreenButton());
		hideFullscreenButton.setTitle("hide fullscreen");
		hideFullscreenButton.setStyleName(RESOURCES.miceLayoutStyle().tabPanelButton());
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
					}
					else {
						if (w.isSelected())
							w.setSelected(false);
					}
				}
			}
		});

	}

	/**
	 * Switches to fullscreen mode
	 * 
	 * @param fullscreen
	 */
	private void setFullscreen(boolean fullscreen) {
		if (fullscreen) {
			fullscreenButton.setWidget(hideFullscreenButton);
			MiceSplitLayoutPanel topPanel = getTopSplitLayoutPanel(getParentSplitLayoutPanel());
			MiceLayout layout = (MiceLayout) topPanel.getParent();
			layout.setFullscreen(MiceLayoutTabPanel.this);
		}
		else {
			fullscreenButton.setWidget(showFullscreenButton);
			// MiceSplitLayoutPanel topPanel =
			// getTopSplitLayoutPanel(getParentSplitLayoutPanel());
			MiceLayout layout = (MiceLayout) getParent();
			layout.unsetFullscreen();
		}
	}

	/**
	 * Sets up the tab button bar (including the full screen button)
	 * 
	 * @return
	 */
	private Widget createTabButtonBar() {
		HorizontalPanel p = new HorizontalPanel();
		fullscreenButton.setWidget(showFullscreenButton);
		p.add(fullscreenButton);
		return p;
	}

	/**
	 * @param p
	 * @return the very root {@link MiceSplitLayoutPanel}
	 */
	private MiceSplitLayoutPanel getTopSplitLayoutPanel(MiceSplitLayoutPanel p) {
		if (p.getParentMiceSplitLayoutPanel() == null)
			return p;
		return getTopSplitLayoutPanel(p.getParentMiceSplitLayoutPanel());
	}

	/**
	 * register mouse out handlers
	 * 
	 * @see com.google.gwt.event.dom.client.HasMouseOutHandlers#addMouseOutHandler(com.google.gwt.event.dom.client.MouseOutHandler)
	 */
	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	/**
	 * register mouse over handler
	 * 
	 * @see com.google.gwt.event.dom.client.HasMouseOverHandlers#addMouseOverHandler(com.google.gwt.event.dom.client.MouseOverHandler)
	 */
	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	/**
	 * @return the direct {@link MiceSplitLayoutPanel}
	 */
	private MiceSplitLayoutPanel getParentSplitLayoutPanel() {
		return (MiceSplitLayoutPanel) getParent().getParent();
	}

}
