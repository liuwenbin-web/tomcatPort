package com.mapbar.tomcatport.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class TomcatUtil {
	private static String projectPath = null; 
	static{
		projectPath = TomcatUtil.class.getClassLoader().getResource("").toString().substring(5).replaceAll("%20", " ").replaceAll("WEB-INF/classes/", "tomcats/");
	}
	
	public static void unTar(String version,String serverShortName) throws Exception{
		File tomcatTar = new File(projectPath + "apache-tomcat"+version+".tar");
		if(tomcatTar.exists()){
			DoShell.shell("tar -zxvf "+tomcatTar.getAbsolutePath().replaceAll(" ", "\\\\ ")+" -C "+Constant.tomcatsPath);
		}
		File file = new File(Constant.tomcatsPath+"apache-tomcat"+version);
		int i = 0;
		while(!file.exists()){
			Thread.sleep(1000);
			file = new File(Constant.tomcatsPath+"apache-tomcat"+version);
			i++;
			if(i > 20){
				break;
			}
		}
		if(file.exists()){
			file.renameTo(new File(file.getAbsoluteFile()+"-"+serverShortName));
		}
	}
	
	public static void changeTomcatPort(String serverName,String shutdownPort,String httpPort,String ajpPort,String redirectPort,String context) throws Exception{
		String tomcatPath = Constant.tomcatsPath+serverName+"/conf/server.xml";
		String content = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(tomcatPath)));
		String line = bufferedReader.readLine();
		while(null!=line){
			if(line.contains("[SHUTDOWNPORT]")){
				line = line.replaceAll("\\[SHUTDOWNPORT\\]", shutdownPort);
			}
			if(line.contains("[REDIRECTPORT]")){
				line = line.replaceAll("\\[REDIRECTPORT\\]", redirectPort);
			}
			if(line.contains("[HTTPPORT]")){
				line = line.replaceAll("\\[HTTPPORT\\]", httpPort);
			}
			if(line.contains("[AJPPORT]")){
				line = line.replaceAll("\\[AJPPORT\\]", ajpPort);
			}
			if(line.contains("[CONTEXT]")){
				line = line.replaceAll("\\[CONTEXT\\]", context);
			}
			content += line+"\n";
			line = bufferedReader.readLine();
		}
		FileOperator.write(tomcatPath, content);
		bufferedReader.close();
	}
	public static void main(String[] args) throws Exception {
		unTar("6", "test");
		//unTar("7", "test");
		
		changeTomcatPort("apache-tomcat6-test","8036","8096","8037","8459","");
		//changeTomcatPort("apache-tomcat7-test","111","112","113","114","<Context >");
	}
}
