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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.smartqa.exception.ElementNotFoundException;
import com.smartqa.exception.InvalidPathException;
import com.smartqa.utils.CommonUtils;
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
	private long timeout = 10;
	private long speed = 500;
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
	 * reset action perform speed, default is 500ms, unit is ms
	 * 
	 * @param speed
	 */
	public void resetSpeed(long speed){
		this.speed = speed;
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
	 * switch context by id
	 * 
	 * @param id - iframe id
	 */
	public void context(String id){
		if(StringUtils.isEmpty(id))
			driver.switchTo().defaultContent();
		else
			driver.switchTo().frame(id);
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
		CommonUtils.waiting(speed);
		return this;
	}
	
	/**
	 * 
	 * @param name
	 * @param args
	 * @return WebEngine
	 */
	public WebEngine clickByArgs(String name, String... args){
		WebElement element = locateByArgs(name, args);
		element.click();
		CommonUtils.waiting(speed);
		return this;
	}
	
	/**
	 * select a drop down web element by its value
	 * 
	 * @param name - stands for web element
	 * @param value - value to select
	 * @return WebEngine
	 */
	public WebEngine select(String name, String value){
		WebElement element = locate(name);
		Select select = new Select(element);
		select.selectByVisibleText(value);
		
		CommonUtils.waiting(speed);
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
		String xpath = path.getPath(namespace, name);
		
		WebElement element = driver.findElement(By.xpath(xpath));
		element.sendKeys(filePath);
		CommonUtils.waiting(speed);
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
		CommonUtils.waiting(speed);
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
		CommonUtils.waiting(speed);
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
		element.clear();
		element.sendKeys(value);
		CommonUtils.waiting(speed);
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
	 * using browser native refresh web page
	 * 
	 * @return WebEngine
	 */
	public WebEngine refresh(){
		driver.navigate().refresh();
		CommonUtils.waiting(2*1000);
		LOG.info("refresh page...");
		return this;
	}
	
	/**
	 * handle alert popup window, just accept it
	 * 
	 * @return WebEngine
	 */
	public WebEngine alert(){
		try{
			CommonUtils.waiting(speed);
			Alert alert = driver.switchTo().alert();
			alert.accept();
		}catch(NoAlertPresentException ex){LOG.info("Try to handle alert, but not found one.");}
		return this;
	}
	
	/**
	 * check web page web element status
	 * 
	 * @param name - name stands for web element
	 * @param condition - currently support: display, enable
	 * @param args - possible need dynamic args to build xpath
	 * @return WebEngine
	 */
	public boolean should(String name, String condition, String... args){
		String xpath = path.getPath(namespace, name);
		
		if(args != null && args.length>0)
			for(int i=0; i < args.length; i++){
				String flag = "\\{"+i+"\\}";
				if(xpath.contains("{"+i+"}"))
					xpath = xpath.replaceAll(flag, args[i]);
			}
		
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
		return locateByArgs(name, new String[]{});
	}
	
	/**
	 * locate web element
	 * 
	 * @param name - name stands for web element
	 * @return element located or null if not found
	 */
	private WebElement locateByArgs(String name, String... args){
		String xpath = path.getPath(namespace, name);
		if(args != null && args.length>0)
		for(int i=0; i < args.length; i++){
			String flag = "\\{"+i+"\\}";
			if(xpath.contains("{"+i+"}"))
				xpath = xpath.replaceAll(flag, args[i]);
		}
		
		if(StringUtils.isEmpty(xpath))
			throw new InvalidPathException(name, namespace);
		
		try{
			if(debug)
		    	LOG.info("Locate web element "+namespace+"."+name+" with xpath: \n" + xpath);
			
			Wait<WebDriver> wait = new WebDriverWait(driver, timeout);    
			WebElement element = wait.until(WebDriverUtils.visibility(By.xpath(xpath)));
			
		    return element;
		}catch(Exception ex){
			throw new ElementNotFoundException(xpath);
		}
	}
}
