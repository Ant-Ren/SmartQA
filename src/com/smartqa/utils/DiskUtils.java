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

import java.io.File;

/**
 * Disk utility class.
 * 
 * @version 1.0
 * @author antren
 *
 */
public abstract class DiskUtils {
	/**
	 * check if file exist and it is file, not directory
	 * 
	 * @param path
	 * @return true if file exist
	 */
	public static boolean fileExist(String path){
		File file = new File(path);
		return file.exists() && file.isFile();
	}
	
	/**
	 * check if folder exist and it is folder, not file
	 * 
	 * @param path
	 * @return true if folder exist
	 */
	public static boolean folderExist(String path){
		File file = new File(path);
		return file.exists() && file.isDirectory();
	}
	
	/**
	 * filter file name, remove extension, for example abc.jpg filter to abc
	 * 
	 * @param fileName
	 * @return name after filter
	 */
	public static String filterFileName(String fileName){
		int index = fileName.lastIndexOf(".");
		return fileName.substring(0, index);
	}
}
