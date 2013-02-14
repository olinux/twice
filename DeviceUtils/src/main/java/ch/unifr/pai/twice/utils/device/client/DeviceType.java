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

public enum DeviceType {
	CURSOR, MULTICURSOR, TOUCH;
	
	private static DeviceTypeProvider provider;
	public static DeviceType getDeviceType(){
		if(provider==null)
			provider = GWT.create(DeviceTypeProvider.class);
		return provider.getDeviceType();		
	}
	
	
}
