package ch.unifr.pai.twice.utils.device.rebind;

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

import java.util.SortedSet;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.linker.ConfigurationProperty;
import com.google.gwt.user.rebind.UserAgentPropertyGenerator;

/**
 * GWT by default does not allow unsupported browsers (which are very few, very exotic ones only) to execute the application. In terms of best effort, we
 * override this behavior by defining that unknown devices should be provided with the standard Firefox "gecko1_8" compatible implementation of the application.
 * 
 * Until now, we only know about the native browser of the Nintendo 3DS browser who is affected by this issue.
 * 
 * @author Oliver Schmid
 * 
 */
public class EnableUnsupportedBrowsers extends UserAgentPropertyGenerator {

	/**
	 * Overriding the default behaviour of GWT and let unknown devices to execute the Firefox "gecko1_8" version of the application.
	 * 
	 * @see com.google.gwt.user.rebind.UserAgentPropertyGenerator#generate(com.google.gwt.core.ext.TreeLogger, java.util.SortedSet, java.lang.String,
	 *      java.util.SortedSet)
	 */
	@Override
	public String generate(TreeLogger logger, SortedSet<String> possibleValues, String fallback, SortedSet<ConfigurationProperty> configProperties) {
		String value = super.generate(logger, possibleValues, fallback, configProperties);
		value = value.replace("return 'unknown';", "return 'gecko1_8';");
		return value;
	}
}
