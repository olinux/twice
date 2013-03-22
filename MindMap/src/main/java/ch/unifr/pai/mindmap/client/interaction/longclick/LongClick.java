package ch.unifr.pai.mindmap.client.interaction.longclick;

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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.unifr.pai.twice.multipointer.provider.client.NoMultiCursorController;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

/**
 * Mouse gesture handler for separating long clicks from standard clicks.
 * 
 * @author Oliver Schmid
 * 
 */
public abstract class LongClick {
	private final Map<String, LongClickInfo> infoByDeviceId = new HashMap<String, LongClickInfo>();
	private HasMouseDownHandlers originator;

	public LongClick(HasMouseDownHandlers originator) {
		this(originator, 500);
	}

	public LongClick(HasMouseDownHandlers originator, final int longClickThreshold) {
		if (originator != null) {
			this.originator = originator;

			originator.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					String deviceId = NoMultiCursorController.getUUID(event.getNativeEvent());
					if (Element.as(event.getNativeEvent().getEventTarget()) == ((Widget) LongClick.this.originator).getElement()) {
						LongClickInfo info = new LongClickInfo(LongClick.this.originator, deviceId, event.getClientX(), event.getClientY());
						infoByDeviceId.put(NoMultiCursorController.getUUID(event.getNativeEvent()), info);
						onStartClick(info);
					}
				}
			});

			((HasMouseUpHandlers) originator).addMouseUpHandler(new MouseUpHandler() {

				@Override
				public void onMouseUp(MouseUpEvent event) {
					LongClickInfo info = infoByDeviceId.get(NoMultiCursorController.getUUID(event.getNativeEvent()));
					if (info != null) {
						if (new Date().getTime() - info.getTime() > longClickThreshold && info.getMouseDownX() == event.getClientX()
								&& info.getMouseDownY() == event.getClientY()) {
							onLongClick(info);
							event.preventDefault();
							event.stopPropagation();
						}
					}
				}
			});
		}
	}

	protected class LongClickInfo {
		private final HasMouseDownHandlers originator;
		private final String deviceId;
		private int mouseDownX = 0;
		private int mouseDownY = 0;
		private final long time = new Date().getTime();

		public LongClickInfo(HasMouseDownHandlers originator, String deviceId, int mouseDownX, int mouseDownY) {
			super();
			this.originator = originator;
			this.deviceId = deviceId;
			this.mouseDownX = mouseDownX;
			this.mouseDownY = mouseDownY;
		}

		public HasMouseDownHandlers getOriginator() {
			return originator;
		}

		public String getDeviceId() {
			return deviceId;
		}

		public int getMouseDownX() {
			return mouseDownX;
		}

		public int getMouseDownY() {
			return mouseDownY;
		}

		public long getTime() {
			return time;
		}
	}

	private abstract class LongClickTimer extends Timer {
		protected LongClickInfo info;

		public LongClickTimer(LongClickInfo info) {
			super();
			this.info = info;
		}
	}

	protected abstract void onLongClick(LongClickInfo info);

	protected void onStartClick(LongClickInfo info) {
	};
}
