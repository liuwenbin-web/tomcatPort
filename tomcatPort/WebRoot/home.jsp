<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta charset="utf-8">
	<title>Tomcat集群控制台-登录</title>
	<link rel="stylesheet" href="css/bootstrap.min.css">
	<link rel="stylesheet" href="css/bootstrap-theme.min.css">
	<script src="js/jquery.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
  </head>
  <body>
   	<table style="width:100%;height:100%">
   		<tr>
   			<td align="center" valign="middle">
   				<div class="col-lg-6">
				    <div class="input-group input-group-lg" style="box-shadow: 0px 0px 35px #afafaf;">
				    	  <span class="input-group-addon" id="sizing-addon1"><span class="glyphicon glyphicon-user" aria-hidden="true"></span></span>
				      <input type="password" id="password" class="form-control" placeholder="请输入登录密码">
				      <span class="input-group-btn">
				        <button id="loginBtn" class="btn btn-primary" type="button">登录</button>
				      </span>
				    </div><!-- /input-group -->
				  </div><!-- /.col-lg-6 -->
   			</td>
   		</tr>
   	</table>
   	<div id="bk" style="width:100%;height:100%;position:absolute;top:0px;left:0px;overflow:hidden;z-index:-1;">
    		<img src="image/bk.jpg" width="100%" height="100%" style="position:absolute;top:0px;left:0px;z-index:-10;"/>
	</div>
   	<script type="text/javascript">
   		$(".col-lg-6").css("margin-left",($(document).width()-$(".col-lg-6").width())/2);
   		$("#password").keydown(function(e){
   			if(e.keyCode == 13){
   				$("#loginBtn").click();
   			}
   		});
   		$("#loginBtn").click(function(){
   			if($(this).html() == "登录"){
   				$.ajax({
   		   			type:"POST",
   		   			url:"/validatePassword",
   		   			data:"password="+$("#password").val(),
   		   			success:function(data){
   					   	if("error"==data){
	   					   	$("#loginBtn").removeClass("btn-primary");
	   			   			$("#loginBtn").addClass("btn-danger");
	   			   			$("#loginBtn").html("失败");
	   			   			setTimeout("resetBtn()",2000);
   					   	}else{
   					   		location.href="/index";
   					   	}
   		   			}
   		   		});
   			}
   		});
   		function resetBtn(){
   			$("#loginBtn").removeClass("btn-danger");
   			$("#loginBtn").addClass("btn-primary");
   			$("#loginBtn").html("登录");
   		}
   	</script>
  </body>
</html>
