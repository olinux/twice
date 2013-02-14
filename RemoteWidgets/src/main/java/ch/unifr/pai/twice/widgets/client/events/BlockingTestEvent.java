package ch.unifr.pai.twice.widgets.client.events;
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
import ch.unifr.pai.twice.comm.serverPush.client.BlockingRemoteEvent;
import ch.unifr.pai.twice.comm.serverPush.client.RemoteEvent;
import ch.unifr.pai.twice.comm.serverPush.client.RemoteEventHandler;
import ch.unifr.pai.twice.widgets.client.events.BlockingTestEvent.BlockingTestHandler;

public abstract class BlockingTestEvent extends BlockingRemoteEvent<BlockingTestHandler>{
	
	public static final Type<BlockingTestHandler> TYPE = new Type<BlockingTestHandler>();

	public static interface BlockingTestHandler extends RemoteEventHandler<BlockingTestEvent>{
	}
	
	public String foo;
	
	public Integer bar;
	
	public Boolean test;
}
