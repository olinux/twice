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
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class ExperimentWorkflow extends DockLayoutPanel implements
		HasStartAndStop {
	
	private static final int ENABLENEXTBUTTONINMS=2000;
	
	private Button nextButton = new Button("Next", new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			switchToNextTask();
		}
	});

	public ExperimentWorkflow(boolean loopExecution) {
		super(Unit.PX);
		addSouth(nextButton, 20);
		nextButton.setVisible(false);
		frame.setHeight("100%");
		frame.setWidth("100%");
		add(frame);
		this.loopExecution = loopExecution;
	}

	private boolean loopExecution;
	private Frame frame = new Frame();
	private List<Task<?>> experiments = new ArrayList<Task<?>>();
	private int currentTaskIndex = -1;
	private Task<?> currentTask;
	private Timer taskTimer;

	public void addTask(Task<?> task) {
		experiments.add(task);
	}

	private void switchToNextTask() {
		int nextTaskIndex;
		if (taskTimer != null) {
			taskTimer.cancel();
			taskTimer = null;
		}
		if(currentTaskIndex>=experiments.size()-1){
			if(loopExecution){
				nextTaskIndex = 0;
			}
			else{
				stop();
				return;
			}			
		}
		else{
			nextTaskIndex = currentTaskIndex+1;
		}
		Task<?> newTask = experiments.get(nextTaskIndex);
		if (currentTask instanceof HasStartAndStop)
			((HasStartAndStop) currentTask).stop();
		// if (currentTask instanceof HasLog)
		// service.log(((HasLog) currentTask).getLog(), false,
		// new DummyAsyncCallback<Void>());
		if (newTask != null) {
			currentTask = newTask;
			if (newTask instanceof Widget) {
//				taskContainerPanel.setWidget(((Widget) newTask));
			}
		}
		if(newTask.hasNextButton()){
			nextButton.setVisible(true);
			nextButton.setEnabled(false);
			Timer t = new Timer(){
				@Override
				public void run() {
					nextButton.setEnabled(true);
				}				
			};
			t.schedule(ENABLENEXTBUTTONINMS);
		}
		else{
			nextButton.setVisible(false);
		}
		//
		// if (newTask instanceof EnforceInputDevice)
		// changeInputDevice(((EnforceInputDevice) newTask).useInputDevice());
		if (newTask instanceof HasStartAndStop) {
			((HasStartAndStop) newTask).start();
		}
		currentTaskIndex++;
		if (newTask.getTimeout() != null) {
			taskTimer = new Timer() {
				@Override
				public void run() {
					switchToNextTask();
				}
			};
			taskTimer.schedule(newTask.getTimeout().intValue());

		}
	}

	@Override
	public void start() {
		switchToNextTask();
	}

	@Override
	public void stop() {
		currentTaskIndex = -1;
		currentTask = null;
		if (taskTimer != null) {
			taskTimer.cancel();
			taskTimer = null;
		}
		clear();
	}

}
