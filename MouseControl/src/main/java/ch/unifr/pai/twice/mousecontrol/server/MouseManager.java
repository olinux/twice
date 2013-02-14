package ch.unifr.pai.twice.mousecontrol.server;
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

//@WebServlet("/mouseManager")
public class MouseManager{
//public class MouseManager extends HttpServlet implements WebSocket.OnTextMessage{
	
//	private Map<String, Connection> wsConnMap = new HashMap<String, Connection>();
//	private static final long serialVersionUID = 1L;
//	public static final Integer DEFAULTPORT = 8182;
//
//	private WebSocketClientFactory wsFactory = new WebSocketClientFactory();
//	private Map<String, String> responseByUUID = new HashMap<String, String>();
//	
//	@Override
//	public void destroy() {
//		try {
//			for(Connection c : wsConnMap.values()){
//				c.close();
//			}			
//			wsFactory.stop();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		super.destroy();
//	}
//
//	@Override
//	public void init() throws ServletException {
//		super.init();
//		try {
//			wsFactory.start();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//			throws ServletException, IOException {		
//		if (req.getParameter("a") != null) {
//			String uuid = req.getParameter("uuid");
//			if (uuid == null || uuid.isEmpty())
//				uuid = req.getSession().getId();
//			// if(!isSocketActive())
//			// resp.sendError(404);
//			String hostName = req.getParameter("host");
//			InetAddress host = null;
//			if (hostName != null && !hostName.isEmpty())
//				host = InetAddress.getByName(hostName);
//			if (host == null)
//				host = InetAddress.getLocalHost();
//			String portNumber = req.getParameter("port");
//			Integer port = null;
//			if (portNumber != null && !portNumber.isEmpty()) {
//				try {
//					port = Integer.parseInt(req.getParameter("port"));
//				} catch (NumberFormatException e) {
//					// Its not number - so ignore
//				}
//			}
//			if (port == null)
//				port = DEFAULTPORT;
//			String result;
//			try {
//				result = update(uuid, host, port, req);
//			} catch (InterruptedException e) {
//				throw new ServletException(e);
//			} catch (ExecutionException e) {
//				throw new ServletException(e);
//			} catch (URISyntaxException e) {
//				throw new ServletException(e);
//			}
//			if (result != null)
//				resp.getWriter().write(result);
//			resp.getWriter().close();
//			return;
//		}
//	}
//
//	private String getMessage(String action, String uuid, 
//			HttpServletRequest req) {
//		StringBuilder sb = new StringBuilder();
//		sb.append(action).append('/').append(uuid);
//		if(action.equals("u") || action.equals("d")){
//			sb.append("/").append(req.getParameter("b"));
//		}		
//		else if(action.equals("m")){
//			sb.append("/").append(req.getParameter("x")).append("/").append(req.getParameter("y"));
//		}		
//		return sb.toString();
//	}	
//
//	private String update(String uuid, InetAddress host, Integer port,
//			HttpServletRequest req) throws IOException, InterruptedException, ExecutionException, URISyntaxException {
//		String action = req.getParameter("a");
//		String hostIdentifier = host.getHostAddress()+":"+port;
//		
//		Connection c = wsConnMap.get(hostIdentifier);
//		if(c==null){
//			WebSocketClient ws = wsFactory.newWebSocketClient();
//			c = ws.open(new URI("ws://"+host.getHostAddress()+":"+port+"/mouse"), this).get();
//			wsConnMap.put(hostIdentifier, c);
//			c.sendMessage("s/"+uuid+"/"+hostIdentifier);
//		}		
//		c.sendMessage(getMessage(action, uuid, req));
//		String resp = responseByUUID.get(uuid);
//		String screenRes = responseByUUID.get(hostIdentifier);
//		StringBuilder sb = new StringBuilder();
//		sb.append(resp!=null ? resp : "");
//		sb.append("@");
//		sb.append(screenRes!=null ? screenRes : "");
//		return sb.toString().equals("@") ? null : sb.toString();
//		
//	}
//
//	@Override
//	public void onOpen(Connection connection) {
//	}
//
//	@Override
//	public void onClose(int closeCode, String message) {
//		wsConnMap.clear();
//	}
//
//	@Override
//	public void onMessage(String data) {
//		if(data!=null && data.length()>0){
//			String[] response = data.split("@");
//			if(response.length == 2){
//				responseByUUID.put(response[0], response[1]);
//			}
//		}
//	}

}
