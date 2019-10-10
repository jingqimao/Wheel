<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>测试</title>
<script type="text/javascript" src="/static/js/jquery-3.3.1.min.js"></script>
<script type="text/javascript" src="/static/test/wheel.js"></script>
</head>
<body>
	<div :set="list">{{list.length}}</div>
	<button type="button" @click="append">click me!</button>
	<div :set="list" :for="list">
		<span :set="item">{{n+1}}.{{item}}</span><button type="button" @click="insert">插入</button><button type="button" @click="info">信息</button><button type="button" @click="del">删除</button><br>
	</div>
</body>
<script>

$$({
	data:{
		list:["苹果","香蕉","雪梨"]
	},
	init:function(){
		
	},
	ready:function(){
		
	},
	event:{
		append:function(){
			wd.list.push("西瓜");
		},
		info:function(e,node){
			console.log(node)
		},
		del:function(e,node){
			wd.list.splice(node.data.n,1);
		},
		insert:function(e,node){
			node.data.item+="+";
		}
	}
});

</script>
</html>