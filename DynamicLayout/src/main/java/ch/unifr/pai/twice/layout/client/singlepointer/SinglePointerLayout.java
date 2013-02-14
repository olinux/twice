package ch.unifr.pai.twice.layout.client.singlepointer;
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
import ch.unifr.pai.twice.layout.client.eclipseLayout.MiceLayout;
import ch.unifr.pai.twice.layout.client.eclipseLayout.MiceLayoutSlot;
import ch.unifr.pai.twice.layout.client.eclipseLayout.MiceTabLabel;
import ch.unifr.pai.twice.module.client.TWICEModule;
import ch.unifr.pai.twice.module.client.TWICEModuleController;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class SinglePointerLayout extends DynamicLayout {

	
	MiceLayout micelayout;
	private MiceLayoutSlot initialSlot = new MiceLayoutSlot();
	private boolean initialized;
	

	@Override
	protected Panel createPanel() {
		if (micelayout == null){
			micelayout = new MiceLayout();
		}
		return micelayout;
	}

	@Override
	protected void addWidget(String componentName, Widget component) {
		initialSlot.add(new MiceTabLabel(componentName, component));
		if(!initialized){
			initialized = true;
		}
	}

	@Override
	public void show() {
		micelayout.getMainPanel().add(initialSlot);	
		DynamicLayout.getPanel().add(micelayout);
	}

	@Override
	public <W extends Widget> void addModule(final String name, TWICEModule<W> module, final AsyncCallback<W> callback) {
		//TODO do instantiate on demand - not at registration time
		
		final SimpleLayoutPanel placeHolder = new SimpleLayoutPanel();
		addWidget(name, placeHolder);
		TWICEModuleController.instantiateModule(module, new AsyncCallback<Widget>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Was not able to instantiate "+name);
			}

			@Override
			public void onSuccess(Widget result) {
				placeHolder.setWidget(result);
				callback.onSuccess((W)result);
//				TWICEModuleController.start(result);
			}
		});
		
	}

	
	
}
