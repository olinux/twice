package ch.unifr.pai.twice.module.client;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class TWICEModuleController {

	Set<TWICEModule<? extends Widget>> modules = new HashSet<TWICEModule<? extends Widget>>();
	private static Map<Widget, TWICEModuleInstantiator<Widget>> instantiatorMap = new HashMap<Widget, TWICEModuleInstantiator<Widget>>();	
	
	public static void start(Widget widget){
		TWICEModuleInstantiator<Widget> instantiator = instantiatorMap.get(widget);
		if(instantiator!=null){
			instantiator.start(widget);
		}
	}
	
	public static void stop(Widget widget){
		TWICEModuleInstantiator<Widget> instantiator = instantiatorMap.get(widget);
		if(instantiator!=null){
			instantiator.stop(widget);
		}
	}
	
	public static TWICEModule<?> getTWICEModule(Widget w){
		TWICEModuleInstantiator<Widget> instantiator = instantiatorMap.get(w);
		if (instantiator instanceof TWICEModule){
			return ((TWICEModule)instantiator);
		}
		return null;
	}
	
	
	public static void instantiateModule(final TWICEModule<? extends Widget> module,
			final AsyncCallback<Widget> callback) {
		if (module instanceof TWICEModuleInstantiator) {
			@SuppressWarnings("unchecked")
			final TWICEModuleInstantiator<Widget> instantiator = ((TWICEModuleInstantiator<Widget>) module);
			GWT.runAsync(instantiator.instantiate(new AsyncCallback<Widget>() {

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				@Override
				public void onSuccess(Widget result) {
					instantiatorMap.put(result, instantiator);
					callback.onSuccess(result);
				}
			}));
		}
	}
	
	public static void restart(Widget w){
		TWICEModuleInstantiator<Widget> instantiator = instantiatorMap.get(w);
		if (instantiator instanceof TWICEModule){
			((TWICEModule)instantiator).stop(w);
			((TWICEModule)instantiator).start(w);
		}
	}

	public static Map<String, Object> getConfigurationForWidget(Widget w) {
		TWICEModuleInstantiator<Widget> instantiator = instantiatorMap.get(w);
		if (instantiator != null)
			return instantiator.getConfigurableFields(w);
		return null;
	}
	
	public static void configure(Map<String, String> properties, Widget w) {
		TWICEModuleInstantiator<Widget> instantiator = instantiatorMap.get(w);
		if (instantiator != null)
			instantiator.configure(properties, w);
	}
}
