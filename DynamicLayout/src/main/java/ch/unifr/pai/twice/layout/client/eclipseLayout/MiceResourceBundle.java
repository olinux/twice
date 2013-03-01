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
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resource bundle for the styles of the cursor layout
 * 
 * @author Oliver Schmid
 * 
 */
public interface MiceResourceBundle extends ClientBundle {
	/**
	 * @return the css style
	 */
	@Source("MiceLayout.css")
	MiceLayoutStyle miceLayoutStyle();

	/**
	 * @return the close button of a tab
	 */
	@Source("close.png")
	ImageResource closeButton();

	/**
	 * @return the fullscreen button
	 */
	@Source("fullscreen.png")
	ImageResource fullscreenButton();

	/**
	 * @return the button to lave the fullscreen mode
	 */
	@Source("nofullscreen.png")
	ImageResource nofullscreenButton();
}
