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
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.smartqa.utils.DiskUtils;

/**
 * <p>
 * Singleton instance to control browsers.<br/>
 * Supported browsers: 
 * <ul>
 * 	<li>Firefox</li>
 * 	<li>Chrome</li>
 * 	<li>IE</li>
 * </ul>
 * </p>
 * 
 * <p>
 * To enable each browser, edit the necessary path in config/smartqa.ini file.
 * </p>
 * 
 * @version 1.0
 * @author antren
 */
public class Browser {
	private final static Logger LOG = LogManager.getLogger("Browser");
	
	private Properties browserProp = new Properties();
	private boolean firefoxReady = false;
	private boolean ieReady = false;
	private boolean chromeReady = false;
	
	private static Browser instance;
	
	/**
	 * private constructor
	 * validate path in smartqa.ini file
	 */
	private Browser(){
		try{
			File config = new File("config/smartqa.ini");
			if(!config.exists())
				throw new Exception("can't find smartqa.ini in config folder.");
			browserProp.load(new FileInputStream(config));
			validate();
			LOG.info("Browser info loaded.");
		}catch(Exception ex){
			LOG.error("Failed to init Browser, caused by: "+ex.getMessage(), ex);
		}
	}
	
	/**
	 * return the only one instance
	 * 
	 * @return Browser instance
	 */
	public static Browser getInstance(){
		synchronized(Browser.class){
			if(instance == null)
				instance = new Browser();
			return instance;
		}
	}
	
	/**
	 * get WebDriver by browser type
	 * 
	 * @param type - string of browser name, for example: IE, Firefox or Chrome
	 * @return WebDriver
	 */
	public WebDriver getDriver(String type){
		if("Chrome".equalsIgnoreCase(type))
			return getChromeDriver();
		else if("IE".equalsIgnoreCase(type))
			return getIEDriver();
		else if("Firefox".equalsIgnoreCase(type))
			return getFirefoxDriver();
		
		LOG.warn("Unknown type: "+type+", using default Chrome Driver");
		return getChromeDriver();
	}
	
	/**
	 * create FirefoxDriver
	 * 
	 * @return FirefoxDriver
	 */
	public WebDriver getFirefoxDriver(){
		if(firefoxReady){
			FirefoxProfile fp = new FirefoxProfile();
			fp.setPreference("dom.disable_open_during_load", true);
			return new FirefoxDriver(fp);
		}
		
		return null;
	}
	
	/**
	 * create InternetExplorerDriver
	 * 
	 * @return InternetExplorerDriver
	 */
	public WebDriver getIEDriver(){
		if(ieReady){
			DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer(); 
			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			return new InternetExplorerDriver(capabilities);
		}
		
		return null;
	}
	
	/**
	 * create ChromeDriver
	 * 
	 * @return ChromeDriver
	 */
	public WebDriver getChromeDriver(){
		if(chromeReady){
			ChromeOptions option = new ChromeOptions();
			option.addArguments(Arrays.asList("--disable-popup-blocking"));
			return new ChromeDriver(option);
		}
		return null;
	}
	
	/**
	 * Overwrite the toString method display browser resource status
	 */
	public String toString(){
		StringBuilder buf = new StringBuilder();
		buf.append("SmartQA-Web Browser").append("\n");;
		buf.append("Firefor driver:").append(firefoxReady?"ok":"n/a").append("\n");
		buf.append("IE driver:").append(ieReady?"ok":"n/a").append("\n");
		buf.append("Chomre driver:").append(chromeReady?"ok":"n/a").append("\n");
		
		return buf.toString();
	}
	
	/**
	 * validate browser available or not
	 */
	private void validate(){
		String ffPath = browserProp.getProperty("Firefox_Path");
		String iePath = browserProp.getProperty("IE_Path");
		String chPath = browserProp.getProperty("Chrome_Path");
		
		if(StringUtils.isEmpty(ffPath) || !DiskUtils.fileExist(ffPath))
			LOG.warn("Firefox driver can't be loaded.");
		else{
			LOG.info("Firefox driver loaded.");
			System.setProperty("webdriver.firefox.bin", ffPath);   
			firefoxReady = true;
		}
		
		if(StringUtils.isEmpty(iePath) || !DiskUtils.fileExist(iePath))
			LOG.warn("IE driver can't be loaded.");
		else{
			LOG.info("IE driver loaded.");
			System.setProperty("webdriver.ie.driver", iePath);
			ieReady = true;
		}
		
		if(StringUtils.isEmpty(chPath) || !DiskUtils.fileExist(chPath))
			LOG.warn("Chrome driver can't be loaded.");
		else{
			LOG.info("Chrome driver loaded.");
			System.setProperty("webdriver.chrome.driver", chPath);
			chromeReady = true;
		}
	}
}
