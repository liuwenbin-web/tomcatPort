package com.mapbar.tomcatport.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileOperator {
	public static void write(String filePath,String content) throws Exception{
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(filePath)));
		bufferedWriter.write(content);
		bufferedWriter.close();
	}
}
