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
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.ImportedWithPrefix;

/**
 * The CSS resource bundle for the mice layout stylesheet
 * 
 * @author Oliver Schmid
 * 
 */
@ImportedWithPrefix("miceLayout")
public interface MiceLayoutStyle extends CssResource {

	String hoverTabBar();

	String hoverGhostTab();

	String hoverAddPanel();

	String tabButton();

	String tabPanelButton();

}
