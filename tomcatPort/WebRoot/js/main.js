 $(document).bind("contextmenu",function(e){
      return false;
 });

var colspan = 7;
$(".showPath").click(function(){
	var tr=$(this).parent().parent().parent();
	var serverName=$.trim(tr.find(".serverName").text());
	var btn = $(this);
	if(btn.html().indexOf("显示")!=-1){
		$.ajax({
			type:"POST",
			url:"/getPathAjax",
			data:"serverName="+serverName,
			success:function(data){
				if(""!=data){
					tr.after("<tr class=\""+serverName+"Path\"><td colspan=\""+colspan+"\">"+data+"</tr>");	
				}else{
					tr.after("<tr class=\""+serverName+"Path\"><td colspan=\""+colspan+"\">没有配置Context节点</tr>");
				}
				
				btn.html("隐藏Context");
				btn.addClass("active");
				btn.removeClass("btn-info");
				btn.addClass("btn-warning");
				$("."+serverName+"Path xmp").dblclick(function(){
					var url = $(this).html();
					url = url.substr(url.indexOf("docBase=\"")+9);
					url = url.split("\"")[0];
					openContextDir(url);
				});
			}
		});
	}else{
		$("."+serverName+"Path").remove();
		btn.html("显示Context");
		btn.removeClass("active");
		btn.removeClass("btn-warning");
		btn.addClass("btn-info");
	}
});
$(".showServerXml").click(function(){
	var tr=$(this).parent().parent().parent().parent().parent().parent();
	var serverName=$.trim(tr.find(".serverName").text());
	var fileType = $("input[name='fileType']:checked").val();
	$.ajax({
		type:"POST",
		url:"/getServerXmlAjax",
		data:"serverName="+serverName+"&fileType="+fileType,
		success:function(data){
			if(0==fileType){
				$("#modalTitle").html(serverName+"服务器的server.xml文件");
				$("#modalContent").html(data);
				$("#modal").modal("show");
				$("#noCommitBtn").click();
			}
		}
	});
});
//显示、隐藏注释
$("#noCommitBtn").click(function(){
	if($(this).attr("class").indexOf("active")!=-1){
		$(this).removeClass("active");
		$("#modalContent font").show();
	}else{
		$(this).addClass("active");
		$("#modalContent font").hide();
	}
});
//关闭server.xml modal的事件
$("#modal").on('hidden.bs.modal', function () {
	if($("#noCommitBtn").attr("class").indexOf("active")!=-1){
		$("#noCommitBtn").removeClass("active");
	}
});
$(".openTomcatPath").click(function(){
	var tr=$(this).parent().parent().parent().parent().parent().parent();
	var serverName=$.trim(tr.find(".serverName").text());
	$.ajax({
		type:"POST",
		url:"/openTomcatPathAjax",
		data:"serverName="+serverName,
		success:function(data){
		}
	});
});
//openTomcatLogsPath
$(".openTomcatLogsPath").click(function(){
	var tr=$(this).parent().parent().parent().parent().parent().parent();
	var serverName=$.trim(tr.find(".serverName").text());
	$.ajax({
		type:"POST",
		url:"/openTomcatLogsPathAjax",
		data:"serverName="+serverName,
		success:function(data){
		}
	});
});
function showCloseBtns(start,close,restart){
	start.remove();
	close.show();
	restart.show();
}
function showStartBtns(close,restart){
	if(close.prev() == undefined){
		close.prev().remove();
	}
	close.parent().prepend("<button class=\"btn btn-primary tomcatBtn\" style=\"width:113px\">启动tomcat</button>");
	close.hide();
	restart.hide();
	var start = close.prev();
	return start;
}
//启动tomcat方法
tomcatBtnClick();
function tomcatBtnClick(){
	$(".tomcatBtn").click(function(){
		var tr=$(this).parent().parent().parent();
		var serverName=$.trim(tr.find(".serverName").text());
		var btn = $(this);
		var closeBtn = btn.next();
		var restartBtn = closeBtn.next();
		if(btn.html().indexOf("启动")!=-1 && btn.html().indexOf("启动中")==-1){
			//启动tomcat
			$.ajax({
				type:"POST",
				url:"/startTomcatAjax",
				async: false,
				data:"serverName="+serverName,
				beforeSend:function(){
					btn.show();
					closeBtn.hide();
					restartBtn.hide();
					btn.html("tomcat启动中");
					btn.removeClass("btn-primary");
					btn.removeClass("btn-danger");
					btn.addClass("btn-warning");
					tr.find(".pid").html("");
				},
				success:function(data){
					var datas = data.split(":");
					if(datas[0] == "started"){
						alert(serverName+"已经启动，pid为："+data);
						tr.find(".pid").html(datas[1]);
						showCloseBtns(btn,closeBtn,restartBtn);
					}else{
						if(datas[1] != 0){
							//启动成功
							tr.find(".pid").html(datas[1]);
							//显示重启按钮
							showCloseBtns(btn,closeBtn,restartBtn);
						}else{
							//启动失败
							tr.find(".pid").html("");
							//btn.show();
							showStartBtns(btn,closeBtn,restartBtn);
							alert(serverName+"启动时间过长，或启动失败");
						}
					}
				}
			});
		}
	});
}
//结束tomcat事件
$(".closeBtn").click(function(){
	var tr=$(this).parent().parent().parent();
	var serverName=$.trim(tr.find(".serverName").text());
	var btn = "";
	var closeBtn = $(this);
	var restartBtn = closeBtn.next();
	
	$.ajax({
		type:"POST",
		url:"/stopTomcatAjax",
		async: false,
		data:"serverName="+serverName,
		beforeSend:function(){
			btn = showStartBtns(closeBtn,restartBtn);
			var i = 0;
			tr.find(".tomcatBtn").each(function(){
				i++;
				if(2 == i){
					$(this).remove();
				}
			});
			btn.html("tomcat关闭中");
			btn.removeClass("btn-primary");
			btn.addClass("btn-warning");
			tr.find(".pid").html("");
		},
		success:function(data){
			if(data == "stoped"){
				alert(serverName+"已经关闭");
			}
			btn.html("启动tomcat");
			btn.removeClass("btn-warning");
			btn.addClass("btn-primary");
			tr.find(".pid").html("");
			tomcatBtnClick();
		}
	});
});
//重启tomcat事件
$(".restartBtn").click(function(){
	var tr=$(this).parent().parent().parent();
	var serverName=$.trim(tr.find(".serverName").text());
	var restartBtn = $(this);
	var closeBtn = restartBtn.prev();
	var startBtn = closeBtn.prev();
	var parentDiv = restartBtn.parent();
	//alert(0);
	closeBtn.click();
	//alert(1);
	tomcatBtnClick();
	parentDiv.find(".tomcatBtn").click();
});
function operateApacheStateData(data){
	if("1" == data){
		$("#apacheConfigBtn").html("<span aria-hidden=\"true\" class=\"glyphicon glyphicon-text-color\"></span>");
		$("#apacheConfigBtn").attr("title","Apache配置管理(状态：运行)");
		$("#apacheStateTd").html("<font color=\"green\"><b>运行</b></font>");
		$("#startApache").hide();
		$("#stopApache").show();
		$("#restartApache").show();
	}else{
		$("#apacheConfigBtn").html("<span aria-hidden=\"true\" class=\"glyphicon glyphicon-font\"></span>");
		$("#apacheConfigBtn").attr("title","Apache配置管理(状态：停止)");
		$("#apacheStateTd").html("<font color=\"red\"><b>停止</b></font>");
		$("#startApache").show();
		$("#stopApache").hide();
		$("#restartApache").hide();
	}
}
function initApacheState(){
	//初始化apache状态
	$.ajax({
		type:"POST",
		url:"/getApacheStateAjax",
		success:function(data){
			operateApacheStateData(data);
		}
	});
}
function initTomcatState(){
	initApacheState();
	//初始化tomcat状态
	$("#tomcatPortsTable").find("tr").each(function(){
		var serverName = $.trim($(this).find(".serverName").text());
		if(serverName != undefined){
			var tr = $(this);
			$.ajax({
				type:"POST",
				url:"/getTomcatStateAjax",
				data:"serverName="+serverName,
				beforeSend:function(){
					tr.find(".pid").html("<img src=\"image/loading.gif\" width=\"33px\" height=\"33px\"/>");
				},
				success:function(data){
					tr.find(".pid").html("");
					var datas = "";
					var pid = "";
					if(data.indexOf(":") != -1){
						datas = data.split(":");
						pid = datas[0];
					}else{
						pid = data;
					}
					if(pid*1 != 0){
						tr.find(".pid").html(pid);
						var btn = tr.find(".tomcatBtn");
						var closeBtn = btn.next();
						var restartBtn = closeBtn.next();
						showCloseBtns(btn,closeBtn,restartBtn);
					}
					if("" != datas){
						var apacheIcon = tr.find(".apacheIcon");
						apacheIcon.show();
						apacheIcon.attr("data",datas[1]);
						apacheIcon.attr("title",datas[2]);
					}
				}
			});
		}
	});
}
initTomcatState();
$("#refreshTomcatStateBtn").click(function(){
	initTomcatState();
	$("#hostsEnv").click();
});
$("#addNewTomcatBtn").click(function(){
	$("#addTomcatModal").modal("show");
});
//清空日志
$(".delLog").click(function(){
	var tr=$(this).parent().parent().parent();
	var serverName=$.trim(tr.find(".serverName").text());
	var btn = $(this);
	if(btn.html()=="清空日志"){
		$.ajax({
			type:"POST",
			url:"/clearLogAjax",
			data:"serverName="+serverName,
			beforeSend:function(){
				btn.html("日志清理中");
			},
			error:function(){
				$("#msgBox").removeClass("alert-success");
				$("#msgBox").addClass("alert-danger");
				$("#msgBox").html("日志删除失败，连接服务器超时");
				$("#msgBox").slideDown(200);
				btn.html("清空日志");
				hideMsgBoxDH();
			},
			success:function(data){
				btn.html("清空日志");
				if(data.indexOf("没有日志")!=-1){
					$("#msgBox").removeClass("alert-success");
					$("#msgBox").addClass("alert-warning");
				}
				if(data.indexOf("未删除")!=-1){
					$("#msgBox").removeClass("alert-success");
					$("#msgBox").addClass("alert-danger");
				}
				$("#msgBox").html(data);
				$("#msgBox").slideDown(200);
				hideMsgBoxDH();
			}
		});
	}
});

