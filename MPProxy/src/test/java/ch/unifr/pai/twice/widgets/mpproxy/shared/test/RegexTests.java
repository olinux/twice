package ch.unifr.pai.twice.widgets.mpproxy.shared.test;
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
import static org.junit.Assert.fail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import ch.unifr.pai.twice.widgets.mpproxy.shared.URLParser;

public class RegexTests {

	private String servletHost = "http://localhost:7070/";
	private String proxyHost = "http://www.google.ch";
	
	@Test
	public void testAbsolute() {
		String absoluteRegex = "(?<=([=\\(])((')|(\"))?)/.+?(?=('|\"| |(/>)|>|(['\"];)|([\"']\\))))";
		
		String document = "x.setUrl('/absolute/path'); " +
				"x.setUrl(\"/absolute/path\"); " +
				"a.href=\"/absolute/path\"; " +
				"a.href='/absolute/path'; " +
				"<a href=\"/absolute/path\"></a>" +
				"<a href=/absolute/path></a> " +
				"<a href='/absolute/path'/>" +
				"<a href=\"/absolute/path\" style.../>";
		
		Pattern p = Pattern.compile(absoluteRegex);
		Matcher m = p.matcher(document);
		StringBuffer sb = new StringBuffer();
		while(m.find()){
			m.appendReplacement(sb, translateAbsolute(m.group()));
			System.out.println(m.group());
		}
		System.out.println(m.appendTail(sb));
		
	}
	
	
	@Test
	public void testFull() {
		String fullRegex = "(?<=([=\\(])((')|(\"))?)http.+?(?=('|\"| |(/>)|>|(['\"];)|([\"']\\))))";
		
		String document = "x.setUrl('http://www.google.ch/path'); " +
				"x.setUrl(\"http://www.google.ch/path\"); " +
				"a.href=\"http://www.google.ch/path\"; " +
				"a.href='http://www.google.ch/path'; " +
				"<a href=\"http://www.google.ch/path\"></a>" +
				"<a href=http://www.google.ch/path></a> " +
				"<a href='http://www.google.ch/path'/>" +
				"<a href=\"http://www.google.ch/path\" style.../>";
		
		Pattern p = Pattern.compile(fullRegex);
		Matcher m = p.matcher(document);
		StringBuffer sb = new StringBuffer();
		while(m.find()){
			m.appendReplacement(sb, translateFull(m.group()));
			System.out.println(m.group());
		}
		System.out.println(m.appendTail(sb));
		
	} 
	
	@Test
	public void testGeneric() {
		String genericRegex = "(?<=([=\\(])((')|(\"))?)(/|http).+?(?=('|\"| |(/>)|>|(['\"];)|([\"']\\))))";
		
		//Since there is no javascript lookbehind, we have to adapt the regex
		String genericJSRegex = "(([=\\(])((')|(\"))?)(/|http).+?(?=('|\"| |(/>)|>|(['\"];)|([\"']\\))))";
		
		String document = "x.setUrl('/absolute/path'); " +
				"x.setUrl(\"/absolute/path\"); " +
				"a.href=\"/absolute/path\";" +
				" a.href='/absolute/path';" +
				" <a href=\"/absolute/path\">" +
				"</a><a href=/absolute/path></a> " +
				"<a href='/absolute/path'/>" +
				"<a href=\"/absolute/path\" style.../> " +
				"x.setUrl('http://www.google.ch/path'); " +
				"x.setUrl(\"http://www.google.ch/path\"); " +
				"a.href=\"http://www.google.ch/path\"; " +
				"a.href='http://www.google.ch/path'; " +
				"<a href=\"http://www.google.ch/path\"></a>" +
				"<a href=http://www.google.ch/path></a> " +
				"<a href='http://www.google.ch/path'/>" +
				"<a href=\"http://www.google.ch/path\" style.../>";
		
		Pattern p = Pattern.compile(genericJSRegex);
		Matcher m = p.matcher(document);
		StringBuffer sb = new StringBuffer();
		while(m.find()){
			m.appendReplacement(sb, translate(m.group()));
			System.out.println(m.group());
		}
		System.out.println(m.appendTail(sb));
		
	}
	
	private String translate(String replace){
		if(replace.matches("(([=\\(])((')|(\"))?)/.*")){
			int separator = replace.indexOf('/');
			return replace.substring(0, separator)+servletHost+proxyHost+replace.substring(separator);
		}
		else{
			int separator = replace.indexOf("http");
			return replace.substring(0, separator)+servletHost+replace.substring(separator);
		}
	}
	
	private String translateFull(String replace){
		return servletHost+replace;
	}
	
	private String translateAbsolute(String replace){
		return servletHost+proxyHost+replace;
	}
	

}
