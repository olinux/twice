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
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.gwt.server.AtmosphereGwtHandler;
import org.atmosphere.gwt.server.GwtAtmosphereResource;

/**
 * The server side handler
 * 
 * @author Oliver Schmid
 * 
 */
@AtmosphereHandlerService
public class AtmosphereHandler extends AtmosphereGwtHandler {

	public static final String BROADCASTERSESSIONKEY = "ch.unifr.pai.mice.comm.atmosphere.broadcaster";
	public static final String GLOBALBROADCASTERID = "ch.unifr.pai.mice.comm.atmosphere.globalBroadcaster";

	private final EventProcessing eventProcessing = new EventProcessing();

	/*
	 * (non-Javadoc)
	 * @see org.atmosphere.gwt.server.AtmosphereGwtHandler#doComet(org.atmosphere.gwt.server.GwtAtmosphereResource)
	 */
	@Override
	public int doComet(GwtAtmosphereResource resource) throws ServletException, IOException {
		HttpSession session = resource.getAtmosphereResource().getRequest().getSession();
		if (session.getAttribute(BROADCASTERSESSIONKEY) == null)
			session.setAttribute(BROADCASTERSESSIONKEY, BroadcasterFactory.getDefault().get());
		resource.getAtmosphereResource().setBroadcaster((Broadcaster) session.getAttribute(BROADCASTERSESSIONKEY));
		Broadcaster b = BroadcasterFactory.getDefault().lookup(GLOBALBROADCASTERID);
		if (b == null) {
			b = BroadcasterFactory.getDefault().get(GLOBALBROADCASTERID);
		}
		b.addAtmosphereResource(resource.getAtmosphereResource());

		if (logger.isDebugEnabled()) {
			logger.debug("Url: " + resource.getAtmosphereResource().getRequest().getRequestURL() + "?"
					+ resource.getAtmosphereResource().getRequest().getQueryString());
		}
		return NO_TIMEOUT;

	}

	/*
	 * (non-Javadoc)
	 * @see org.atmosphere.gwt.server.AtmosphereGwtHandler#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.util.List,
	 * org.atmosphere.gwt.server.GwtAtmosphereResource)
	 */
	@Override
	public void doPost(HttpServletRequest postRequest, HttpServletResponse postResponse, List<?> messages, GwtAtmosphereResource cometResource) {
		for (final Object s : messages) {
			eventProcessing.processMessage(s, cometResource.getAtmosphereResource());
		}
	}

}
