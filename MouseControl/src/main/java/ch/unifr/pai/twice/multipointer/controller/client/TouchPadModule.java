package ch.unifr.pai.twice.multipointer.controller.client;

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
import ch.unifr.pai.twice.utils.device.client.UUID;

/**
 * The {@link TWICEModule} wrapper for the {@link TouchPadWidget}
 * 
 * @author Oliver Schmid
 * 
 */
public class TouchPadModule implements TWICEModule<TouchPadWidget> {

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.module.client.TWICEModule#start(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public void start(TouchPadWidget instance) {
		instance.initialize(UUID.get(), null, null);
		instance.start();
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.module.client.TWICEModule#stop(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public void stop(TouchPadWidget instance) {
		instance.stop();
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.module.client.TWICEModule#dontShowInMenu(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public boolean dontShowInMenu(TouchPadWidget instance) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.unifr.pai.twice.module.client.TWICEModule#attachToRootPanel(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public boolean attachToRootPanel(TouchPadWidget instance) {
		return instance != null ? instance.attachToRootPanel() : false;
	}

}
