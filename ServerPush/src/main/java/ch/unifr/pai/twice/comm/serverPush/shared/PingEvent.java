package ch.unifr.pai.twice.comm.serverPush.shared;

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
import ch.unifr.pai.twice.comm.serverPush.client.DiscardingRemoteEvent;
import ch.unifr.pai.twice.comm.serverPush.client.RemoteEventHandler;
import ch.unifr.pai.twice.comm.serverPush.shared.PingEvent.PingEventHandler;

/**
 * The ping event used for confirmation of blocking events
 * 
 * @author Oliver Schmid
 * 
 */
public abstract class PingEvent extends DiscardingRemoteEvent<PingEventHandler> {
	public static final Type<PingEventHandler> TYPE = new Type<PingEventHandler>();

	public static interface PingEventHandler extends RemoteEventHandler<PingEvent> {
	}
}
