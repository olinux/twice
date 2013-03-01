package ch.unifr.pai.twice.layout.client;

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
import ch.unifr.pai.twice.authentication.client.Authentication;
import ch.unifr.pai.twice.module.client.TWICEModule;
import ch.unifr.pai.twice.module.client.TWICEModuleController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The dynamic layout component allows to define device specific layouts optimized for the different input modalities.
 * 
 * 
 * @author Oliver Schmid
 * 
 */
public abstract class DynamicLayout {

	/**
	 * The device dependent layout instantiated through deferred binding
	 */
	private static DynamicLayout layout = GWT.create(DynamicLayout.class);
	/**
	 * The base panel to which the layout is attached
	 */
	private Panel panel;
	/**
	 * A boolean defining if the layout has been initialized already
	 */
	private static boolean initialized;
	/**
	 * The {@link TWICEModuleController} allowing to control the lifecycle of the different visual modules of a TWICE application
	 */
	public static TWICEModuleController controller = new TWICEModuleController();

	/**
	 * @return the root panel which shall be the initial hook to attach the components. By default, this is the {@link RootLayoutPanel} but it can be overridden
	 *         by subtypes if necessary
	 */
	protected Panel getRootPanel() {
		return RootLayoutPanel.get();
	}

	public static Panel getPanel() {
		return layout.getRootPanel();
	}

	/**
	 * @return the current {@link DynamicLayout} - if necessary, it is initialized first.
	 */
	public static DynamicLayout get() {
		if (!initialized) {
			Authentication.getUserName();
			layout.panel = layout.createPanel();
			if (layout.panel != null) {
				layout.getRootPanel().add(layout.panel);
			}
			initialized = true;
		}
		return layout;
	}

	/**
	 * Remove all components from the panel
	 */
	public void clear() {
		layout.getRootPanel().clear();
		layout.panel = null;
	}

	/**
	 * Show the layout
	 */
	public abstract void show();

	/**
	 * Instantiate the panel during the initialization process
	 * 
	 * @return
	 */
	protected abstract Panel createPanel();

	/**
	 * Add a widget to the layout with the according name
	 * 
	 * @param componentName
	 * @param component
	 */
	protected abstract void addWidget(String componentName, Widget component);

	/**
	 * Add a {@link TWICEModule} to the layout.
	 * 
	 * @param name
	 * @param module
	 * @param callback
	 *            - the callback which is invoked when the component is instantiated. Due to the lazy loading mechanism, this does not happen until the
	 *            component is actually activated
	 */
	public abstract <W extends Widget> void addModule(final String name, TWICEModule<W> module, final AsyncCallback<W> callback);

	/**
	 * Add either a Widget or a TWICEModule
	 * 
	 * @param callback
	 *            - the callback which is invoked when the component is instantiated. Due to the lazy loading mechanism, this does not happen until the
	 *            component is actually activated
	 */
	public <W extends Widget> void addComponent(String name, Object component, AsyncCallback<W> callback) {
		if (component instanceof TWICEModule) {
			if (!((TWICEModule) component).dontShowInMenu(null))
				addModule(name, (TWICEModule<W>) component, callback);
		}
		else if (component instanceof Widget) {
			TWICEModule m = TWICEModuleController.getTWICEModule((Widget) component);
			if (m != null && m.dontShowInMenu((Widget) component)) {
				addWidget(name, (Widget) component);
			}
			callback.onSuccess((W) component);
		}
		else {
			throw new RuntimeException("You have added an object which is neither a TWICEModule nor a Widget");
		}
	}
}
