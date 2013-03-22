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
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The toolbar displayed on the shared screen at the bottom that allows the native cursor of the shared screen device to enable/disable functionalities and
 * gives it some extra power
 * 
 * @author Oliver Schmid
 * 
 */
public class Toolbar extends VerticalPanel {

	private final FlowPanel masterToolbox = new FlowPanel();
	NoMultiCursorController controller = GWT.create(NoMultiCursorController.class);
	private final ListBox lb = new ListBox();
	/**
	 * A textbox for the definition of the maximum number of mouse pointers on the screen
	 */
	TextBox maxCursorsOnScreen = new TextBox();
	/**
	 * button to toggle the visibility of the content of the notes
	 */
	final ToggleButton discloseNotes = new ToggleButton(new Image(GWT.getModuleBaseURL() + "images/notesvisible.png"), new Image(GWT.getModuleBaseURL()
			+ "images/noteshidden.png")) {
		@Override
		public void onBrowserEvent(Event event) {
			if (NoMultiCursorController.isDefaultCursor(event)) {
				super.onBrowserEvent(event);
			}
		}
	};
	/**
	 * button to enable / disable the text input functionality from remote devices
	 */
	final ToggleButton enableTextInput = new ToggleButton(new Image(GWT.getModuleBaseURL() + "images/texteditenabled.png"), new Image(GWT.getModuleBaseURL()
			+ "images/texteditdisabled.png")) {
		@Override
		public void onBrowserEvent(Event event) {
			if (NoMultiCursorController.isDefaultCursor(event)) {
				super.onBrowserEvent(event);
			}
		}
	};
	final ToggleButton enableMultiCursor = new ToggleButton(new Image(GWT.getModuleBaseURL() + "images/cursorenabled.png"), new Image(GWT.getModuleBaseURL()
			+ "images/cursordisabled.png")) {
		@Override
		public void onBrowserEvent(Event event) {
			if (NoMultiCursorController.isDefaultCursor(event)) {
				super.onBrowserEvent(event);
			}
		}
	};
	final TextSize textSize;

	/**
	 * @param the
	 *            assigned canvas
	 */
	public Toolbar(final MindMapCanvas canvas) {
		textSize = new TextSize(new AsyncCallback<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				canvas.setFontSizeOfNotes(result);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
		controller.start();
		maxCursorsOnScreen.setValue(String.valueOf(controller.getMaxCursorsOnScreen()));
		maxCursorsOnScreen.getElement().getStyle().setFontSize(28, Unit.PX);
		maxCursorsOnScreen.getElement().getStyle().setPosition(Position.RELATIVE);
		maxCursorsOnScreen.getElement().getStyle().setTop(-7, Unit.PX);
		maxCursorsOnScreen.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if (event.getValue() != null) {
					try {
						controller.setMaxCursorsOnScreen(Integer.parseInt(event.getValue().trim()));
					}
					catch (NumberFormatException e) {
						maxCursorsOnScreen.setText(String.valueOf(controller.getMaxCursorsOnScreen()));
					}
				}
				else {
					maxCursorsOnScreen.setText(String.valueOf(controller.getMaxCursorsOnScreen()));
				}

			}

		});
		maxCursorsOnScreen.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!NoMultiCursorController.isDefaultCursor(event.getNativeEvent())) {
					event.preventDefault();
				}
			}
		});
		maxCursorsOnScreen.setWidth("35px");
		maxCursorsOnScreen.setTitle("Set the number of maximal cursors on the screen");
		discloseNotes.setDown(true);
		discloseNotes.setTitle("Show or hide the content of the notes");
		discloseNotes.setWidth("70px");
		discloseNotes.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		enableTextInput.setWidth("70px");
		enableTextInput.setTitle("Enable or disable text input");
		enableTextInput.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		enableMultiCursor.setWidth("70px");
		enableMultiCursor.setTitle("Enable or disable remote cursor control");
		enableMultiCursor.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		enableMultiCursor.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (!event.getValue())
					controller.start();
				else
					controller.stop();
			}
		});

		// lb.addItem("10px", "10");
		// lb.addItem("12px", "12");
		// lb.addItem("14px", "14");
		// lb.addItem("16px", "16");
		// lb.addItem("18px", "18");
		// lb.addItem("20px", "20");
		// lb.addItem("22px", "22");
		// lb.setItemSelected(2, true);
		// lb.addMouseDownHandler(new MouseDownHandler() {
		//
		// @Override
		// public void onMouseDown(MouseDownEvent event) {
		// if (!MultiCursorController.isDefaultCursor(event
		// .getNativeEvent())) {
		// event.preventDefault();
		// }
		// }
		// });
		// lb.addChangeHandler(new ChangeHandler() {
		//
		// @Override
		// public void onChange(ChangeEvent event) {
		// canvas.setFontSizeOfNotes(Integer.parseInt(lb.getValue(lb
		// .getSelectedIndex())));
		// }
		// });

		final BackgroundImgRepo repo = new BackgroundImgRepo(canvas.getBGImage());
		repo.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		repo.addButton(textSize.getDecreaseButton());
		repo.addButton(textSize.getIncreaseButton());
		textSize.getDecreaseButton().setTitle("Decrease the text size of the notes");
		textSize.getIncreaseButton().setTitle("Increase the text size of the notes");
		repo.addButton(enableTextInput);
		repo.addButton(maxCursorsOnScreen);
		repo.addButton(enableMultiCursor);
		repo.addButton(discloseNotes);
		add(masterToolbox);
		// masterToolbox.add(discloseNotes);
		// masterToolbox.add(textSize.getDecreaseButton());
		// masterToolbox.add(textSize.getIncreaseButton());
		// masterToolbox.add(enableTextInput);
		// masterToolbox.add(enableMultiCursor);

		discloseNotes.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				canvas.toggleDisclosureOfNotes(event.getValue());
			}
		});
		masterToolbox.add(repo);
		masterToolbox.getElement().getStyle().setPosition(Position.ABSOLUTE);
		masterToolbox.getElement().getStyle().setBottom(0, Unit.PX);
		masterToolbox.getElement().getStyle().setLeft(0, Unit.PX);
		masterToolbox.getElement().getStyle().setRight(0, Unit.PX);

	}

	/**
	 * @return true if the notes are disclosed false otherwise (also used for newly created notes to define their initial state)
	 */
	public boolean isDiscloseNotes() {
		return discloseNotes.getValue();
	}

	/**
	 * @return the current font size of the notes
	 */
	public int getFontSize() {
		return textSize.getTextSize();
	}

	/**
	 * @return true if remote users can create new notes / enter text, false otherwise.
	 */
	public boolean isTextInputEnabled() {
		return !enableTextInput.getValue();
	}
}
