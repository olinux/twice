package ch.unifr.pai.mindmap.client.mindmap;
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
import ch.unifr.pai.twice.multipointer.client.MultiCursorController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class BackgroundImgRepo extends AbsolutePanel {

	private FocusPanel fp = new FocusPanel();
	private FlowPanel hp = new FlowPanel();

	private native JsArray<JavaScriptObject> getDataTransferFiles(
			JavaScriptObject dataTransfer)/*-{											
											return  dataTransfer.files;
											}-*/;

	public native void addBackgroundImage(Element img,
			JavaScriptObject file)
	/*-{
		var readFileSize = 0;
		readFileSize += file.fileSize;
		// Only process image files. 
		var reader = new FileReader();
		//
		//// Create a closure to capture the file information. 
		reader.onload = (function(aFile) {
			return function(evt) {
				img.src = evt.target.result;
			}
		})(file);

		// Read in the image file as a data url. 
		reader.readAsDataURL(file);
		//} 
	}-*/;

	private final Image bgImg;
	private final Image previewImage = new Image();

	public void addButton(Widget w) {
		hp.insert(w, 0);
	}

	public BackgroundImgRepo(Image bgImg) {
		super();
		add(fp);
		add(previewImage);
		getElement().getStyle().setOverflow(Overflow.VISIBLE);
		this.bgImg = bgImg;
		this.setHeight("100%");
		this.setWidth("100%");
		fp.getElement().getStyle().setBackgroundColor("grey");
		fp.setWidth("100%");
		fp.setHeight("100%");
		fp.setWidget(hp);
		hp.setWidth("100%");
		hp.setHeight("100%");
		fp.setWidget(hp);
		previewImage.getElement().getStyle().setPosition(Position.ABSOLUTE);
		previewImage.getElement().getStyle().setTop(-120, Unit.PX);
		previewImage.getElement().getStyle().setHeight(110, Unit.PX);
		previewImage.getElement().getStyle().setBorderWidth(2, Unit.PX);
		previewImage.getElement().getStyle().setBorderColor("black");
		previewImage.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		previewImage.getElement().getStyle().setDisplay(Display.NONE);
		PushButton emptyBg = new PushButton(new Image(GWT.getModuleBaseURL()
				+ "images/emptyscreen.png")){
			@Override
			public void onBrowserEvent(Event event) {
				if (MultiCursorController.isDefaultCursor(event)) {
					super.onBrowserEvent(event);						
				}
			}
		};
		emptyBg.setTitle("Remove background-image");
		emptyBg.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		emptyBg.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (MultiCursorController.isDefaultCursor(event
						.getNativeEvent())) {
					BackgroundImgRepo.this.bgImg.setVisible(false);
				}
			}
		});
		hp.add(emptyBg);
		fp.addDropHandler(new DropHandler() {

			@Override
			public void onDrop(DropEvent event) {
				event.stopPropagation();
				event.preventDefault();
				JsArray<JavaScriptObject> files = getDataTransferFiles(event.getDataTransfer());
				for(int i=0; i<files.length(); i++){
					final Image img = new Image();
					PushButton button = new PushButton(img){
						@Override
						public void onBrowserEvent(Event event) {
							if (MultiCursorController.isDefaultCursor(event)) {
								super.onBrowserEvent(event);						
							}
						}
						
					};
					button.setTitle("Set this image as background");
					button.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							if (MultiCursorController.isDefaultCursor(event
									.getNativeEvent())) {
								BackgroundImgRepo.this.bgImg.setVisible(true);
								BackgroundImgRepo.this.bgImg.getElement()
										.setAttribute(
												"src",
												img.getElement()
														.getAttribute("src"));
							}
						}
					});
					button.addMouseOverHandler(new MouseOverHandler() {

						@Override
						public void onMouseOver(MouseOverEvent event) {
							if (MultiCursorController.isDefaultCursor(event
									.getNativeEvent())) {
								BackgroundImgRepo.this.previewImage.getElement()
										.getStyle().setDisplay(Display.BLOCK);
								BackgroundImgRepo.this.previewImage.getElement()
										.setAttribute(
												"src",
												img.getElement()
														.getAttribute("src"));
								Scheduler.get().scheduleDeferred(
										new ScheduledCommand() {

											@Override
											public void execute() {
												previewImage
														.getElement()
														.getStyle()
														.setLeft(
																img.getAbsoluteLeft()
																		- BackgroundImgRepo.this
																				.getAbsoluteLeft()
																		- previewImage
																				.getOffsetWidth()
																		/ 2
																		+ img.getOffsetWidth()
																		/ 2,
																Unit.PX);
											}
										});
							}
						}
					});
					button.addMouseOutHandler(new MouseOutHandler() {

						@Override
						public void onMouseOut(MouseOutEvent event) {
							if (MultiCursorController.isDefaultCursor(event
									.getNativeEvent())) {

								BackgroundImgRepo.this.previewImage.getElement()
										.getStyle().setDisplay(Display.NONE);
							}
						}
					});
					// img.getElement().getStyle().setMargin(5, Unit.PX);
					// img.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
					img.setHeight("35px");
					button.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
					hp.add(button);
					addBackgroundImage(img.getElement(), files.get(i));
				}
				
				
			
			}
		});
	}
}
