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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.smartqa.exception.ElementNotFoundException;
import com.smartqa.exception.InvalidPathException;
import com.smartqa.utils.WebDriverUtils;
import com.smartqa.webdriver.Browser;
import com.smartqa.webdriver.PathController;

/**
 * <p>
 * WebEngine class responsible for execute web page actions, 
 * those public methods will be exposed to script developer used.<br/>
 * </p>
 * 
 * <p>
 * Currently only supported basic web operations such as:<br/>
 * <ul>
 * 	<li>click</li>
 * 	<li>getText</li>
 * 	<li>should</li>
 * 	<li>mouseover</li>
 * 	<li>upload</li>
 *  <li>dragAndDrop</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Beside, basic browser control methods include:<br/>
 * <ul>
 * 	<li>getDriver</li>
 * 	<li>namespace</li>
 *  <li>timeout</li>
 *  <li>browser</li>
 *  <li>size</li>
 * </ul>
 * </p>
 * 
 * @version 1.0
 * @author antren
 *
 */
public class WebEngine {
	private final static Logger LOG = LogManager.getLogger("WebEngine");
	
	private Browser browser = Browser.getInstance();
	private WebDriver driver;
	private PathController path;
	private String namespace = "default";
	private long timeout = 20;
	private boolean debug = false;
	
	/**
	 * constructor only need path controller, recommend to use
	 * 
	 * @param path
	 */
	public WebEngine(PathController path){
		this(null, path);
	}
	
	/**
	 * constructor need path controller and web driver, not recommend to use
	 * 
	 * @param driver
	 * @param path
	 */
	public WebEngine(WebDriver driver, PathController path){
		if(driver == null)
			driver = browser.getDriver(null);
			
		this.driver = driver;
		this.path = path;
		
		if("true".equalsIgnoreCase(System.getProperty("smartqa.debug")))
			debug = true;	
	}
	
	/**
	 * switch browser by type
	 * 
	 * @param type - browser type like "Firefox", "Chrome", "IE"
	 */
	public void browser(String type){
		this.driver.quit();
		LOG.info("switch driver to " + type);
		this.driver = browser.getDriver(type);
	}
	
	/**
	 * switch namespace
	 * 
	 * @param namespace
	 */
	public void namespace(String namespace){
		LOG.info("switch namespace to " + namespace);
		this.namespace = namespace;
	}
	
	/**
	 * switch web element locate timeout value<br/>
	 * unit: second, default one is 20 seconds
	 * 
	 * @param timeout
	 */
	public void timeout(long timeout){
		LOG.info("adjust global timeout to " + timeout + "s");
		this.timeout = timeout;
	}
	
	/**
	 * change the browser window size
	 * 
	 * @param width
	 * @param height
	 */
	public void size(int width, int height){
		LOG.info("adjust browser window size to " + width + " , " + height);
		driver.manage().window().setSize(new Dimension(width, height));
	}
	
	/**
	 * get web driver instance currently used
	 * 
	 * @return WebDriver instance
	 */
	public WebDriver getDriver(){
		return driver;
	}
	
	/**
	 * close web driver
	 */
	public void close(){
		WebDriverUtils.closeWebDriver(driver);
	}
	
	/**
	 * click web element
	 * 
	 * @param name - stands for web element
	 * @return WebEngine
	 */
	public WebEngine click(String name){
		WebElement element = locate(name);
		element.click();
		return this;
	}    
	
	/**
	 * get web element text content
	 * 
	 * @param name - stands for web element
	 * @return WebEngine
	 */
	public String getText(String name){
		WebElement element = locate(name);
		return element.getText();
	}
	
	/**
	 * upload local file to web page
	 * 
	 * @param name - name stands for upload component
	 * @param filePath
	 * @return WebEngine
	 */
	public WebEngine upload(String name, String filePath){
		WebElement element = locate(name);
		element.sendKeys(filePath);
		element.submit();
		
		return this;
	}
	
	/**
	 * move mouse over web element
	 * 
	 * @param name - name stands for web element
	 * @return WebEngine
	 */
	public WebEngine mouseover(String name){
		WebElement element = locate(name);
		Actions builder = new Actions(driver);    
		builder.moveToElement(element).build().perform();
		
		return this;
	}
	
	/**
	 * drag and drop web element
	 * 
	 * @param srcName - name stands for source web element
	 * @param destName - name stands for target web element
	 * @return WebEngine
	 */
	public WebEngine dragAndDrop(String srcName, String destName){
		WebElement srcElement = locate(srcName);
		WebElement destElement = locate(destName);
		
		Actions builder = new Actions(driver);
		builder.dragAndDrop(srcElement, destElement).build().perform();
		
		return this;
	}
	
	/**
	 * fill web element
	 * 
	 * @param name - name stands for web element
	 * @param value - fill value
	 * @return WebEngine
	 */
	public WebEngine fill(String name, String value){
		WebElement element = locate(name);
		element.sendKeys(value);
		return this;
	}
	
	/**
	 * navigate browser to specific web page
	 * 
	 * @param url - page address
	 * @return WebEngine
	 */
	public WebEngine navigate(String url){
		driver.navigate().to(url);
		return this;
	}
	
	/**
	 * check web page web element status
	 * 
	 * @param name - name stands for web element
	 * @param condition - currently support: display, enable
	 * @return WebEngine
	 */
	public boolean should(String name, String condition){
		String xpath = path.getPath(namespace, name);
		if(StringUtils.isEmpty(xpath))
			throw new InvalidPathException(name, namespace);
		
		WebElement element = driver.findElement(By.xpath(xpath));
		if(element == null)
			throw new ElementNotFoundException(xpath);
		
		if("display".equalsIgnoreCase(condition) || "show".equalsIgnoreCase(condition))
			return element.isDisplayed();
		else if("enable".equalsIgnoreCase(condition))
			return element.isEnabled();
		
		return false;
	}
	
	/**
	 * locate web element
	 * 
	 * @param name - name stands for web element
	 * @return element located or null if not found
	 */
	private WebElement locate(String name){
		String xpath = path.getPath(namespace, name);
		if(StringUtils.isEmpty(xpath))
			throw new InvalidPathException(name, namespace);
		
		try{
			Wait<WebDriver> wait = new WebDriverWait(driver, timeout);    
			WebElement element = wait.until(WebDriverUtils.visibility(By.xpath(xpath)));
			if(debug)
		    	LOG.info("Locate web element "+namespace+"."+name+" with xpath: \n" + xpath);
		    
		    return element;
		}catch(Exception ex){
			throw new ElementNotFoundException(xpath);
		}
	}
}
