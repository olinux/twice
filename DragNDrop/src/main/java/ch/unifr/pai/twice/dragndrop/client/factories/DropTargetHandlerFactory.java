package ch.unifr.pai.twice.dragndrop.client.factories;

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

import ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandler;
import ch.unifr.pai.twice.dragndrop.client.intf.DropTargetHandlerAdapter;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

public class DropTargetHandlerFactory {
	
	public enum Priority{
		LOW(1), NORMAL(2), HIGH(3);
		
		private int value;
		
		private Priority(int value){
			this.value = value;
		}
		
		public int getValue(){
			return value;
		}
	}
	
	public final static int THRESHOLD_PERCENTAGE = 99;
	
	public static DropTargetHandler completeIntersection(final Command com, final boolean resetPosition){
		return new DropTargetHandlerAdapter(){
				@Override
				public boolean onDrop(String deviceId, Widget widget, Element dragProxy,
						Event event, Double intersectionPercentage, Double intersectionPercentageWithTarget) {
					if(intersectionPercentage>THRESHOLD_PERCENTAGE)
						com.execute();
					return !resetPosition;
				}
		};
	}
	
	public static DropTargetHandler incompleteIntersection(final Command com, final boolean resetPosition){
		return new DropTargetHandlerAdapter(){
			@Override
			public boolean onDrop(String deviceId, Widget widget, Element dragProxy,
					Event event, Double intersectionPercentage, Double intersectionPercentageWithTarget) {
				com.execute();
				return !resetPosition;
			}
		};
	}
	
	public static DropTargetHandler rejectIfNotFullyIntersecting(){
		return new DropTargetHandlerAdapter(){

			@Override
			public boolean onDrop(String deviceId, Widget widget, Element dragProxy,
					Event event, Double intersectionPercentage, Double intersectionPercentageWithTarget) {
				return intersectionPercentage>THRESHOLD_PERCENTAGE;
			}
			
		};
		
	}
	
	public static DropTargetHandler rejectWhenIntersecting(Priority p){
		return new DropTargetHandlerAdapter(p) {			
			@Override
			public boolean onDrop(String deviceId, Widget widget, Element dragProxy, Event event,
					Double intersectionPercentage, Double intersectionPercentageWithTarget) {
				return false;
			}
		};
	}
}
