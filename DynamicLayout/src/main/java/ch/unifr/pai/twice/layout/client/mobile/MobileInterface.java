package ch.unifr.pai.twice.layout.client.mobile;

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
import java.util.LinkedHashMap;
import java.util.Map;

import ch.unifr.pai.twice.layout.client.DynamicLayout;
import ch.unifr.pai.twice.module.client.TWICEModule;
import ch.unifr.pai.twice.module.client.TWICEModuleController;
import ch.unifr.pai.twice.multipointer.controller.client.MobileKeyboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The mobile layout mechanism logic. The mobile layout contains a menu bar on the top, displaying a button on the top left to open a menu. The menu appears
 * from the left and presents the different components which are available. There is always only one
 * 
 * @author Oliver Schmid
 * 
 */
public class MobileInterface {
	static MobileResourceBundle RESOURCES = GWT.create(MobileResourceBundle.class);

	SimpleLayoutPanel main = new SimpleLayoutPanel();
	private final static Map<String, Widget> components = new LinkedHashMap<String, Widget>();
	private final static Map<String, AsyncCallback<Widget>> callbacks = new LinkedHashMap<String, AsyncCallback<Widget>>();
	private static String currentcomponentname;
	private final FlowPanel controlbar = new FlowPanel();
	Menu menu = new Menu();
	Button others = new Button();

	private final MobileKeyboard keyboardButton = new MobileKeyboard();
	private final Button hideKeyboard = new Button();
	private final AbsolutePanel keyboardPanel = new AbsolutePanel();

