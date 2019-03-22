# Wheel
A Personal Developed Web Application Framework


这是一个独立开发的网络应用框架，已实现设计的大部分功能，暂时只上传了前端框架。

wheel.js

介绍：这是一个基于jQuery的前端MVC框架，简洁高效功能强大（我觉得），吸收了各种流行框架的优点（捂脸）

功能特点：
全节点扫描实现MVC模式，每个节点都可以独立渲染，
节点控制标签，无需脚本即可做出多种多样的显示效果
双向绑定修改数据模型，支持定义自定义控件
模块化引用外部资源（JS+CSS），快速封装自定义模块
路由功能，配合节点独立渲染单页应用
还提供许多基础小工具，方便日常开发

快速使用方法：

```
<div>{{msg}}</div>

wheel({
	mode_src:"/mode/",//模块文件夹路径
	mode:["format"],//引入模块
	data:{
		msg:"hello world"//数据存放
	},
	onLoad:function(){  
		//初始化前
	},
	onReady:function(){  
		//初始化完成
	},
	event:{//事件存放
    		click:function(e,node){
      			
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





