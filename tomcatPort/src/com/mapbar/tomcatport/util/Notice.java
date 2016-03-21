package com.mapbar.tomcatport.util;

public class Notice {
	public static void open(String title,String content) throws Exception{
		DoShell.shell("osascript -e 'display notification \""+content+"\" with title \""+title+"\"'");
	}
	public static void main(String[] args) throws Exception {
		open("成功", "测试成功");
	}
}
