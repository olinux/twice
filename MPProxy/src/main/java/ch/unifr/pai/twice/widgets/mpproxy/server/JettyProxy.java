// ========================================================================
// $Id: ProxyServlet.java 5263 2009-06-26 09:42:21Z gregw $
// Copyright 2004-2004 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ========================================================================

package ch.unifr.pai.twice.widgets.mpproxy.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import ch.unifr.pai.twice.widgets.mpproxy.shared.Rewriter;

/**
 * Proxy Servlet.
 * <p>
 * Forward requests to another server either as a standard web proxy (as defined
 * by RFC2616) or as a transparent proxy.
 * 
 */
public class JettyProxy {

	protected HashSet<String> _DontProxyHeaders = new HashSet<String>();
	{
		_DontProxyHeaders.add("proxy-connection");
		_DontProxyHeaders.add("connection");
		_DontProxyHeaders.add("keep-alive");
		_DontProxyHeaders.add("transfer-encoding");
		_DontProxyHeaders.add("te");
		_DontProxyHeaders.add("trailer");
		_DontProxyHeaders.add("proxy-authorization");
		_DontProxyHeaders.add("proxy-authenticate");
		_DontProxyHeaders.add("upgrade");
		_DontProxyHeaders.add("content-length");
	}

	public ProcessResult loadFromProxy(HttpServletRequest request,
			HttpServletResponse response, String uri, String servletPath,
			String proxyPath) throws ServletException, IOException {
		System.out.println("LOAD "+uri);
		if ("CONNECT".equalsIgnoreCase(request.getMethod())) {
			handleConnect(request, response);
		} else {
			URL url = new URL(uri);

			URLConnection connection = url.openConnection();
			connection.setAllowUserInteraction(false);

			// Set method
			HttpURLConnection http = null;
			if (connection instanceof HttpURLConnection) {
				http = (HttpURLConnection) connection;
				http.setRequestMethod(request.getMethod());
				http.setInstanceFollowRedirects(false);
			}

			// check connection header
			String connectionHdr = request.getHeader("Connection");
			if (connectionHdr != null) {
				connectionHdr = connectionHdr.toLowerCase();
				if (connectionHdr.equals("keep-alive")
						|| connectionHdr.equals("close"))
					connectionHdr = null;
			}

			// copy headers
			boolean xForwardedFor = false;
			boolean hasContent = false;
			Enumeration enm = request.getHeaderNames();
			while (enm.hasMoreElements()) {
				// TODO could be better than this!
				String hdr = (String) enm.nextElement();
				String lhdr = hdr.toLowerCase();

				if (_DontProxyHeaders.contains(lhdr))
					continue;
				if (connectionHdr != null && connectionHdr.indexOf(lhdr) >= 0)
					continue;

				if ("content-type".equals(lhdr))
					hasContent = true;

				Enumeration vals = request.getHeaders(hdr);
				while (vals.hasMoreElements()) {
					String val = (String) vals.nextElement();
					if (val != null) {
						connection.addRequestProperty(hdr, val);
						xForwardedFor |= "X-Forwarded-For"
								.equalsIgnoreCase(hdr);
					}
				}
			}

			// Proxy headers
			connection.setRequestProperty("Via", "1.1 (jetty)");
			if (!xForwardedFor)
				connection.addRequestProperty("X-Forwarded-For",
						request.getRemoteAddr());

			// a little bit of cache control
			String cache_control = request.getHeader("Cache-Control");
			if (cache_control != null
					&& (cache_control.indexOf("no-cache") >= 0 || cache_control
							.indexOf("no-store") >= 0))
				connection.setUseCaches(false);

			// customize Connection

			try {
				connection.setDoInput(true);

				// do input thang!
				InputStream in = request.getInputStream();
				if (hasContent) {
					connection.setDoOutput(true);
					IOUtils.copy(in, connection.getOutputStream());
				}

				// Connect
				connection.connect();
			} catch (Exception e) {
				e.printStackTrace();
			}

			InputStream proxy_in = null;

			// handler status codes etc.
			int code = 500;
			if (http != null) {
				proxy_in = http.getErrorStream();

				code = http.getResponseCode();
				response.setStatus(code, http.getResponseMessage());
			}

			if (proxy_in == null) {
				try {
					proxy_in = connection.getInputStream();
				} catch (Exception e) {
					e.printStackTrace();
					proxy_in = http.getErrorStream();
				}
			}

			// clear response defaults.
			response.setHeader("Date", null);
			response.setHeader("Server", null);

			// set response headers
			int h = 0;
			String hdr = connection.getHeaderFieldKey(h);
			String val = connection.getHeaderField(h);
			while (hdr != null || val != null) {
				String lhdr = hdr != null ? hdr.toLowerCase() : null;
				if (hdr != null && val != null
						&& !_DontProxyHeaders.contains(lhdr)) {
					if (hdr.equalsIgnoreCase("Location")) {
						val = Rewriter.translateCleanUrl(val, servletPath,
								proxyPath);
					}
					response.addHeader(hdr, val);

				}

				h++;
				hdr = connection.getHeaderFieldKey(h);
				val = connection.getHeaderField(h);

			}

			boolean isGzipped = connection.getContentEncoding() != null
					&& connection.getContentEncoding().contains("gzip");
			response.addHeader("Via", "1.1 (jetty)");
			// boolean process = connection.getContentType() == null
			// || connection.getContentType().isEmpty()
			// || connection.getContentType().contains("html");
			boolean process = connection.getContentType()!=null && connection.getContentType().contains("text");
			if (proxy_in != null) {
				if (!process) {
					IOUtils.copy(proxy_in, response.getOutputStream());
					proxy_in.close();
				} else {
					InputStream in;
					if (isGzipped && proxy_in!=null && proxy_in.available()>0) {
						in = new GZIPInputStream(proxy_in);
					}
					else{
						in = proxy_in;
					}
					ByteArrayOutputStream byteArrOS = new ByteArrayOutputStream();
					IOUtils.copy(in, byteArrOS);
					in.close();
					if(in!=proxy_in)
						proxy_in.close();
					String charset = response.getCharacterEncoding();
					if (charset == null || charset.isEmpty()) {
						charset = "ISO-8859-1";
					}
					String originalContent = new String(
							byteArrOS.toByteArray(), charset);
					byteArrOS.close();
					return new ProcessResult(originalContent,
							connection.getContentType(), charset, isGzipped);
				}
			}

		}
		return null;
	}

