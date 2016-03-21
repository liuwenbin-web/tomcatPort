package com.mapbar.tomcatport.util;

import java.io.File;

public class DeleteLog {
	public static void delByServerName(String serverName) throws Exception{
		String logPath = Constant.tomcatsPath + serverName+"/logs/*";
		DoShell.shell("rm -rf "+logPath);
	}
	
	public static Integer[] childFileCount(String serverName){
		String logPath = Constant.tomcatsPath + serverName+"/logs";
		File[] files = new File(logPath).listFiles();
		int fileCount = 0;
		int dirCount = 0;
		for (File file : files) {
			if(!file.getName().equals(".DS_Store")){
				if(file.isDirectory()){
					dirCount ++;
				}else{
					fileCount ++;
				}
			}
		}
		return new Integer[]{fileCount,dirCount};
	}
	
	public static void main(String[] args) throws Exception {
		delByServerName("apache-tomcat-map");
	}
}