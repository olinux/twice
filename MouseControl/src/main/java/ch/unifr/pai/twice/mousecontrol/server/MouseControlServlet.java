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
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/mouseManagerXBrowser")
public class MouseControlServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	public static final Integer DEFAULTPORT = 8182;	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {		
		String response = null;
		String action = req.getParameter("a");			
		if (action != null) {
			if(action.equals("g")){
				response = askController("g");
			}
			else{
				String targetUUID = req.getParameter("targetUUID");
				StringBuilder sb = new StringBuilder();
				sb.append("m@").append(targetUUID);
				sb.append("@").append(req.getParameter("uuid"));
				for(Object s : req.getParameterMap().keySet()){
					if(!s.toString().equals("targetUUID") && !s.toString().equals("uuid"))
						sb.append("@").append(s.toString()).append("=").append(req.getParameter(s.toString()));
				}
				response = askController(sb.toString());
			}
		}
		if (response != null)
			resp.getWriter().write(response);
		resp.getWriter().close();
	}
	
	private String askController(String data) {
		byte[] send_data = new byte[100];
		byte[] receive_data = new byte[100];
		try {
			InetAddress host = InetAddress.getLocalHost();
			Integer port = DEFAULTPORT;
			send_data = data.getBytes();
			DatagramPacket send_packet = new DatagramPacket(send_data,
					send_data.length, host, port);
			DatagramSocket socket = new DatagramSocket();
			socket.send(send_packet);
			DatagramPacket response_packet = new DatagramPacket(receive_data,
					receive_data.length);
			socket.receive(response_packet);
			socket.close();
			return new String(response_packet.getData(), 0,
					response_packet.getLength());
		} catch (SocketException e) {
			// ignore exception because there will be a new event rather soon...
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// ignore exception because there will be a new event rather soon...
			e.printStackTrace();
		} catch (IOException e) {
			// ignore exception because there will be a new event rather soon...
			e.printStackTrace();
		}
		return null;
	}

}
