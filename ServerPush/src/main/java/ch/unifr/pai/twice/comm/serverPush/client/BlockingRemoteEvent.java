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
/**
 * A blocking remote event is the least responsive. Such events are well suited for triggering actions on non-controlled third party systems (e.g. credit card
 * transactions) or other actions which can not be undone. To be able to execute a blocking remote event, all involved devices have to confirm that they do not
 * have any event in their queue which could conflict with this event. Therefore, the execution of such an event can be delayed rather essentially. Please use
 * with caution!
 * 
 * @author Oliver Schmid
 * 
 * @param <H>
 */
public abstract class BlockingRemoteEvent<H extends RemoteEventHandler<?>> extends RemoteEvent<H> {

}
