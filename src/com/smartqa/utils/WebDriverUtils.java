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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 * WebDriver utility class.
 * 
 * @version 1.0
 * @author antren
 *
 */
public abstract class WebDriverUtils {
	private final static Logger LOG = LogManager.getLogger("WebDriverUtils");
	
	private final static String DNS_START = "domainLookupStart";
	private final static String DNS_END = "domainLookupEnd";
	private final static String TCP_START = "connectStart";
	private final static String TCP_END = "connectEnd";
	private final static String SERVER_START = "requestStart";
	private final static String SERVER_END = "responseEnd";
	private final static String PAGE_START = "domLoading";
	private final static String PAGE_END = "loadEventEnd";
	
	//DNS parse address time
	public final static String DNS_TIME = "dns.time";
	
	//TCP connection time
	public final static String TCP_TIME = "tcp.time";
	
	//server handle request and resolve response time
	public final static String SERVER_TIME = "server.time";
	
	//page load element time
	public final static String PAGE_TIME = "page.time";
	
	//W3C web performance API, 
	protected static String w3cPerfScript = "var performance = window.performance || window.webkitPerformance || " +  
			"window.mozPerformance || window.msPerformance || {}; " +  
			"var timings = performance.timing || {}; return timings;";  
	
	/**
	 * analysis page performance by w3c web performance API<br/>
	 * inject javascript to get the performance time
	 * 
	 * @param driver - web driver instance
	 * @return map stored performance data
	 */
	public static Map<String, Long> analysis(WebDriver driver){
		Map<String, Long> dataMap = buildMap();
		
		try{
			StringBuilder timeData = new StringBuilder().append(((RemoteWebDriver)driver).executeScript(w3cPerfScript).toString());  
			timeData.deleteCharAt(0);  
		    timeData.deleteCharAt(timeData.length()-1);  
		    Map<String, String> timeMap = CommonUtils.buildMap(timeData.toString().split(","));
		    
		    dataMap.put(DNS_TIME, getTime(DNS_START, DNS_END, timeMap));
		    dataMap.put(TCP_TIME, getTime(TCP_START, TCP_END, timeMap));
		    dataMap.put(SERVER_TIME, getTime(SERVER_START, SERVER_END, timeMap));
		    dataMap.put(PAGE_TIME, getTime(PAGE_START, PAGE_END, timeMap));
		}catch(Exception ex){
			LOG.warn("failed to resolve performance data, caused by "+ex.getMessage());
		}
		
		return dataMap;
	}
	
	/**
	 * create visibility expected condition<br/>
	 * so avoid web element not show up in time
	 * 
	 * @param by 
	 * @return condition instance
	 */
	public static ExpectedCondition<WebElement> visibility(final By by) {    
	    return new ExpectedCondition<WebElement>() {    
	          public WebElement apply(WebDriver driver) {    
	            WebElement element = driver.findElement(by);    
	            return element.isDisplayed() ? element : null;
	          }    
	    };    
	}
	
	/**
	 * close web drvier properly
	 * 
	 * @param driver
	 */
	public static void closeWebDriver(WebDriver driver){
		if(driver == null)
			return;
		
		try{
			String current = driver.getWindowHandle();
			Set<String> otherWins = driver.getWindowHandles();
			for(String winId : otherWins)
				if(winId.equals(current))
					continue;
				else
					driver.switchTo().window(winId).close();
		}catch(Exception ex){
			LOG.warn("Error happen when close web driver: "+ex.getMessage());
		}finally{
			try{
				driver.quit();
			}catch(Exception ex){}
		}
	}
	
	/**
	 * check alert window show up or not
	 * 
	 * @param driver
	 * @return true if alert show up, otherwise false
	 */
	public static boolean isAlertExist(WebDriver driver){
		try{
			driver.switchTo().alert();
			return true;
		}catch(NoAlertPresentException  ex){
			return false;
		}
	}
	
	/**
	 * take screenshot of web page
	 * 
	 * @param driver
	 * @param savePath
	 * @return screenshot image path
	 */
	public static String takeScreenshot(WebDriver driver, String savePath){
		 try {
			File screenShotFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			String path = "screenshot/"+savePath;
			if(!savePath.endsWith("/.png"))
				path = path + ".png";
			
			FileUtils.copyFile(screenShotFile, new File(path));
			LOG.info("Take screenshot at "+savePath);
			return path;
		} catch (IOException ex) {
			LOG.warn("failed to take screenshot for current page, caused by "+ex.getMessage());
			return null;
		}
	}
	
	/**
	 * help to calculate performance time
	 * 
	 * @param startKey
	 * @param endKey
	 * @param timeMap
	 * @return performance time
	 */
	private static Long getTime(String startKey, String endKey, Map<String, String> timeMap){
		try{
			long startTime = NumberUtils.toLong(timeMap.get(startKey));
			long endTime = NumberUtils.toLong(timeMap.get(endKey));
			return (endTime-startTime);
		}catch(Exception ex){
			return -1L;
		}
	}
	
	/**
	 * build performance map
	 * 
	 * @return empty map
	 */
	private static Map<String, Long> buildMap(){
		Map<String, Long> emptyMap = new HashMap<String, Long>();
		emptyMap.put(DNS_TIME, -1L);
		emptyMap.put(TCP_TIME, -1L);
		emptyMap.put(SERVER_TIME, -1L);
		emptyMap.put(PAGE_TIME, -1L);
		
		return emptyMap;
	}
}