$(".delWork").click(function(){
	var tr=$(this).parent().parent().parent();
	var serverName=$.trim(tr.find(".serverName").text());
	var btn = $(this);
	if(btn.html()=="清空work"){
		$.ajax({
			type:"POST",
			url:"/clearWorkAjax",
			data:"serverName="+serverName,
			beforeSend:function(){
				btn.html("work清理中");
			},
			error:function(){
				$("#msgBox").removeClass("alert-success");
				$("#msgBox").addClass("alert-danger");
				$("#msgBox").html("work删除失败，连接服务器超时");
				$("#msgBox").slideDown(200);
				btn.html("清空work");
				hideMsgBoxDH();
			},
			success:function(data){
				btn.html("清空work");
				if(data.indexOf("没有work")!=-1){
					$("#msgBox").removeClass("alert-success");
					$("#msgBox").addClass("alert-warning");
				}
				if(data.indexOf("成功")!=-1){
					$("#msgBox").removeClass("alert-danger");
					$("#msgBox").addClass("alert-success");
				}
				if(data.indexOf("失败")!=-1){
					$("#msgBox").removeClass("alert-success");
					$("#msgBox").addClass("alert-danger");
				}
				$("#msgBox").html(data);
				$("#msgBox").slideDown(200);
				hideMsgBoxDH();
			}
		});
	}
});
//创建新的tomcat
//msgBox
$(document).scroll(function(){
	$("#msgBox").css("top",$(document).scrollTop());
});
function hideMsgBox(){
	$("#msgBox").slideUp(300,function(){
		$("#msgBox").removeClass("alert-warning");
		$("#msgBox").removeClass("alert-danger");
		$("#msgBox").removeClass("alert-success");
		$("#msgBox").addClass("alert-success");
	});
}
function hideMsgBoxDH(){
	setTimeout("hideMsgBox()",3000);
}
//tomcatType的切换事件
$("#tomcatVersion").html($("input[name='tomcatType']:checked").val());
$("input[name='tomcatType']").change(function(){
	$("#tomcatVersion").html($("input[name='tomcatType']:checked").val());
});
$("#domainConfigBtn").click(function(){
	if($(this).prop("class").indexOf("active")!=-1){
		$(this).removeClass("active");
		$("#domainTr").hide();
		$("#domainTr").val("");
		$("input[name='isUseDomain']").val("0");
	}else{
		$(this).addClass("active");
		$("#domainTr").show();
		$("#domain").val($("#tomcatName").val()+".mapbar.com");
		$("input[name='isUseDomain']").val("1");
	}
	return false;
});
$("#pathBtns button").click(function(){
	var pathType = $(this).html();
	var context = $("#context").val();
	if("" == context){
		context = $("#context").attr("defaultValue");
	}
	var front = context.substr(0,context.indexOf("docBase=\"")+9);
	var end = context.replace(front,"");
	end = end.substr(end.indexOf("\""));
	var result = front;
	if(pathType == "Git"){
		result += $(this).attr("path")+$("#tomcatName").val()+"/"+$("#tomcatName").val()+"/WebRoot/";
	}
	if(pathType == "MyEclipse"){
		result += $(this).attr("path")+$("#tomcatName").val()+"/WebRoot/";
	}
	if(pathType == "webapps"){
		result += $(this).attr("path")+"apache-tomcat"+$("#tomcatVersion").html()+"-"+$("#tomcatName").val()+"/webapps/ROOT/";
	}
	result += end;
	$("#context").val(result);
	return false;
});
function showRight(idName){
	$("#"+idName+"Img").show();
	$("#"+idName+"Img").attr("src","image/right.jpg");
}
function showWrong(idName){
	$("#"+idName+"Img").show();
	$("#"+idName+"Img").attr("src","image/wrong.jpg");
}
//验证提交参数
//验证tomcat名称
var nameResult = "";
$("#tomcatName").blur(function(){
	var tomcatName = $("#tomcatName").val();
	$.ajax({
		type:"POST",
		async: false,
		url:"/validateTomcatNameAjax",
		data:"tomcatName="+tomcatName,
		success:function(data){
			if("OK" == data){
				showRight("tomcatName");
				nameResult="";
			}else{
				showWrong("tomcatName");
				if("used" == data){
					nameResult = tomcatName+"名称已被使用";
				}else{
					nameResult = "tomcat名称不正确";
				}
			}
		}
	});
});
//验证各个端口
var shutdownPortResult = "";
var httpPortResult = "";
var ajpPortResult = "";
var redirectPortResult = "";
$("#shutdownPort,#httpPort,#ajpPort,#redirectPort").blur(function(){
	var img = $(this).next();
	var port = $(this).val();
	var name = $(this).attr("id");
	$.ajax({
		type:"POST",
		async: false,
		url:"/validatePortAjax",
		data:"port="+port,
		success:function(data){
			if("OK" == data){
				img.show();
				img.attr("src","image/right.jpg");
				if(name == "shutdownPort"){
					shutdownPortResult = "";
				}
				if(name == "httpPort"){
					httpPortResult = "";
				}
				if(name == "ajpPort"){
					ajpPortResult = "";
				}
				if(name == "redirectPort"){
					redirectPortResult = "";
				}
			}else{
				img.show();
				img.attr("src","image/wrong.jpg");
				$("#msgBox").removeClass("alert-success");
				$("#msgBox").addClass("alert-danger");
				$("#msgBox").html(data);
				$("#msgBox").slideDown(200);
				hideMsgBoxDH();
				if(name == "shutdownPort"){
					shutdownPortResult = data;
				}
				if(name == "httpPort"){
					httpPortResult = data;
				}
				if(name == "ajpPort"){
					ajpPortResult = data;
				}
				if(name == "redirectPort"){
					redirectPortResult = data;
				}
			}
		}
	});
});
var contextResult = "";
$("#context").blur(function(){
	if($(this).val() == ""){
		contextResult = "请配置Context";
		showWrong("context");
	}else if($(this).val().indexOf("docBase=\"\"")!=-1){
		contextResult = "请配置Context中的docBase的值，如果不确定，可以点击\"webapps\"按钮进行获取";
		showWrong("context");
	}else{
		contextResult = "";
		showRight("context");
	}
});
//验证结果
var domainResult = "";
$("#domain").blur(function(){
	if($("input[name='isUseDomain']").val()=="1"){
		if($("#domain").val() == ""){
			domainResult = "请填写域名";
			showWrong("domain");
		}else if($("#domain").val() == ".mapbar.com"){
			domainResult = "域名不正确";
			showWrong("domain");
		}else{
			domainResult = "";
			showRight("domain");
		}
	}
});
var finalResult = "";
function validate(){
	$("#tomcatName").blur();
	$("#shutdownPort,#httpPort,#ajpPort,#redirectPort").blur();
	$("#context").blur();
	$("#domain").blur();
	finalResult = "";
	if("" != nameResult){
		finalResult += nameResult + "<br/>";
	}
	if("" != shutdownPortResult){
		finalResult += shutdownPortResult + "<br/>";
	}
	if("" != httpPortResult){
		finalResult += httpPortResult + "<br/>";
	}
	if("" != ajpPortResult){
		finalResult += ajpPortResult + "<br/>";
	}
	if("" != redirectPortResult){
		finalResult += redirectPortResult + "<br/>";
	}
	if("" != contextResult){
		finalResult += contextResult + "<br/>";
	}
	if("" != domainResult){
		finalResult += domainResult + "<br/>";
	}
}
$("#createBeforeSubmit").click(function(){
	$("#createBeforeSubmit").addClass("btn-warning");
	$("#createBeforeSubmit").removeClass("btn-default");
	$("#createBeforeSubmit").removeClass("btn-danger");
	$("#createBeforeSubmit").removeClass("btn-success");
	$("#createBeforeSubmit").html("校验中");
	validate();
	if(finalResult != ""){
		$("#checkResultDiv").show();
		$("#checkResult").html("<font color='red' size='2'><b>"+finalResult+"</b></font>");
		$("#createBeforeSubmit").addClass("btn-danger");
		$("#createBeforeSubmit").removeClass("btn-default");
		$("#createBeforeSubmit").removeClass("btn-warning");
		$("#createBeforeSubmit").removeClass("btn-success");
		$("#createBeforeSubmit").html("校验失败");
	}else{
		$("#checkResultDiv").hide();
		$("#createBeforeSubmit").addClass("btn-success");
		$("#createBeforeSubmit").removeClass("btn-danger");
		$("#createBeforeSubmit").removeClass("btn-default");
		$("#createBeforeSubmit").removeClass("btn-warning");
		$("#createBeforeSubmit").html("校验成功");
	}
});
$("#createNewTomcat").click(function(){
	if($("#createNewTomcat").html() == "创建" || $("#createNewTomcat").html() == "校验失败"){
		$("#createNewTomcat").removeClass("btn-primary");
		$("#createNewTomcat").removeClass("btn-danger");
		$("#createNewTomcat").removeClass("btn-warning");
		$("#createNewTomcat").removeClass("btn-success");
		$("#createNewTomcat").addClass("btn-default");
		$("#createNewTomcat").html("校验中...");
		validate();
		if(finalResult != ""){
			$("#checkResultDiv").show();
			$("#checkResult").html("<font color='red' size='2'><b>"+finalResult+"</b></font>");
			$("#createNewTomcat").addClass("btn-danger");
			$("#createNewTomcat").removeClass("btn-default");
			$("#createNewTomcat").removeClass("btn-warning");
			$("#createNewTomcat").removeClass("btn-success");
			$("#createNewTomcat").html("校验失败");
		}else{
			$("#checkResultDiv").hide();
			$("#createNewTomcat").addClass("btn-warning");
			$("#createNewTomcat").removeClass("btn-danger");
			$("#createNewTomcat").removeClass("btn-default");
			$("#createNewTomcat").removeClass("btn-success");
			$("#createNewTomcat").html("创建中");
			//验证成功提交
			$.ajax({
				type:"POST",
				async: false,
				url:"/createNewTomcatAjax",
				data:$("#createNewTomcatForm").serialize(),
				success:function(data){
					var datas = data.split("|");
					var result = "";
					for(var i = 0;i < datas.length;i ++){
						var allSuccess = true;
						if(datas[i].indexOf("成功")!=-1){
							result += "<font color = 'green'>" + datas[i] + "</font><br/>";
						}else if(datas[i].indexOf("失败")!=-1){
							result += "<font color = 'red'><b>" + datas[i] + "</b></font><br/>";
							allSuccess = false;
						}else{
							result += datas[i] + "<br/>";
						}
					}
					if(allSuccess){
						$("#createNewTomcat").addClass("btn-success");
						$("#createNewTomcat").removeClass("btn-danger");
						$("#createNewTomcat").removeClass("btn-default");
						$("#createNewTomcat").removeClass("btn-warning");
						$("#createNewTomcat").html("创建成功");
						result += "<font color = 'green'>tomcat全部配置成功，请刷新页面后查看tomcat状态</font>";
					}else{
						$("#createNewTomcat").addClass("btn-danger");
						$("#createNewTomcat").removeClass("btn-success");
						$("#createNewTomcat").removeClass("btn-default");
						$("#createNewTomcat").removeClass("btn-warning");
						$("#createNewTomcat").html("创建失败");
						result += "<font color = 'red'><b>tomcat配置失败，请刷新页面后重试</b></font>";
					}
					$("#checkResult").html(result);
					$("#checkResultDiv").show();
				}
			});
		}
	}
});
function initCreateTomcatDialog(){
	$("#tomcat6").click();
	$("#tomcatName").val("");
	$("#shutdownPort").val($("#shutdownPort").attr("defaultValue"));
	$("#httpPort").val($("#httpPort").attr("defaultValue"));
	$("#ajpPort").val($("#ajpPort").attr("defaultValue"));
	$("#redirectPort").val($("#redirectPort").attr("defaultValue"));
	$("#context").val($("#context").attr("defaultValue"));
	if($("#domainConfigBtn").attr("class").indexOf("active")!=-1){
		$("#domainConfigBtn").click();
	}
	$("#domain").val("");
	$(".stateImg").hide();
	$("#checkResult").html("");
	$("#checkResultDiv").hide();
	$("#createNewTomcat").addClass("btn-primary");
	$("#createNewTomcat").removeClass("btn-danger");
	$("#createNewTomcat").removeClass("btn-default");
	$("#createNewTomcat").html("创建");
	$("#startupAfterSuccess").attr("checked",false);
}
$("#addTomcatModal").on('hidden.bs.modal', function () {
	initCreateTomcatDialog();
});
initCreateTomcatDialog();
$("#openTomcatsBtn").click(function(){
	$.ajax({
		type:"POST",
		url:"openTomcatsPathAjax",
		success:function(data){
		}
	});
});
//批量管理tomcat切换开关
$("#multiTomcatConfigBtn").click(function(){
	if($(this).attr("class").indexOf("btn-info")!=-1){
		$(this).removeClass("btn-info");
		$(this).addClass("btn-default");
		$(this).removeClass("active");
		$(".multiTomcatConfigBtns").hide();
		$(".multiTd").hide();
		colspan = 7;
		//初始化对号
		$("#tomcatPortsTable tr").each(function(){;
			var tr = $(this);
			var td = tr.find(".multiTd");
			if(td.html() != undefined && td.text()!="批量" && td.html()!=""){
				td.html("");
			}
		});
	}else{
		$(this).removeClass("btn-default");
		$(this).addClass("btn-info");
		$(this).addClass("active");
		$(".multiTomcatConfigBtns").show();
		$(".multiTd").show();
		colspan = 8;
	}
	$("#tomcatPortsTable td[colspan]").each(function(){
		$(this).attr("colspan",colspan);
	});
});
$(".multiTd").click(function(){
	if($(this).html()==""){
		$(this).html("<span class=\"glyphicon glyphicon-ok\" aria-hidden=\"true\"></span>");
	}else{
		$(this).html("");
	}
});
$("#multiTomcatStartBtn").click(function(){
	$("#multiTomcatStartBtn").html("<span class=\"glyphicon glyphicon-hourglass\" aria-hidden=\"true\">");
	$("#multiTomcatStartBtn").removeClass("btn-info");
	$("#multiTomcatStartBtn").addClass("btn-warning");
	var count = 0;
	$("#tomcatPortsTable tr").each(function(){;
		var tr = $(this);
		var td = tr.find(".multiTd");
		if(td.html() != undefined && td.text()!="批量" && td.html()!=""){
			tr.find(".tomcatBtn").click();
			count ++;
		}
	});
	$("#msgBox").removeClass("alert-success");
	$("#msgBox").removeClass("alert-danger");
	$("#msgBox").removeClass("alert-warning");
	if(count == 0){
		$("#msgBox").addClass("alert-warning");
		$("#msgBox").html("没有选中tomcat需要启动");
	}else{
		$("#msgBox").addClass("alert-success");
		$("#msgBox").html("启动了"+count+"台tomcat");
	}
	$("#multiTomcatStartBtn").html("<span class=\"glyphicon glyphicon-play-circle\" aria-hidden=\"true\">");
	$("#multiTomcatStartBtn").removeClass("btn-warning");
	$("#multiTomcatStartBtn").addClass("btn-info");
	$("#msgBox").slideDown(200);
	hideMsgBoxDH();
});
$("#multiTomcatCloseBtn").click(function(){
	$("#multiTomcatCloseBtn").html("<span class=\"glyphicon glyphicon-hourglass\" aria-hidden=\"true\">");
	$("#multiTomcatCloseBtn").removeClass("btn-info");
	$("#multiTomcatCloseBtn").addClass("btn-warning");
	var count = 0;
	$("#tomcatPortsTable tr").each(function(){;
		var tr = $(this);
		var td = tr.find(".multiTd");
		if(td.html() != undefined && td.text()!="批量" && td.html()!=""){
			tr.find(".closeBtn").click();
			count ++;
		}
	});
	$("#msgBox").removeClass("alert-success");
	$("#msgBox").removeClass("alert-danger");
	$("#msgBox").removeClass("alert-warning");
	if(count == 0){
		$("#msgBox").addClass("alert-warning");
		$("#msgBox").html("没有选中tomcat需要关闭");
	}else{
		$("#msgBox").addClass("alert-success");
		$("#msgBox").html("关闭了"+count+"台tomcat");
	}
	$("#multiTomcatCloseBtn").html("<span class=\"glyphicon glyphicon-off\" aria-hidden=\"true\">");
	$("#multiTomcatCloseBtn").removeClass("btn-warning");
	$("#multiTomcatCloseBtn").addClass("btn-info");
	$("#msgBox").slideDown(200);
	hideMsgBoxDH();
});
$("#multiTomcatRestartBtn").click(function(){
	$("#multiTomcatRestartBtn").html("<span class=\"glyphicon glyphicon-hourglass\" aria-hidden=\"true\">");
	$("#multiTomcatRestartBtn").removeClass("btn-info");
	$("#multiTomcatRestartBtn").addClass("btn-warning");
	var count = 0;
	$("#tomcatPortsTable tr").each(function(){
		var tr = $(this);
		var td = tr.find(".multiTd");
		if(td.html() != undefined && td.text()!="批量" && td.html()!=""){
			tr.find(".restartBtn").click();
			count ++;
		}
	});
	$("#msgBox").removeClass("alert-success");
	$("#msgBox").removeClass("alert-danger");
	$("#msgBox").removeClass("alert-warning");
	if(count == 0){
		$("#msgBox").addClass("alert-warning");
		$("#msgBox").html("没有选中tomcat需要重启");
	}else{
		$("#msgBox").addClass("alert-success");
		$("#msgBox").html("重启了"+count+"台tomcat");
	}
	$("#multiTomcatRestartBtn").html("<span class=\"glyphicon glyphicon-repeat\" aria-hidden=\"true\">");
	$("#multiTomcatRestartBtn").removeClass("btn-warning");
	$("#multiTomcatRestartBtn").addClass("btn-info");
	$("#msgBox").slideDown(200);
	hideMsgBoxDH();
});
$("body a").focus(function(){
	$(this).blur();
});
//右击打开连接
$(".serverNameSpan").mousedown(function(e){
	if(2 == e.button){
		var url = "http://localhost:";
		var port = $(this).parent().next().next().html();
		window.open(url + port);
	}
});
function openContextDir(dir){
	$.ajax({
		type:"POST",
		url:"/openDirAjax",
		data:"dir="+dir,
		success:function(data){}
	});
}
//系统配置
$("#sysConfigBtn").click(function(){
	$("#sysConfigModal").modal("show");
});
initSysConfig();
function initSysConfig(){
	$("#sysConfigFrom input[name='sortType'][value='"+$("#sysDefaultConfig input[name='sortType']").val()+"']").click();
	if($("#sysDefaultConfig input[name='letterSortType']").val() == "AZ"){
		$("#AtoZBtn").addClass("active");
		$("#ZtoABtn").removeClass("active");
		$("#sysConfigFrom input[name='letterSortType']").val("AZ");
	}else{
		$("#ZtoABtn").addClass("active");
		$("#AtoZBtn").removeClass("active");
		$("#sysConfigFrom input[name='letterSortType']").val("ZA");
	}
	if($("#sysDefaultConfig input[name='sortType']").val() == "letter"){
		$("#letterSortTr").show();
	}else{
		$("#letterSortTr").hide();
	}
	$("#sysConfigFrom input[name='fileType'][value='"+$("#sysDefaultConfig input[name='fileType']").val()+"']").click();
	$("#sysConfigFrom input[name='password']").val($("#sysDefaultConfig input[name='password']").val());
	$("#sysConfigResult").hide();
	$("#sysConfigResult td").html("");
}
$("#AtoZBtn").click(function(){
	$(this).addClass("active");
	$("#ZtoABtn").removeClass("active");
	$("#sysConfigFrom input[name='letterSortType']").val("AZ");
});
$("#ZtoABtn").click(function(){
	$(this).addClass("active");
	$("#AtoZBtn").removeClass("active");
	$("#sysConfigFrom input[name='letterSortType']").val("ZA");
});
$("input[name='sortType']").change(function(){
	if($("input[name='sortType']:checked").val() == "letter"){
		$("#letterSortTr").show();
	}else{
		$("#letterSortTr").hide();
	}
});

