package com.mapbar.tomcatport.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * 获取用户IP
 * 当使用反向代理时，穿过防火墙获取IP。
 * @author zhouyao
 * copy from internet
 */
public class IPUtils {

	public final static String getIpAddress(HttpServletRequest request) {  
        String ip = "";
		try {
			ip = request.getHeader("X-Forwarded-For");  
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			        ip = request.getHeader("Proxy-Client-IP");  
			    }  
			    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			        ip = request.getHeader("WL-Proxy-Client-IP");  
			    }  
			    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			        ip = request.getHeader("HTTP_CLIENT_IP");  
			    }  
			    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			        ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
			    }  
			    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			        ip = request.getRemoteAddr();  
			    }  
			} else if (ip.length() > 15) {  
			    String[] ips = ip.split(",");  
			    for (int index = 0; index < ips.length; index++) {  
			        String strIp = (String) ips[index];  
			        if (!("unknown".equalsIgnoreCase(strIp))) {  
			            ip = strIp;  
			            break;  
			        }  
			    }  
			}
		} catch (Exception e) {
			System.out.println("获取ip失败");
		}  
        return ip;  
    }  
}
