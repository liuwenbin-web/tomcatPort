package com.mapbar.tomcatport.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MakeHtml {
	public static String getHead(){
		String style = "<link rel=\"stylesheet\" href=\"http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css\"><link rel=\"stylesheet\" href=\"http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap-theme.min.css\">";
		String script = "<script src=\"http://cdn.bootcss.com/jquery/1.11.3/jquery.min.js\"></script><script src=\"http://cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js\"></script>";
		return "<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>Tomcat集群端口分析结果</title>"+style+script+"</head><body><div class=\"container\">";
	}
	
	public static String getFoot(){
		return "</div></body></html>";
	}
	
	public static String getErrorInfo(List<String> analysisResult){
		if(analysisResult.size()!=0){
			boolean isHttpError = false;
			for (String analysis : analysisResult) {
				if(analysis.contains("http端口")){
					isHttpError = true;
					break;
				}
			}
			if(isHttpError){
				return "<div class=\"alert alert-danger\" role=\"alert\">有端口冲突！</div>";
			}else {
				return "<div class=\"alert alert-warning\" role=\"alert\">有端口冲突！</div>";
			}
		}else{
			return "<div class=\"alert alert-success\" role=\"alert\">端口状态正常，无冲突</div>";
		}
	}
	
	public static String getAllTomcatPortInfo(Map<String, ArrayList<String>> tomcatPorts){
		String result = "<table class=\"table table-bordered table-hover\"><tr><td colspan=\"5\"><b>各个tomcat的端口统计</b></td></tr><tr><td>Tomcat名称</td><td>shutdown端口</td><td>http端口</td><td>ajp端口</td><td>redirect端口</td></tr>";
		Set<String> tomcatNames = tomcatPorts.keySet();
		List<String> ports = null;
		for (String tomcatName : tomcatNames) {
			ports = tomcatPorts.get(tomcatName);
			result += "<tr><td>"+tomcatName+"</td><td>"+ports.get(0)+"</td><td>"+ports.get(1)+"</td><td>"+ports.get(2)+"</td><td>"+ports.get(3)+"</td></tr>";
		}
		result += "</table>";
		return result;
	}
	
	public static String getAnalysis(List<String> analysisResult){
		String result = "<table class=\"table table-bordered table-hover\"><tr><td><b>端口冲突统计结果&nbsp;</b><span class=\"badge\">"+analysisResult.size()+"</span></td></tr>";
		if(analysisResult.size() == 0){
			result += "<tr><td>无冲突端口</td></tr>";
		}else{
			for (String analysis : analysisResult) {
				if(analysis.contains("http端口")){
					result += "<tr class=\"danger\"><td>"+analysis+"</td></tr>";
				}else{
					result += "<tr class=\"warning\"><td>"+analysis+"</td></tr>";
				}
			}
		}
		result += "</table>";
		return result;
	}
	
	public static String getCommonResult(List<Integer> commonPorts){
		String result = "<div class=\"alert alert-info\" role=\"alert\">新tomcat的推荐端口：<br/>";
		result += "shutdown端口："+commonPorts.get(0)+"<br/>";
		result += "http端口："+commonPorts.get(1)+"<br/>";
		result += "ajp端口："+commonPorts.get(2)+"<br/>";
		result += "redirect端口："+commonPorts.get(3)+"</div>";
		return result;
	}
}
