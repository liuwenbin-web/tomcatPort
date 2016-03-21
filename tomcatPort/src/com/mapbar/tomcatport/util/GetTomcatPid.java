package com.mapbar.tomcatport.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GetTomcatPid {
	public static long getByTomcatName(String tomcatName) throws Exception {
		long pid = 0;
		String com = "ps -ef|grep \""+tomcatName+"\"";
		String[] command = {"/bin/sh", "-c", com };
		Process process = Runtime.getRuntime().exec(command);
		InputStream is = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = reader.readLine()) != null) {
			if(line.contains("logging.properties")){
				line = line.trim();
				pid = Long.parseLong(line.split("\\s+")[1]);
			}
		}
		process.waitFor();
		is.close();
		reader.close();
		process.destroy();
		return pid;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(getByTomcatName("apache-tomcat-r"));
	}
}
