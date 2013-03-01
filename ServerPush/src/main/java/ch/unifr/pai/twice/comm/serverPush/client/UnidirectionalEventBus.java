package ch.unifr.pai.twice.comm.serverPush.client;

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
import ch.unifr.pai.twice.authentication.client.security.TWICESecurityManager;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.Event.Type;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * The unidirectional event bus is thought to be a fallback solution for devices which either are not capable of establishing a bi-directional communication or
 * such components which do not need bi-directionality because they do not react at real-time on events from external devices. The implementation is not yet
 * completed since the main functionalities can also be established by the use of the bi-directional event bus and this fallback solution therefore didn't have
 * high priority.
 * 
 * @author Oliver Schmid
 * 
 */
// TODO implement properly
public class UnidirectionalEventBus extends SimpleEventBus {

	RemoteEventing eventing = new RemoteEventing() {

		@Override
		protected void sendMessage(String message) {
			// TODO Auto-generated method stub

		}

		@Override
		protected void fireEventLocally(Event<?> e) {
			UnidirectionalEventBus.super.fireEvent(e);
		}

		@Override
		protected void fireEventFromSourceLocally(Event<?> e, Object source) {
			UnidirectionalEventBus.super.fireEventFromSource(e, source);
		}

		@Override
		protected TWICESecurityManager getSecurityManager() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	@Override
	public void fireEvent(Event<?> event) {
		fireEventFromSource(event, null);
	}

	@Override
	public void fireEventFromSource(Event<?> event, Object source) {
		eventing.fireEventFromSource(event, source);
	}

	@Override
	public <H> HandlerRegistration addHandlerToSource(Type<H> type, Object source, H handler) {
		if (source instanceof RemoteWidget) {
			source = ((RemoteWidget) source).getEventSource();
		}
		return super.addHandlerToSource(type, source, handler);
	}
}
