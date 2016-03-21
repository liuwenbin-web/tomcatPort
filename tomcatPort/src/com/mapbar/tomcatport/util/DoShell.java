package com.mapbar.tomcatport.util;

public class DoShell {
	public static void shell(String com) throws Exception{
		String[] command = {"/bin/sh", "-c", com};
		Runtime.getRuntime().exec(command);
	}
}
