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
 * Parsing logic of a URL dependent on the current servletPath
 * 
 * @author Oliver Schmid
 * 
 */
public class URLParser {
	private String proxyBasePath;
	private String fullProxyPath;
	private final String servletPath;
	private String refererRelative;

	public URLParser(String currentPath, String servletPath) {
		this.servletPath = servletPath;
		int lastIndex = currentPath.lastIndexOf(servletPath);
		if (lastIndex != -1) {
			fullProxyPath = currentPath.substring(lastIndex + servletPath.length());
			int endOfProtocol = fullProxyPath.indexOf("//");
			if (endOfProtocol != -1) {
				endOfProtocol += 2;
				int endOfProxyHostName = fullProxyPath.indexOf('/', endOfProtocol);
				String domain;
				if (endOfProxyHostName != -1) {
					domain = fullProxyPath.substring(endOfProtocol, endOfProxyHostName);
					proxyBasePath = fullProxyPath.substring(0, endOfProxyHostName);
				}
				else {
					domain = fullProxyPath.substring(endOfProtocol);
					fullProxyPath = fullProxyPath + '/';
					proxyBasePath = fullProxyPath;
				}
				if (domain.indexOf('.') == -1 && domain.indexOf(':') == -1) {
					refererRelative = fullProxyPath.substring(endOfProtocol);
				}
			}
		}
	}

	public String getRefererRelative() {
		return refererRelative;
	}

	public String getFullProxyPath() {
		return fullProxyPath;
	}

	public String getProxyBasePath() {
		return proxyBasePath;
	}

	public String getServletPath() {
		return servletPath;
	}

	public static String getServletPathForRequest(String requestUrl) {
		// Second "http"
		if (requestUrl == null)
			return null;
		int index = requestUrl.indexOf("http", 1);
		if (index == -1)
			return requestUrl;
		return requestUrl.substring(0, index);
	}
}
