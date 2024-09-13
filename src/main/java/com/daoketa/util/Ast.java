package com.daoketa.util;

/**
 * @author wangcy 2024/9/13 10:19
 */
public abstract class Ast {

	public static void isTrue(boolean exp, String message) {
		if(!exp) {
			throw new IllegalArgumentException(message);
		}
	}
	
	public static void notEmpty(Iterable<?> iter, String message) {
		if(iter != null && !iter.iterator().hasNext()) {
			throw new IllegalArgumentException(message);
		}
	}
	
}
