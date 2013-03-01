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
import ch.unifr.pai.twice.module.client.TWICEModule;
import ch.unifr.pai.twice.utils.qrcode.QRCodeGeneratorServlet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * A {@link TWICEModule} that provides a visual tag (QR code) of the current URL by requesting its data from the {@link QRCodeGeneratorServlet}
 * 
 * @author Oliver Schmid
 * 
 */
public class QRUrl extends Image implements RequiresResize, TWICEModule<QRUrl> {

	public QRUrl() {
		super();
		setUrl(getEntryPoint());

	}

	/**
	 * @return the dynamically combined URL of the qr code servlet
	 */
	private String getEntryPoint() {
		String hostPage = GWT.getHostPageBaseURL();
		return hostPage + "utils/qr?url=MindMap";
	}

	/**
	 * Adaptation of the image size to the resizing of the container element
	 * 
	 * @see com.google.gwt.user.client.ui.RequiresResize#onResize()
	 */
	@Override
	public void onResize() {

		int mathdim = Math.min(this.getParent().getOffsetHeight(), (int) (this.getParent().getOffsetWidth() * 0.66));
		this.setWidth((mathdim * 1.5) + "px");
		this.setHeight(mathdim + "px");
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.module.client.TWICEModule#start(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public void start(QRUrl instance) {
		instance.onResize();
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.module.client.TWICEModule#stop(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public void stop(QRUrl instance) {

	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.module.client.TWICEModule#dontShowInMenu(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public boolean dontShowInMenu(QRUrl instance) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.module.client.TWICEModule#attachToRootPanel(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public boolean attachToRootPanel(QRUrl instance) {
		// TODO Auto-generated method stub
		return false;
	}
}
