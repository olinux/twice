package ch.unifr.pai.twice.widgets.mpproxy.client.navig;

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
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TextBox;

/**
 * The URL box allows to input standard URLs which are then prefixed with the proxy servlet path. Additionally, the value of the URL box is updated when
 * following links within the frame.
 * 
 * @author Oliver Schmid
 * 
 */
public class Navigation {

	private TextBox urlBox;
	private final Element element;

	public Navigation() {
		super();
		element = DOM.getElementById("miceUrlBox");
		if (element != null) {
			urlBox = TextBox.wrap(element);
			History.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					updateAddressBar();
				}
			});

			urlBox.setHeight("25px");
			urlBox.getElement().getStyle().setFontSize(20, Unit.PX);
			urlBox.setWidth("100%");
			urlBox.addValueChangeHandler(new ValueChangeHandler<String>() {

				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					// Window.alert(Window.Location.getProtocol()+"//"+Window.Location.getHost()+"/"+event.getValue());
					Window.Location.replace(Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/" + event.getValue());
					// TODO Auto-generated method stub

				}
			});
			urlBox.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
				}
			});
		}
	}

	/**
	 * Set the value of the URL bar to the current URL without the proxy prefix
	 */
	private void updateAddressBar() {
		String proxyServlet = Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/";
		urlBox.setValue(Window.Location.getHref().substring(proxyServlet.length()));
	}

}
