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
import ch.unifr.pai.twice.layout.client.DynamicLayout;
import ch.unifr.pai.twice.module.client.TWICEModule;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The mobile layout mechanism - currently used for all touch based devices.
 * 
 * @author Oliver Schmid
 * 
 */
public class MobileLayout extends DynamicLayout {
	SimpleLayoutPanel layout;
	/**
	 * The actual interface logic defining the components and the layout mechanism
	 */
	MobileInterface intf = new MobileInterface();

	public MobileLayout() {
		MobileUtils.preparePage();
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.layout.client.DynamicLayout#createPanel()
	 */
	@Override
	protected Panel createPanel() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.layout.client.DynamicLayout#addWidget(java.lang.String, com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	protected void addWidget(String componentName, Widget component) {
		intf.addComponent(componentName, component);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.layout.client.DynamicLayout#show()
	 */
	@Override
	public void show() {
		intf.switchComponent(null);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.layout.client.DynamicLayout#getRootPanel()
	 */
	@Override
	protected Panel getRootPanel() {
		return RootPanel.get();
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.layout.client.DynamicLayout#addModule(java.lang.String, ch.unifr.pai.twice.module.client.TWICEModule,
	 * com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public <W extends Widget> void addModule(final String name, TWICEModule<W> module, final AsyncCallback<W> callback) {
		intf.addModule(name, module, callback);
	}

}
