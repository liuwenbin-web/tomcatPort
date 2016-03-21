package com.mapbar.tomcatport.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ReadServerXml {
	public static String[] getPort(File tomcatFile) throws Exception {
		String path = tomcatFile.getAbsolutePath() + "/conf/server.xml";
		System.out.println(path);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(path)));
		String line = bufferedReader.readLine();
		String shutDownPort = "";
		String httpPort = "";
		String ajpPort = "";
		String redirectPort = "";
		
		while(null != line){
			if(line.contains("shutdown=\"SHUTDOWN\"")){
				shutDownPort = line.substring(line.indexOf("port=\"")+6,line.indexOf("\" shutdown"));
			}
			if(line.contains("protocol=\"HTTP") && line.contains("Connector") && !line.contains("SSLEnabled")){
				httpPort = line.substring(line.indexOf(" port=\"")+7,line.indexOf("\" protocol"));
			}
			if(line.contains("protocol=\"AJP/1.3\"")){
				ajpPort = line.substring(line.indexOf(" port=\"")+7,line.indexOf("\" protocol="));
				redirectPort = line.substring(line.indexOf(" redirectPort=\"")+15,line.indexOf("\" />"));
			}
			line = bufferedReader.readLine();
		}
		bufferedReader.close();
		return new String[]{tomcatFile.getName(),shutDownPort,httpPort,ajpPort,redirectPort};
	}
}
