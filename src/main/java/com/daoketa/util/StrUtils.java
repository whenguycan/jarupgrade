package com.daoketa.util;

/**
 * @author wangcy 2024/9/13 10:32
 */
public class StrUtils {

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
	
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

}
