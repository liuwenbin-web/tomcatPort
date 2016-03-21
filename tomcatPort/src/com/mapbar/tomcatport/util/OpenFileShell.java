package com.mapbar.tomcatport.util;


public class OpenFileShell {
	public static void open(String file) throws Exception{
		Runtime.getRuntime().exec("open "+file);
	}
	
	public static void main(String[] args) throws Exception {
		open("/mapbar/jar/apache-tomcat-work");
	}
}
