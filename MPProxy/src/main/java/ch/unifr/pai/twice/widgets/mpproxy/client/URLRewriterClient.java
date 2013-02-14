package ch.unifr.pai.twice.widgets.mpproxy.client;
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
import ch.unifr.pai.twice.widgets.mpproxy.shared.Rewriter;

import com.google.gwt.regexp.shared.RegExp;

public class URLRewriterClient {

	public static final RegExp p = RegExp.compile(Rewriter.URLREWRITERREGEX);
	
//	public static String process(String content, String currentUrl) {
//		NewURLParser parser = new NewURLParser(currentUrl);
//		Matcher m = URLREWRITERPATTERN.matcher(content);
//		StringBuffer sb = new StringBuffer();
//		while (m.find()) {
//			m.appendReplacement(sb, NewRewriter.translateUrl(m.group(),  parser.getServletPath(), parser.getProxyBasePath()));
//		}
//		return m.appendTail(sb).toString();
//	}
	
}
