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
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A twice module interface. This is a lightweight placeholder of a component widget for the lazy loading mechanism (the initialization of the actual component
 * is delayed until it is accessed the first time by the user). To profit from the lazy loading mechanism, make sure that this widget does not consume too many
 * resources. It is therefore good practice to separate the implementation of the {@link TWICEModule} from the implementation of the actual component widget
 * (<M>).
 * 
 * @author Oliver Schmid
 * 
 * @param <M>
 *            the class of the actual component widget
 */
public interface TWICEModule<M extends Widget> {

	/**
	 * Invoked when the component is activated
	 * 
	 * @param instance
	 *            - the actual component widget
	 */
	void start(M instance);

	/**
	 * Invoked when the component is deactivated - release occupied resources if possible
	 * 
	 * @param instance
	 *            - the actual component widget
	 */
	void stop(M instance);

	/**
	 * Tells the system if this implementation of the module shall be hidden from the menu. Usually this method returns false. If a component is not implemented
	 * for a specific type, return true and the component will not be displayed in the menu.
	 * 
	 * Attention! instance can be null if the module is instantiated asynchronously!
	 * 
	 * @return if the implementation shall be hidden in the menu
	 */
	boolean dontShowInMenu(M instance);

	/**
	 * This method decides if the component is appended to the root panel (@link {@link RootPanel} - if the layout allows it) or if it is attached to the root
	 * layout panel (@link {@link RootLayoutPanel}). Usually this method should return false.
	 * 
	 * Attention! instance can be null if the module is instantiated asynchronously!
	 * 
	 * @return if the component shall be attached to the root panel or the root layout panel.
	 */
	boolean attachToRootPanel(M instance);
}
