package com.mapbar.tomcatport.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class GetServerXmlPath {
	
	public static List<String> getServerXmlContext(String serverName) throws Exception{
		String serverXml = Constant.tomcatsPath + serverName + "/conf/server.xml";
		List<String> result = new ArrayList<String>();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(serverXml)));
		String line = bufferedReader.readLine();
		while(null != line){
			line = line.trim();
			if(line.startsWith("<Context ")){
				result.add(line);
			}
			line = bufferedReader.readLine();
		}
		bufferedReader.close();
		return result;
	}
	
	public static List<String> getServerXml(String serverName) throws Exception{
		String serverXml = Constant.tomcatsPath + serverName + "/conf/server.xml";
		List<String> result = new ArrayList<String>();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(serverXml)));
		String line = bufferedReader.readLine();
		while(null != line){
			result.add(line);
			line = bufferedReader.readLine();
		}
		bufferedReader.close();
		return result;
	}
}
