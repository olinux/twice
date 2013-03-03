package ch.unifr.pai.twice.widgets.mpproxy.server;

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
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.unifr.pai.twice.widgets.mpproxy.shared.Rewriter;
import ch.unifr.pai.twice.widgets.mpproxy.shared.URLParser;

/**
 * Logic to rewrite URLs at the server side while passing through the web site content
 * 
 * @author Oliver Schmid
 * 
 */
public class URLRewriterServer {
	private final static Pattern URLREWRITERPATTERN = Pattern.compile(Rewriter.URLREWRITERREGEX);

	/**
	 * Apply the different filters to the content
	 * 
	 * @param content
	 * @param currentUrl
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String process(String content, String currentUrl) throws UnsupportedEncodingException {
		URLParser parser = new URLParser(currentUrl, Rewriter.getServletPath(currentUrl));
		Matcher m = URLREWRITERPATTERN.matcher(content);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String found = m.group();
			String replacement = Rewriter.translateUrl(found, parser.getServletPath(), parser.getProxyBasePath());
			if (replacement != null) {
				// Protect escaped values
				replacement = replacement.replace("\\", "\\\\");
				// Since dollar signs are interpreted as special characters in the matcher, they have to be escaped
				replacement = replacement.replace("$", "\\$");
				m.appendReplacement(sb, replacement);
			}
		}
		return m.appendTail(sb).toString();
	}

	/**
	 * @param content
	 * @return
	 */
	public static String removeTopHref(String content) {
		// TODO really find the last frame before the mice frame (which is one beyond the window.top)
		return content.replaceAll("top\\.location", "document.location");

	}
}
