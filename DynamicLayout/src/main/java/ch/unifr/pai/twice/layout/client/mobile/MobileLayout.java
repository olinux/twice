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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MobileLayout extends DynamicLayout {
	SimpleLayoutPanel layout;
	MobileInterface intf= new MobileInterface();
	
	public MobileLayout() {
		MobileUtils.preparePage();	
	}

	@Override
	protected Panel createPanel() {
		return null;
	}

	@Override
	protected void addWidget(String componentName, Widget component) {
		intf.addComponent(componentName, component);
	}

	@Override
	public void show() {
		intf.switchComponent(null);
	}

	@Override
	protected Panel getRootPanel() {
		return RootPanel.get();
	}

	@Override
	public <W extends Widget> void addModule(final String name, TWICEModule<W> module, final AsyncCallback<W> callback) {
		intf.addModule(name, module, callback);
	}
	
	
}
