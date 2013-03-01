package ch.unifr.pai.mindmap.client.mindmap;

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

/**
 * The {@link TWICEModule} wrapper for the {@link MindMapComponent}
 * 
 * @author Oliver Schmid
 * 
 */
public class MindMapModule implements TWICEModule<MindMapComponent> {

	@Override
	public void start(MindMapComponent instance) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(MindMapComponent instance) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean dontShowInMenu(MindMapComponent instance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean attachToRootPanel(MindMapComponent instance) {
		// TODO Auto-generated method stub
		return false;
	}

}
