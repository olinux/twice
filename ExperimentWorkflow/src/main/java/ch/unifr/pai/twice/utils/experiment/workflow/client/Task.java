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

/**
 * A task that can be registered in the {@link ExperimentWorkflow} for execution.
 * 
 * @author Oliver Schmid
 * 
 * @param <W>
 */
public abstract class Task<W extends Widget> extends SimpleLayoutPanel implements HasLog, HasStartAndStop {
	Long timeout;
	boolean nextButton;

	/**
	 * @param rootWidget
	 *            - the widget that contains the visualization of the task and that shall be attached to the application
	 */
	public Task(W rootWidget) {
		setWidget(rootWidget);
	}

	/**
	 * @return the root widget (wrapping all elements of the task)
	 */
	public W getRootWidget() {
		return (W) getWidget();
	}

	/**
	 * @return true if this task has a next button available for the user to skip the execution
	 */
	public boolean hasNextButton() {
		return nextButton;
	}

	/**
	 * sets the property "nextButton" to true
	 * 
	 * @return this task
	 */
	public Task<W> addNextButton() {
		nextButton = true;
		return this;
	}

	/**
	 * Sets a timeout for the task after which it shall automatically be stopped.
	 * 
	 * @param timeout
	 *            in ms
	 * @return this task
	 */
	public Task<W> setTimeout(long timeout) {
		this.timeout = timeout;
		return this;
	}

	/**
	 * @return the defined timeout if it is defined, otherwise null
	 */
	public Long getTimeout() {
		return timeout;
	}

}
