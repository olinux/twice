package ch.unifr.pai.twice.dragndrop.client.utils;

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

/**
 * A container to return three independent objects from a method
 * 
 * @author Oliver Schmid
 * 
 * @param <First>
 * @param <Second>
 * @param <Third>
 */
public class Triple<First, Second, Third> extends Tuple<First, Second> {
	private static final long serialVersionUID = 1L;
	private Third third;

	private Triple() {
		super(null, null);
	}

	public Triple(First first, Second second, Third third) {
		super(first, second);
		this.third = third;
	}

	public Third getThird() {
		return third;
	}

}
