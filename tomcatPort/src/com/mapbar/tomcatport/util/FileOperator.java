package com.mapbar.tomcatport.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileOperator {
	public static void write(String filePath,String content) throws Exception{
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(filePath)));
		bufferedWriter.write(content);
		bufferedWriter.close();
	}
	public static void read(String filePath) throws Exception{
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(filePath)));
		String line = bufferedReader.readLine();
		while (null != line){
			System.out.println(line);
			line = bufferedReader.readLine();
		}
		bufferedReader.close();
	}
}
