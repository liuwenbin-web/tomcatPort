package com.mapbar.tomcatport.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PortInUse {
	public static boolean isPortInUse(String port) throws Exception{
		boolean isUse = false;
		String com = "lsof -i:" + port;
		String[] command = {"/bin/sh", "-c", com };
		Process process = Runtime.getRuntime().exec(command);
		InputStream is = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = reader.readLine()) != null) {
			if(null!=line && !"".equals(line)){
				isUse = true;
			}
		}
		process.waitFor();
		is.close();
		reader.close();
		process.destroy();
		//Thread.sleep(1000);
		return isUse;
	}
}
