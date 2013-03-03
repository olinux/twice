package ch.unifr.pai.twice.widgets.mpproxyframe.client;

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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A more complex widget providing control mechanisms for a {@link MPFrame}
 * 
 * @author Oliver Schmid
 * 
 */
public class ComplexMPFrame extends SplitLayoutPanel {

	private final Button back = new Button("back");
	private final Button forward = new Button("forward");
	private final Button reload = new Button("reload");
	private final TextBox url = new TextBox();
	private final MPFrame frame = new MPFrame();
	private final Controller controller = new Controller(frame);

	public ComplexMPFrame() {
		super();
		HorizontalPanel p = new HorizontalPanel();
		p.add(back);
		p.add(forward);
		p.add(url);
		p.add(reload);
		p.setWidth("100%");
		p.setHeight("100%");

		back.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				frame.back();
			}
		});

		forward.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				frame.forward();
			}
		});

		reload.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				frame.reload();
			}
		});
		p.setCellWidth(url, "100%");

		addNorth(p, 30);
		url.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> arg0) {
				frame.setUrl(arg0.getValue());
			}
		});
		addSouth(controller, 170);
		add(frame);
	}

	public MPFrame getFrame() {
		return frame;
	}

}
