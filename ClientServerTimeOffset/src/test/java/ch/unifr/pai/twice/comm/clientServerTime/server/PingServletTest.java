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

package ch.unifr.pai.twice.comm.clientServerTime.server;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class PingServletTest {

	@Test
	public void test() throws ServletException, IOException {
		// given
		HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		Mockito.when(resp.getWriter()).thenReturn(new PrintWriter(sw));

		// when
		new PingServlet().doGet(null, resp);

		// then
		String s = sw.toString();
		assertNotNull(s);
		long parsedTimestamp = Long.parseLong(s);
		assertTrue(new Date().getTime() >= parsedTimestamp);
	}

}
