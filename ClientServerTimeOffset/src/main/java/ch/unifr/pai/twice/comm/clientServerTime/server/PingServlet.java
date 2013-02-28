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

import ch.unifr.pai.twice.comm.clientServerTime.client.ClientServerTimeOffset;

/**
 * The ping servlet called by {@link ClientServerTimeOffset}. All this servlet does is responding to a GET request and providing the current system time of the
 * server in milliseconds
 * 
 * @author Oliver Schmid
 */
@WebServlet("/ping")
public class PingServlet extends HttpServlet {

	/**
	 * Responds to a GET request and returns the current system time in milliseconds within the HTTP response.
	 * 
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().write(String.valueOf(new Date().getTime()));
	}

}
