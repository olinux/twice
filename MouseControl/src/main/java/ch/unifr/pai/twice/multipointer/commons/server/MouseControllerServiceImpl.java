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

package ch.unifr.pai.twice.multipointer.commons.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.annotation.WebServlet;

import org.atmosphere.cpr.AtmosphereResource;

import ch.unifr.pai.twice.comm.serverPush.server.EventProcessing;
import ch.unifr.pai.twice.multipointer.commons.client.rpc.MouseControllerService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@WebServlet(urlPatterns = { MouseControllerService.PATH })
public class MouseControllerServiceImpl extends RemoteServiceServlet implements ch.unifr.pai.twice.multipointer.commons.client.rpc.MouseControllerService {

	private final static List<String> mpclients = Collections.synchronizedList(new ArrayList<String>());

	@Override
	public List<String> getMPProviders() {
		Set<String> toRemove = new HashSet<String>();
		for (String client : mpclients) {
			AtmosphereResource res = EventProcessing.getAtmosphereResourceByUUID(client);
			if (res == null || res.isCancelled())
				toRemove.add(client);
		}
		for (String rm : toRemove) {
			mpclients.remove(rm);
		}
		return new ArrayList<String>(mpclients);
	}

	@Override
	public void registerAsMPProvider(String uuid) {
		mpclients.add(uuid);
	}

}
