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
import ch.unifr.pai.twice.multipointer.provider.client.NoMultiCursorController;

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
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
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
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * A widget which allows (by the means of HTML5 drag and drop functionalities) to drag images from external sources (e.g. a file browser) on top of this panel.
 * On drop, it stores the image as a data-url and offers it as a thumbnail to be used for the background of the canvas
 * 
 * 
 * @author Oliver Schmid
 * 
 */
public class BackgroundImgRepo extends AbsolutePanel {

	private final FocusPanel fp = new FocusPanel();
	private final FlowPanel hp = new FlowPanel();

	/**
	 * Read the data transfer files from the native transfer object
	 * 
	 * @param dataTransfer
	 * @return
	 */
	private native JsArray<JavaScriptObject> getDataTransferFiles(JavaScriptObject dataTransfer)/*-{
		return dataTransfer.files;
	}-*/;

	/**
	 * Reads the provided file and fills the img element
	 * 
	 * @param img
	 * @param file
	 */
	public native void addBackgroundImage(Element img, JavaScriptObject file)
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

	/**
	 * The background image object which is filled with the data-url if the background image changes
	 */
	private final Image bgImg;
	/**
	 * The preview image which is shown above the image-buttons if the native mouse hovers them
	 */
	private final Image previewImage = new Image();

	/**
	 * Add an image button to the panel with a background-image trigger button
	 * 
	 * @param w
	 */
	public void addButton(Widget w) {
		hp.insert(w, 0);
	}

	/**
	 * @param bgImg
	 *            - the image object that shall be adapted accordingly to the chosen data-url
	 */
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

		// Add a button for cleaning the browser as well
		PushButton emptyBg = new PushButton(new Image(GWT.getModuleBaseURL() + "images/emptyscreen.png")) {
			@Override
			public void onBrowserEvent(Event event) {
				if (NoMultiCursorController.isDefaultCursor(event)) {
					super.onBrowserEvent(event);
				}
			}
		};
		emptyBg.setTitle("Remove background-image");
		emptyBg.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		emptyBg.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (NoMultiCursorController.isDefaultCursor(event.getNativeEvent())) {
					BackgroundImgRepo.this.bgImg.setVisible(false);
				}
			}
		});
		hp.add(emptyBg);
		fp.addDragOverHandler(new DragOverHandler() {

			@Override
			public void onDragOver(DragOverEvent event) {
				fp.getElement().getStyle().setBackgroundColor("darkgrey");
			}
		});
		fp.addDragLeaveHandler(new DragLeaveHandler() {

			@Override
			public void onDragLeave(DragLeaveEvent event) {
				fp.getElement().getStyle().setBackgroundColor("grey");
			}

		});
		fp.addDropHandler(new DropHandler() {

			/**
			 * Prevents the default behavior of the browser (otherwise, the browser would open the image in the current tab), reads the dragged files and adds
			 * them as {@link PushButton} to the panel.
			 * 
			 * @see com.google.gwt.event.dom.client.DropHandler#onDrop(com.google.gwt.event.dom.client.DropEvent)
			 */
			/*
			 * (non-Javadoc)
			 * @see com.google.gwt.event.dom.client.DropHandler#onDrop(com.google.gwt.event.dom.client.DropEvent)
			 */
			@Override
			public void onDrop(DropEvent event) {
				// event.stopPropagation();
				event.preventDefault();
				fp.getElement().getStyle().setBackgroundColor("grey");
				JsArray<JavaScriptObject> files = getDataTransferFiles(event.getDataTransfer());
				for (int i = 0; i < files.length(); i++) {
					final Image img = new Image();
					PushButton button = new PushButton(img) {
						@Override
						public void onBrowserEvent(Event event) {
							if (NoMultiCursorController.isDefaultCursor(event)) {
								super.onBrowserEvent(event);
							}
						}

					};
					button.setTitle("Set this image as background");
					button.addClickHandler(new ClickHandler() {

						/**
						 * Set the image of the button as the current background image
						 * 
						 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
						 */
						@Override
						public void onClick(ClickEvent event) {
							if (NoMultiCursorController.isDefaultCursor(event.getNativeEvent())) {
								BackgroundImgRepo.this.bgImg.setVisible(true);
								BackgroundImgRepo.this.bgImg.getElement().setAttribute("src", img.getElement().getAttribute("src"));
							}
						}
					});
					button.addMouseOverHandler(new MouseOverHandler() {

						/**
						 * Show the preview image at the appropriate position and replace the data-url
						 * 
						 * @see com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.google.gwt.event.dom.client.MouseOverEvent)
						 */
						@Override
						public void onMouseOver(MouseOverEvent event) {
							if (NoMultiCursorController.isDefaultCursor(event.getNativeEvent())) {
								BackgroundImgRepo.this.previewImage.getElement().getStyle().setDisplay(Display.BLOCK);
								BackgroundImgRepo.this.previewImage.getElement().setAttribute("src", img.getElement().getAttribute("src"));
								Scheduler.get().scheduleDeferred(new ScheduledCommand() {

									@Override
									public void execute() {
										previewImage
												.getElement()
												.getStyle()
												.setLeft(
														img.getAbsoluteLeft() - BackgroundImgRepo.this.getAbsoluteLeft() - previewImage.getOffsetWidth() / 2
																+ img.getOffsetWidth() / 2, Unit.PX);
									}
								});
							}
						}
					});
					button.addMouseOutHandler(new MouseOutHandler() {

						/**
						 * Hide the preview image
						 * 
						 * @see com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google.gwt.event.dom.client.MouseOutEvent)
						 */
						@Override
						public void onMouseOut(MouseOutEvent event) {
							if (NoMultiCursorController.isDefaultCursor(event.getNativeEvent())) {

								BackgroundImgRepo.this.previewImage.getElement().getStyle().setDisplay(Display.NONE);
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
