package ch.unifr.pai.twice.utils.device.client;

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

import ch.unifr.pai.twice.utils.device.client.deviceType.DeviceTypeProvider;

import com.google.gwt.core.client.GWT;

/**
 * Enumeration of the currently supported device types.
 * 
 * 
 * @author Oliver Schmid
 * 
 */
public enum DeviceType {
	
	/**
	 * Cursor-oriented devices (e.g. desktop-PCs, notebooks)
	 */
	CURSOR,

	/**
	 * Multi-cursor devices - shared devices which allow the use of multiple input devices (e.g. mouse pointers / text input mechanisms) on a single device. The
	 * multi-cursor is thought to be executed on rather powerful devices with big screens (visible for multiple users at the same time) and can be established
	 * through the URL-Parameter "deviceType=multicursor"
	 */
	MULTICURSOR,

	/**
	 * Touch devices - typically mobile devices such as smart phones and tablets
	 */
	TOUCH;

	/**
	 * The current {@link DeviceTypeProvider}
	 */
	private static DeviceTypeProvider provider;
	public static final String SESSION_STORAGE_VARIABLE="ch.unifr.pai.twice.deviceType";
	

	/**
	 * Function to get the current device type through deferred binding. To save resources, the provider is instantiated lazily
	 * 
	 * @return
	 */
	public static DeviceType getDeviceType() {
		if (provider == null)
			provider = GWT.create(DeviceTypeProvider.class);
		return provider.getDeviceType();
	}

}
