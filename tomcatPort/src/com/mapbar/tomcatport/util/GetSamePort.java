package com.mapbar.tomcatport.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.mapbar.tomcatport.GetRightTomcatPort;

public class GetSamePort {
	public static List<String> analysis(Map<String, ArrayList<String>> tomcatPorts) {
		Set<String> tomcatNames = tomcatPorts.keySet();
		
		List<String> ports = new ArrayList<String>();
		List<String> resultList = new ArrayList<String>();
		for (String tomcatName : tomcatNames) {
			System.out.println(tomcatName);
			ports = tomcatPorts.get(tomcatName);
			for(int i = 0;i < ports.size();i++){
				if(!GetRightTomcatPort.portToTomcat.containsKey(ports.get(i))){
					GetRightTomcatPort.portToTomcat.put(ports.get(i), tomcatName+"的"+getPortNameByIndex(i));
				}else{
					resultList.add(GetRightTomcatPort.portToTomcat.get(ports.get(i))+"和"+tomcatName+"的"+getPortNameByIndex(i)+"冲突，都为："+ports.get(i));
				}
			}
		}
		return resultList;
	}
	
	private static String getPortNameByIndex(int index){
		if(0 == index){
			return "shutDown端口";
		}
		if(1 == index){
			return "http端口";
		}
		if(2 == index){
			return "ajp端口";
		}
		if(3 == index){
			return "redirect端口";
		}
		return null;
	} 
}
