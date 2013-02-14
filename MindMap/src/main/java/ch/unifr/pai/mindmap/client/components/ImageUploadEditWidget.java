package ch.unifr.pai.mindmap.client.components;
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

import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;

public class ImageUploadEditWidget extends FormPanel implements TWICEModule<ImageUploadEditWidget>{

	private FlowPanel fp = new FlowPanel();
	
	private FileUpload fileUpload = new FileUpload();
	
	public ImageUploadEditWidget(){
		super();
		setWidget(fp);
		fp.add(fileUpload);
	}
	
	@Override
	public void start(ImageUploadEditWidget instance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop(ImageUploadEditWidget instance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean dontShowInMenu(ImageUploadEditWidget instance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean attachToRootPanel(ImageUploadEditWidget instance) {
		// TODO Auto-generated method stub
		return false;
	}

}
