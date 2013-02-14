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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.SystemDefaultHttpClient;

import ch.unifr.pai.twice.widgets.mpProxyScreenShot.server.ReadOnlyPresentation;
import ch.unifr.pai.twice.widgets.mpproxy.shared.Constants;
import ch.unifr.pai.twice.widgets.mpproxy.shared.Rewriter;
import ch.unifr.pai.twice.widgets.mpproxy.shared.URLParser;

//@WebFilter(urlPatterns = "*")
public class SimpleHttpUrlConnectionServletFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	private String getFullRequestString(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getRequestURL());
		if (request.getQueryString() != null) {
			sb.append("?");
			sb.append(request.getQueryString());
		}
		return sb.toString();
	}

	public void doFilter(ServletRequest genericRequest,
			ServletResponse genericResponse, FilterChain chain)
			throws IOException, ServletException {
		if (genericRequest instanceof HttpServletRequest
				&& genericResponse instanceof HttpServletResponse) {
			HttpServletRequest request = (HttpServletRequest) genericRequest;
			HttpServletResponse response = (HttpServletResponse) genericResponse;
			try {
				if (request.getSession().getAttribute(Constants.uuidCookie) == null) {
					request.getSession().setAttribute(Constants.uuidCookie,
							UUID.randomUUID().toString());
				}
				response.addCookie(new Cookie(Constants.uuidCookie, request
						.getSession().getAttribute(Constants.uuidCookie)
						.toString()));
				String fullUrl = getFullRequestString(request);

				fullUrl.replace("gwt.codesvr=127.0.0.1:9997&", "");
				String servletPath = getServletPath(request);
				if (!servletPath.endsWith("/"))
					servletPath += "/";

				// System.out.println("Processing: " + fullUrl);
				URLParser parser = new URLParser(fullUrl, servletPath);
				String url = parser.getFullProxyPath();

				// Prevent the managing resources to be filtered.
				if (request.getRequestURL().toString()
						.startsWith(servletPath + Constants.nonFilterPrefix)
						|| (url != null && url.equals(fullUrl))) {
					chain.doFilter(genericRequest, genericResponse);
					return;
				}

				// The read only screen
				if (request.getRequestURL().toString()
						.contains("miceScreenShot")) {
					String result = ReadOnlyPresentation
							.getScreenshotForUUID(request.getParameter("uuid"));
					PrintWriter w = response.getWriter();
					if (result == null) {
						w.println("No screenshot available");
					} else {
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
						URLParser refererParser = new URLParser(referer,
								Rewriter.getServletPath(referer));
						if (refererParser.getProxyBasePath() != null
								&& !refererParser.getProxyBasePath().isEmpty()) {
							newProxyBase = refererParser.getProxyBasePath();
						}
					}
					// ... otherwise use the last used proxy (since it probably
					// is a
					// redirection we might have success with this)
					if (newProxyBase == null) {
						newProxyBase = (String) request.getSession()
								.getAttribute("lastProxy");
					}

					// Now redirect the client to the new url
					if (newProxyBase != null) {
						url = newProxyBase
								+ (url != null && !url.isEmpty() ? '/' + url
										: "/");
						response.sendRedirect(servletPath + url);
						// System.out.println("Redirect to: " + servletPath +
						// url);
					} else {
						response.sendError(404);
					}
					return;

				}

				HttpUriRequest req = null;
				boolean post = false;
				url = url.replace("\\|", "&#124;");
				// System.out.println(url);
				if (request.getMethod().equalsIgnoreCase("GET")) {
					req = new HttpGet(url);
				} else if (request.getMethod().equalsIgnoreCase("POST")) {
					req = new HttpPost(url);
					post = true;
					HttpEntity entity = new InputStreamEntity(
							request.getInputStream(),
							request.getContentLength());
					((HttpPost) req).setEntity(entity);
				}
				if (req != null) {
					Enumeration<String> headers = request.getHeaderNames();
					while (headers.hasMoreElements()) {
						String name = headers.nextElement();
						boolean process = !name.equalsIgnoreCase("host")
								&& (!post || !name
										.equalsIgnoreCase("content-length"));
						if (process) {
							if (name.equalsIgnoreCase("cookie")) {
								String cleanedCookie = request.getHeader(name);
								cleanedCookie = cleanedCookie
										.replaceAll(
												"((ch\\.unifr\\.pai)|(_pk_)).*?(([;\\]][ ]?)|$)",
												"").trim();
								if (cleanedCookie.endsWith(";"))
									cleanedCookie = cleanedCookie.substring(0,
											cleanedCookie.length() - 2);
								req.addHeader(name, cleanedCookie);
							} else
								req.addHeader(name, request.getHeader(name));
						}
					}
					HttpClient client = new SystemDefaultHttpClient();
					HttpResponse resp;

					resp = client.execute(req);

					if (resp != null && resp.getEntity() != null) {
						boolean isText = resp.getEntity().getContentType() == null
								|| resp.getEntity().getContentType().getValue() == null
								|| resp.getEntity().getContentType().getValue()
										.isEmpty()
								|| resp.getEntity().getContentType().getValue()
										.contains("text/");
						response.setStatus(resp.getStatusLine().getStatusCode());
						for (Header h : resp.getAllHeaders()) {
							if (h.getName() != null
									&& !h.getName().equalsIgnoreCase(
											"content-length")) {
								if (h.getName().equalsIgnoreCase("Location")) {
									response.setHeader(h.getName(), Rewriter
											.translateCleanUrl(h.getValue(),
													servletPath,
													parser.getProxyBasePath()));
								} else {
									response.setHeader(h.getName(),
											h.getValue());
								}
							}
						}
						// If an error is returned, we don't need to process the
						// inputstream
						if (resp.getStatusLine().getStatusCode() < 400) {
							if (!isText) {
								BufferedInputStream webToProxyBuf = new BufferedInputStream(
										resp.getEntity().getContent());
								BufferedOutputStream proxyToClientBuf = new BufferedOutputStream(
										response.getOutputStream());
								int b;
								while ((b = webToProxyBuf.read()) != -1)
									proxyToClientBuf.write(b);
								proxyToClientBuf.flush();
								webToProxyBuf.close();
								proxyToClientBuf.close();
							} else {
								boolean isGzipped = resp.getEntity()
										.getContentEncoding() != null
										&& resp.getEntity()
												.getContentEncoding()
												.getValue().contains("gzip");
								InputStream input;
								OutputStream output;
								if (isGzipped) {
									input = new GZIPInputStream(resp
											.getEntity().getContent());
									output = new GZIPOutputStream(
											response.getOutputStream());
								} else {
									input = new BufferedInputStream(resp
											.getEntity().getContent());
									output = new BufferedOutputStream(
											response.getOutputStream());
								}
								ByteArrayOutputStream byteArrOS = new ByteArrayOutputStream();
								int b;
								while ((b = input.read()) != -1) {
									byteArrOS.write(b);
								}
								byteArrOS.flush();
								byteArrOS.close();
								String charset = request
										.getHeader("Accept-Charset");
								if (charset == null || charset.isEmpty()) {
									charset = "ISO-8859-1";
								} else {
									int indexOfSeparator = charset.indexOf(',');
									if (indexOfSeparator != -1)
										charset = charset.substring(0,
												indexOfSeparator);
								}
								String originalContent = new String(
										byteArrOS.toByteArray(), charset);
								// String s = originalContent;
								String s = URLRewriterServer.process(
										originalContent, fullUrl);
								s = URLRewriterServer.removeTopHref(s);
								if (request.getSession().getAttribute(
										Constants.miceManaged) == null
										|| !request
												.getSession()
												.getAttribute(
														Constants.miceManaged)
												.equals("true")) {
									s = s.replace(
											"<head>",
											"<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1\">");
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
								if (resp.getEntity().getContentType() != null
										&& resp.getEntity().getContentType()
												.getValue()
												.contains("text/html")
										&& (s.contains("body") || s
												.contains("BODY")))
									s += "<script type=\"text/javascript\" language=\"javascript\" src=\""
											+ servletPath
											+ "miceproxy/miceproxy.nocache.js\"></script>";
								PrintWriter out = new PrintWriter(
										new OutputStreamWriter(output, charset),
										true);

								StringReader r = new StringReader(s);
								// Read and write 32K chars at a time
								// (Far more efficient than reading and writing
								// a
								// line at a
								// time)
								char[] charbuf = new char[32 * 1024]; // 32Kchar
																		// buffer
								int len;
								while ((len = r
										.read(charbuf, 0, charbuf.length)) != -1) {
									out.write(charbuf, 0, len);
								}
								out.flush();
								out.close();
							}
						} else {
							response.sendError(resp.getStatusLine()
									.getStatusCode());
						}
					}

				}
			} catch (UnknownHostException e) {
				response.sendError(404);
			}
		}
	}

	public void destroy() {
	}

	public static String getServletPath(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":"
				+ request.getServerPort() + request.getContextPath();
	}
}
