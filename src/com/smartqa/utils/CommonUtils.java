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
package com.smartqa.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Common utility class.
 * 
 * @version 1.0
 * @author antren
 *
 */
public abstract class CommonUtils {
	private final static Logger LOG = LogManager.getLogger("CommonUtils");
	
	/**
	 * only for windows<br/>
	 * check if process is alive or not
	 * 
	 * @param name - process name
	 * @return true if running, otherwise false
	 */
	public static boolean isProcessAlive(String name){
		BufferedReader in = null;  
	    try {
	    	Process proc = Runtime.getRuntime().exec("tasklist /FI \"+IMAGENAME eq " + name + "\"");  
	    	in = new BufferedReader(new InputStreamReader(proc.getInputStream()));  
	    	String line = null;  
	    	while ((line = in.readLine()) != null)  
	    		if (line.contains(name))   
	    			return true;  
	    	 
	    	return false;  
	    } catch (Exception ex) {  
	    	ex.printStackTrace();  
	    	return false;  
	    } finally {  
	      if (in != null)
	        try {  
	        	in.close();  
	        } catch (Exception ex) {}  
	    }//end of try  
	}
	
	/**
	 * simply execute a process
	 * 
	 * @param path - process path
	 */
	public static void runProcess(String path){
	    try {
	    	Process proc = Runtime.getRuntime().exec(path);  
	    	int code = proc.waitFor();
	    	LOG.info("Process "+path+" running with exit code: "+code);
	    } catch (Exception ex) {  
	    	LOG.error("Process "+path+" failed to run with message: "+ex.getMessage());
	    }
	}
	
	/**
	 * read one line string from console
	 * @return console string
	 */
	public static String readFromConsole(){
		try{
			BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
			return buf.readLine();
		}catch(IOException ex){
			return "";
		}
	}
	
	/**
	 * print string array with specific separator
	 * 
	 * @param arr
	 * @param separator
	 * @return single string
	 */
	public static String printArray(String[] arr, char separator){
		StringBuilder buf = new StringBuilder();
		for(String s : arr)
			buf.append(s).append(separator);
		
		if(buf.length()>0)
			buf.deleteCharAt(buf.length()-1);
		
		return buf.toString();
	}
	
	/**
	 * only for windows, kill process
	 * 
	 * @param name
	 * @return kill finished or not
	 */
	public static boolean killProcess(String name){
		try {  
			Runtime.getRuntime().exec("taskkill /FI \"+IMAGENAME eq " + name + "\"");
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
	/**
	 * make sure process exit, by repeat check and kill command
	 * 
	 * @param name - name of process
	 */
	public static void makeSureExit(final String name){
		new Thread(new Runnable(){
			@Override
			public void run(){
				long startTime = System.currentTimeMillis();
				long TIMEOUT = 60 * 1000;
				
				while(isProcessAlive(name)){
					if((System.currentTimeMillis()-startTime) > TIMEOUT){
						LOG.warn("Timeout, over 60s can't kill process with name "+name);
						break;
					}
					killProcess(name);
				}
			}//end of run
		});
	}
	
	/**
	 * wait until process exist, timeout is 60 seconds
	 * 
	 * @param name - process name
	 */
	public static void waitUtilProcessExit(final String name){
		long startTime = System.currentTimeMillis();
		long TIMEOUT = 60 * 1000;
		
		waiting(1000);
		while(true){
			if((System.currentTimeMillis()-startTime) > TIMEOUT){
				LOG.warn("Timeout, over 60s process with name "+name+" still alive.");
				break;
			}
			
			waiting(200);
			if(!isProcessAlive(name))
				break;
		}
	}
	
	/**
	 * build map by array of string<br/>
	 * each element in string array should be formatted like "key=value"
	 * 
	 * @param split
	 * @return map
	 */
	public static Map<String, String> buildMap(String[] split) {
		Map<String, String> map = new HashMap<String, String>();
		for(String s : split){
			String[] keyValue = keyValue(s, "=");
			map.put(keyValue[0], keyValue[1]);
		}
		
		return map;
	}
	
	/**
	 * parse key value pair from a single string by separator<br/>
	 * for string "key=value", separator should be "="
	 * 
	 * @param src
	 * @param separator
	 * @return [key,value] string array
	 */
	public static String[] keyValue(String src, String separator){
		String[] pairs = new String[2];
		
		if(!src.contains(separator)){
			LOG.warn("no idea how to parse key value from "+src+" by "+separator);
			return null;
		}
		
		int index = src.indexOf(separator);
		pairs[0] = src.substring(0, index).trim();
		pairs[1] = src.substring(index+1).trim();	
		
		return pairs;
	}
	
	/**
	 * fetch number from string by index
	 * 
	 * @param src
	 * @param idx
	 * @return number
	 */
	public static String filterNumber(String src, int idx){
		Pattern numbers = Pattern.compile("\\d+");
		Matcher matcher = numbers.matcher(src);
		while (matcher.find()){
			if(idx==1)
				return matcher.group();
			else{
				idx--;
				continue;
			}
		}
		
		return null;
	}
	
	/**
	 * call current thread sleep with time parameter
	 * 
	 * @param time
	 */
	public static void waiting(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			//ignore
		}
	}
}
