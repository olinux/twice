package ch.unifr.pai.twice.comm.serverPush.server;

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
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import org.atmosphere.cpr.AtmosphereServlet;

import ch.unifr.pai.twice.comm.serverPush.shared.Constants;

/**
 * Extension of the {@link AtmosphereServlet} to make use of the Servlet 3.0 annotation and to prevent the requirement of a web.xml entry
 * 
 * @author Oliver Schmid
 * 
 */
@WebServlet(urlPatterns = "/" + Constants.BASEPATH + "*", asyncSupported = true, loadOnStartup = 1, initParams = {
		@WebInitParam(name = "org.atmosphere.disableOnStateEvent", value = "true"),
		@WebInitParam(name = "org.atmosphere.cpr.AtmosphereHandler", value = "ch.unifr.pai.twice.comm.serverPush.server.AtmosphereHandler") })
public class AtmosphereServlet3 extends AtmosphereServlet {

	public static final String PATH = "/" + Constants.BASEPATH + "*";

	private static final long serialVersionUID = 1L;

	/**
	 * @see AtmosphereServlet#AtmosphereServlet()
	 */
	public AtmosphereServlet3() {
		super(false, false);
	}

}
