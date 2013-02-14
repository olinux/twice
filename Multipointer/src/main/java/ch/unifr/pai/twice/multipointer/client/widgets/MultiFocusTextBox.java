package ch.unifr.pai.twice.multipointer.client.widgets;
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

import java.util.HashMap;
import java.util.Map;

import ch.unifr.pai.twice.multipointer.client.MultiCursorController;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

public class MultiFocusTextBox extends Composite implements HasValue<String> {

	private int padding = 5;
	private int fontSize = 13;
	private final Map<String, Cursor> cursors = new HashMap<String, Cursor>();

	FlowPanel p = new FlowPanel();
	private String value;
	AbsolutePanel multiFocus = new AbsolutePanel();
	private final int cursorSpeed = 700;
	private final Context2d context;
	private TextBox textBox = new TextBox();
	private final Canvas c;
	private Timer blinkTimer;
	private boolean cursorsVisible;

	private void showCursor() {
		Event.addNativePreviewHandler(new NativePreviewHandler() {

			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				if (event.getTypeInt() == Event.ONMOUSEUP
						&& !c.getElement().isOrHasChild(
								Element.as(event.getNativeEvent()
										.getEventTarget()))) {
					String uuid = MultiCursorController.getUUID(event
							.getNativeEvent());
					Cursor c = cursors.get(uuid);
					if (c != null) {
						c.hide();
					}
				}
			}
		});
	}
	
	private void setStyle(){
		getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		getElement().getStyle().setBorderWidth(1, Unit.PX);
	}

	private InputElement replacedElement;

	int currentWidth;
	int currentHeight;
	
	public void replaceTextInput(InputElement el) {
		if (!el.isDisabled() && !el.getAttribute("multifocus").equals("true")) {
			replacedElement = el;
			RootPanel.get().add(this);
			setValue(el.getValue());
			currentWidth = el.getOffsetWidth();
			currentHeight = el.getOffsetHeight();
			this.getElement().getStyle().setPosition(Position.RELATIVE);
			this.getElement().getStyle().setPadding(0, Unit.PX);
			this.getElement().setId(el.getId());
			refreshDisplay();
		}
	}
	
	public void refreshDisplay(){
		if(replacedElement!=null){
			if(replacedElement.getParentElement()!=null)
				replacedElement.getParentElement().insertAfter(this.getElement(), replacedElement);
			setStyle();
//			c.getElement().setAttribute("style", replacedElement.getAttribute("style"));
//			c.getElement().setAttribute("class", replacedElement.getAttribute("class"));
			copyProperties(replacedElement, this.getElement(), "border", "padding", "margin", "outline", "verticalAlign");
			this.setWidth(currentWidth+"px");
			this.setHeight(currentHeight+"px");
			replacedElement.setAttribute("multifocus", "true");
			replacedElement.setAttribute("id", "");
			replacedElement.getStyle().setDisplay(Display.NONE);
			
		}
	}
	
	private void copyProperties(Element original, Element target, String... properties){
		for(String property : properties)
			copyProperty(property, original, target);
	}
	
	private void copyProperty(String property, Element original, Element target){
		String value = original.getStyle().getProperty(property);
		if(value!=null && !value.isEmpty()){
			target.getStyle().setProperty(property, value);
		}
	}

	
	
	public MultiFocusTextBox() {
		blinkTimer = new Timer() {

			@Override
			public void run() {
				for (Cursor c : cursors.values()) {
					c.setVisible(cursorsVisible);
				}
				cursorsVisible = !cursorsVisible;
			}
		};
		blinkTimer.scheduleRepeating(cursorSpeed);
		p.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		c = Canvas.createIfSupported();
		c.setCoordinateSpaceWidth(10000);
		c.addStyleName("multiFocusWidget");
		c.getElement().getStyle().setBorderWidth(0, Unit.PX);
		c.getElement().getStyle().setProperty("outline", "none");
		c.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				processInput(MultiCursorController.getUUID(event
						.getNativeEvent()), event.getCharCode());
			}
		});
		c.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				Cursor c = cursors.get(MultiCursorController
						.getUUID(event.getNativeEvent()));
				if (c != null) {
					switch (event.getNativeKeyCode()) {
					case KeyCodes.KEY_LEFT:
						c.setPosition(Math.max(0, c.position - 1));
						scrollIfNecessary();
						break;
					case KeyCodes.KEY_RIGHT:
						c.setPosition(Math.min(value.length(), c.position + 1));
						scrollIfNecessary();
						break;
					case KeyCodes.KEY_UP:
						c.setPosition(0);
						scrollIfNecessary();
						break;
					case KeyCodes.KEY_DOWN:
						c.setPosition(value != null ? value.length() : 0);
						scrollIfNecessary();
						break;
					case KeyCodes.KEY_DELETE:
						if (value != null && c.position < value.length()) {
							setValue(value.substring(0, c.position)
									+ value.substring(c.position + 1));
							for (Cursor cursor : cursors.values()) {
								if (c.position < cursor.getPosition()) {
									cursor.setPosition(cursor.getPosition() - 1);
								}
							}
							scrollIfNecessary();
						}
						break;
					case KeyCodes.KEY_BACKSPACE:
						if (value != null && c.position > 0
								&& c.position <= value.length()) {
							setValue(value.substring(0, c.position - 1)
									+ value.substring(c.position));
							c.setPosition(c.position - 1);
							for (Cursor cursor : cursors.values()) {
								if (c.position < cursor.position) {
									cursor.setPosition(cursor.getPosition() - 1);
								}
							}
							scrollIfNecessary();
						}
						break;
					}
				}
			}
		});
		c.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				repositionCursor(MultiCursorController.getUUID(event
						.getNativeEvent()), MultiCursorController
						.getColorNative(event.getNativeEvent()), event
						.getRelativeX(c.getCanvasElement()), event
						.getRelativeY(c.getCanvasElement()));
			}
		});
		multiFocus.insert(c, 0, 0, 0);
		initWidget(multiFocus);
		context = c.getContext2d();
		context.setTextAlign(TextAlign.LEFT);
		context.setTextBaseline(TextBaseline.TOP);
		context.setFont("normal "+fontSize+"px sans-serif");
		c.getElement().getStyle().setPadding(padding, Unit.PX);
		setStyle();
		// TODO Auto-generated constructor stub
		// multiFocus.setVisible(false);

		multiFocus.setWidth("161px");
		multiFocus.setHeight("25px");

	}

	private void repositionCursor(String uuid, String color, int x, int y) {
		Cursor c = getOrCreateCursor(uuid, color);
		if (c != null) {
			c.setPosition(findChar(x));
		}
	}

	private int findChar(int x) {
		StringBuilder b = new StringBuilder();
		// remove padding space
		x = x - padding;
		if (value == null)
			return 0;
		for (char c : value.toCharArray()) {
			b.append(c);
			double textWidth = context.measureText(b.toString()).getWidth();
			if (x - 5 < textWidth) {
				double charWidth = context.measureText(String.valueOf(c))
						.getWidth();
				if (textWidth - (charWidth / 2.0) > x)
					return Math.max(b.length() - 1, 0);
				else
					return b.length();
			}
		}
		return b.length();
	}

	private void registerCursor(String uuid, Cursor c) {
		cursors.put(uuid, c);
	}

	protected class Cursor extends HTML {
		int x;
		int y;
		final String uuid;
		int position;

		private Cursor(String color, String uuid) {
			this.uuid = uuid;
			setColor(color);
			setWidth("1px");
			setHeight("20px");
			registerCursor(uuid, this);
		}

		private void setColor(String color) {
			getElement().getStyle().setBackgroundColor(color);
		}

		private void hide() {
			multiFocus.remove(this);

		}

		private void show() {
			multiFocus.add(this);
			multiFocus.setWidgetPosition(this, x + 5, y + 3);
			showCursor();

		}

		public void setPosition(int position) {
			for (Cursor c : cursors.values()) {
				c.hide();
			}
			this.position = position;
			this.x = (int) Math.max(
					0,
					context.measureText(
							value == null ? ""
									: position < value.length() ? value
											.substring(0, position) : value)
							.getWidth());
			for (Cursor c : cursors.values()) {
				c.show();
			}
		}

		public int getPosition() {
			return position;
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<String> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public String getValue() {
		return value;
	}

	protected Cursor getOrCreateCursor(String uuid, String color) {
		Cursor c = cursors.get(uuid);
		if (c == null) {
			c = new Cursor(color, uuid);
			registerCursor(uuid, c);
		} else {
			c.setColor(color);
		}
		return c;
	}

	protected Map<String, Cursor> getCursors() {
		return cursors;
	}

	private void processInput(String uuid, char c) {
		Cursor origincursor = cursors.get(uuid);
		int pos;
		if (origincursor != null) {
			pos = origincursor.position;
		} else {
			pos = value != null ? value.length() : 0;
		}
		if (value == null)
			setValue(String.valueOf(c));
		else if (pos <= value.length()) {
			setValue(value.substring(0, pos) + c
					+ ((pos == value.length()) ? "" : value.substring(pos)));
		}
		for (Cursor cursor : cursors.values()) {
			if (pos <= cursor.getPosition()) {
				cursor.setPosition(cursor.getPosition() + 1);
			}
		}
		scrollIfNecessary();
	}

	private void scrollIfNecessary() {
		int lastCursor = 0;
		for (Cursor cursor : cursors.values()) {
			if (cursor.position > lastCursor)
				lastCursor = cursor.position;
		}
		if (value != null) {
			int width = (int) context.measureText(
					value.substring(0, lastCursor)).getWidth()
					+ padding;
			int fullwidth = (int) context.measureText(value).getWidth();
			int relPosLeft = width - multiFocus.getElement().getScrollLeft();
			if (relPosLeft < 0) {
				multiFocus.getElement().setScrollLeft(width - padding);
			} else if (relPosLeft > multiFocus.getOffsetWidth() - padding) {
				multiFocus.getElement().setScrollLeft(
						width - multiFocus.getOffsetWidth() + padding);
			}
			if (multiFocus.getElement().getScrollLeft() > 0
					&& (multiFocus.getElement().getScrollLeft() + multiFocus
							.getOffsetWidth()) > fullwidth) {
				multiFocus.getElement().setScrollLeft(
						fullwidth - multiFocus.getOffsetWidth() + 2
								* padding);
			}
		}
	}

	@Override
	public void setValue(String value) {
		this.value = value;
		if (replacedElement != null)
			replacedElement.setValue(value);
		textBox.setValue(value);
		context.clearRect(0, 0, c.getOffsetWidth(), c.getOffsetHeight());
		context.setTextAlign(TextAlign.LEFT);
		context.setTextBaseline(TextBaseline.TOP);
		context.fillText(value, 0, 0);
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		setValue(value);
		// TODO
	}
}
