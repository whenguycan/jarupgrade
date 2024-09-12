package com.daoketa;

import com.daoketa.jar.upgrade.Upgrade;

/**
 * @author wangcy 2024/9/11 16:18
 */
public class WebApplication {

	public static void main(String[] args) throws Exception {
		new Upgrade(args).execute();
	}
	
}
