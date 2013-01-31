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
package com.smartqa.exception;

/**
 * <p>
 * Element not found exception, extends {@link SmartQAException}, means can't locate such element in web page.<br/>
 * When thrown, xpath used should be included for investigation
 * </p>
 * 
 * @see SmartQAException
 * @version 1.0
 * @author antren
 *
 */
public class ElementNotFoundException extends SmartQAException{
	private static final long serialVersionUID = 1L;

	public ElementNotFoundException(String path) {
		super("Element not found. Locate path is: \n"+path);
	}
}