	/* ------------------------------------------------------------ */
	/**
	 * Resolve requested URL to the Proxied URL
	 * 
	 * @param scheme
	 *            The scheme of the received request.
	 * @param serverName
	 *            The server encoded in the received request(which may be from
	 *            an absolute URL in the request line).
	 * @param serverPort
	 *            The server port of the received request (which may be from an
	 *            absolute URL in the request line).
	 * @param uri
	 *            The URI of the received request.
	 * @return The URL to which the request should be proxied.
	 * @throws MalformedURLException
	 */
	protected URL proxyHttpURL(String scheme, String serverName,
			int serverPort, String uri) throws MalformedURLException {
		return new URL(scheme, serverName, serverPort, uri);
	}

	/* ------------------------------------------------------------ */
	public void handleConnect(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String uri = request.getRequestURI();

		String port = "";
		String host = "";

		int c = uri.indexOf(':');
		if (c >= 0) {
			port = uri.substring(c + 1);
			host = uri.substring(0, c);
			if (host.indexOf('/') > 0)
				host = host.substring(host.indexOf('/') + 1);
		}

		InetSocketAddress inetAddress = new InetSocketAddress(host,
				Integer.parseInt(port));

		// if
		// (isForbidden(HttpMessage.__SSL_SCHEME,addrPort.getHost(),addrPort.getPort(),false))
		// {
		// sendForbid(request,response,uri);
		// }
		// else
		{
			InputStream in = request.getInputStream();
			final OutputStream out = response.getOutputStream();

			final Socket socket = new Socket(inetAddress.getAddress(),
					inetAddress.getPort());

			response.setStatus(200);
			response.setHeader("Connection", "close");
			response.flushBuffer();

//			try {
//				Thread copy = new Thread(new Runnable() {
//					public void run() {
//						try {
							IOUtils.copy(socket.getInputStream(), out);
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//				});
//				copy.start();
				IOUtils.copy(in, socket.getOutputStream());
//				copy.join();
//				copy.join(10000);
//			}
//			catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
	}

	public static class ProcessResult {
		String content;
		String contentType;
		String charset;
		boolean gzipped;

		public ProcessResult(String content, String contentType,
				String charset, boolean gzipped) {
			this.content = content;
			this.contentType = contentType;
			this.charset = charset;
			this.gzipped = gzipped;
		}

		public String getCharset() {
			return charset;
		}

		public String getContent() {
			return content;
		}

		public boolean isGzipped() {
			return gzipped;
		}

		public String getContentType() {
			return contentType;
		}
	}
}