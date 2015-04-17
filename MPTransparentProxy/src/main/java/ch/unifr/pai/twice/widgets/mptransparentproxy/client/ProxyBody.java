package ch.unifr.pai.twice.widgets.mptransparentproxy.client;

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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import ch.unifr.pai.twice.multipointer.provider.client.widgets.MultiFocusTextBox;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Standard logic to be injected by proxy (non-multi pointer)
 * 
 * @author Oliver Schmid
 * 
 */
public class ProxyBody {

	protected static List<MultiFocusTextBox> replacements = new ArrayList<MultiFocusTextBox>();

	/**
	 * Register required listeners and set up the basic objects
	 */
	public void initialize() {			
		publishInterfaces();
//		addDomChangeEvents();
	}

	/**
	 * If something in the DOM has changed, call the different handlers and replace the standard widgets
	 * 
	 * @param evt
	 */
	public static void onDomChanged(Event evt) {
		if (Element.is(evt.getEventTarget())) {
			Element e = (Element) Element.as(evt.getEventTarget());
			for (DOMChangeHandler h : domChangeHandler)
				h.onDomChanged(evt);
			for (MultiFocusTextBox b : replacements)
				b.refreshDisplay();
		}
	}

	private static List<DOMChangeHandler> domChangeHandler = new ArrayList<DOMChangeHandler>();

	/**
	 * Add a dom change handler
	 * 
	 * @param handler
	 */
	public static void addDOMChangeHandler(DOMChangeHandler handler) {
		domChangeHandler.add(handler);
	}

	/**
	 * Add a listener for dom change events
	 */
	private native void addDomChangeEvents() /*-{
												$wnd.document.addEventListener('DOMSubtreeModified', @ch.unifr.pai.twice.widgets.mptransparentproxy.client.ProxyBody::onDomChanged(Lcom/google/gwt/user/client/Event;), false);
												}-*/;

	/**
	 * @return true if this is the parent frame
	 */
	private native boolean isParentFrame() /*-{
		return $wnd.location == $wnd.parent.location;
	}-*/;

	/**
	 * Set up the JS-callable signature as a global JS function.
	 * 
	 */
	private native void publishInterfaces() /*-{
		$wnd.backInHistory = @com.google.gwt.user.client.History::back();
		$wnd.forwardInHistory = @com.google.gwt.user.client.History::forward();
		$wnd.reloadFrame = @com.google.gwt.user.client.Window.Location::reload();
	}-*/;

}