	/**
	 * Initializes the main components
	 */
	public MobileInterface() {
		RESOURCES.mobileLayoutStyle().ensureInjected();
		controlbar.setStyleName("controlbar");
		others.setStyleName(RESOURCES.mobileLayoutStyle().menuButton());
		others.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				menu.show();
			}
		});
		others.getElement().getStyle().setDisplay(Display.INLINE);
		others.setWidth("auto");
		others.getElement().getStyle().setFloat(Float.LEFT);
		keyboardPanel.getElement().getStyle().setFloat(Float.RIGHT);
		keyboardButton.addStyleName("mobileKeyboard");
		hideKeyboard.addStyleName("mobileKeyboard");
		keyboardPanel.add(hideKeyboard);
		keyboardPanel.add(keyboardButton);
		// hideKeyboard.setWidth("50px");
		// hideKeyboard.setHeight("100%");
		// keyboardButton.setWidth("50px");
		// keyboardButton.setHeight("50px");
		// keyboardPanel.setWidgetPosition(hideKeyboard, 0, 0);
		// keyboardPanel.setWidgetPosition(keyboardButton, 0, 0);
		hideKeyboard.getElement().getStyle().setDisplay(Display.NONE);
		keyboardButton.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				hideKeyboard.getElement().getStyle().setDisplay(Display.BLOCK);
				keyboardButton.getElement().getStyle().setDisplay(Display.NONE);
			}
		});
		keyboardButton.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				hideKeyboard.getElement().getStyle().setDisplay(Display.NONE);
				keyboardButton.setValue(null);
				keyboardButton.getElement().getStyle().setDisplay(Display.BLOCK);

			}
		});
		hideKeyboard.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hideKeyboard.getElement().getStyle().setDisplay(Display.NONE);
				keyboardButton.setValue(null);
				keyboardButton.getElement().getStyle().setDisplay(Display.BLOCK);

			}
		});
		controlbar.add(others);
		controlbar.add(keyboardPanel);
	}

	/**
	 * Adds the top bar to the layout
	 */
	private void addControlBar() {
		DynamicLayout.getPanel().add(controlbar);
		controlbar.getElement().getStyle().setTop(0, Unit.PX);
		controlbar.getElement().getStyle().setHeight(40, Unit.PX);
		controlbar.getElement().getStyle().setLeft(0, Unit.PX);
		controlbar.getElement().getStyle().setRight(0, Unit.PX);
		controlbar.getElement().getStyle().setPosition(Position.ABSOLUTE);
		controlbar.getElement().getStyle().setDisplay(Display.BLOCK);
		currentWidget.getElement().getStyle().setMarginTop(40, Unit.PX);
	}

	private Widget currentWidget;

	/**
	 * Switch to a specific component ({@link TWICEModule}). If the module has not been accessed yet, it is instantiated and the callback is invoked.
	 * 
	 * @param componentName
	 */
	void switchComponent(String componentName) {
		RootLayoutPanel.get().clear();
		if (componentName == null && components.keySet().size() > 0) {
			componentName = (String) components.keySet().toArray()[0];
		}
		// Is it a not yet instantiated module?
		final TWICEModule module = modules.get(componentName);
		if (module != null) {
			// Then instantiate it and add it to the components
			final String moduleComponentName = componentName;
			TWICEModuleController.instantiateModule(module, new AsyncCallback<Widget>() {

				@Override
				public void onSuccess(Widget result) {
					if (currentWidget != null)
						TWICEModuleController.stop(currentWidget);
					components.put(moduleComponentName, result);
					modules.remove(moduleComponentName);
					AsyncCallback<Widget> callback = callbacks.get(moduleComponentName);
					if (callback != null) {
						callback.onSuccess(result);
					}
					switchToWidget(moduleComponentName, result);
				}

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Was not able to initialize module " + moduleComponentName);
				}
			});
		}
		else {
			Widget w = components.get(componentName);
			if (w == null && components.size() > 0) {
				componentName = (String) components.keySet().toArray()[0];
				w = components.get(componentName);
			}
			switchToWidget(componentName, w);

		}
	}

	/**
	 * Hide the current widget if there is one and show the given component on the single screen
	 * 
	 * @param componentName
	 * @param w
	 */
	private void switchToWidget(String componentName, Widget w) {
		if (w != null) {
			boolean issamewidget = currentWidget == w;
			if (currentWidget != null) {
				if (!issamewidget)
					TWICEModuleController.stop(currentWidget);
				currentWidget.removeFromParent();
			}
			currentWidget = w;
			currentcomponentname = componentName;
			TWICEModule m = TWICEModuleController.getTWICEModule(currentWidget);
			if (m != null && m.attachToRootPanel(currentWidget)) {
				RootPanel.get().add(w);
			}
			else {
				RootLayoutPanel.get().add(w);
			}
			if (!issamewidget)
				TWICEModuleController.start(currentWidget);
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				addControlBar();
			}
		});
	}

	boolean initialized;

	/**
	 * Adds a component - if it is the first one, it will be directly shown on the main screen (initialized)
	 * 
	 * @param componentName
	 * @param component
	 */
	public void addComponent(String componentName, Widget component) {
		components.put(componentName, component);
		if (!initialized) {
			switchComponent(componentName);
			initialized = true;
		}
		// menu.addEntry(componentName);
	}

	/**
	 * A map of registered modules (used to lookup for lazy instantiation)
	 */
	private static Map<String, TWICEModule<? extends Widget>> modules = new LinkedHashMap<String, TWICEModule<? extends Widget>>();

	/**
	 * Adds the module with the given name to the layout
	 * 
	 * @param moduleName
	 * @param module
	 * @param callback
	 */
	public void addModule(String moduleName, TWICEModule<? extends Widget> module, AsyncCallback<? extends Widget> callback) {
		modules.put(moduleName, module);
		components.put(moduleName, null);
		callbacks.put(moduleName, (AsyncCallback<Widget>) callback);
	}

	/**
	 * The menu that appears from the left if the menu button is pressed
	 * 
	 * @author Oliver Schmid
	 * 
	 */
	public class Menu extends FlowPanel {
		Map<String, Label> menuButtons = new HashMap<String, Label>();

		public Menu() {
			// setWidth("240px");
			addStyleName(RESOURCES.mobileLayoutStyle().menu());
			// getElement().getStyle().setHeight(100, Unit.PCT);
			// getElement().getStyle().setBackgroundColor("#2E2E2E");
			// getElement().getStyle().setZIndex(10001);
			// getElement().getStyle().setBorderWidth(0, Unit.PX);

		}

		/**
		 * Hide the menu
		 */
		public void hide() {
			removeFromParent();
		}

		/**
		 * Show the menu
		 */
		public void show() {

			clear();
			for (String s : components.keySet()) {
				final String name = s;
				final Label b = new Label(name);
				menuButtons.put(name, b);
				b.getElement().getStyle().setColor("#ffffff");
				b.getElement().getStyle().setFontSize(16, Unit.PX);
				b.getElement().getStyle().setMargin(5, Unit.PX);
				b.getElement().getStyle().setPadding(10, Unit.PX);
				b.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						if (currentcomponentname != null) {
							Label old = menuButtons.get(currentcomponentname);
							if (old != null)
								old.removeStyleName(RESOURCES.mobileLayoutStyle().selectedMenuEntry());
						}
						switchComponent(name);
						b.setStyleName(RESOURCES.mobileLayoutStyle().selectedMenuEntry());
						Menu.this.hide();
					}
				});
				if (s.equals(currentcomponentname)) {
					b.setStyleName(RESOURCES.mobileLayoutStyle().selectedMenuEntry());
				}
				add(b);
			}
			if (currentWidget != null) {
				currentWidget.getElement().getStyle().setLeft(240, Unit.PX);
			}
			controlbar.getElement().getStyle().setLeft(240, Unit.PX);
			DynamicLayout.getPanel().add(this);
		}
	}

}
