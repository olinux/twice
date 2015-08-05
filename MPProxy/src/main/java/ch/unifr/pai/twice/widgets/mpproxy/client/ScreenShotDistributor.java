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

import ch.unifr.pai.twice.widgets.mpproxy.shared.Rewriter;
import ch.unifr.pai.twice.widgets.mpproxy.shared.URLParser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Element;

/**
 * Takes a snapshot of the current HTML DOM structure, removes script tags and
 * submits the content to the server in regular intervals
 * 
 * @author Oliver Schmid
 * 
 */
public class ScreenShotDistributor {

	private String lastSent;
	private int lastTop = -1;
	private int lastLeft = -1;

	private static String lastTitle = "";
	private static String lastInput = "";
	private static String lastSelect = "";

	private int height = 932; // Window.getClientHeight()
	private int width = 1920; // Window.getClientWidth()

	private final Timer t = new Timer() {

		@Override
		public void run() {

			sendScreenShot(); // ScreenShot mode
			// sendMetaData(); // MetaData mode
		}
	};

	/**
	 * Begin to update the server with screenshots
	 */
	public void start() {

		t.scheduleRepeating(4000); // 4 seconds
	}

	/**
	 * Stop the updating of the server with screenshots
	 */
	public void stop() {
		t.cancel();
	}

	/**
	 * Send the screenshot to the server
	 */
	public void sendScreenShot() {

		String screen = Document.get().getDocumentElement().getInnerHTML();

		if (!screen.equals(lastSent) || lastLeft != Window.getScrollLeft()
				|| lastTop != Window.getScrollTop()) {
			String url = Window.Location.getHref();
			URLParser p = new URLParser(url,
					Rewriter.getServletPath(Window.Location.getHref()));
			RequestBuilder rb = new RequestBuilder(RequestBuilder.POST,
					GWT.getModuleBaseURL() + "manager?url="
							+ p.getProxyBasePath() + "&width=" + width
							+ "&height=" + height + "&top="
							+ Window.getScrollTop() + "&left="
							+ Window.getScrollLeft());

			lastSent = screen;
			lastLeft = Window.getScrollLeft();
			lastTop = Window.getScrollTop();

			// screen = screen.replace('\n', ' ');
			// screen = screen.replaceAll("<body",
			// "<body><div class=\"readOnlyView\" style=\"width:" +
			// Window.getClientWidth() + "; height:" + Window.getClientHeight()
			// + ";\"");
			// screen = screen.replaceAll("<\\/body>", "</div></body>");
			// screen = screen.replaceAll("(<script).*?(\\/script>)", "");

			try {
				rb.sendRequest(screen, new RequestCallback() { // screen

							@Override
							public void onResponseReceived(Request request,
									Response response) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onError(Request request,
									Throwable exception) {
								Window.alert("Screenshot sent");
							}
						});
			} catch (RequestException e) {
				e.printStackTrace();
			}

		}

	}

