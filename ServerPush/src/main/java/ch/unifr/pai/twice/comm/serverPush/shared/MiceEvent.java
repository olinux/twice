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
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import ch.unifr.pai.twice.comm.serverPush.client.RemoteEventHandler;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

/**
 * Use ServerPushEventBus functionality instead
 * 
 * @author Oliver Schmid
 * 
 * @param <T>
 */
@Deprecated
public abstract class MiceEvent<T extends Enum<?>> implements Serializable {

	private static final long serialVersionUID = 1L;
	private T type;
	private long timeStamp;
	private Map<String, String> params;

	public String getParam(String param) {
		if (params == null)
			return null;
		return params.get(param);
	}

	public void addParam(String key, String value) {
		if (params == null)
			params = new HashMap<String, String>();
		params.put(key, value);
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	private MiceEvent() {
		super();
	}

	public MiceEvent(T type) {
		super();
		this.type = type;
	}

	public T getType() {
		return type;
	}

	public static abstract class MiceEventHandler<T extends Enum<?>, E extends MiceEvent<T>> implements RemoteEventHandler {
		private final T type;

		public MiceEventHandler(T type) {
			this.type = type;
		}

		public void processEvent(Serializable event) {
			onEvent((E) event);
		}

		public abstract void onEvent(E event);

		public T getType() {
			return type;
		}
	}

	private static Map<Object, Type<?>> typeMap = new HashMap<Object, Type<?>>();

	public static <H extends MiceEventHandler<?, ?>> Type<H> getGwtEventType(H handler) {
		Type<H> t = (Type<H>) typeMap.get(handler.getType());
		if (t == null) {
			t = new Type<H>();
			typeMap.put(handler.getType(), t);
		}
		return t;
	}

	public static GwtEvent<MiceEventHandler<?, MiceEvent<?>>> getGwtEvent(final MiceEvent<?> event) {
		return new GwtEvent<MiceEventHandler<?, MiceEvent<?>>>() {
			@Override
			public com.google.gwt.event.shared.GwtEvent.Type<MiceEventHandler<?, MiceEvent<?>>> getAssociatedType() {
				return (com.google.gwt.event.shared.GwtEvent.Type<MiceEventHandler<?, MiceEvent<?>>>) typeMap.get(event.getType());
			}

			@Override
			protected void dispatch(MiceEventHandler<?, MiceEvent<?>> handler) {
				handler.onEvent(event);
			}

			@Override
			public Object getSource() {
				return event;
			}
		};

	}

}
