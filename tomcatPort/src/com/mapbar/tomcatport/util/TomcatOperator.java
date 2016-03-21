package com.mapbar.tomcatport.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TomcatOperator {
	public static void startTomcat(String tomcatName) throws Exception{
		String com = "sh "+Constant.tomcatsPath+tomcatName+"/bin/startup.sh";
		String[] command = {"/bin/sh", "-c", com };
		System.out.println(com);
		Process process = Runtime.getRuntime().exec(command);
		InputStream is = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		process.waitFor();
		is.close();
		reader.close();
		process.destroy();
	}
	
	public static void stopTomcat(long pid) throws Exception{
		String com = "kill -9 "+pid;
		String[] command = {"/bin/sh", "-c", com };
		System.out.println(com);
		Process process = Runtime.getRuntime().exec(command);
		InputStream is = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		process.waitFor();
		is.close();
		reader.close();
		process.destroy();
	}
	public static void main(String[] args) throws Exception {
		stopTomcat(36676);
	}
}
