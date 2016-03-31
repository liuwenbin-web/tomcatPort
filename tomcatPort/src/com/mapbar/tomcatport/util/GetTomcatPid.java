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
	/**
	 * 执行cmd（shell）指令
	 * @param cmd 需要执行的指令 如：netstat -ntlp等
	 * @throws Exception
	 */
	public static void runCmd(String cmd) throws Exception {
		String[] command = {"/bin/sh", "-c", cmd};
		Process process = Runtime.getRuntime().exec(command);
		InputStream is = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
		}
		process.waitFor();
		is.close();
		reader.close();
		process.destroy();
	}
}
