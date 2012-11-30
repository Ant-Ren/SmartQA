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

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Keyboard utility class.<br/>
 * Simulate native keyboard event.
 * 
 * @version 1.0
 * @author antren
 *
 */
public abstract class KeyboardUtils {
	static Robot robot;
	static Map<Character, Integer> keyMap = new HashMap<Character, Integer>();
	
	//supported key code mapping
	static{
		try {
			keyMap.put('a', KeyEvent.VK_A);
			keyMap.put('b', KeyEvent.VK_B);
			keyMap.put('c', KeyEvent.VK_C);
			keyMap.put('d', KeyEvent.VK_D);
			keyMap.put('e', KeyEvent.VK_E);
			keyMap.put('f', KeyEvent.VK_F);
			keyMap.put('g', KeyEvent.VK_G);
			keyMap.put('h', KeyEvent.VK_H);
			keyMap.put('i', KeyEvent.VK_I);
			keyMap.put('j', KeyEvent.VK_J);
			keyMap.put('k', KeyEvent.VK_K);
			keyMap.put('l', KeyEvent.VK_L);
			keyMap.put('m', KeyEvent.VK_M);
			keyMap.put('n', KeyEvent.VK_N);
			keyMap.put('o', KeyEvent.VK_O);
			keyMap.put('p', KeyEvent.VK_P);
			keyMap.put('q', KeyEvent.VK_Q);
			keyMap.put('r', KeyEvent.VK_R);
			keyMap.put('s', KeyEvent.VK_S);
			keyMap.put('t', KeyEvent.VK_T);
			keyMap.put('u', KeyEvent.VK_U);
			keyMap.put('v', KeyEvent.VK_V);
			keyMap.put('w', KeyEvent.VK_W);
			keyMap.put('x', KeyEvent.VK_X);
			keyMap.put('y', KeyEvent.VK_Y);
			keyMap.put('z', KeyEvent.VK_Z);
			keyMap.put('0', KeyEvent.VK_0);
			keyMap.put('1', KeyEvent.VK_1);
			keyMap.put('2', KeyEvent.VK_2);
			keyMap.put('3', KeyEvent.VK_3);
			keyMap.put('4', KeyEvent.VK_4);
			keyMap.put('5', KeyEvent.VK_5);
			keyMap.put('6', KeyEvent.VK_6);
			keyMap.put('7', KeyEvent.VK_7);
			keyMap.put('8', KeyEvent.VK_8);
			keyMap.put('9', KeyEvent.VK_9);
			keyMap.put('@', KeyEvent.VK_AT);
			keyMap.put(',', KeyEvent.VK_COMMA);
			keyMap.put('=', KeyEvent.VK_EQUALS);
			keyMap.put('!', KeyEvent.VK_EXCLAMATION_MARK);
			keyMap.put('#', KeyEvent.VK_NUMBER_SIGN);
			keyMap.put('.', KeyEvent.VK_PERIOD);
			keyMap.put('+', KeyEvent.VK_PLUS);
			keyMap.put(';', KeyEvent.VK_SEMICOLON);
			keyMap.put(':', KeyEvent.VK_COLON);
			keyMap.put('_', KeyEvent.VK_UNDERSCORE);
			keyMap.put(' ', KeyEvent.VK_SPACE);
			keyMap.put('-', KeyEvent.VK_SUBTRACT);
			keyMap.put('/', KeyEvent.VK_SLASH);
			keyMap.put(')', KeyEvent.VK_RIGHT_PARENTHESIS);
			keyMap.put('(', KeyEvent.VK_LEFT_PARENTHESIS);
			keyMap.put('[', KeyEvent.VK_OPEN_BRACKET);
			keyMap.put(']', KeyEvent.VK_CLOSE_BRACKET);
			
			robot = new Robot();
		} catch (AWTException ex) {
			//ignore
		}
	}
	
	/**
	 * type string
	 * 
	 * @param value
	 */
	public static void type(String value){
		if(robot == null)
			return;
		
		char[] words = value.toCharArray();
		for(char c : words){
			if(c>='A' && c<='Z')
				pressKeyWithShift(keyMap.get(c+32));
			
			if(keyMap.containsKey(c))
				pressKey(keyMap.get(c));
		}
	}
	
	/**
	 * type enter key
	 */
	public static void enter(){
		pressKey(KeyEvent.VK_ENTER);
	}
	
	/**
	 * type key with key code
	 * 
	 * @param keyvalue
	 */
	private static void pressKey(int keyvalue) {
		robot.keyPress(keyvalue); 
		CommonUtils.waiting(500);
		robot.keyRelease(keyvalue);
	}
	
	/**
	 * shift type key with key code
	 * 
	 * @param keyvalue
	 */
	private static void pressKeyWithShift(int keyvalue) {
		robot.keyPress(KeyEvent.VK_SHIFT);
		robot.keyPress(keyvalue);
		CommonUtils.waiting(500);
		robot.keyRelease(keyvalue);
		robot.keyRelease(KeyEvent.VK_SHIFT);
	}
}
