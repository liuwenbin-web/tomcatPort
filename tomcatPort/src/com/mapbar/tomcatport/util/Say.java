package com.mapbar.tomcatport.util;

public class Say {
	public static void chinese(String words) throws Exception{
		DoShell.shell("say \""+words+"\" -v Ting-Ting");
	}
	
	public static void english(String words) throws Exception{
		DoShell.shell("say \""+words+"\" -v Samantha");
	}
	
	public static void main(String[] args) throws Exception {
		english("create New Tomcat with apache configure success");
	}
}
