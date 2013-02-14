package ch.unifr.pai.twice.widgets.client;
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

import ch.unifr.pai.twice.comm.serverPush.client.RemoteWidget;
import ch.unifr.pai.twice.utils.device.client.UUID;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.TextBox;

public class MultiFocusTextBox extends Composite implements HasValue<String>{

	private final Map<String, Cursor> cursors = new HashMap<String, Cursor>();
	
	
	FlowPanel p = new FlowPanel();
	private String value;	
	AbsolutePanel multiFocus = new AbsolutePanel();
	private final int cursorSpeed = 700;
	String[] colors = new String[]{"red", "blue"};
	Cursor blueCursor = new Cursor("blue", UUID.createNew());
	Cursor redCursor = new Cursor("red", UUID.createNew());
	private final Context2d context;
	private TextBox textBox = new TextBox();
	private final Canvas c;
	private Timer blinkTimer;
	private boolean cursorsVisible;	
	
	
	
	
	public MultiFocusTextBox() {
		blinkTimer = new Timer(){

			@Override
			public void run() {
				for(Cursor c : cursors.values()){
					c.setVisible(cursorsVisible);
				}
				cursorsVisible = !cursorsVisible;
			}
		};
		blinkTimer.scheduleRepeating(cursorSpeed);
		p.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		c = Canvas.createIfSupported();
		c.getElement().getStyle().setBorderWidth(0, Unit.PX);
		c.getElement().getStyle().setProperty("outline", "none");
		c.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				//TODO if it is a new device, create a new cursor with the 
				repositionCursor(null, event.getRelativeX(c.getCanvasElement()), event.getRelativeY(c.getCanvasElement()));
			}
		});
		multiFocus.insert(c, 0, 0, 0);
		initWidget(multiFocus);
		getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		getElement().getStyle().setBorderWidth(1, Unit.PX);
		c.getElement().getStyle().setMargin(5, Unit.PX);
		context = c.getContext2d();
		context.setTextAlign(TextAlign.LEFT);
		context.setTextBaseline(TextBaseline.TOP);
		context.setFont("13px sans-serif;");
		
		// TODO Auto-generated constructor stub
//		multiFocus.setVisible(false);
		
		multiFocus.setWidth("161px");
		multiFocus.setHeight("28px");
		
	}
	

	
	
	private void repositionCursor(String deviceId, int x, int y){
		blueCursor.setPosition(findChar(x));
	}
	
	private int findChar(int x){
		StringBuilder b = new StringBuilder();
		for(char c : value.toCharArray()){
			b.append(c);
			double textWidth = context.measureText(b.toString()).getWidth();
			if(x<textWidth){
				double charWidth = context.measureText(String.valueOf(c)).getWidth();
				if(textWidth-(charWidth/2.0)>x)
					return Math.max(b.length()-1, 0);
				else
					return b.length();
			}	
		}
		return b.length();
	}
	
	
	private void registerCursor(String uuid, Cursor c){
		cursors.put(uuid, c);
	}
	
	private void unregisterCursor(String uuid){
		cursors.remove(uuid);
	}
	
	
	protected class Cursor extends HTML{
		int x;
		int y;
		final String uuid;
		int position;
		HandlerRegistration reg;
		
		
		private Cursor(String color, String uuid){
			this.uuid = uuid;
			getElement().getStyle().setBackgroundColor(color);
			setWidth("1px");
			setHeight("18px");
		}
		
		private void hide(){
			multiFocus.remove(this);
			unregisterCursor(uuid);
			if(reg!=null){
				reg.removeHandler();
				reg = null;
			}
		}
		
		private void show(){
			registerCursor(uuid, this);
			multiFocus.add(this);
			multiFocus.setWidgetPosition(this, x+5, y+5);
			reg = Event.addNativePreviewHandler(new NativePreviewHandler() {
				
				@Override
				public void onPreviewNativeEvent(NativePreviewEvent event) {
					if(event.getTypeInt() == Event.ONMOUSEUP && !c.getElement().isOrHasChild(Element.as(event.getNativeEvent().getEventTarget()))){
						//TODO enable after testing
						//						hide();
					}					
				}
			});
			
		}
		
		public void setPosition(int position){
			hide();		
			this.position = position;
			this.x = (int)Math.max(0, context.measureText(position<value.length() ? value.substring(0, position) : value).getWidth());
			show();
		}
		
		public int getPosition(){
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
	
	protected Cursor getOrCreateCursor(String uuid){
		Cursor c = cursors.get(uuid);
		if(c==null){
			c = new Cursor("blue", uuid);
			registerCursor(uuid, c);
		}
		return c;
	}

	protected Map<String, Cursor> getCursors(){
		return cursors;
	}
	
	private void processInput(String uuid, int pos, char c){
		if(pos<=value.length()){
			setValue(value.substring(0, pos)+c+((pos==value.length())? "" : value.substring(pos)));
		}
		for(Cursor cursor : cursors.values()){
			if(pos<=cursor.getPosition()){
				cursor.setPosition(cursor.getPosition()+1);
			}
		}
	}
	
	
	@Override
	public void setValue(String value) {
		this.value = value;
		textBox.setValue(value);
		context.clearRect(0, 0, c.getOffsetWidth(), c.getOffsetHeight());
		context.fillText(value, 0, 0);		
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		setValue(value);
		//TODO
	}
}
