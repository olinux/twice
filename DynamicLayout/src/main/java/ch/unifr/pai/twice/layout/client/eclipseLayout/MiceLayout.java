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

public class MiceLayout extends ResizeLayoutPanel {

	private Widget nonFullscreenWidget;
	
	private SimplePanel originOfFullScreenWidget;

	private MiceSplitLayoutPanel thePanel;
	
	public MiceSplitLayoutPanel getMainPanel(){
		if(thePanel==null){
			thePanel = new MiceSplitLayoutPanel();
			add(thePanel);
		}
		return thePanel;
	}
	
	@Override
	public void setWidget(Widget w) {
		if(nonFullscreenWidget==null)
			nonFullscreenWidget = w;
		super.setWidget(w);
	}
	
	public void setFullscreen(Widget w){
		originOfFullScreenWidget = (SimplePanel)w.getParent();
		super.setWidget(w);
	}
	
	public void unsetFullscreen(){
		originOfFullScreenWidget.setWidget(getWidget());
		super.setWidget(nonFullscreenWidget);
	}
	
}
