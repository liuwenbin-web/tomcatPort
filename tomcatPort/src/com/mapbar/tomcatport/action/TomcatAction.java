package com.mapbar.tomcatport.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mapbar.tomcatport.GetRightTomcatPort;
import com.mapbar.tomcatport.util.ApacheUtil;
import com.mapbar.tomcatport.util.Constant;
import com.mapbar.tomcatport.util.DeleteLog;
import com.mapbar.tomcatport.util.DeleteWork;
import com.mapbar.tomcatport.util.DoShell;
import com.mapbar.tomcatport.util.GetHostEnv;
import com.mapbar.tomcatport.util.GetServerXmlPath;
import com.mapbar.tomcatport.util.GetTomcatPid;
import com.mapbar.tomcatport.util.IPUtils;
import com.mapbar.tomcatport.util.OpenFileShell;
import com.mapbar.tomcatport.util.PingDomain;
import com.mapbar.tomcatport.util.PortInUse;
import com.mapbar.tomcatport.util.Say;
import com.mapbar.tomcatport.util.SerializableToFile;
import com.mapbar.tomcatport.util.SysConfigFile;
import com.mapbar.tomcatport.util.TomcatOperator;
import com.mapbar.tomcatport.util.TomcatUtil;
import com.mapbar.tomcatport.util.ValueComparator;

@Controller
@RequestMapping(value="/")
public class TomcatAction {
	