	public void sendMetaData() {
		List<String> mytext = new ArrayList<String>();
		List<String> mySelectiontext = new ArrayList<String>();

		String inputscreen = "";
		String selectscreen = "";
		String full = "";
		String domainUrl = "";

		String title = Document.get().getTitle();

		NodeList<com.google.gwt.dom.client.Element> iframeFields = Document
				.get().getElementsByTagName("iframe");

		for (int i = 0; i < iframeFields.getLength(); i++) {
			final com.google.gwt.dom.client.Element el = iframeFields
					.getItem(i);
			com.google.gwt.dom.client.Element parentEl = el.getParentElement();

			parentEl.removeChild(el);

		}

		NodeList<com.google.gwt.dom.client.Element> scriptFields = Document
				.get().getElementsByTagName("script");

		for (int i = 0; i < scriptFields.getLength(); i++) {
			final com.google.gwt.dom.client.Element el = scriptFields
					.getItem(i);

			if (el.getAttribute("src").contains("adobedtm")) {
				el.removeAttribute("src");
				com.google.gwt.dom.client.Element parentEl = el
						.getParentElement();
				parentEl.removeChild(el);
			}

		}

		NodeList<com.google.gwt.dom.client.Element> inputFields = Document.get().getElementsByTagName("input");
		NodeList<com.google.gwt.dom.client.Element> selectionFields = Document.get().getElementsByTagName("select");

		for (int i = 0; i < inputFields.getLength(); i++) {

			final com.google.gwt.dom.client.Element el = inputFields.getItem(i);
			String type = el.getAttribute("type");

			if (!type.equalsIgnoreCase("hidden")) {
				if (type == null || type.isEmpty()
						|| type.equalsIgnoreCase("text")
						|| type.equalsIgnoreCase("search")
						|| type.equalsIgnoreCase("radio")
						|| type.equalsIgnoreCase("checkbox")) {

					InputElement input = InputElement.as(el);

					if (type.equalsIgnoreCase("radio")) {
						if (input.isChecked() && input.getOffsetHeight() != 0
								&& input.getOffsetWidth() != 0) {
							NodeList<com.google.gwt.dom.client.Element> labelFields = input
									.getParentElement().getElementsByTagName(
											"label");

							if (labelFields.getLength() > 0) {
								for (int s = 0; s < labelFields.getLength(); s++) {
									final com.google.gwt.dom.client.Element label = labelFields
											.getItem(s);
									LabelElement labelinput = LabelElement
											.as(label);

									if (el.getAttribute("checked")
											.equalsIgnoreCase("")
											&& (input
													.getClassName()
													.equalsIgnoreCase(
															"b-booker-type__input b-booker-type__input_business-booker") || input
													.getClassName()
													.equalsIgnoreCase(
															"b-booker-type__input b-booker-type__input_leisure-booker"))) {
										mytext.add(labelinput.getInnerText());
									}
									if (!labelinput.getInnerText().isEmpty()
											&& !labelinput
													.getId()
													.equalsIgnoreCase(
															"extra-information-select-label-s")
											&& !labelinput
													.getId()
													.equalsIgnoreCase(
															"extra-information-select-label-d")) {

										mytext.add(labelinput.getInnerText());
									}
								}
							}
						}
					}

					else if (type.equalsIgnoreCase("checkbox")) {

						if (input.isChecked()) {
							NodeList<com.google.gwt.dom.client.Element> cb_spanFields = input
									.getFirstChildElement()
									.getElementsByTagName("span");

							if (cb_spanFields.getLength() > 0) {
								for (int s = 0; s < cb_spanFields.getLength(); s++) {
									final com.google.gwt.dom.client.Element span = cb_spanFields
											.getItem(s);
									SpanElement spaninput = SpanElement
											.as(span);
									if (!spaninput.getInnerText().isEmpty()) {

										mytext.add(spaninput.getInnerText());
									}
								}
							}
						}
					}

					else
					// create labels
					if (!input.isDisabled() && input.getOffsetHeight() != 0
							&& input.getOffsetWidth() != 0) {
						if (!input.getValue().isEmpty()) {
							if (input.getId().equalsIgnoreCase("home_from")
									|| input.getId().equalsIgnoreCase("from")
									|| input.getId().equalsIgnoreCase("Origin")) { // SBB
																					// //SWISS

								mytext.add("From: " + input.getValue());
							} else if (input.getId()
									.equalsIgnoreCase("home_to")
									|| input.getId().equalsIgnoreCase("to")
									|| input.getId().equals("Destination")) { // SBB
																				// //SWISS

								mytext.add("To: " + input.getValue());
							} else if (input.getId().equalsIgnoreCase(
									"home_via")
									|| input.getId().equalsIgnoreCase("via1")) { // SBB
								mytext.add("Via: " + input.getValue());
							} else if (input.getId().equalsIgnoreCase(
									"home_date")) { // SBB
								mytext.add("Date: " + input.getValue());
							} else if (input.getId().equalsIgnoreCase(
									"home_time")
									|| input.getId().equalsIgnoreCase(
											"hfs_time")) { // SBB
								mytext.add("Time: " + input.getValue());
							} else if (input.getId().equals("destination")) { // Booking
								mytext.add("Destination/Hotel Name: "
										+ input.getValue());
							} else if (!input.getId().equalsIgnoreCase(
									"newsletter_to")) {
								mytext.add(input.getValue());
							}
						}
					}
				}
			}

		}

		for (int a = 0; a < selectionFields.getLength(); a++) {
			final com.google.gwt.dom.client.Element ele = selectionFields
					.getItem(a);

			SelectElement selectinput = SelectElement.as(ele);

			if (selectinput.getOffsetHeight() != 0
					&& selectinput.getOffsetWidth() != 0) {
				NodeList<OptionElement> options = selectinput.getOptions();

				for (int j = 0; j < options.getLength(); j++) {
					if (options.getItem(j).isSelected()
							&& !options.getItem(j).isDisabled()) {
						if (!options.getItem(j).getInnerText().isEmpty()) {
							if (selectinput.getName().equalsIgnoreCase(
									"checkin_monthday")) {
								mySelectiontext.add("Check-in Day: "
										+ options.getItem(j).getInnerText());
							} else if (selectinput.getName().equalsIgnoreCase(
									"checkin_year_month")) {
								mySelectiontext.add("Check-in Month: "
										+ options.getItem(j).getInnerText());
							} else if (selectinput.getName().equalsIgnoreCase(
									"checkout_monthday")) {
								mySelectiontext.add("Check-out Day: "
										+ options.getItem(j).getInnerText());
							} else if (selectinput.getName().equalsIgnoreCase(
									"checkout_year_month")) {
								mySelectiontext.add("Check-out Month: "
										+ options.getItem(j).getInnerText());
							} else if (selectinput
									.getClassName()
									.equalsIgnoreCase(
											"b-selectbox__element b-selectbox__groupselection")) {
								mySelectiontext.add("Guests: "
										+ options.getItem(j).getInnerText());
							} else if (selectinput.getName().equalsIgnoreCase(
									"no_rooms")) {
								mySelectiontext.add("Rooms: "
										+ options.getItem(j).getInnerText());
							} else if (selectinput.getName().equalsIgnoreCase(
									"group_adults")) {
								mySelectiontext.add("Adults: "
										+ options.getItem(j).getInnerText());
							} else if (selectinput.getName().equalsIgnoreCase(
									"group_children")) {
								mySelectiontext.add("Children: "
										+ options.getItem(j).getInnerText());
							}

							else {
								mySelectiontext.add(options.getItem(j)
										.getInnerText()); // "s: "+
							}
						}

					}

				}
			}
		}

		String url = Window.Location.getHref();

		URLParser p = new URLParser(url,
				Rewriter.getServletPath(Window.Location.getHref()));

		RequestBuilder rb = new RequestBuilder(RequestBuilder.POST,
				GWT.getModuleBaseURL() + "manager?url=" + p.getProxyBasePath()
						+ "&width=" + 1200 + "&height=" + 600 + "&top="
						+ Window.getScrollTop() + "&left="
						+ Window.getScrollLeft());

		if (p.getProxyBasePath().contains("adobedtm")) {  //due to screenshot input problem in SBB, presents the previous home page

			Element input_element = Document.get().getElementById("from");
			InputElement input = InputElement.as(input_element);

			String value = input.getValue();

			full = lastTitle + '\n' + "- - - - - - - - - - - - - - - - - "
					+ value + '\n' + lastSelect + '\n'
					+ "- - - - - - - - - - - - - - - - - " + '\n'
					+ "http://www.sbb.ch";
		}

		else {

			for (int k = 0; k < mytext.size(); k++) {
				inputscreen = inputscreen + '\n' + mytext.get(k);
			}

			for (int l = 0; l < mySelectiontext.size(); l++) {
				selectscreen = selectscreen + '\n' + mySelectiontext.get(l);
			}

			domainUrl = p.getProxyBasePath();

			full = title + '\n' + "- - - - - - - - - - - - - - - - - "
					+ inputscreen + '\n' + selectscreen + '\n'
					+ "- - - - - - - - - - - - - - - - - " + '\n' + domainUrl;

			lastTitle = title;
			lastInput = inputscreen;
			lastSelect = selectscreen;

		}

		try {
			rb.sendRequest(full, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,
						Response response) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onError(Request request, Throwable exception) {
					Window.alert("Screenshot sent");
				}
			});
		} catch (RequestException e) {
			e.printStackTrace();
		}

	}

}
