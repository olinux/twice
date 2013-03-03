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
import java.util.ArrayList;
import java.util.List;

import ch.unifr.pai.twice.multipointer.client.widgets.MultiFocusTextBox;
import ch.unifr.pai.twice.widgets.mpproxy.client.navig.Navigation;
import ch.unifr.pai.twice.widgets.mpproxy.shared.Rewriter;
import ch.unifr.pai.twice.widgets.mpproxy.shared.URLParser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Standard (non-multi pointer proxy)
 * 
 * @author Oliver Schmid
 * 
 */
public class ProxyBody {

	private static String[] attributesToManipulate = { "src", "href", "action" };
	private final ScreenShotDistributor screenShot = new ScreenShotDistributor();
	private static Navigation navigation = GWT.create(Navigation.class);

	protected static List<MultiFocusTextBox> replacements = new ArrayList<MultiFocusTextBox>();

	/**
	 * Register required listeners and set up the basic objects
	 */
	public void initialize() {
		Event.addNativePreviewHandler(new NativePreviewHandler() {

			/**
			 * Rewrites the URLs on mouse down if somebody clicks on an anchor element
			 * 
			 * @see com.google.gwt.user.client.Event.NativePreviewHandler#onPreviewNativeEvent(com.google.gwt.user.client.Event.NativePreviewEvent)
			 */
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				if (event.getTypeInt() == Event.ONMOUSEDOWN) {
					EventTarget t = event.getNativeEvent().getEventTarget();
					Element e = (Element) Element.as(t);
					if (e != null && e.getTagName().equalsIgnoreCase("a"))
						rewriteUrls(e);

				}
			}
		});
		Storage s = Storage.getSessionStorageIfSupported();
		if (s != null) {
			RootPanel.get().add(new Label("Multicursor: " + s.getItem("ch.unifr.pai.mice.deviceType")));
		}
		publishInterfaces();
		rewriteUrls((Element) Document.get().getDocumentElement());
		addDomChangeEvents();
		// screenShot.start();
		if (!isParentFrame()) {
			Element e = DOM.getElementById("miceNavigation");
			if (e != null)
				e.removeFromParent();
		}
	}

	/**
	 * Client side logic to rewrite a given URL
	 * 
	 * @param e
	 */
	private static void rewriteUrls(Element e) {
		URLParser parser = new URLParser(Window.Location.getHref(), Rewriter.getServletPath(Window.Location.getHref()));
		if (parser.getServletPath() != null && !parser.getServletPath().isEmpty() && parser.getProxyBasePath() != null && !parser.getProxyBasePath().isEmpty())
			rewriteUrl(e, parser.getServletPath(), parser.getProxyBasePath());
	}

	/**
	 * Client side logic to rewrite a given URL
	 * 
	 * @param element
	 * @param servletPath
	 * @param proxyPath
	 */
	public static void rewriteUrl(com.google.gwt.dom.client.Element element, String servletPath, String proxyPath) {
		NodeList<Node> nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.getItem(i);
			if (com.google.gwt.dom.client.Element.is(n)) {
				com.google.gwt.dom.client.Element e = com.google.gwt.dom.client.Element.as(n);
				if (e != null && e.getTagName() != null && e.getTagName().equalsIgnoreCase("a")) {
					AnchorElement anchor = AnchorElement.as(e);
					if (anchor.getHref() != null && !anchor.getHref().isEmpty())
						anchor.removeAttribute("onmousedown");
				}
				for (String att : attributesToManipulate) {
					String value = e.getAttribute(att);
					if (value != null && !value.startsWith(servletPath) && value.matches("((http)|/).*")) {
						String transformed = Rewriter.translateCleanUrl(value, servletPath, proxyPath);
						if (!transformed.equals(value))
							e.setAttribute(att, transformed);
					}
				}
				rewriteUrl(e, servletPath, proxyPath);
			}
		}
	}

	/**
	 * If something in the DOM has changed, call the different handlers and replace the standard widgets
	 * 
	 * @param evt
	 */
	public static void onDomChanged(Event evt) {
		if (Element.is(evt.getEventTarget())) {
			Element e = (Element) Element.as(evt.getEventTarget());
			rewriteUrls(e);
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
												$wnd.document.addEventListener('DOMSubtreeModified', @ch.unifr.pai.twice.widgets.mpproxy.client.ProxyBody::onDomChanged(Lcom/google/gwt/user/client/Event;), false);
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
