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

import ch.unifr.pai.twice.module.client.TWICEAnnotations.Configurable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

/**
 * Controller for {@link TWICEModule}s that provides the necessary functionality to manage the lifecycle of a component. The controller logic also handles the
 * lazy initialization of the elements.
 * 
 * @author Oliver Schmid
 * 
 */
public class TWICEModuleController {

	Set<TWICEModule<? extends Widget>> modules = new HashSet<TWICEModule<? extends Widget>>();
	private static Map<Widget, TWICEModuleInstantiator<Widget>> instantiatorMap = new HashMap<Widget, TWICEModuleInstantiator<Widget>>();

	/**
	 * Starts the given component
	 * 
	 * @param widget
	 *            - the actual component widget
	 */
	public static void start(Widget widget) {
		TWICEModuleInstantiator<Widget> instantiator = instantiatorMap.get(widget);
		if (instantiator != null) {
			instantiator.start(widget);
		}
	}

	/**
	 * Stops the given component
	 * 
	 * @param widget
	 *            - the actual component widget
	 */
	public static void stop(Widget widget) {
		TWICEModuleInstantiator<Widget> instantiator = instantiatorMap.get(widget);
		if (instantiator != null) {
			instantiator.stop(widget);
		}
	}

	/**
	 * @param w
	 *            - the actual component widget
	 * @return the corresponding {@link TWICEModule} if available, null otherwise
	 */
	public static TWICEModule<?> getTWICEModule(Widget w) {
		TWICEModuleInstantiator<Widget> instantiator = instantiatorMap.get(w);
		if (instantiator instanceof TWICEModule) {
			return (instantiator);
		}
		return null;
	}

	/**
	 * Instantiates the component based on the {@link TWICEModule} implementation
	 * 
	 * @param module
	 *            - the {@link TWICEModule} with the instantiation logic
	 * @param callback
	 *            - the callback invoked after the instantiation handing over the actual component widget
	 */
	public static void instantiateModule(final TWICEModule<? extends Widget> module, final AsyncCallback<Widget> callback) {
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

	/**
	 * Restarts the widget (call to stop and start)
	 * 
	 * @param w
	 */
	public static void restart(Widget w) {
		TWICEModuleInstantiator<Widget> instantiator = instantiatorMap.get(w);
		if (instantiator instanceof TWICEModule) {
			((TWICEModule) instantiator).stop(w);
			((TWICEModule) instantiator).start(w);
		}
	}

	/**
	 * @param w
	 * @return a map of configurable fields (annotated with {@link Configurable}) of a {@link TWICEModule}
	 */
	public static Map<String, Object> getConfigurationForWidget(Widget w) {
		TWICEModuleInstantiator<Widget> instantiator = instantiatorMap.get(w);
		if (instantiator != null)
			return instantiator.getConfigurableFields(w);
		return null;
	}

	/**
	 * Configure the widget with the given properties.
	 * 
	 * @param properties
	 * @param w
	 */
	public static void configure(Map<String, String> properties, Widget w) {
		TWICEModuleInstantiator<Widget> instantiator = instantiatorMap.get(w);
		if (instantiator != null)
			instantiator.configure(properties, w);
	}
}
