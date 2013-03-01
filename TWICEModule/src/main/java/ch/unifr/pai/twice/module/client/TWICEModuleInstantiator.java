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
import java.util.Map;

import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

/**
 * An internal interface for the instantiator logic - the actual implementations are generated through GWT at compile time
 * 
 * @author Oliver Schmid
 * 
 * @param <M>
 */
public interface TWICEModuleInstantiator<M extends Widget> extends TWICEModule<M> {

	/**
	 * Instantiate the current widget (<M>) and invoke the callback.
	 * 
	 * @param callback
	 * @return
	 */
	RunAsyncCallback instantiate(AsyncCallback<M> callback);

	/**
	 * @param instance
	 * @return the configurable fields of the given instance including its value
	 */
	Map<String, Object> getConfigurableFields(M instance);

	/**
	 * Configure the instance with the given properties
	 * 
	 * @param properties
	 * @param instance
	 */
	void configure(Map<String, String> properties, M instance);

}
