package com.mapbar.tomcatport.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PingDomain {
	public static String[] ping(String domain) throws Exception{
		String com = "ping -c1 "+domain;
		System.out.println(com);
		String[] command = {"/bin/sh", "-c", com };
		Process process = Runtime.getRuntime().exec(command);
		InputStream is = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			if(line.contains("icmp_seq")){
				break;
			}
		}
		process.waitFor();
		is.close();
		reader.close();
		process.destroy();
		String ip = "";
		String time = "";
		if(line != null){
			ip = line.substring(line.indexOf("from ") + 5,line.indexOf(": icmp"));
			time = line.substring(line.indexOf("time=")+5);
		}
		return new String[]{ip,time};
	}
}
