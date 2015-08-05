package ch.unifr.pai.twice.widgets.mpproxy.client;

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

import ch.unifr.pai.twice.multipointer.provider.client.widgets.MultiFocusTextBox;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Element;
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

	// private static TextBox hiddenTextBoxForFocusControl = new TextBox();
	// private final String[] colors = { "red", "green", "blue", "yellow" };
	// private final Map<String, String> deviceToColor = new HashMap<String,
	// String>();
	// private final int currentColorIndex = -1;
	private static Map<String, TextBoxBase> focusedElement = new HashMap<String, TextBoxBase>();
	private static Set<String> owningDevices = new HashSet<String>();

	// private final MultiCursorController multiCursor = new
	// MultiCursorController();

	/**
	 * Replace all textboxes with multi focus text boxes
	 * 
	 * @param mainElement
	 */
	private void replaceAllTextBoxes(Element mainElement) {
		NodeList<com.google.gwt.dom.client.Element> inputFields = mainElement
				.getElementsByTagName("input");
		for (int i = 0; i < inputFields.getLength(); i++) {
			final com.google.gwt.dom.client.Element el = inputFields.getItem(i);
			String type = el.getAttribute("type");
			if (type == null || type.isEmpty() || type.equalsIgnoreCase("text")
					|| type.equalsIgnoreCase("search")) {
				MultiFocusTextBox box = new MultiFocusTextBox();
				box.replaceTextInput(InputElement.as(el));
				replacements.add(box);

				// Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				//
				// @Override
				// public void execute() {
				// el.getStyle().setDisplay(Display.NONE);
				// }
				// });

			}
		}
	}

	@Override
	public void initialize() {
		// multiCursor.start();
		super.initialize();
		replaceAllTextBoxes(RootPanel.getBodyElement());
		// // hiddenTextBoxForFocusControl.setHeight("0px");
		// // hiddenTextBoxForFocusControl.setWidth("0px");
		// // hiddenTextBoxForFocusControl.getElement().getStyle()
		// // .setPosition(Position.ABSOLUTE);
		// //
		// hiddenTextBoxForFocusControl.getElement().getStyle().setZIndex(-1);
		// // hiddenTextBoxForFocusControl.getElement().getStyle().setTop(0,
		// Unit.PX);
		// // hiddenTextBoxForFocusControl.getElement().getStyle()
		// // .setLeft(0, Unit.PX);
		// // RootPanel.get().add(hiddenTextBoxForFocusControl);
		// Event.addNativePreviewHandler(new NativePreviewHandler() {
		// public void onPreviewNativeEvent(NativePreviewEvent event) {
		//
		// // boolean doProcess = true;
		// //
		// // // Block all mouse events which are not originated from a
		// // // owningDevice (if defined)
		// // switch (event.getTypeInt()) {
		// // case Event.ONCLICK:
		// // case Event.ONMOUSEOVER:
		// // case Event.ONMOUSEUP:
		// // case Event.ONMOUSEDOWN:
		// // case Event.ONMOUSEMOVE:
		// // case Event.ONMOUSEOUT:
		// // case Event.ONDBLCLICK:
		// // case Event.ONMOUSEWHEEL:
		// // String device = MultiCursorController.getUUIDNative(event
		// // .getNativeEvent());
		// // if (owningDevices != null && owningDevices.size() > 0
		// // && !owningDevices.contains(device)) {
		// // doProcess = false;
		// // event.cancel();
		// // event.getNativeEvent().preventDefault();
		// // }
		// // break;
		// // }
		//
		// // if (doProcess) {
		// // Record the currently focused element per device
		// switch (event.getTypeInt()) {
		// case Event.ONMOUSEUP:
		// case Event.ONMOUSEDOWN:
		// case Event.ONCLICK:
		// Element e = event.getNativeEvent().getEventTarget()
		// .<Element> cast();
		// TextBoxBase base = getTextBoxBase(e);
		// TextBoxBase old = focusedElement
		// .remove(MultiCursorController.getUUIDNative(event
		// .getNativeEvent()));
		// if (old != null) {
		// old.getElement().getStyle().clearBorderColor();
		// RootPanel.detachNow(old);
		// }
		// if (base != null) {
		// String device = MultiCursorController.getUUIDNative(event
		// .getNativeEvent());
		// focusedElement.put(device, base);
		// String color = deviceToColor.get(device);
		// if (color == null) {
		// if (currentColorIndex > -1
		// && currentColorIndex < colors.length - 2) {
		// color = colors[currentColorIndex];
		// currentColorIndex++;
		// } else {
		// color = colors[0];
		// currentColorIndex = 0;
		// }
		// deviceToColor.put(device, color);
		// }
		//
		// base.getElement().getStyle()
		// .setBorderStyle(BorderStyle.SOLID);
		// base.getElement().getStyle()
		// .setBorderWidth(1, Unit.PX);
		// base.getElement().getStyle().setBorderColor(color);
		// // event.cancel();
		// // event.getNativeEvent().preventDefault();
		// // DomEvent.fireNativeEvent(Document.get().createMouseDownEvent(0,
		// // 0, 0, 0, 0, false, false, false, false, 0),
		// // hiddenTextBoxForFocusControl);
		// }
		// break;
		// }
		// }
		// }
		//
		// });
		//
		// // Scheduler.get().scheduleDeferred(new ScheduledCommand() {
		// //
		// // @Override
		// // public void execute() {
		// // hiddenTextBoxForFocusControl.setFocus(true);
		// // }
		// // });
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
	 * @return a textbox or textarea widget if the element is one of those,
	 *         otherwise null
	 */
	private TextBoxBase getTextBoxBase(Element e) {
		if (e.getTagName().equalsIgnoreCase("input")
				&& (e.getAttribute("type") == null
						|| e.getAttribute("type").isEmpty() || e.getAttribute(
						"type").equalsIgnoreCase("text")))
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
											$wnd.miceSetOwningDevices = @ch.unifr.pai.twice.widgets.mpproxy.client.MPProxyBody::setOwningDevices(Ljava/lang/String;);
											$wnd.backInHistory = @com.google.gwt.user.client.History::back();
											$wnd.forwardInHistory = @com.google.gwt.user.client.History::forward();
											$wnd.reloadFrame = @com.google.gwt.user.client.Window.Location::reload();
											}-*/;
}
