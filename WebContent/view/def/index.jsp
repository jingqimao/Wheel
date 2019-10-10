<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>首页</title>
<script type="text/javascript" src="/static/js/jquery-3.3.1.min.js"></script>
<script type="text/javascript" src="/static/js/wheel.min.js"></script>
</head>
<body>
	欢迎访问！
	<tmp name="tmp1">
		<h1>{{tmp}}</h1>
	</tmp>
	<div wh-bind="user">account:{{user.account}}</div>
	<div wh-bind="user">name:{{user.name}}</div>
	<button wh-e:click="upd">随机更新</button>
	
	<div>标准化接口测试:{{api_data}}</div>
	
	<div>
		<a href="#!/tmp1">页面一</a>
		<a href="#!/tmp2">页面二</a>
		<a href="#!/tmp3">页面三</a>
		<a href="#!/tmp4">页面四</a>
	</div>
	<div wh-route>{{tmp}}</div>
	<div wh-route="/tmp1,/tmp2"></div>
	<div wh-route="/tmp3,/tmp4">{{tmp}}</div>
	
	<div>多语言接口测试:{{lang_data.name}}</div>
	
	<a href="/jump">跳转</a>
	
	<button type="button" wh-e:click="pa">添加</button>
	<div wh-for="list" wh-bind="list">
		<div>{{n+1}}.{{item}},{{we.hello(_parent.user.account)}}<button type="button" wh-e:click="pad">添加</button><button type="button" wh-e:click="pp">刷新</button><button type="button" wh-e:click="del">删除</button></div>
	</div>
	
	<div>翻译：测试，测试人</div>
	
	<div wh-for="list2" wh-bind="list2_show" wh-bind2="list3_show">
		{{item.no}}.<a wh-e:init="init">{{item.name}}</a>
	</div>
	<button type="button" wh-e:click="l2p">刷新</button>
	
	<input wh-listen="text-text_exc"/>
</body>
<script>

wheel({
	data:{
		user:null,
		api_data:null,
		tmp:null,
		lang_data:null,
		list:["苹果","雪梨","香蕉"],
		list2:[],
		list2_show:false,
		list3_show:false,
		
		text:""
	},
	onLoad:function(){  
		wd.user=wt.get("/user/getxx",{id:8});
		
		var res=wt.get("/user/division",{a:9,b:4});
		wd.api_data=res.data;
		
		var res=wt.get("/user/get_tt"); console.log(res) 
		wd.lang_data=res;
		
		wd.list2.push({
			no:1,
			name:"Tom"
		});
	},
	onReady:function(){
		
		//console.log(wt.get("/user/test"));
	},
	event:{
		upd:function(e,node){
			wt.get("/user/setxx",{id:8,name:Math.random().toString(36).substr(2)});
			wd.user=wt.get("/user/getxx",{id:8});
		},
		hello:function(name){
			return this.hello2(name);
		},
		hello2:function(name){
			return "Hello "+name+" !";
		},
		pa:function(e,node){
			wd.list.push("西瓜");
		},
		pad:function(e,node){
			wd.list[node.data.n]+="+"; 
			//console.log(wd.list) 
		},
		pp:function(e,node){
			wd.list[node.data.n]=wd.list[node.data.n];
		},
		del:function(e,node){
			wd.list.splice(node.data.n,1);console.log(wd.list)
		},
		init:function(e,node){
			//console.log(node.data)
		},
		l2p:function(e,node){
			wd.list2.push({
				no:2,
				name:"jerry"
			});
			wd.list3_show=true;
		},
		
		text_exc:function(){
			return {
				get:function(v){
					return v;console.log(v)
				},
				set:function(v){
					return v+"xx";
				}
			}
		}
	},
	app:[
		{
			url:"/tmp1",
			tmp:"tmp1",
			controller:function(){
				wd.tmp="tmp1";
			}
		},
		{
			url:"/tmp2",
			tmp:"tmp1",
			controller:function(node){
				wd.tmp="tmp2";
			}
		},
		{
			url:"/tmp3",
			controller:function(node){
				wd.tmp="tmp3";
			}
		},
		{
			url:"/tmp4",
			controller:function(node){
				wd.tmp="tmp4";
			}
		}
	]
	
});
</script>
</html>