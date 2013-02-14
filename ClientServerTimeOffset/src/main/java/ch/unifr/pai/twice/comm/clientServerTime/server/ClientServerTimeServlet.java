package ch.unifr.pai.twice.comm.clientServerTime.server;

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
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/clientServerTime")
public class ClientServerTimeServlet extends HttpServlet {	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		long serverTime = new Date().getTime();
		String time = req.getParameter("time");
		long clientTime = Long.parseLong(time);
		//If is initial call
		if(req.getSession().getAttribute("ch.unifr.pai.mice.gwt.sync.clientServerTime.client")==null){
			req.getSession().setAttribute("ch.unifr.pai.mice.gwt.sync.clientServerTime.client", clientTime);
			req.getSession().setAttribute("ch.unifr.pai.mice.gwt.sync.clientServerTime.server", serverTime);		
		}
		else{
			long firstClientTime = (Long)req.getSession().getAttribute("ch.unifr.pai.mice.gwt.sync.clientServerTime.client");
			long firstServerTime = (Long)req.getSession().getAttribute("ch.unifr.pai.mice.gwt.sync.clientServerTime.server");
			req.getSession().setAttribute("ch.unifr.pai.mice.gwt.sync.clientServerTime.client", null);
			req.getSession().setAttribute("ch.unifr.pai.mice.gwt.sync.clientServerTime.server", null);		
			long fullTimeSpan = serverTime-firstClientTime;
			long completeRoundTrip = serverTime-firstServerTime;
			
			long offset = fullTimeSpan-completeRoundTrip;
			
		
		
		}
	}
	
	
	
}