//关闭弹窗的事件
$("#sysConfigModal").on('hidden.bs.modal', function () {
	initSysConfig();
});
//保存
$("#saveSysConfigBtn").click(function(){
	$.ajax({
		type:"POST",
		url:"/saveSysConfigAjax",
		data:$("#sysConfigFrom").serialize(),
		success:function(data){
			$("#sysConfigResult").show();
			$("#sysConfigResult td").html(data+"&nbsp;，刷新页面生效！");
		}
	});
});
//apache管理的弹窗
$("#apacheConfigBtn").click(function(){
	$("#apacheConfigModal").modal("show");
});
//右键（双指点按）apache配置按钮的事件
$("#apacheConfigBtn").mousedown(function(e){
	var apacheConfigBtn = $("#apacheConfigBtn");
	if(2 == e.button){
		$.ajax({
			type:"POST",
			url:"/changeApacheStateAjax",
			//添加beforeSend
			beforeSend:function(){
				apacheConfigBtn.html("<span class=\"glyphicon glyphicon-hourglass\" aria-hidden=\"true\"></span>");
				apacheConfigBtn.removeClass("btn-default");
				apacheConfigBtn.addClass("btn-warning");
			},
			success:function(data){
				apacheConfigBtn.removeClass("btn-warning");
				apacheConfigBtn.addClass("btn-default");
				operateApacheStateData(data);
			}
		});
	}
	return false;
});
//启动，关闭，重启的事件
$("#startApache,#stopApache,#restartApache").click(function(){
	var btn = $(this);
	$.ajax({
		type:"POST",
		url:"/modifyApacheStateAjax",
		data:"state="+$(this).attr("state"),
		beforeSend:function(){
			btn.removeClass("btn-default");
			btn.addClass("btn-warning");
		},
		success:function(data){
			operateApacheStateData(data);
			btn.addClass("btn-default");
			btn.removeClass("btn-warning");
		}
	});
});

