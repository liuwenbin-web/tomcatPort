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
	/**
	 * 对象序列化到本地文件
	 * @param object 
	 * @param filePath
	 */
	public static void objectToFile(Object object,String filePath) {
		try {
			FileOutputStream fs = new FileOutputStream(filePath);
			ObjectOutputStream os = new ObjectOutputStream(fs);
			os.writeObject(object);
			os.close();
			fs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * 从序列化文件中读取对象
	 * @param filePath 序列化文件路径
	 * @return
	 */
	public static Object fileToObject(String filePath) {
		Object object = null;
		try {
			FileInputStream fs = new FileInputStream(filePath);
			ObjectInputStream oi = new ObjectInputStream(fs);
			object = oi.readObject();
			oi.close();
			fs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return object;
	}
}
