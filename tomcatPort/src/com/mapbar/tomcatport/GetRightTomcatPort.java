package com.mapbar.tomcatport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.mapbar.tomcatport.util.Constant;
import com.mapbar.tomcatport.util.GetCommonPort;
import com.mapbar.tomcatport.util.GetHostEnv;
import com.mapbar.tomcatport.util.GetSamePort;
import com.mapbar.tomcatport.util.ReadServerXml;
import com.mapbar.tomcatport.util.SerializableToFile;
import com.mapbar.tomcatport.util.SysConfigFile;

public class GetRightTomcatPort {
	public static Map<String, ArrayList<String>> tomcatPorts = new HashMap<String, ArrayList<String>>();
	public static List<String> analysisResult = new ArrayList<String>();
	public static List<Integer> commonPortList = new ArrayList<Integer>();
	public static Map<String,String> portToTomcat = new HashMap<String, String>();
	public static List<File> vhostFileList = new ArrayList<File>();
	public static String hostsEnv = null;

	public static void init() throws Exception {
		tomcatPorts.clear();
		analysisResult.clear();
		commonPortList.clear();
		portToTomcat.clear();
		vhostFileList.clear();
		File[] tomcats = new File(Constant.tomcatsPath).listFiles();
		ArrayList<String> ports = null;
		String[] result = null;
		for (File tomcat : tomcats) {
			if (tomcat.isDirectory() && tomcat.getName().startsWith("apache-tomcat")) {
				result = ReadServerXml.getPort(tomcat);
				ports = new ArrayList<String>();
				ports.add(result[1]);
				ports.add(result[2]);
				ports.add(result[3]);
				ports.add(result[4]);
				tomcatPorts.put(result[0], ports);
			}
		}
		analysisResult = GetSamePort.analysis(tomcatPorts);
		commonPortList = GetCommonPort.getCommonPort(tomcatPorts);
		//序列化tomcat的访问次数map
		if(!new File(Constant.serFile).exists()){
			//创建文件
			Set<String> tomcatNames = tomcatPorts.keySet();
			for (String tomcatName : tomcatNames) {
				Constant.tomcatHitMap.put(tomcatName, 0);
			}
			SerializableToFile.objectToFile();
		}else{
			Constant.tomcatHitMap.clear();
			SerializableToFile.fileToObject();
			//判断有没有删除的tomcat
			Set<String> tomcatHitNames = Constant.tomcatHitMap.keySet();
			List<String> notContainHitName = new ArrayList<String>();
			System.out.println("tomcatPorts:");
			System.out.println(new Gson().toJson(tomcatPorts));
			System.out.println("Constant.tomcatHitMap:");
			System.out.println(new Gson().toJson(Constant.tomcatHitMap));
			for (String tomcatHitName : tomcatHitNames) {
				if(!tomcatPorts.containsKey(tomcatHitName)){
					notContainHitName.add(tomcatHitName);
				}
			}
			System.out.println("notContainHitName:");
			System.out.println(new Gson().toJson(notContainHitName));
			if(notContainHitName.size() != 0){
				for (String string : notContainHitName) {
					Constant.tomcatHitMap.remove(string);
				}
				SerializableToFile.objectToFile();
			}
		}
		//序列化系统配置
		if(!new File(Constant.sysSerFile).exists()){
			//创建文件
			Constant.sysconfigMap.put("sortType", "hot");
			Constant.sysconfigMap.put("letterSortType", "AZ");
			Constant.sysconfigMap.put("fileType", "0");
			Constant.sysconfigMap.put("password", "111qqq,,,");
			SysConfigFile.objectToFile();
		}else{
			Constant.sysconfigMap.clear();
			SysConfigFile.fileToObject();
		}
		//初始化apache的vhosts文件
		File[] files = new File(Constant.apachePath+"conf/extra/vhosts/").listFiles();
		for (File file : files) {
			if(file.isFile() && file.getName().endsWith("-vhosts.conf")){
				vhostFileList.add(file);
			}
		}
		//检测系统环境
		hostsEnv = GetHostEnv.getEnv();
		System.out.println(hostsEnv);
	}
}
