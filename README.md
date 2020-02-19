# Wheel
A Personal Developed Web Application Framework


这是一个独立开发的网络应用框架，已实现设计的大部分功能，包括前端和后端。

## Wheel.jar 

介绍：一个简便的MVC网络应用框架，包含核心部件和辅助开发工具。  
特点：  
1.分层明确  
2.支持服务缓存  
3.支持映射配置和权限配置  
4.支持多语言  
5.小工具丰富  
6.支持对接辅助工具PropellerSup进行可视化开发  

## wheel.js

介绍：这是一个基于jQuery的前端MVC框架，简洁高效功能强大（我觉得），吸收了各种流行框架的优点（捂脸）

## 功能特点：
扫描替换节点树，每个节点都可以独立渲染（包括属性、内容、子节点、事件、循环）  
节点控制标签，无需脚本即可做出多种多样的显示效果  
双向绑定修改数据模型，支持定义自定义控件  
模块化引用外部资源（JS+CSS），快速封装自定义模块  
路由功能，配合节点独立渲染单页应用  
还提供许多基础小工具，方便日常开发  

2020/2/19主要修改：  
1.增加对列表渲染的监听（push\splice）  
2.自动移除无用节点的绑定事件，解决了重复绑定的问题  
3.渲染列表的子节点获取parent改为_parent  
4.增加页面渲染动画  
5.增加一些小工具  

## 快速入门：

```
<div wh-e:click="say">{{msg}}</div>
<div wh-for="list:fruit:n">
	{{n}}.{{fruit}}
</div>

wheel({
	mode_src:"/mode/",//模块文件夹路径
	mode:["format"],//引入模块
	data:{
		msg:"hello world",//数据存放
		list:["apple","banana","pear"]
	},
	onLoad:function(){  
		//初始化前
	},
	onReady:function(){  
		//初始化完成
	},
	event:{//事件存放
    		say:function(e,node){
      			alert(node.data.msg);
    		}
	},
	app:[//路由存放
    		{
			url:"/tab1",//路径
			controller:function(){//触发函数
        
		}
 	]
	
});
```

还有很多功能，自己摸索吧~（我懒,sorry）


