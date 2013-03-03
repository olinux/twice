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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import ch.unifr.pai.twice.widgets.mpProxyScreenShot.server.ReadOnlyPresentation;
import ch.unifr.pai.twice.widgets.mpproxy.server.JettyProxy.ProcessResult;
import ch.unifr.pai.twice.widgets.mpproxy.shared.Constants;
import ch.unifr.pai.twice.widgets.mpproxy.shared.Rewriter;
import ch.unifr.pai.twice.widgets.mpproxy.shared.URLParser;

/**
 * Servlet filter for manipulations on the server side as well as the JavaScript injection for client-side control.
 * 
 * @author Oliver Schmid
 * 
 */
@WebFilter(urlPatterns = "*")
public class SimpleHttpUrlConnectionServletFilter implements Filter {

	private final JettyProxy servlet = new JettyProxy();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	/**
	 * @param request
	 * @return the full request string including GET-parameters
	 */
	private String getFullRequestString(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getRequestURL());
		if (request.getQueryString() != null) {
			sb.append("?");
			sb.append(request.getQueryString());
		}
		return sb.toString();
	}

	/**
	 * Apply the filter logic
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest genericRequest, ServletResponse genericResponse, FilterChain chain) throws IOException, ServletException {
		if (genericRequest instanceof HttpServletRequest && genericResponse instanceof HttpServletResponse) {
			HttpServletRequest request = (HttpServletRequest) genericRequest;
			HttpServletResponse response = (HttpServletResponse) genericResponse;
			if (request.getSession().getAttribute(Constants.uuidCookie) == null) {
				request.getSession().setAttribute(Constants.uuidCookie, UUID.randomUUID().toString());
			}
			response.addCookie(new Cookie(Constants.uuidCookie, request.getSession().getAttribute(Constants.uuidCookie).toString()));
			String fullUrl = getFullRequestString(request);

			fullUrl.replace("gwt.codesvr=127.0.0.1:9997&", "");
			String servletPath = getServletPath(request);
			if (!servletPath.endsWith("/"))
				servletPath += "/";

			URLParser parser = new URLParser(fullUrl, servletPath);
			String url = parser.getFullProxyPath();

			// Prevent the managing resources to be filtered.
			if (request.getRequestURL().toString().startsWith(servletPath + Constants.nonFilterPrefix) || (url != null && url.equals(fullUrl))) {
				chain.doFilter(genericRequest, genericResponse);
				return;
			}

			// The read only screen
			if (request.getRequestURL().toString().contains("miceScreenShot")) {
				String result = ReadOnlyPresentation.getScreenshotForUUID(request.getParameter("uuid"));
				PrintWriter w = response.getWriter();
				if (result == null) {
					w.println("No screenshot available");
				}
				else {
					w.print(result);
				}
				w.flush();
				w.close();
				return;
			}
			// ProxyURLParser parser = new ProxyURLParser(fullUrl);
			// String url = parser.writeRequestUrl();
			if (url == null || url.isEmpty() || !url.startsWith("http")) {
				// We've lost context - lets try to re-establish it from
				// other
				// sources...
				String newProxyBase = null;

				// ... a referer is the best hint
				String referer = request.getHeader("Referer");
				if (referer != null && !referer.isEmpty()) {
					URLParser refererParser = new URLParser(referer, Rewriter.getServletPath(referer));
					if (refererParser.getProxyBasePath() != null && !refererParser.getProxyBasePath().isEmpty()) {
						newProxyBase = refererParser.getProxyBasePath();
					}
				}
				// ... otherwise use the last used proxy (since it probably
				// is a
				// redirection we might have success with this)
				if (newProxyBase == null) {
					newProxyBase = (String) request.getSession().getAttribute("lastProxy");
				}

				// Now redirect the client to the new url
				if (newProxyBase != null) {
					url = newProxyBase + (url != null && !url.isEmpty() ? '/' + url : "/");
					response.sendRedirect(servletPath + url);

				}
				else {
					response.sendError(404);
				}
				return;

			}
			url = url.replace("\\|", "&#124;");
			// System.out.println(url);

			ProcessResult result = null;
			try {
				result = servlet.loadFromProxy(request, response, url, servletPath, parser.getProxyBasePath());
			}
			catch (UnknownHostException e) {
				// If we get a unknown host exception, we try it with the
				// referer
				String referer = request.getHeader("Referer");
				if (parser.getRefererRelative() != null && referer != null && !referer.isEmpty()) {
					URLParser refererParser = new URLParser(referer, Rewriter.getServletPath(referer));
					if (refererParser.getProxyBasePath() != null && !refererParser.getProxyBasePath().isEmpty()) {
						String newUrl = refererParser.getProxyBasePath() + parser.getRefererRelative();
						try {
							result = servlet.loadFromProxy(request, response, newUrl, servletPath, refererParser.getProxyBasePath());
						}
						catch (UnknownHostException e1) {
							result = null;
							response.sendError(404);
						}
					}
					else {
						result = null;
						response.sendError(404);
					}
				}
				else {
					result = null;
					response.sendError(404);
				}

			}

			if (result != null) {
				// If an error is returned, we don't need to process the
				// inputstream
				InputStream input;
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				OutputStream output = outputStream;
				if (result.isGzipped()) {
					output = new GZIPOutputStream(outputStream, 100000);
				}
				String s = URLRewriterServer.process(result.getContent(), fullUrl);
				s = URLRewriterServer.removeTopHref(s);
				if (request.getSession().getAttribute(Constants.miceManaged) == null
						|| !request.getSession().getAttribute(Constants.miceManaged).equals("true")) {
					s = s.replace("<head>", "<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1\">");
					// Pattern p = Pattern.compile("<body.*?>");
					// Matcher m = p.matcher(s);
					// StringBuffer sb = new StringBuffer();
					// while (m.find()) {
					// m.appendReplacement(
					// sb,
					// m.group()
					// + "<link href=\""
					// + servletPath
					// +
					// "miceproxy/navigation.css\" rel=\"stylesheet\" type=\"text/css\"/><div id=\"miceNavigation\"><input id=\"miceUrlBox\" type=\"text\" value=\""
					// + parser.getFullProxyPath()
					// +
					// "\"/></div><div id=\"contentWrapper\">");
					// }
					// s = m.appendTail(sb).toString();
					// s = s.replace("</body>",
					// "</div></body>");
				}

				// The page shall only be injected if it is a
				// html page and if it really has html content
				// (prevent e.g. blank.html to be injected)
				if (result.getContentType() != null && result.getContentType().contains("text/html") && (s.contains("body") || s.contains("BODY")))
					s += "<script type=\"text/javascript\" language=\"javascript\" src=\"" + servletPath + "miceproxy/miceproxy.nocache.js\"></script>";
				IOUtils.write(s, output, result.getCharset());
				output.flush();
				if (output instanceof GZIPOutputStream)
					((GZIPOutputStream) output).finish();
				outputStream.writeTo(response.getOutputStream());
			}

		}
	}

	@Override
	public void destroy() {
	}

	/**
	 * @param request
	 * @return the path to the current servlet
	 */
	public static String getServletPath(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
	}
}
