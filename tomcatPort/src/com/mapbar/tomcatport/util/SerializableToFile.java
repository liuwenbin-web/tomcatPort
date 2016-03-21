package com.mapbar.tomcatport.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

public class SerializableToFile {

	public static void objectToFile() {
		try {
			FileOutputStream fs = new FileOutputStream(Constant.serFile);
			ObjectOutputStream os = new ObjectOutputStream(fs);
			os.writeObject(Constant.tomcatHitMap);
			os.close();
			fs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void fileToObject() {
		try {
			FileInputStream fs = new FileInputStream(Constant.serFile);
			ObjectInputStream oi = new ObjectInputStream(fs);
			Constant.tomcatHitMap = (Map<String, Integer>) oi.readObject();
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
