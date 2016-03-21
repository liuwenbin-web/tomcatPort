package com.mapbar.tomcatport.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

public class SysConfigFile {

	public static void objectToFile() {
		try {
			FileOutputStream fs = new FileOutputStream(Constant.sysSerFile);
			ObjectOutputStream os = new ObjectOutputStream(fs);
			os.writeObject(Constant.sysconfigMap);
			os.close();
			fs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void fileToObject() {
		try {
			FileInputStream fs = new FileInputStream(Constant.sysSerFile);
			ObjectInputStream oi = new ObjectInputStream(fs);
			Constant.sysconfigMap = (Map<String, String>) oi.readObject();
			oi.close();
			fs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		fileToObject();
	}
}
