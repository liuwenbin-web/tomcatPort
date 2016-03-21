package com.mapbar.tomcatport.util;

import java.util.HashMap;
import java.util.Map;

public class Constant {
	//tomcat集群的路径
	public static String tomcatsPath = "/Users/liuwenbin/mapbar/tomcats/";
	
	//git地址
	public static String gitPath = "/Users/liuwenbin/git/";
	
	//myeclipse地址
	public static String myeclipsePath = "/Users/liuwenbin/Workspaces/MyEclipse for Spring 10/";
	
	//serFile
	public static String serFile = "/mapbar/jar/tomcat.ser";
	
	//sysSerFile
	public static String sysSerFile = "/mapbar/jar/sysconfig.ser";
	
	//apache path
	public static String apachePath = "/Users/liuwenbin/mapbar/Apache2.2.8/";
	
	//hosts
	public static String hostsPath = "/etc/hosts";
	//
	public static Map<String, Integer> tomcatHitMap = new HashMap<String, Integer>();
	
	public static Map<String, String> sysconfigMap = new HashMap<String, String>();
}
