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

/**
 * Simple JavaBean to store scenario info 
 * 
 * @version 1.0
 * @author antren
 *
 */
public class Scenario {
	String name;
	String result;
	String status;
	
	/**
	 * default constructor, each scenario must has a name
	 * 
	 * @param name
	 */
	public Scenario(String name){
		this.name = name;
		this.result = "n/a";
		this.status = "n/a";
	}
	
	/**
	 * print scenario info into a format string
	 */
	@Override
	public String toString(){
		StringBuilder buf = new StringBuilder();
		buf.append("Scenario [").append(name).append("]\n");
		buf.append("status = ").append(status).append("\n");
		buf.append("result = ").append(result).append("\n");
		
		return buf.toString();
	}
}
