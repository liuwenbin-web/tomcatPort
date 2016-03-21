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

	private static Logger logger = Logger.getLogger(IPUtils.class);
	
	public final static String getIpAddress(HttpServletRequest request) {  
        String ip = "";
		try {
			ip = request.getHeader("X-Forwarded-For");  
			if (logger.isInfoEnabled()) {  
			    logger.info("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);  
			}  
  
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			        ip = request.getHeader("Proxy-Client-IP");  
			        if (logger.isInfoEnabled()) {  
			            logger.info("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);  
			        }  
			    }  
			    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			        ip = request.getHeader("WL-Proxy-Client-IP");  
			        if (logger.isInfoEnabled()) {  
			            logger.info("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);  
			        }  
			    }  
			    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			        ip = request.getHeader("HTTP_CLIENT_IP");  
			        if (logger.isInfoEnabled()) {  
			            logger.info("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);  
			        }  
			    }  
			    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			        ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
			        if (logger.isInfoEnabled()) {  
			            logger.info("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);  
			        }  
			    }  
			    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			        ip = request.getRemoteAddr();  
			        if (logger.isInfoEnabled()) {  
			            logger.info("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip=" + ip);  
			        }  
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
			logger.info("IPUtils.getIPAddress cause exception : " + e.getMessage());
		}  
        return ip;  
    }  
}
