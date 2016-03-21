package com.mapbar.tomcatport.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GetCommonPort {
	public static List<Integer> getCommonPort(Map<String, ArrayList<String>> tomcatPorts){
		Set<String> tomcatNames = tomcatPorts.keySet();
		List<Integer> shutDownPortList = new ArrayList<Integer>();
		List<Integer> httpPortList = new ArrayList<Integer>();
		List<Integer> ajpPortList = new ArrayList<Integer>();
		List<Integer> redirectPortList= new ArrayList<Integer>();
		List<String> ports = null;
		for (String tomcatName : tomcatNames) {
			ports = tomcatPorts.get(tomcatName);
			shutDownPortList.add(Integer.parseInt(ports.get(0)));
			httpPortList.add(Integer.parseInt(ports.get(1)));
			ajpPortList.add(Integer.parseInt(ports.get(2)));
			redirectPortList.add(Integer.parseInt(ports.get(3)));
		}
		List<List<Integer>> allPorts = new ArrayList<List<Integer>>();
		allPorts.add(shutDownPortList);
		allPorts.add(httpPortList);
		allPorts.add(ajpPortList);
		allPorts.add(redirectPortList);
		List<Integer> allPortList = new ArrayList<Integer>();
		allPortList.addAll(shutDownPortList);
		allPortList.addAll(httpPortList);
		allPortList.addAll(ajpPortList);
		allPortList.addAll(redirectPortList);
		int max = 0;
		List<Integer> result = new ArrayList<Integer>();
		for(int i = 0;i<allPorts.size();i++){
			max = getMaxPort(allPorts.get(i));
			while(allPortList.contains(max)){
				max = max + 1;
			}
			allPortList.add(max);
			result.add(i, max);
		}
		return result;
	}
	
	private static Integer getMaxPort(List<Integer> list){
		int max = 0;
		for (Integer integer : list) {
			if(integer >= max){
				max = integer;
			}
		}
		return max;
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