$(".topOpenFile").click(function(){
	$.ajax({
		type:"POST",
		url:"/topOpenFileAjax",
		data:"filePath="+$(this).attr("data"),
		success:function(data){}
	});
});

$(".apacheIcon").mousedown(function(e){
	//点击
	if(0 == e.button){
		$.ajax({
			type:"POST",
			url:"/topOpenFileAjax",
			data:"filePath="+$(this).attr("data"),
			success:function(data){}
		});
	}
	//右击
	if(2 == e.button){
		$.ajax({
			type:"POST",
			url:"/toOpenDomainAjax",
			data:"filePath="+$(this).attr("data"),
			success:function(data){
				if("" != data){
					window.open("http://"+data);
				}
			}
		});
	}
	return false;
});
$("#howToUseBtn").click(function(){
	$("#howToUseModal").modal("show");
});
$("#hostsEnv").click(function(){
	$.ajax({
		type:"POST",
		url:"/getHostsEnvAjax",
		success:function(data){
			if(data.indexOf("本地")!=-1){
				$("#hostsEnv").attr("color","green");
			}else{
				$("#hostsEnv").attr("color","red");
			}
			$("#hostsEnv").html(data);
		}
	});
});
//tomcat名称双击事件
$(".serverNameSpan").dblclick(function(){
	var filePath = $(this).prev().attr("data");
	if(filePath != undefined){
		$.ajax({
			type:"POST",
			url:"/pingDomainAjax",
			data:"filePath="+filePath,
			success:function(data){
				if(data.indexOf("失败")!=-1){
					$("#msgBox").addClass("alert-danger");
					$("#msgBox").html(data);
					$("#msgBox").slideDown(200);
					hideMsgBoxDH();
				}else{
					if(data.indexOf("127.0.0.1")!=-1){
						$("#msgBox").addClass("alert-success");
					}else{
						$("#msgBox").addClass("alert-warning");
					}
					$("#msgBox").html(data);
					$("#msgBox").slideDown(200);
					hideMsgBoxDH();
				}
			}
		});
	}
});