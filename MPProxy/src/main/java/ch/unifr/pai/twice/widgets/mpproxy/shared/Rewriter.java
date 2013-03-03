package ch.unifr.pai.twice.widgets.mpproxy.shared;

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
/**
 * The rewriting logic applicable by both - the client and the server side.
 * 
 * @author Oliver Schmid
 * 
 */
public class Rewriter {

	// private static final String lookbehind = "(([=\\(])((')|(\")))(/|http)";
	private static final String lookbehind = "((([=\\[,?])[ ]*((')|(\")))|(URL=))(/|http)";
	/**
	 * A look behind would have been more appropriate, unfortunately javascript does not support this.
	 */
	public static final String URLREWRITERREGEX = lookbehind + ".*?(?=['\"])";

	/**
	 * @param requestUrl
	 * @return the path to the proxy servlet extracted from the given request URL
	 */
	public static String getServletPath(String requestUrl) {
		return URLParser.getServletPathForRequest(requestUrl);
	}

	/**
	 * Translates the given URL to a proxy-prefixed URL
	 * 
	 * @param replace
	 * @param servletHost
	 * @param proxyHost
	 * @return
	 */
	public static String translateUrl(String replace, String servletHost, String proxyHost) {
		if (!servletHost.endsWith("/"))
			servletHost += '/';
		if (proxyHost != null && proxyHost.endsWith("/"))
			proxyHost = proxyHost.substring(0, proxyHost.length() - 1);
		if (replace.endsWith("\"") || replace.endsWith("'"))
			replace = replace.substring(0, replace.length() - 1);
		if (replace.matches(lookbehind + ".*")) {
			int http = replace.indexOf("http");
			int slash = replace.indexOf("/");
			int index;
			if (http == -1)
				index = slash;
			else if (slash == -1)
				index = http;
			else
				index = Math.min(http, slash);
			if (index == slash) {
				if (index < replace.length() - 1 && replace.charAt(slash + 1) == '/') {
					// Double slash
					return replace.substring(0, index) + servletHost + "http:" + replace.substring(index);
				}
				else {
					// Single slash
					return replace.substring(0, index) + servletHost + (proxyHost != null ? proxyHost : "") + replace.substring(index);
				}
			}
			else {
				return replace.substring(0, index) + servletHost + replace.substring(index);
			}
		}
		else {
			return replace;
		}
	}

	/**
	 * @param cleanUrl
	 * @param servletHost
	 * @param proxyHost
	 * @return
	 */
	public static String translateCleanUrl(String cleanUrl, String servletHost, String proxyHost) {
		if (!servletHost.endsWith("/"))
			servletHost += '/';

		if (proxyHost != null && proxyHost.endsWith("/"))
			proxyHost = proxyHost.substring(0, proxyHost.length() - 1);

		if (cleanUrl.startsWith("//"))
			cleanUrl = "http:" + cleanUrl;

		if (cleanUrl.startsWith("/")) {
			return servletHost + proxyHost + cleanUrl;
		}
		else if (cleanUrl.startsWith("http")) {
			return servletHost + cleanUrl;
		}
		else
			return cleanUrl;

	}
}
