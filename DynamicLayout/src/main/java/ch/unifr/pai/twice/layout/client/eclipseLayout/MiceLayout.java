package ch.unifr.pai.twice.layout.client.eclipseLayout;

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
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The actual layouting logic for cursor-oriented devices
 * 
 * @author Oliver Schmid
 * 
 */
public class MiceLayout extends ResizeLayoutPanel {

	/**
	 * The widget that is attached to the root panel if the application does not have another component in fullscreen mode
	 */
	private Widget nonFullscreenWidget;

	/**
	 * The parent panel of the widget which is currently presented in fullscreen mode
	 */
	private SimplePanel originOfFullScreenWidget;

	/**
	 * The root panel
	 */
	private MiceSplitLayoutPanel thePanel;

	/**
	 * @return the root {@link MiceSplitLayoutPanel}
	 */
	public MiceSplitLayoutPanel getMainPanel() {
		if (thePanel == null) {
			thePanel = new MiceSplitLayoutPanel();
			add(thePanel);
		}
		return thePanel;
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.ResizeLayoutPanel#setWidget(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public void setWidget(Widget w) {
		if (nonFullscreenWidget == null)
			nonFullscreenWidget = w;
		super.setWidget(w);
	}

	/**
	 * Show the component widget in fullscreen mode
	 * 
	 * @param w
	 */
	public void setFullscreen(Widget w) {
		originOfFullScreenWidget = (SimplePanel) w.getParent();
		super.setWidget(w);
	}

	/**
	 * Close fullscreen mode and switch to standard mode again
	 */
	public void unsetFullscreen() {
		originOfFullScreenWidget.setWidget(getWidget());
		super.setWidget(nonFullscreenWidget);
	}

}
