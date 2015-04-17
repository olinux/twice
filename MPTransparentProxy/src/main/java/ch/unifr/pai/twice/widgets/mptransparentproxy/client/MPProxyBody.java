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
import ch.unifr.pai.twice.multipointer.provider.client.MultiCursorController;
import ch.unifr.pai.twice.multipointer.provider.client.widgets.MultiFocusTextBox;

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Multipointer extension of the {@link ProxyBody}
 * 
 * @author Oliver Schmid
 * 
 */
public class MPProxyBody extends ProxyBody {

	private static Map<String, TextBoxBase> focusedElement = new HashMap<String, TextBoxBase>();
	private static Set<String> owningDevices = new HashSet<String>();

	/**
	 * Replace all textboxes with multi focus text boxes
	 * 
	 * @param mainElement
	 */
	private void replaceAllTextBoxes(Element mainElement) {
		NodeList<com.google.gwt.dom.client.Element> inputFields = mainElement.getElementsByTagName("input");
		for (int i = 0; i < inputFields.getLength(); i++) {
			final com.google.gwt.dom.client.Element el = inputFields.getItem(i);
			String type = el.getAttribute("type");
			if (type == null || type.isEmpty() || type.equalsIgnoreCase("text") || type.equalsIgnoreCase("search")) {
				MultiFocusTextBox box = new MultiFocusTextBox();
				box.replaceTextInput(InputElement.as(el));
				replacements.add(box);
			}
		}
	}

	@Override
	public void initialize() {
		super.initialize();
		replaceAllTextBoxes(RootPanel.getBodyElement());
	}

	/**
	 * Expose the deviceOwnership to JavaScript
	 * 
	 * @param devices
	 */
	private static void setOwningDevices(String devices) {
		owningDevices.clear();
		if (devices != null && !devices.isEmpty()) {
			for (String s : devices.split(",")) {
				owningDevices.add(s);
			}
		}

		// If a device no longer owns the frame, it does not have any focused
		// element anymore.
		if (!owningDevices.isEmpty()) {
			for (String current : focusedElement.keySet()) {
				if (!owningDevices.contains(current)) {
					focusedElement.remove(current);
				}
			}
		}
	}

	/**
	 * @param e
	 * @return a textbox or textarea widget if the element is one of those, otherwise null
	 */
	private TextBoxBase getTextBoxBase(Element e) {
		if (e.getTagName().equalsIgnoreCase("input")
				&& (e.getAttribute("type") == null || e.getAttribute("type").isEmpty() || e.getAttribute("type").equalsIgnoreCase("text")))
			return TextBox.wrap(e);
		else if (e.getTagName().equalsIgnoreCase("textarea"))
			return TextArea.wrap(e);
		return null;
	}

	/**
	 * Set up the JS-callable signature as a global JS function.
	 * 
	 */
	private native void publishInterfaces() /*-{
											$wnd.miceSetOwningDevices = @ch.unifr.pai.twice.widgets.mptransparentproxy.client.MPProxyBody::setOwningDevices(Ljava/lang/String;);
											$wnd.backInHistory = @com.google.gwt.user.client.History::back();
											$wnd.forwardInHistory = @com.google.gwt.user.client.History::forward();
											$wnd.reloadFrame = @com.google.gwt.user.client.Window.Location::reload();
											}-*/;
}
