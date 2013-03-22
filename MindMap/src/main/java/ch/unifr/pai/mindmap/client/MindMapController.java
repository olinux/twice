package ch.unifr.pai.mindmap.client;

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
import ch.unifr.pai.mindmap.client.components.MindmapCreateEditWidget;
import ch.unifr.pai.mindmap.client.mindmap.MindMapComponent;
import ch.unifr.pai.mindmap.client.mindmap.MindMapModule;
import ch.unifr.pai.twice.layout.client.DynamicLayout;
import ch.unifr.pai.twice.multipointer.controller.client.TouchPadModule;
import ch.unifr.pai.twice.multipointer.controller.client.TouchPadWidget;
import ch.unifr.pai.twice.utils.device.client.DeviceType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;

/**
 * The main logic of the controller, setting up the application
 * 
 * @author Oliver Schmid
 * 
 */
public class MindMapController extends Composite {

	private static String mindmapId;

	/**
	 * @return a unique id for the current mindmap
	 */
	public static String getMindmapId() {
		return mindmapId;
	}

	private MindMapComponent c;

	/**
	 * Clears the layout, registers the widgets and presents them (initial load mechanism)
	 */
	public void load() {
		DynamicLayout.get().clear();
		registerWidgets();
		DynamicLayout.get().show();
	}

	/**
	 * Registers the different components which shall be added depending on the device type. Please notice, that the distinction between the device types only
	 * defines if a component shall be added to the application or not. The actual distinction between the different implementations of the components is
	 * provided through deferred binding.
	 */
	protected void registerWidgets() {
		if (DeviceType.getDeviceType() == DeviceType.MULTICURSOR) {
			DynamicLayout.get().addComponent("QR", GWT.create(QRUrl.class), new AsyncCallback<QRUrl>() {

				@Override
				public void onFailure(Throwable caught) {
				}

				@Override
				public void onSuccess(QRUrl result) {
				}
			});
		}
		if (DeviceType.getDeviceType() != DeviceType.MULTICURSOR) {

			DynamicLayout.get().addComponent("TouchPad", GWT.create(TouchPadModule.class), new AsyncCallback<TouchPadWidget>() {

				@Override
				public void onFailure(Throwable caught) {
				}

				@Override
				public void onSuccess(TouchPadWidget result) {
				}
			});
			DynamicLayout.get().addComponent("Edit", GWT.create(MindmapCreateEditWidget.class), new AsyncCallback<MindmapCreateEditWidget>() {

				@Override
				public void onFailure(Throwable caught) {
				}

				@Override
				public void onSuccess(MindmapCreateEditWidget result) {
				}
			});
		}
		if (DeviceType.getDeviceType() == DeviceType.MULTICURSOR) {
			DynamicLayout.get().addComponent("Mindmap", GWT.create(MindMapModule.class), new AsyncCallback<MindMapComponent>() {

				@Override
				public void onFailure(Throwable caught) {
				}

				@Override
				public void onSuccess(MindMapComponent result) {
					c = result;
					c.initialize(mindmapId);
				}
			});
		}
	}
}
