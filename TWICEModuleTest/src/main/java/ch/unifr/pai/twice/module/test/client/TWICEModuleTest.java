package ch.unifr.pai.twice.module.test.client;

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

import ch.unifr.pai.twice.module.client.TWICEModuleInstantiator;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * An example of asynchronous module instantiation (the instantiation of a module with GWT.runAsync allows to create split points. This means, that the
 * application does not even need to download the code of a module as long as it has not been accessed).
 * 
 * @author Oliver Schmid
 * 
 */
public class TWICEModuleTest implements EntryPoint {

	@Override
	public void onModuleLoad() {
		TWICEModuleInstantiator<SomeClass> module = GWT.create(GenerationTestModule.class);
		GWT.runAsync(module.instantiate(new AsyncCallback<SomeClass>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(SomeClass result) {
				// TODO Auto-generated method stub

			}
		}));
	}

}
