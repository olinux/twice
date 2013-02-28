package ch.unifr.pai.twice.utils.device.client.deviceType;

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

import ch.unifr.pai.twice.utils.device.client.DeviceType;

/**
 * A provider to ask for the current device type - by default this value is set to {@link DeviceType#CURSOR} unless the implementation is changed through
 * deferred binding. This class can be used to differentiate implementations between device types without using explicit deferred binding and is therefore well
 * suited for very small distinctions between the different implementations
 * 
 * @author Oliver Schmid
 * 
 */
public class DeviceTypeProvider {

	/**
	 * @return the device type of the currently executed application
	 */
	public DeviceType getDeviceType() {
		return DeviceType.CURSOR;
	}

}
