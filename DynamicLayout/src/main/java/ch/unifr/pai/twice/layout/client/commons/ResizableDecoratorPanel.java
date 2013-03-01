package ch.unifr.pai.twice.layout.client.commons;

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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * The resizable decorator panel used to manage the resizing of the different split layout panels in the eclipse layout
 * 
 * @author Oliver Schmid
 * 
 */
public class ResizableDecoratorPanel extends DecoratorPanel implements RequiresResize {

	private final int splitterSize;

	public ResizableDecoratorPanel() {
		this(10);
	}

	public ResizableDecoratorPanel(int splitterSize) {
		super();
		this.splitterSize = splitterSize;
	}

	@Override
	public void onResize() {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				ResizableDecoratorPanel.this.getWidget().setWidth(
						Math.max(ResizableDecoratorPanel.this.getElement().getParentElement().getOffsetWidth() - splitterSize, 0) + "px");
				ResizableDecoratorPanel.this.getWidget().setHeight(
						Math.max(ResizableDecoratorPanel.this.getElement().getParentElement().getOffsetHeight() - splitterSize, 0) + "px");
			}
		});
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		onResize();
	}

}