	@RequestMapping("/index")
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		ModelAndView mav = new ModelAndView();
		String ip = IPUtils.getIpAddress(request);
		//未登录，不让查看
		if(!"127.0.0.1".equals(ip)){
			if(null == session.getAttribute("user")){
				mav.setViewName("home");
				return mav;
			}
		}
		GetRightTomcatPort.init();
		//判断是否存在tomcat.ser
		mav.addObject("analysisResult", GetRightTomcatPort.analysisResult);
		mav.addObject("commonPortList", GetRightTomcatPort.commonPortList);
		mav.addObject("tomcatPorts", GetRightTomcatPort.tomcatPorts);
		//sortKey
		ArrayList<String> sortKey = new ArrayList<String>();
		if("letter".equals(Constant.sysconfigMap.get("sortType"))){
			sortKey.addAll(GetRightTomcatPort.tomcatPorts.keySet());
			Collections.sort(sortKey);
			if("ZA".equals(Constant.sysconfigMap.get("letterSortType"))){
				Collections.reverse(sortKey);
			}
		}else{
			ValueComparator bvc =  new ValueComparator(Constant.tomcatHitMap);  
	        TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
	        sorted_map.putAll(Constant.tomcatHitMap);
	        sortKey.addAll(sorted_map.keySet());
			Collections.reverse(sortKey);
		}
		mav.addObject("sortKeys", sortKey);
		//判断是否显示错误信息
		if(GetRightTomcatPort.analysisResult.size()!=0){
			boolean isHttpError = false;
			for (String analysis : GetRightTomcatPort.analysisResult) {
				if(analysis.contains("http端口")){
					isHttpError = true;
					break;
				}
			}
			if(isHttpError){
				mav.addObject("portError", "<div class=\"alert alert-danger\" role=\"alert\">有端口冲突！</div>");
			}else {
				mav.addObject("portError", "<div class=\"alert alert-warning\" role=\"alert\">有端口冲突！</div>");
			}
		}else{
			mav.addObject("portError", "<div class=\"alert alert-success\" role=\"alert\">端口状态正常，无冲突</div>");
		}
		mav.addObject("gitPath", Constant.gitPath);
		mav.addObject("tomcatsPath", Constant.tomcatsPath);
		mav.addObject("myeclipsePath", Constant.myeclipsePath);
		mav.addObject("sysConfig", Constant.sysconfigMap);
		mav.addObject("vhostFileList",GetRightTomcatPort.vhostFileList);
		mav.addObject("apachePath",Constant.apachePath);
		mav.addObject("hostsEnv",GetRightTomcatPort.hostsEnv);
		mav.setViewName("index");
		return mav;
	}
	
	@RequestMapping("/getPathAjax")
	public void getPathAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String serverName = request.getParameter("serverName");
		List<String> paths = GetServerXmlPath.getServerXmlContext(serverName);
		String result = "";
		for (String path : paths) {
			result += "<xmp>"+path+"</xmp>";
		}
		out.write(result);
		out.flush();
		out.close();
	}
	
	@RequestMapping("/getServerXmlAjax")
	public void getServerXmlAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String serverName = request.getParameter("serverName");
		String fileType = request.getParameter("fileType");
		if("1".equals(fileType)){
			//使用程序打开
			OpenFileShell.open(Constant.tomcatsPath+serverName+"/conf/server.xml");
		}else{
			//使用弹窗打开
			List<String> paths = GetServerXmlPath.getServerXml(serverName);
			String result = "";
			ArrayList<String> ports = GetRightTomcatPort.tomcatPorts.get(serverName);
			String shutDownPort = ports.get(0);
			String httpPort = ports.get(1);
			String ajpPort = ports.get(2);
			String redirectPort = ports.get(3);
			boolean isCommit = false;
			for (String path : paths) {
				if(path.contains("<!--")){
					isCommit = true;
				}
				if(isCommit){
					result += "<font color=\"#afafaf\"><xmp>"+path+"</xmp></font>";
				}else{
					if(path.contains("\""+shutDownPort+"\"") || path.contains("\""+httpPort+"\"") || path.contains("\""+ajpPort+"\"") || path.contains("\""+redirectPort+"\"")){
						result += "<b><xmp>"+path+"</xmp></b>";
					}else{
						result += "<xmp>"+path+"</xmp>";
					}
				}
				if(path.contains("-->")){
					isCommit = false;
				}
			}
			out.write(result);
			out.flush();
			out.close();
		}
	}
	
	@RequestMapping("/openTomcatPathAjax")
	public void openTomcatPathAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String serverName = request.getParameter("serverName");
		OpenFileShell.open(Constant.tomcatsPath+serverName);
	}
	
	@RequestMapping("/openTomcatLogsPathAjax")
	public void openTomcatLogsPathAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String serverName = request.getParameter("serverName");
		OpenFileShell.open(Constant.tomcatsPath+serverName+"/logs");
	}
	
	@RequestMapping("/startTomcatAjax")
	public void startTomcatAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String serverName = request.getParameter("serverName");
		Constant.tomcatHitMap.put(serverName, Constant.tomcatHitMap.get(serverName) + 1);
		SerializableToFile.objectToFile();
		//先检测后没有启动
		long pid = GetTomcatPid.getByTomcatName(serverName);
		if(pid == 0){
			//没有启动，启动
			TomcatOperator.startTomcat(serverName);
			int count = 0;
			while(pid == 0){
				Thread.sleep(2000);
				pid = GetTomcatPid.getByTomcatName(serverName);
				count ++;
				if(count > 20){
					pid = 0;
					break;
				}
			}
			out.write("start:"+pid);
		}else{
			//已经启动
			out.write("started:"+pid);
		}
		Say.english("tomcat start success");
		out.flush();
		out.close();
	}
	
	@RequestMapping("/stopTomcatAjax")
	public void stopTomcatAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String serverName = request.getParameter("serverName");
		//先检测是否已经关闭了
		long pid = GetTomcatPid.getByTomcatName(serverName);
		if(pid != 0){
			//没有关闭，关闭
			TomcatOperator.stopTomcat(pid);
			int count = 0;
			while(pid != 0){
				Thread.sleep(2000);
				pid = GetTomcatPid.getByTomcatName(serverName);
				count ++;
				if(count > 20){
					break;
				}
			}
			out.write("stop");
		}else{
			//已经关闭了
			out.write("stoped");
		}
		out.flush();
		out.close();
	}
	
	@RequestMapping("/getTomcatStateAjax")
	public void getTomcatStateAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String serverName = request.getParameter("serverName");
		long pid = GetTomcatPid.getByTomcatName(serverName);
		//获取是否配置了apache服务器，返回配置文件的名称和路径
		String apacheVhosts = "";
		try {
			String serverShortName = serverName.substring(serverName.lastIndexOf("-")+1);
			File vhostsFile = new File(Constant.apachePath + "conf/extra/vhosts/" + serverShortName + "-vhosts.conf");
			if(vhostsFile.exists()){
				apacheVhosts += ":" + vhostsFile.getAbsolutePath() + ":" +vhostsFile.getName();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		out.write(pid+apacheVhosts);
		out.flush();
		out.close();
	}
	
	@RequestMapping("/getApacheStateAjax")
	public void getApacheStateAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		if(ApacheUtil.isApcheStart()){
			out.write("1");
		}else{
			out.write("0");
		}
		out.flush();
		out.close();
	}
	
	@RequestMapping("/clearLogAjax")
	public void clearLogAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String serverName = request.getParameter("serverName");
		Integer[] total = DeleteLog.childFileCount(serverName);
		if(total[0] == 0 && total[1] == 0){
			out.write("没有日志文件可以清除");
			out.flush();
			out.close();
			return;
		}
		DeleteLog.delByServerName(serverName);
		Thread.sleep(2000);
		Integer[] remain = DeleteLog.childFileCount(serverName);
		if(remain[0] == 0 && remain[1] == 0){
			String result = "成功删除了";
			if(total[0] != 0){
				result += total[0] + "个文件";
			}
			if(total[1] != 0){
				if(result.endsWith("文件")){
					result += ",";
				}
				result += total[1] + "个文件夹";
			}
			out.write(result);
		}else{
			String result = "还有";
			if(remain[0] != 0){
				result += remain[0] + "个文件";
			}
			if(remain[1] != 0){
				if(result.endsWith("文件")){
					result += ",";
				}
				result += remain[1] + "个文件夹";
			}
			result += "未删除";
			out.write(result);
		}
		out.flush();
		out.close();
	}
	
	@RequestMapping("/clearWorkAjax")
	public void clearWorkAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String serverName = request.getParameter("serverName");
		Integer[] total = DeleteWork.childFileCount(serverName);
		if(total[0] == 0 && total[1] == 0){
			out.write("没有work文件可以清除");
			out.flush();
			out.close();
			return;
		}
		DeleteWork.delByServerName(serverName);
		Thread.sleep(2000);
		Integer[] remain = DeleteWork.childFileCount(serverName);
		if(remain[0] == 0 && remain[1] == 0){
			out.write("清理work成功");
		}else{
			out.write("清理work失败");
		}
		out.flush();
		out.close();
	}
	
	@RequestMapping("/validateTomcatNameAjax")
	public void validateTomcatNameAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String tomcatName = request.getParameter("tomcatName");
		if("work".equals(tomcatName) || "".equals(tomcatName)){
			out.write("error");
			out.flush();
			out.close();
			return;
		}
		boolean validate = true;
		File[] tomcats = new File(Constant.tomcatsPath).listFiles();
		for (File tomcat : tomcats) {
			if(tomcat.getName().endsWith("-"+tomcatName)){
				validate = false;
			}
		}
		if(validate){
			out.write("OK");
		}else{
			out.write("used");
		}
		out.flush();
		out.close();
	}
	
	@RequestMapping("/validatePortAjax")
	public void validatePortAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String port = request.getParameter("port");
		//先判断有没有在其他的tomcat中使用
		String tomcat = GetRightTomcatPort.portToTomcat.get(port);
		if(null != tomcat){
			out.write(port+"端口已经被" +tomcat+"使用");
			out.flush();
			out.close();
			return;
		}
		boolean result = PortInUse.isPortInUse(port);
		if(result){
			out.write("端口已被系统软件占用");
		}else{
			out.write("OK");
		}
		out.flush();
		out.close();
	}
	
	@RequestMapping("/createNewTomcatAjax")
	public void createNewTomcatAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String tomcatType = request.getParameter("tomcatType");
		String tomcatName = request.getParameter("tomcatName");
		String shutdownPort = request.getParameter("shutdownPort");
		String httpPort = request.getParameter("httpPort");
		String ajpPort = request.getParameter("ajpPort");
		String redirectPort = request.getParameter("redirectPort");
		String context = request.getParameter("context");
		String domain = request.getParameter("domain");
		String isUseDomain = request.getParameter("isUseDomain");
		String startupAfterSuccess = request.getParameter("startupAfterSuccess");
		boolean needStart = false;
		if(null != startupAfterSuccess && "on".equals(startupAfterSuccess)){
			needStart = true;
		}
		String serverName = "apache-tomcat"+tomcatType+"-"+tomcatName;
		String result = "";
		try {
			TomcatUtil.unTar(tomcatType, tomcatName);
			result += "解压tomcat成功|";
		} catch (Exception e) {
			result += "解压tomcat失败|";
			out.write(result.substring(0,result.length() - 1));
			out.flush();
			out.close();
			return;
		}
		try {
			TomcatUtil.changeTomcatPort(serverName, shutdownPort, httpPort, ajpPort, redirectPort, context);
			result += "配置端口成功|";
		} catch (Exception e) {
			result += "配置端口失败|";
			out.write(result.substring(0,result.length() - 1));
			out.flush();
			out.close();
			return;
		}
		//添加新tomcat到排序
		SerializableToFile.fileToObject();
		Constant.tomcatHitMap.put(serverName, 0);
		SerializableToFile.objectToFile();
		if(needStart){
			//启动tomcat
			try {
				TomcatOperator.startTomcat(serverName);
				result += "启动tomcat成功|";
			} catch (Exception e) {
				result += "启动tomcat失败|";
			}
		}
		//如果isUseDomain 为 1，则需要配置apache
		String sayResult = "";
		if("1".equals(isUseDomain)){
			//先配置httpd-vhosts.conf文件
			try {
				String httpdVhostsFilePath = Constant.apachePath + "conf/extra/httpd-vhosts.conf";
				ApacheUtil.addHttpdVhostsFile(httpdVhostsFilePath,tomcatName);
				result += "配置httpd-vhosts.conf文件成功|";
			} catch (Exception e) {
				result += "配置httpd-vhosts.conf文件失败|";
			}
			try {
				//配置vhosts文件
				String vhostsFilePath = Constant.apachePath + "conf/extra/vhosts/" + tomcatName + "-vhosts.conf";
				String contextPath = context.split("docBase=\"")[1];
				contextPath = contextPath.substring(0,contextPath.indexOf("\""));
				ApacheUtil.writeNewVhostsFile(vhostsFilePath, tomcatName, ajpPort, domain, contextPath);
				result += "配置" + tomcatName + "-vhosts.conf文件成功|";
			} catch (Exception e) {
				result += "配置" + tomcatName + "-vhosts.conf文件失败|";
			}
			//重启apache
			try {
				ApacheUtil.restartApache();
				result += "启动或重启apache成功|";
			} catch (Exception e) {
				result += "启动或重启apache失败|";
			}
			result += "<font color='#e38000'><b>请手动配置hosts：127.0.0.1 "+domain+"</b></font>|";
			//添加hosts配置
			sayResult = " with apache configure";
		}
		out.write(result.substring(0,result.length() - 1));
		Say.english("create New Tomcat "+sayResult+" success");
		out.flush();
		out.close();
	}
	
	@RequestMapping("/openTomcatsPathAjax")
	public void openTomcatsPathAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		OpenFileShell.open(Constant.tomcatsPath);
	}
	
	@RequestMapping("/openDirAjax")
	public void openDirAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String dir = request.getParameter("dir");
		OpenFileShell.open(dir);
	}
	
	@RequestMapping("/saveSysConfigAjax")
	public void saveSysConfigAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String sortType = request.getParameter("sortType");
		String letterSortType = request.getParameter("letterSortType");
		String fileType = request.getParameter("fileType");
		String password = request.getParameter("password");
		String ip = IPUtils.getIpAddress(request);
		Constant.sysconfigMap.put("sortType", sortType);
		Constant.sysconfigMap.put("letterSortType", letterSortType);
		Constant.sysconfigMap.put("fileType", fileType);
		String changePasswordResult = "";
		if(!"127.0.0.1".equals(ip) && !Constant.sysconfigMap.get("password").equals(password)){
			changePasswordResult = "<font color='red'><b>，但是，不允许其他的电脑修改密码，密码修改失败</b></font>";
		}
		if("127.0.0.1".equals(ip)){
			Constant.sysconfigMap.put("password", password);
		}
		SysConfigFile.objectToFile();
		out.write("<font color='green'><b>保存成功</b></font>"+changePasswordResult);
		out.flush();
		out.close();
	}
	
	@RequestMapping("/validatePassword")
	public void validatePassword(HttpServletRequest request, HttpServletResponse response,HttpSession session) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		if(null == Constant.sysconfigMap.get("password")){
			GetRightTomcatPort.init();
		}
		String password = request.getParameter("password");
		if(null != password && Constant.sysconfigMap.get("password").equals(password)){
			out.write("success");
			session.setAttribute("user", "OK");
		}else{
			out.write("error");
		}
		out.flush();
		out.close();
	}
	
	@RequestMapping("/changeApacheStateAjax")
	public void changeApacheStateAjax(HttpServletRequest request, HttpServletResponse response,HttpSession session) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		if(ApacheUtil.isApcheStart()){
			ApacheUtil.stopApache();
			while (ApacheUtil.isApcheStart()){
				Thread.sleep(500);
			}
		}else{
			ApacheUtil.startApache();
			while (!ApacheUtil.isApcheStart()){
				Thread.sleep(500);
			}
			Say.english("Apache start success");
		}
		PrintWriter out = response.getWriter();
		if(ApacheUtil.isApcheStart()){
			out.write("1");
		}else{
			out.write("0");
		}
		out.flush();
		out.close();
	}
	
	@RequestMapping("/modifyApacheStateAjax")
	public void modifyApacheStateAjax(HttpServletRequest request, HttpServletResponse response,HttpSession session) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String state = request.getParameter("state");
		if("0".equals(state)){
			//启动
			ApacheUtil.startApache();
			while (!ApacheUtil.isApcheStart()){
				Thread.sleep(500);
			}
			Say.english("Apache start success");
		}else if("1".equals(state)){
			//关闭
			ApacheUtil.stopApache();
			while (ApacheUtil.isApcheStart()){
				Thread.sleep(500);
			}
		}else if("2".equals(state)){
			ApacheUtil.restartApache();
			Thread.sleep(1000);
			Say.english("Apache restart success");
		}
		PrintWriter out = response.getWriter();
		if(ApacheUtil.isApcheStart()){
			out.write("1");
		}else{
			out.write("0");
		}
		out.flush();
		out.close();
	}
	
	@RequestMapping("/topOpenFileAjax")
	public void topOpenFileAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String filePath = request.getParameter("filePath");
		DoShell.shell("open "+filePath);
	}
	
	@RequestMapping("/toOpenDomainAjax")
	public void toOpenDomainAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String filePath = request.getParameter("filePath");
		File file = new File(filePath);
		String domain = "";
		if(file.exists()){
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String line = bufferedReader.readLine();
			while(null != line){
				if(line.contains("ServerName ")){
					domain = line.trim().replaceAll("ServerName", "").trim();
				}
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
		}
		out.write(domain);
		out.flush();
		out.close();
	}
	
	@RequestMapping("/getHostsEnvAjax")
	public void getHostsEnvAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.write(GetHostEnv.getEnv());
		out.flush();
		out.close();
	}
	
	@RequestMapping("/pingDomainAjax")
	public void pingDomainAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String filePath = request.getParameter("filePath");
		File file = new File(filePath);
		String domain = "";
		if(file.exists()){
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String line = bufferedReader.readLine();
			while(null != line){
				if(line.contains("ServerName ")){
					domain = line.trim().replaceAll("ServerName", "").trim();
				}
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
		}
		System.out.println("domain:"+domain);
		String[] hostsRes = PingDomain.ping(domain);
		if(!"".equals(hostsRes[0])){
			out.write(hostsRes[0]+"，耗时："+hostsRes[1]);
		}else{
			out.write("ping失败");
		}
		out.flush();
		out.close();
	}
}
