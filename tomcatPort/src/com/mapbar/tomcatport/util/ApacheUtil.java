package com.mapbar.tomcatport.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ApacheUtil {
	private static String projectPath = null; 
	static{
		projectPath = TomcatUtil.class.getClassLoader().getResource("").toString().substring(5).replaceAll("%20", " ").replaceAll("WEB-INF/classes/", "apache/");
	}
	
	public static void addHttpdVhostsFile(String filePath,String serverShortName) throws Exception{
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(filePath)));
		String result = "";
		String line = bufferedReader.readLine();
		boolean isExist = false;
		while(null != line){
			result += line + "\n";
			if(line.contains(serverShortName+"-vhosts.conf")){
				isExist = true;
			}
			line = bufferedReader.readLine();
		}
		bufferedReader.close();
		if(isExist){
			result += "Include conf/extra/vhosts/"+serverShortName+"-vhosts.conf";
			FileOperator.write(filePath, result);
		}
	}
	
	public static void writeNewVhostsFile(String newFilePath,String serverShortName,String ajpPort,String domain,String contextPath) throws Exception{
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(projectPath + "vhosts.conf")));
		String line = bufferedReader.readLine();
		String result = "";
		while (null != line){
			if(line.contains("[DOMAIN]")){
				line = line.replaceAll("\\[DOMAIN\\]", domain);
			}
			if(line.contains("[CONTEXT]")){
				line = line.replaceAll("\\[CONTEXT\\]", contextPath);
			}
			if(line.contains("[SERVERSHORTNAME]")){
				line = line.replaceAll("\\[SERVERSHORTNAME\\]", serverShortName);
			}
			if(line.contains("[AJPPORT]")){
				line = line.replaceAll("\\[AJPPORT\\]", ajpPort);
			}
			result += line + "\n";
			line = bufferedReader.readLine();
		}
		bufferedReader.close();
		FileOperator.write(newFilePath, result);
	}
	
	public static void startApache() throws Exception{
		DoShell.shell("sudo "+Constant.apachePath+"bin/httpd -k start");
	}
	public static void stopApache() throws Exception{
		DoShell.shell("sudo "+Constant.apachePath+"bin/httpd -k stop");
	}
	
	public static boolean isApcheStart() throws Exception{
		String com = "ps -ef|grep httpd";
		String[] command = {"/bin/sh", "-c", com };
		Process process = Runtime.getRuntime().exec(command);
		InputStream is = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		boolean isStart = false;
		while ((line = reader.readLine()) != null) {
			if(line.contains(Constant.apachePath)){
				isStart = true;
			}
		}
		process.waitFor();
		is.close();
		reader.close();
		process.destroy();
		return isStart;
	}
	public static void restartApache() throws Exception{
		if(isApcheStart()){
			stopApache();
			while(isApcheStart()){
				Thread.sleep(1000);
			}
		}
		startApache();
	}
	public static boolean addHost(String domain) throws Exception{
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(Constant.hostsPath)));
		String line = bufferedReader.readLine();
		boolean isChange = false;
		String result = "";
		while (null != line){
			if(line.contains("本地环境")){
				isChange = true;
			}
			if(line.equals("127.0.0.1 "+domain)){
				bufferedReader.close();
				return true;
			}
			result += line + "\n";
			line = bufferedReader.readLine();
		}
		bufferedReader.close();
		if(isChange){
			result += "127.0.0.1 "+domain;
			FileOperator.write(Constant.hostsPath, result);
		}
		return isChange;
	}
}
