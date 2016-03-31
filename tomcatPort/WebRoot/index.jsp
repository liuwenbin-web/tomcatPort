<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Tomcat集群控制台</title>
<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/bootstrap-theme.min.css">
<style type="text/css">
xmp {
	white-space: pre-wrap;
	word-wrap: break-word;
}
</style>
<script type="text/javascript">
	if("${commonPortList[0]}"==""){
		location.href="/index";
	}
</script>
<script src="js/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
</head>
<body>
	<div class="container">
		<br/>
		${portError}
		<table class="table table-bordered table-hover">
			<tr>
				<td><b>端口冲突统计结果&nbsp;</b><span class="badge"><c:out value="${fn:length(analysisResult)}"></c:out></span>
				</td>
			</tr>
			<c:if test="${fn:length(analysisResult) == 0}">
				<tr>
					<td>无端口冲突</td>
				</tr>
			</c:if>
			<c:if test="${fn:length(analysisResult) != 0}">
				<c:forEach items="${analysisResult}" var="analysis">
					<c:if test="${fn:contains(analysis, 'http端口')}">
						<tr class="danger"><td>${analysis}</td></tr>
					</c:if>
					<c:if test="${!fn:contains(analysis, 'http端口')}">
						<tr class="warning"><td>${analysis}</td></tr>
					</c:if>
				</c:forEach>
			</c:if>
		</table>
		<table id="tomcatPortsTable" class="table table-bordered table-hover">
			<tr>
				<td colspan="7">
					<b>各个tomcat的端口统计</b>
					&nbsp;<span class="badge"><c:out value="${fn:length(tomcatPorts)}"></c:out></span>&nbsp;&nbsp;&nbsp;
					<b><c:if test="${fn:contains(hostsEnv, '本地')}">
						<font id="hostsEnv" color='green'>${hostsEnv}</font>
					</c:if>
					<c:if test="${!fn:contains(hostsEnv, '本地')}">
						<font id="hostsEnv" color='red'>${hostsEnv}</font>
					</c:if></b>
					<span style="float:right">
						<div class="btn-group" role="group">
							<a title="创建新的Tomcat" href="javascript:;" id="addNewTomcatBtn" class="btn btn-default"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></a>
							<a title="打开Tomcat集群目录" href="javascript:;" id="openTomcatsBtn" class="btn btn-default"><span class="glyphicon glyphicon-folder-open" aria-hidden="true"></span></a>
							<a title="Apache配置管理" href="javascript:;" id="apacheConfigBtn" class="btn btn-default"><span class="glyphicon glyphicon-font" aria-hidden="true"></span></a>
							<a title="刷新Tomcat状态" href="javascript:;" id="refreshTomcatStateBtn" class="btn btn-default"><span class="glyphicon glyphicon-refresh" aria-hidden="true"></span></a>
							<a title="系统设置" href="javascript:;" id="sysConfigBtn" class="btn btn-default"><span class="glyphicon glyphicon-cog" aria-hidden="true"></span></a>
							<a style="display:none" title="批量启动" href="javascript:;" id="multiTomcatStartBtn" class="btn btn-info multiTomcatConfigBtns"><span class="glyphicon glyphicon-play-circle" aria-hidden="true"></span></a>
							<a style="display:none" title="批量关闭" href="javascript:;" id="multiTomcatCloseBtn" class="btn btn-info multiTomcatConfigBtns"><span class="glyphicon glyphicon-off" aria-hidden="true"></span></a>
							<a style="display:none" title="批量重启" href="javascript:;" id="multiTomcatRestartBtn" class="btn btn-info multiTomcatConfigBtns"><span class="glyphicon glyphicon-repeat" aria-hidden="true"></span></a>&nbsp;&nbsp;
							<a title="批量管理Tomcat" href="javascript:;" id="multiTomcatConfigBtn" class="btn btn-default"><span class="glyphicon glyphicon-th-list" aria-hidden="true"></span></a>
							<a title="使用方法" href="javascript:;" id="howToUseBtn" class="btn btn-default"><span class="glyphicon glyphicon-question-sign" aria-hidden="true"></span></a>
						</div>
					</span>
				</td>
			</tr>
			<tr>
				<td style="width:220px"><b>Tomcat</b></td>
				<td><b>shutdown</b></td>
				<td><b>http</b></td>
				<td><b>ajp</b></td>
				<td><b>redirect</b></td>
				<td><b>PID</b></td>
				<td style="width:529px"><b>编辑</b></td>
				<td class="multiTd" style="display:none"><b>批量</b></td>
			</tr>
			<c:forEach var="sortKey" items="${sortKeys}">
				<tr>
					<td class="serverName"><a href="javascript:;" class="apacheIcon" style="display:none"><kbd><img src="image/apache.png" width="4%" height="4%"/></kbd></a>&nbsp;&nbsp;<span class="serverNameSpan">${sortKey}</span></td>
					<c:forEach items="${tomcatPorts[sortKey]}" var="port">
						<td>${port}</td>
					</c:forEach>
					<td class="pid"></td>
					<td>
					<div class="btn-group" role="group">
						<button class="btn btn-primary tomcatBtn" style="width:113px">启动tomcat</button>
						<a style="width:57px;display:none" title="关闭tomcat" href="javascript:;" class="btn btn-danger closeBtn"><span class="glyphicon glyphicon-off" aria-hidden="true"></span></a>
						<a style="width:56px;display:none" title="重启tomcat" href="javascript:;" class="btn btn-warning restartBtn"><span class="glyphicon glyphicon-repeat" aria-hidden="true"></span></a>
						<!-- Single button -->
						<div class="btn-group">
						  <button type="button" class="btn btn-info dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" style="width:104px">打开<span class="caret"></span>
						  </button>
						  <ul class="dropdown-menu">
						    <li><a href="javascript:;" class="showServerXml">server.xml</a></li>
						    <li><a href="javascript:;" class="openTomcatPath">Tomcat目录</a></li>
						    <li><a href="javascript:;" class="openTomcatLogsPath">Logs目录</a></li>
						  </ul>
						</div>
						<button class="btn btn-info showPath">显示Context</button>
						<button class="btn btn-warning delLog" style="width:96px">清空日志</button>
						<button class="btn btn-warning delWork" style="width:96px">清空work</button>
					</div>
					</td>
					<td class="multiTd" align="center" valign="middle" style="display:none"></td>
				</tr>
			</c:forEach>
		</table>
		<div class="alert alert-info" role="alert">
			新tomcat的推荐端口：<br />shutdown端口：${commonPortList[0]}<br />http端口：${commonPortList[1]}<br />ajp端口：${commonPortList[2]}<br />redirect端口：${commonPortList[3]}
		</div>
	</div>
	
	<div id="modal" class="modal fade">
	  <div class="modal-dialog" style="width:85%">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	        <h4 class="modal-title" id="modalTitle"></h4>
	      </div>
	      <div class="modal-body" style="margin:0 20px 0 20px">
	        <p>
	        <button id="noCommitBtn" type="button" class="btn btn-default">隐藏注释</button>
	        	<div id="modalContent"></div>
	        </p>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	      </div>
	    </div><!-- /.modal-content -->
	  </div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
	
	<div id="addTomcatModal" class="modal fade">
	  <div class="modal-dialog" style="width:50%">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	        <h4 class="modal-title" id="modalTitle">创建新的tomcat</h4>
	      </div>
	      <div class="modal-body" style="margin:0 20px 0 20px">
	        <p id="modalContent">
	        		<form id="createNewTomcatForm">
	        		<table class="table table-bordered table-hover">
	        			<tr>
	        				<td>Tomcat版本</td>
	        				<td>
	        					<input type="radio" id="tomcat6" value="6" name="tomcatType" checked>&nbsp;&nbsp;<label for="tomcat6">Tomcat 6</label>&nbsp;&nbsp;&nbsp;&nbsp;
	        					<input type="radio" id="tomcat7" value="7" name="tomcatType">&nbsp;&nbsp;<label for="tomcat7">Tomcat 7</label>&nbsp;&nbsp;&nbsp;&nbsp;
	        					<input type="radio" id="tomcat8" value="8" name="tomcatType">&nbsp;&nbsp;<label for="tomcat8">Tomcat 8</label>
	        				</td>
	        			</tr>
	        			<tr>
	        				<td>Tomcat名称</td>
	        				<td>
	        					<span>apache-tomcat</span><span id="tomcatVersion"></span><span>-</span><input type="text" id="tomcatName" name="tomcatName" class="form-control" style="width:195px;display:inline" placeholder="不能为work，不能包含符号">
	        					<img class="stateImg" style="display:none" id="tomcatNameImg" src="image/right.jpg" width="31px" height="31px" style="margin-left:5px"/>
	        				</td>
	        			</tr>
	        			<tr>
	        				<td>shutdown端口</td>
	        				<td>
	        					<input style="width:300px;display:inline" type="text" id="shutdownPort" name="shutdownPort" class="form-control" placeholder="推荐使用${commonPortList[0]}" value="${commonPortList[0]}" defaultValue="${commonPortList[0]}">
	        					<img class="stateImg" style="display:none" src="image/right.jpg" width="31px" height="31px" style="margin-left:5px"/>
	        				</td>
	        			</tr>
	        			<tr>
	        				<td>http端口</td>
	        				<td>
	        					<input style="width:300px;display:inline" type="text" id="httpPort" name="httpPort" class="form-control" placeholder="推荐使用${commonPortList[1]}" value="${commonPortList[1]}" defaultValue="${commonPortList[1]}">
	        					<img class="stateImg" style="display:none" src="image/right.jpg" width="31px" height="31px" style="margin-left:5px"/>
	        				</td>
	        			</tr>
	        			<tr>
	        				<td>ajp端口</td>
	        				<td>
	        					<input style="width:300px;display:inline" type="text" id="ajpPort" name="ajpPort" class="form-control" placeholder="推荐使用${commonPortList[2]}" value="${commonPortList[2]}" defaultValue="${commonPortList[2]}">
	        					<img class="stateImg" style="display:none" src="image/right.jpg" width="31px" height="31px" style="margin-left:5px"/>
	        				</td>
	        			</tr>
	        			<tr>
	        				<td>redirect端口</td>
	        				<td>
	        					<input style="width:300px;display:inline" type="text" id="redirectPort" name="redirectPort" class="form-control" placeholder="推荐使用${commonPortList[3]}" value="${commonPortList[3]}" defaultValue="${commonPortList[3]}">
	        					<img class="stateImg" style="display:none" src="image/right.jpg" width="31px" height="31px" style="margin-left:5px"/>
	        				</td>
	        			</tr>
	        			<tr>
	        				<td>Context</td>
	        				<td>
	        					<textarea style="height:120px;" id="context" name="context" class="form-control" defaultValue="&lt;Context path=&quot;&quot; docBase=&quot;&quot; reloadable=&quot;false&quot; crossContext=&quot;true&quot;&gt;&lt;/Context&gt;">&lt;Context path=&quot;&quot; docBase=&quot;&quot; reloadable=&quot;false&quot; crossContext=&quot;true&quot;&gt;&lt;/Context&gt;</textarea>
	        					<div class="btn-group" role="group" style="margin-top:5px" id="pathBtns">
								<button class="btn btn-default" path="${gitPath}" style="width:100px">Git</button>
								<button class="btn btn-default" path="${tomcatsPath}" style="width:100px">webapps</button>
							</div>
							<img class="stateImg" style="display:none" id="contextImg" src="image/right.jpg" width="31px" height="31px" style="margin-left:5px"/>
	        				</td>
	        			</tr>
	        			<tr>
	        				<td colspan="2" align="center">
	        					<button id="domainConfigBtn" class="btn btn-default">配置域名</button>
	        					<input type="hidden" name="isUseDomain" value="0"/>
	        				</td>
	        			</tr>
	        			<tr style="display:none" id="domainTr">
	        				<td>域名</td>
	        				<td>
	        					<input style="width:300px;display:inline" type="text" id="domain" name="domain" class="form-control"/>
	        					<img class="stateImg" style="display:none" id="domainImg" src="image/right.jpg" width="31px" height="31px" style="margin-left:5px"/>
	        				</td>
	        			</tr>
	        			<tr>
	        				<td>操作</td>
	        				<td>
	        					<input type="checkbox" name="startupAfterSuccess" id="startupAfterSuccess"/>&nbsp;
	        					<label for="startupAfterSuccess">创建成功后启动tomcat</label>
	        				</td>
	        			</tr>
	        			<tr id="checkResultDiv" style="display:none">
	        				<td colspan="2" id="checkResult"></td>
	        			</tr>
	        		</table>
	        		</form>
	        </p>
	      </div>
	      <div class="modal-footer">
	      	<button type="button" class="btn btn-primary" id="createBeforeSubmit">校验</button>
	      	<button type="button" class="btn btn-primary" id="createNewTomcat">创建</button>
	        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	      </div>
	    </div><!-- /.modal-content -->
	  </div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
	
	<div id="sysConfigModal" class="modal fade">
	  <div class="modal-dialog" style="width:50%">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	        <h4 class="modal-title">系统配置</h4>
	      </div>
	      <div class="modal-body" style="margin:0 20px 0 20px">
	        <p>
	        	<div>
	        		<div id="sysDefaultConfig">
		        		<input type="hidden" name="sortType" value="${sysConfig['sortType']}"/>
		        		<input type="hidden" name="letterSortType" value="${sysConfig['letterSortType']}"/>
		        		<input type="hidden" name="fileType" value="${sysConfig['fileType']}"/>
		        		<input type="hidden" name="password" value="${sysConfig['password']}"/>
	        		</div>
	        		<form id="sysConfigFrom">
	        		<table class="table table-bordered table-hover">
	        			<tr>
	        				<td>tomcat排序方式</td>
	        				<td>
	        					<input type="radio" name="sortType" id="hotSort" value="hot"/>&nbsp;
	        					<label for="hotSort">按照使用频率排序</label>
	        					&nbsp;&nbsp;&nbsp;&nbsp;
	        					<input type="radio" name="sortType" id="letterSort" value="letter"/>&nbsp;
	        					<label for="letterSort">按照字母顺序排序</label>
	        					&nbsp;&nbsp;&nbsp;&nbsp;
	        				</td>
	        			</tr>
	        			<tr id="letterSortTr" style="display:none">
	        				<td>字母排序方式</td>
	        				<td>
	        					<div class="btn-group" role="group">
								<a href="javascript:;" id="AtoZBtn" class="btn btn-default"><span class="glyphicon glyphicon-sort-by-alphabet" aria-hidden="true"></span>&nbsp;A-Z</a>
								<a href="javascript:;" id="ZtoABtn" class="btn btn-default"><span class="glyphicon glyphicon-sort-by-alphabet-alt" aria-hidden="true"></span>&nbsp;Z-A</a>
								<input type="hidden" name="letterSortType" value=""/>
							</div>
	        				</td>
	        			</tr>
	        			<tr>
	        				<td>文件打开方式：</td>
	        				<td>
	        					<input id="inner" type="radio" name="fileType" checked value="0"/>
							<label for="inner">页面弹窗</label>&nbsp;&nbsp;
							<input id="outer" type="radio" name="fileType" value="1"/>
							<label for="outer">外部程序</label>
	        				</td>
	        			</tr>
	        			<tr>
	        				<td>
	        					访问密码：
	        				</td>
	        				<td>
	        					<input type="password" id="password" name="password" class="form-control" value="${sysConfig['password']}"/>
	        				</td>
	        			</tr>
	        			<tr style="display:none" id="sysConfigResult">
	        				<td colspan="2"></td>
	        			</tr>
	        		</table>
	        		</form>
	        	</div>
	        </p>
	      </div>
	      <div class="modal-footer">
	      	<button type="button" class="btn btn-primary" id="saveSysConfigBtn">保存</button>
	        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	      </div>
	    </div><!-- /.modal-content -->
	  </div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
	
	<div id="apacheConfigModal" class="modal fade">
	  <div class="modal-dialog" style="width:50%">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	        <h4 class="modal-title">Apache管理</h4>
	      </div>
	      <div class="modal-body" style="margin:0 20px 0 20px">
	        <p>
	        	<div>
	        		<form id="apacheConfigFrom">
	        		<table class="table table-bordered table-hover">
	        			<tr>
	        				<td>当前Apache状态</td>
	        				<td id="apacheStateTd"></td>
	        			</tr>
	        			<tr>
	        				<td>Apache操作</td>
	        				<td>
	        					<!-- <div class="btn-group" role="group"> -->
								<a href="javascript:;" id="startApache" state="0" class="btn btn-default"><span class="glyphicon glyphicon-play-circle" aria-hidden="true"></span>&nbsp;启动apache</a>
								<a href="javascript:;" id="stopApache" state="1" class="btn btn-default"><span class="glyphicon glyphicon-off" aria-hidden="true"></span>&nbsp;停止apache</a>
								<a href="javascript:;" id="restartApache" state="2" class="btn btn-default"><span class="glyphicon glyphicon-repeat" aria-hidden="true"></span>&nbsp;重启apache</a>
							<!-- </div> -->
	        				</td>
	        			</tr>
	        			<tr>
	        				<td>Apache配置</td>
	        				<td>
	        					<a class="topOpenFile" href="javascript:;" data="${apachePath}conf/extra/httpd-vhosts.conf">httpd-vhosts.conf</a>
	        				</td>
	        			</tr>
	        			<tr>
	        				<td>Apache的vhosts</td>
	        				<td>
	        					<c:forEach items="${vhostFileList}" var="vhostFile">
	        						<a class="topOpenFile" href="javascript:;" data="${vhostFile.absoluteFile}">${vhostFile.name}</a><br/>
	        					</c:forEach>			
	        				</td>
	        			</tr>
	        		</table>
	        		</form>
	        	</div>
	        </p>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	      </div>
	    </div><!-- /.modal-content -->
	  </div><!-- /.modal-dialog -->
	  </div><!-- /.modal -->
	  
	  <div id="howToUseModal" class="modal fade">
	  <div class="modal-dialog" style="width:50%">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	        <h4 class="modal-title">使用方法</h4>
	      </div>
	      <div class="modal-body" style="margin:0 20px 0 20px">
	        <p>
	        	<div>
	        		<form id="apacheConfigFrom">
	        		<table class="table table-bordered table-hover">
	        			<tr>
	        				<td><img src="image/howtouse/1.png"/></td>
	        			</tr>
	        			<tr>
	        				<td>
	        					<b>点击：</b>打开Tomcat对应的vhosts配置<br/>
	        					<b>右击：</b>新窗口打开域名
	        				</td>
	        			</tr>
	        			<tr><td>&nbsp;</td></tr>
	        			<tr>
	        				<td><img src="image/howtouse/3.png"/></td>
	        			</tr>
	        			<tr>
	        				<td>
	        					<b>双击：</b>Ping对应的域名<br/>
	        					<b>右击：</b>新窗口打开IP+端口
	        				</td>
	        			</tr>
	        			<tr><td>&nbsp;</td></tr>
	        			<tr>
	        				<td><img src="image/howtouse/4.png" width="100%"/></td>
	        			</tr>
	        			<tr>
	        				<td>
	        					<b>双击：</b>打开Context配置中docBase对应的地址
	        				</td>
	        			</tr>
	        			<tr><td>&nbsp;</td></tr>
	        			<tr>
	        				<td><img src="image/howtouse/2.png"/></td>
	        			</tr>
	        			<tr>
	        				<td>
	        					<b>点击：</b>打开Apache配置弹窗<br/>
	        					<b>右击：</b>快速启动/停止Apache（有下划线时是运行状态）
	        				</td>
	        			</tr>
	        			<tr><td>&nbsp;</td></tr>
	        			<tr>
	        				<td><img src="image/howtouse/5.png"/></td>
	        			</tr>
	        			<tr>
	        				<td>
	        					<b>点击：</b>刷新hosts环境
	        				</td>
	        			</tr>
	        		</table>
	        		</form>
	        	</div>
	        </p>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	      </div>
	    </div><!-- /.modal-content -->
	  </div><!-- /.modal-dialog -->
	  </div><!-- /.modal -->
	
	<div id="msgBox" class="alert alert-success" role="alert" style="display:none;position:absolute;top:0;z-index:99999;width:100%;"></div>
	
	<script type="text/javascript" src="js/main.js"></script>
</body>
</html>