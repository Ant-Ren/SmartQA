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
package com.smartqa.webdriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.smartqa.utils.DiskUtils;

/**
 * <p>
 * <b>PathController</b> responsible for converter web element xpath to keyword.<br/>
 * So script developer can focus on keyword driven automation development.<br/>
 * Notice: singleton mode<br/>
 * </p>
 * 
 * <p>
 * Path mapping are stored in path library, default library is classpath:<b>path</b> folder.<br/>
 * In path library, each single file stands for one namespace.<br/>
 * Each namespace stores many xpath to keyword mapping.<br/>
 * </p>
 * 
 * @version 1.0
 * @author antren
 * 
 */
public class PathController {
	private final static Logger LOG = LogManager.getLogger("PathController");
	private final String pathLib = System.getProperty("smartqa.path","path");
	private Map<String, Map<String, String>> pathMap = new ConcurrentHashMap<String, Map<String, String>>();
	
	private static PathController instance;
	
	/**
	 * return the only instance
	 * 
	 * @return PathController
	 */
	public static PathController getInstance(){
		synchronized(Browser.class){
			if(instance == null)
				instance = new PathController();
			return instance;
		}
	}
	
	/**
	 * private constructor
	 */
	private PathController(){
		if(!DiskUtils.folderExist(pathLib)){
			LOG.error("can't locate path library, web driver wouldn't work");
			return;
		}
		
		loadPath();
	}
	
	/**
	 * list all namespace in path library
	 * 
	 * @return List of namespace
	 */
	public List<String> listNamespace(){
		List<String> list = new LinkedList<String>();
		for(String namespace : pathMap.keySet()){
			String info = namespace + " - " + pathMap.get(namespace).size();
			list.add(info);
		}
		
		return list;
	}
	
	/**
	 * load path mapping in path library
	 */
	private void loadPath(){
		File lib = new File(pathLib);
		try{
			int size = 0;
			for(File subLib : lib.listFiles()){
				Properties subProp = new Properties();
				subProp.load(new InputStreamReader(new FileInputStream(subLib), "utf-8"));
				
				Map<String, String> subMap = new ConcurrentHashMap<String, String>();
				for(Object key : subProp.keySet())
					subMap.put(key.toString(), subProp.getProperty(key.toString()));
				
				size += subMap.size();
				String namespace = DiskUtils.filterFileName(subLib.getName());
				pathMap.put(namespace, subMap);
			}
			
			LOG.info("Load path successfully, "+size+" paths found");
		}catch(Exception ex){
			LOG.warn("Error happen when loading path, caused by "+ex.getMessage());
		}
	}
	
	/**
	 * reload path mapping in path library
	 */
	public void refresh(){
		int beforeSize = 0;
		int laterSize = 0;
		for(String namespace : pathMap.keySet())
			beforeSize += pathMap.get(namespace).size();
		
		loadPath();
		for(String namespace : pathMap.keySet())
			laterSize += pathMap.get(namespace).size();
		
		LOG.info("Refresh path library, "+(laterSize-beforeSize)+" path(s) added.");
	}
	
	/**
	 * fetch path by namespace and keyword
	 * 
	 * @param namespace
	 * @param key
	 * @return xpath
	 */
	public String getPath(String namespace, String key){
		if(!pathMap.containsKey(namespace)){
			LOG.warn("path library doestn't contain the namespace: "+namespace);
			return null;
		}
		
		if(!pathMap.get(namespace).containsKey(key)){
			LOG.warn("path library "+namespace+" doesn't contain the key: "+key);
			return null;
		}
		
		return pathMap.get(namespace).get(key);
	}
}
