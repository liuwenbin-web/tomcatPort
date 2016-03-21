package com.mapbar.tomcatport.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class GetHostEnv {
	public static String getEnv() throws Exception{
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(Constant.hostsPath)));
		String line = bufferedReader.readLine();
		if(line.contains("#")){
			line = line.replaceAll("#", "");
			line.trim();
		}
		return transEnv(line);
	}
	
	private static String transEnv(String env){
		if("local".equals(env)){
			return "本地环境";
		}
		if("outer".equals(env)){
			return "外网环境";
		}
		if("press".equals(env)){
			return "压力环境";
		}
		if("inner".equals(env)){
			return "内网环境";
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(getEnv());
	}
	
}
