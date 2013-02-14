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

public abstract class DynamicLayout {

	private static DynamicLayout layout = GWT.create(DynamicLayout.class);
	private Panel panel;
	private static boolean initialized;
	public static TWICEModuleController controller = new TWICEModuleController();

	/**
	 * This method defines if it shall be added to a GWT Layout Panel or to the
	 * standard root panel.
	 * 
	 * @return
	 */
	protected Panel getRootPanel() {
		return RootLayoutPanel.get();
	}

	public static Panel getPanel() {
		return layout.getRootPanel();
	}

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

	public void clear() {
		layout.getRootPanel().clear();
		layout.panel = null;
	}

	public abstract void show();

	protected abstract Panel createPanel();

	protected abstract void addWidget(String componentName, Widget component);

	public abstract <W extends Widget> void addModule(final String name,
			TWICEModule<W> module, final AsyncCallback<W> callback);

	/**
	 * Add either a Widget or a TWICEModule
	 * 
	 * @param name
	 * @param component
	 */
	public <W extends Widget> void addComponent(String name, Object component, AsyncCallback<W> callback){
		if(component instanceof TWICEModule){
			if(!((TWICEModule)component).dontShowInMenu(null))
				addModule(name, (TWICEModule<W>) component, callback);
		}
		else if(component instanceof Widget){
			TWICEModule m = TWICEModuleController.getTWICEModule((Widget)component);
			if(m!=null && m.dontShowInMenu((Widget)component)){
				addWidget(name, (Widget) component);
			}
			callback.onSuccess((W) component);
		}
		else{
			throw new RuntimeException("You have added an object which is neither a TWICEModule nor a Widget");
		}
	}
}
