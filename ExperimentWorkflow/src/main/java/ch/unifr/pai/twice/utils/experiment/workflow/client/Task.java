package ch.unifr.pai.twice.utils.experiment.workflow.client;
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
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class Task<W extends Widget> extends SimpleLayoutPanel implements HasLog, HasStartAndStop{	
	Long timeout;
	boolean nextButton;
	
	public Task(W rootWidget){
		setWidget(rootWidget);
	}
	
	public W getRootWidget(){
		return (W)getWidget();
	}
	
	public boolean hasNextButton(){
		return nextButton;
	}
	
	public Task<W> addNextButton(){
		nextButton = true;
		return this;
	}
	
	public Task<W> setTimeout(long timeout){
		this.timeout = timeout;
		return this;
	}
	
	public Long getTimeout(){
		return timeout;
	}

}
