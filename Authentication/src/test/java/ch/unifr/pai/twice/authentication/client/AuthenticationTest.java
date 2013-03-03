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

package ch.unifr.pai.twice.authentication.client;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Window.class, GWT.class })
public class AuthenticationTest {

	@Test
	public void testGetUserName() {
		// given
		PowerMockito.mockStatic(GWT.class);
		PowerMockito.mockStatic(Window.class);
		when(Window.prompt(anyString(), anyString())).thenReturn("myUserName");

		// when
		String userName = Authentication.getUserName();
		// mock another return value - the username should stay the same, because prompt is not invoked anymore
		when(Window.prompt(anyString(), anyString())).thenReturn("anotherUserName");
		String userNameAfterSecondRequest = Authentication.getUserName();

		// then
		Assert.assertNotNull(userName);
		Assert.assertEquals("myUserName", userName);
		Assert.assertNotNull(userNameAfterSecondRequest);
		Assert.assertEquals("myUserName", userNameAfterSecondRequest);
	}

}
