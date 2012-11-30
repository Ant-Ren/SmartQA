/*
 * Copyright 2012 Software Freedom Conservancy.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.smartqa.engine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

import com.smartqa.exception.SmartQAException;
import com.smartqa.webdriver.PathController;

/**
 * <p>
 * Everything starts from here.<br/>
 * SmartQA web engine is a simple tool based on selenium webdriver, current version contains selenium 2.25.0.<br/>
 * For script API, please see {@link WebEngine} public methods, in script it is renamed: <b>core</b>
 * </p>
 * 
 * <p>
 * <b>Why use SmartQA?</b>
 * <ul>
 * 	<li>script developer don't need any programming skills, just simple API calling in script</li>
 * 	<li>script developer only need to know xpath knowledge, that's enough</li>
 * 	<li>for advance use, script developer can still use any java or groovy technique in their script</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>How to use SmartQA</b>
 * <ul>
 * 	<li>store xpath of web element in path folder, mapping each xpath to a keyword</li>
 * 	<li>write scenario script in scenario folder calling API handle web action by keyword</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Example script is like this: <br/>
 * 
 * <p style="color:gray">
 * core.navigate('http://www.sina.com.cn');<br/>
 * core.click('sport_link');<br/>
 * core.click('nba_link');<br/>
 * core.mouseover('east_rank_link');<br/>
 * name = core.getText('top_name_text');<br/>
 * return name;
 * </p>
 * 
 * Note: This script go to sina web site, NBA page then read east top one team's name. 
 * <p>
 * 
 * @version 1.0
 * @author antren
 *
 */
public class EngineMain {
	private final static Logger LOG = LogManager.getLogger("EngineMain");
	
	public static void main(String[] args){
		//could add -Dsmartqa.debug=true to open debug log
		System.setProperty("smartqa.debug", "false");
		
		PathController path = PathController.getInstance();
		WebEngine engine = new WebEngine(path);
		
		try{
			//default scenario library is classpath: scenario folder
			GroovyScriptEngine groovy = new GroovyScriptEngine("scenario");
			Binding binding = new Binding();
			binding.setVariable("core", engine);
			Object value = groovy.run(parseArgs(args), binding);
			
			//scenario can return result
			if(value != null)
				LOG.info("Scenario result: "+value);
		}catch(Exception ex){
			ex.printStackTrace();
			throw new SmartQAException(ex.getMessage());
		}finally{
			//at last, don't forget to close web engine
			engine.close();
		}
	}
	
	/**
	 * parse input args, to indicate which scenario should be executed
	 * 
	 * @param args
	 * @return scenario name to execute
	 */
	private static String parseArgs(String[] args){
		if(args.length < 1)
			throw new SmartQAException("Please indicate one scenario name to execute.");
		return args[0];
	}
}
