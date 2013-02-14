package ch.unifr.pai.twice.widgets.mpproxyframe.client;
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
import ch.unifr.pai.twice.utils.device.client.UUID;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.NamedFrame;

public class MPFrame extends NamedFrame {

	public MPFrame() {
		super(UUID.createNew());
		this.setWidth("100%");
		this.setHeight("100%");
	}
	
	@Override
	public void setUrl(String url) {
		super.setUrl(Window.Location.getProtocol()+"//"+Window.Location.getHost()+"/"+url);
	}

	public void sendDataToFrame(String device,	String data){
		sendDataToFrameJSNI(getName(), device, data);
	}

	public void setOwningDevices(String... devices) {
		if (devices == null)
			setOwningDevicesJSNI(getName(), null);
		StringBuilder sb = new StringBuilder();
		for (String d : devices) {
			sb.append(d);
			sb.append(",");
		}
		setOwningDevicesJSNI(getName(), devices.length > 0 ? sb.substring(0, sb.length() - 1) : sb.toString());
	}
	
	public void back(){
		backJSNI(getName());
	}
	
	public void forward(){
		forwardJSNI(getName());
	}
	
	public void reload(){
		reloadJSNI(getName());
	}

	// Set up the JS-callable signature as a global JS function.
	private native void setOwningDevicesJSNI(String frameName, String devices) /*-{
		$wnd.frames[frameName].miceSetOwningDevices(devices);
	}-*/;

	// Set up the JS-callable signature as a global JS function.
	private native void sendDataToFrameJSNI(String frameName, String device,
			String data) /*-{
		$wnd.frames[frameName].miceInput(device, data);
	}-*/;
	
	private native void backJSNI(String frameName) /*-{
	$wnd.frames[frameName].backInHistory();
}-*/;
	
	private native void forwardJSNI(String frameName) /*-{
	$wnd.frames[frameName].forwardInHistory();
}-*/;
	
	private native void reloadJSNI(String frameName) /*-{
	$wnd.frames[frameName].reloadFrame();
}-*/;
}
