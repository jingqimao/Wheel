/**
 * wheel前端MVC工具
 * 
 * wheel初始化
 * 0.获取节点
 * 获取view并保存节点和内容
 * 1.渲染节点
 * 遍历节点内容和数据绑定
 * 2.逻辑控制和监听
 * 遍历节点进行控制和监听
 * 3.模块插入
 * 动态引入自定义模块
 * 4.基础工具
 * 引入各种简化小工具
 * 5.基础模块
 * 引入万能小模块
 * ----1.请求中提示 2.弹窗 3.简单动画和动画监听 4.表单验证 5.统一规范模块开发js+css+img
 */

//小工具
var WheelTool={
	//请求服务
	get:function(url,data){
		//直接请求URL
		if(typeof url == "string"){
			if(data==undefined)data={};
			var res=$.ajax({url:url,data:data,async:false,type:"POST"}).responseText;
			if(res!=undefined){
				res=res.replace(/\\/g,"\\\\"); 
				if(WheelTool.isJSON(res)){
					return WheelTool.toJSON(res); 
				}else{
					return res; 
				}
			}else{
				console.error(url+" 请求出错！返回结果：undefined");   
			}
		//参数请求
		}else if(typeof obj == 'object' && obj ){
			if(url.url!=undefined&&url.url!=null){
				var data=url.data||{};
				if(url.success!=undefined&&typeof url.success == "function"){
					$.ajax(url);
				}else{
					var res=$.ajax({url:url.url,data:data,async:false,type:"POST"}).responseText;
					if(res!=undefined){
						res=res.replace(/\\/g,"\\\\"); 
						if(WheelTool.isJSON(res)){
							return toJSON(res);   
						}else{
							return res; 
						}
					}else{
						console.error(url+" 请求出错！返回结果：undefined");   
					}
				}
			}
		}
	},
	//判断字符串是否JSON格式
	isJSON:function(str) {
	    if (typeof str == 'string') {
	        try {
	            var obj=JSON.parse(str);
	            if(typeof obj == 'object' && obj ){
	                return true;
	            }else{
	                return false;
	            }

	        } catch(e) {
	            return false;
	        }
	    }
	},
	//字符串转JSON对象
	toJSON:function(str){
		if(str!=undefined&&str!=null&&WheelTool.isJSON(str)){
			return JSON.parse(str);
		}else{
			console.error("转换失败！非JSON字符串:"+str);
			return;
		}
	},
	//字符串转JSON对象
	toSTR:function(json){
		if(json!=undefined&&json!=null){
			return JSON.stringify(json);
		}else{
			console.error("转换失败！非JSON对象:"+json);
			return;
		}
	},
		
	//遍历数组根据属性值获取对象
	getItemByArr:function(k,v,arr){
		for(var i in arr){
			if(arr[i][k]==v)return arr[i]; 
		}
		return null;
	},
	//根据字符串获取时间
	getTime:function(str){
		str=str.replace(/-/g,"/");
		return new Date(str).getTime();
	},
	//获取当前时间字符串
	now:function(){
		var date = new Date();
	    var seperator1 = "-";
	    var seperator2 = ":";
	    var month = date.getMonth() + 1;
	    var strDate = date.getDate();
	    if (month >= 1 && month <= 9) {
	        month = "0" + month;
	    }
	    if (strDate >= 0 && strDate <= 9) {
	        strDate = "0" + strDate;
	    }
	    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
	            + " " + date.getHours() + seperator2 + date.getMinutes()
	            + seperator2 + date.getSeconds();
	    return currentdate;
	},
	//获取URL参数合集
	getUrlArg:function(){ 
		var para=[];
		var url = document.location.toString();
		if(url.indexOf("?")>0){
			var ps = url.split("?")[1].split("&");
			for(var i in ps){
				var pss=ps[i].split("="); 
				para[decodeURI(pss[0])]=decodeURI(pss[1]);    
			}
		}
		return para; 
	},
	//传参跳转页面
	jump:function(url,para){ 
		var has=false;
		for(var i in para){
			if(!has){
				has=true;
				url+="?";
			}
			url+=i+"="+para[i]+"&";
		}
		url=url.substring(0,url.length-1);   
		window.location.href=url; 
	},
	//根据正则表达式获取URL参数
	getUrlItem:function(reg){
		var url = document.location.toString(); 
		pattern =new RegExp(reg,"i");  
		var res=url.match(pattern)[1];
		return res;
	},
	//深度克隆对象
	clone:function(obj){ 
		var str, newobj = obj.constructor === Array ? [] : {};
	    if(typeof obj !== 'object'){
	        return;
	    } else if(window.JSON){
	        str = JSON.stringify(obj);
	        newobj = JSON.parse(str);
	    } else {
	        for(var i in obj){
	            newobj[i] = typeof obj[i] === 'object' ? cloneObj(obj[i]) : obj[i]; 
	        }
	    }
	    return newobj;
	}
};

//模块化工具
var WheelMode={
		src:"mode/",
		mode:[],
		css:[],
		init:function(src){ 
			WheelMode.src=src;
		},
		load:function(modeName,call){ 
			//modeName=modeName.split(","); 
			var mload={n:0,m:0,mm:[]};
			for(var i in modeName){
				if(WheelMode.mode[modeName[i]]==undefined){
					var script=document.createElement("script"); 
					script.type="text/javascript";
					script.src=WheelMode.src+modeName[i]+".js";
					document.getElementsByTagName('head')[0].appendChild(script);
					
					mload.n++; 
					mload.mm.push(modeName[i]);
					script.onload=script.onreadystatechange=function(){
						mload.m++;
						if(mload.n==mload.m){
							for(var j in mload.mm){  
								if(WheelMode.css[mload.mm[j]]==undefined&&WheelMode.mode[mload.mm[j]].css!=undefined&&WheelMode.mode[mload.mm[j]].css==true){
									var link = document.createElement('link');
							        link.type='text/css';
							        link.rel = 'stylesheet';
							        link.href = WheelMode.src+modeName[j]+".css";
							        document.getElementsByTagName('head')[0].appendChild(link); 
							        WheelMode.css[mload.mm[j]]=mload.mm[j];
								}
								window[mload.mm[j]]=WheelMode.mode[mload.mm[j]];
							}
							if(call!=undefined)call();
						}
					};
				}
			}
		},
		initload:function(src,modeName,call){  
			WheelMode.init(src);
			WheelMode.load(modeName,call);
		}
};


//控制器
function wheel(args){
	//加载数据
	if(args.data==undefined)args.data={};
	//小工具
	//args.tool=WheelTool;
	
	//是否显示wheel控制标签
	if(args.showTag==undefined)args.showTag=false;
	
	//动态执行语句并返回结果
	args.runjs=function(jsstr,data){
		var head="";
		if(data==undefined)data=null;
		var item={
			data:data,
			res:null
		}; 
		for(var i in data){
			if(i.indexOf(".")<0)head+="var "+i+"=item_.data."+i+";";
		}
		jsstr=head+"(typeof("+jsstr+")!=\"undefined\")?item_.res="+jsstr+":null;";
		function callAnotherFunc(fnFunction, vArgument) { 
			fnFunction(vArgument);
		}
		callAnotherFunc(new Function("item_", jsstr), item); 
		return item.res; 
	};
	
	//对象属性修改事件绑定
	args.setObj=function(obj,key,fun){
		var bValue=null;
		//孙节点
		if(key.indexOf(".")>0){
			var ss=key.split(".");
			key=ss[ss.length-1];
			bValue=obj[ss[0]];
			obj=bValue;
			for(var i=1;i<ss.length;i++){
				bValue=bValue[ss[i]];
				if(i<ss.length-1){
					obj=bValue;
				}
			}
		}else{//子节点
			bValue=obj[key];
		}
		try {
			var oj=Object.defineProperty(obj, key, {
		        get:function() {
		        	return bValue;
		        },
		        set:function(newValue) { 
		        	bValue=newValue; 
		        	fun(bValue);
		        },
		        enumerable: true,
		        configurable: true
			});
	    } catch (error) {
	    	console.log("browser not supported.");
	    }
	};
	
	//所有视图节点
	args.node=[]; 
	//视图模板
	args.tmp=[];
	
	//节点渲染
	args.render=function(node,data){  
		
		var go=true;
		var regex1 = /\{\{(.+?)\}\}/g; 
		var regex2 = /wh-e-(.+?)/g;
		
		for(var i in node.attr){
			var name=node.attr[i].name;
			var val=node.attr[i].val;  
			
			//标签控制wh-if
			if(name=="wh-if"){
				if(!args.showTag)$(node.item).removeAttr(name);
				if(args.runjs(val,data)){     
					$(node.item).show();
				}else{
					$(node.item).hide();
					return;
				}
			}
			
			//标签控制wh-for
			if(name=="wh-for"){
				if(!args.showTag)$(node.item).removeAttr(name); 
				var idx="index"; 
				var itx="item";
				for(var j in node.attr){ 
					if(node.attr[j].name=="wh-for-index"){
						if(!args.showTag)$(node.item).removeAttr(node.attr[j].name);
						idx=node.attr[j].val;
					}
				}
				for(var j in node.attr){
					if(node.attr[j].name=="wh-for-item"){
						if(!args.showTag)$(node.item).removeAttr(node.attr[j].name); 
						itx=node.attr[j].val;
					}
				}
				$(node.item).html(""); 
				var arr=args.runjs(val,args.data); 
				for(var j in arr){
					var item=$(node.html);
					var dd={};
					dd[idx]=j;
					dd[itx]=arr[j]; 
					for(var k in data){ 
						dd[k]=data[k];
					}
					if(item.length>1){  
						item.each(function(ii,itemx){
							$(node.item).append(itemx); 
							args.getNode(itemx,dd);    
						});
					}else{
						$(node.item).append(item); 
						args.getNode(item,dd); 
						/*if(item.find("*").length>0){     
							
						}else{
							args.getNode(item,dd);
						}*/
					}
					
					
				}
				go=false;
			}
			
			//标签控制wh-tmp
			if(name=="wh-tmp"){
				if(!args.showTag)$(node.item).removeAttr(name); 
				var dd={};
				for(var j in data){ 
					dd[j]=data[j];
				}
				if($(node.item).attr("wh-data")!=undefined){
					var ddx=args.runjs($(node.item).attr("wh-data"),args.data);
					if(!args.showTag)$(node.item).removeAttr("wh-data");
					for(var j in ddx){   
						dd[j]=ddx[j];
					}
					 
				}
				if(val.indexOf("'")>-1)val=args.runjs(val,dd);  
				var tmp=wt.getItemByArr("name",val,args.tmp);
				if(tmp!=null){   
					$(node.item).html(tmp.html);
					args.getNodes(node.item,dd);  
				}
			}
			
			//输入监听绑定变量
			if(name=="wh-listen"){
				if(node.item.tagName=="INPUT"){
					if((node.item.type=="text"||node.item.type=="password")&&data[val]!=undefined){
						$(node.item).val(data[val]); 
						$(node.item).keyup(function(e){ 
							data[val]=this.value;
						});
					}
				}
			}
			
			//动作事件监听
			var m=name.match(regex2);
			if(m!=null){
				if(!args.showTag)$(node.item).removeAttr(name);
				if(name=="wh-e-click"&&args.event[val]!=undefined&&typeof args.event[val] === "function"){
					$(node.item).click(args.event[val]);
				}
				if(name=="wh-e-dblclick"&&args.event[val]!=undefined&&typeof args.event[val] === "function"){
					$(node.item).dblclick(args.event[val]);
				}
				if(name=="wh-e-focus"&&args.event[val]!=undefined&&typeof args.event[val] === "function"){
					$(node.item).focus(args.event[val]);
				}
				if(name=="wh-e-blur"&&args.event[val]!=undefined&&typeof args.event[val] === "function"){
					$(node.item).blur(args.event[val]);
				}
				if(name=="wh-e-change"&&args.event[val]!=undefined&&typeof args.event[val] === "function"){
					$(node.item).change(args.event[val]);
				}
				if(name=="wh-e-keydown"&&args.event[val]!=undefined&&typeof args.event[val] === "function"){
					$(node.item).keydown(args.event[val]);
				}
				if(name=="wh-e-keypress"&&args.event[val]!=undefined&&typeof args.event[val] === "function"){
					$(node.item).keypress(args.event[val]);
				}
				if(name=="wh-e-keyup"&&args.event[val]!=undefined&&typeof args.event[val] === "function"){
					$(node.item).keyup(args.event[val]);
				}
				if(name=="wh-e-mousedown"&&args.event[val]!=undefined&&typeof args.event[val] === "function"){
					$(node.item).mousedown(args.event[val]);
				}
				if(name=="wh-e-mouseup"&&args.event[val]!=undefined&&typeof args.event[val] === "function"){
					$(node.item).mouseup(args.event[val]);
				}
				if(name=="wh-e-mousemove"&&args.event[val]!=undefined&&typeof args.event[val] === "function"){
					$(node.item).mousemove(args.event[val]);
				}
				if(name=="wh-e-mouseover"&&args.event[val]!=undefined&&typeof args.event[val] === "function"){
					$(node.item).mouseover(args.event[val]);
				}
				if(name=="wh-e-ready"&&args.event[val]!=undefined&&typeof args.event[val] === "function"){
					$(node.item).ready(args.event[val]);
				}
				if(name=="wh-e-resize"&&args.event[val]!=undefined&&typeof args.event[val] === "function"){
					$(node.item).resize(args.event[val]);
				}
				if(name=="wh-e-scroll"&&args.event[val]!=undefined&&typeof args.event[val] === "function"){
					$(node.item).scroll(args.event[val]);
				}
				if(name=="wh-e-submit"&&args.event[val]!=undefined&&typeof args.event[val] === "function"){
					$(node.item).submit(args.event[val]);
				}
			}
			
			//数据变动绑定更新节点
			if(name=="wh-set"){
				if(!args.showTag)$(node.item).removeAttr(name);
				if(val!=undefined&&val!=""){
					args.setObj(data,val,function(v){  
						node.update();
					});
				}
			}
			
			//节点ID绑定
			if(name=="wh-id"){
				if(!args.showTag)$(node.item).removeAttr(name);
				if(val!=undefined&&val!=""){
					node.id=val;
				}
			}
			
			//src替换消除加载前失败
			if(name=="wh-src"){
				if(!args.showTag)$(node.item).removeAttr(name);
				if(val!=undefined&&val!=""){
					var m=val.match(regex1);  
					if(m!=null){  
						for(var j in m){
							var s=m[j].replace(/\{\{/g,'').replace(/\}\}/g,'');   
							var reg=m[j].replace(/\[/g,'\\\[').replace(/\]/g,'\\\]').replace(/\*/g,'\\\*').replace(/\+/g,'\\\+').replace(/\=/g,'\\\=').replace(/\?/g,'\\\?').replace(/\'/g,'\\\'');   
							val=val.replace(new RegExp(reg,"g"),args.runjs(s,data)); 
						} 
						node.item.src=val; 
					}
				}
			}
			
			//元素属性数据插入和运算
			if(typeof(val)=="string"){ 
				var m=val.match(regex1);  
				if(m!=null){  
					for(var j in m){
						var s=m[j].replace(/\{\{/g,'').replace(/\}\}/g,'');   
						var reg=m[j].replace(/\[/g,'\\\[').replace(/\]/g,'\\\]').replace(/\*/g,'\\\*').replace(/\+/g,'\\\+').replace(/\=/g,'\\\=').replace(/\?/g,'\\\?').replace(/\'/g,'\\\'');   
						val=val.replace(new RegExp(reg,"g"),args.runjs(s,data)); 
					} 
					$(node.item).attr(node.attr[i].name,val);  
				}
	    	}
		}
		
		
		//元素内容数据插入和运算
		if(!node.hasChild){ 
			if(node.html==undefined)return; 
			var m=node.html.match(regex1);   
			if(m!=null){ 
				var html=node.html;
				for(var j in m){
					var s=m[j].replace(/\{\{/g,'').replace(/\}\}/g,''); 
					var reg=m[j].replace(/\[/g,'\\\[').replace(/\]/g,'\\\]').replace(/\*/g,'\\\*').replace(/\+/g,'\\\+').replace(/\=/g,'\\\=').replace(/\?/g,'\\\?').replace(/\'/g,'\\\''); 
					html=html.replace(new RegExp(reg,"g"),args.runjs(s,data));
				}
				$(node.item).html(html);   
			}
		}else{ 
			if(go)args.getNodes(node.item,data);    
		}
	};
	//添加节点
	args.getNode=function(item,data){
		if(item instanceof jQuery)item=item[0];
		function hasTMP(obj){ 
			while (obj != undefined && obj != null && obj.tagName.toUpperCase() != 'BODY'){
				if (obj.tagName.toUpperCase() == 'TMP'){
					return true;
				}
				obj = obj.parentNode;
			}
			return false;
		}
		//遍历所有节点，除去脚本和模板 
		if(item.tagName=="TMP"){
			args.tmp.push({
				name:$(item).attr("name"),
				html:$(item).html()
			});  
			$(item).remove();   
		}
		if(item.tagName!=undefined&&item.tagName!="SCRIPT"&&item.tagName!="TMP"){  
			//保存属性
			var attr=[];
			$.each(item.attributes, function() {    
			    if(this.specified) {
			    	attr.push({
						name:this.name,
						val:this.value
					});
			    }
			});
			//保存节点
			var node={
					item:item,
					data:data,
					attr:attr,
					html:$(item).html(),
					outhtml:$(item).prop("outerHTML"),
					hasChild:($(item).children().length>0)?true:false,
					update:function(){
						args.render(this,this.data);
					}
				};
			args.node.push(node);   
			args.render(node,data);
		} 
		
		 
	};
	//遍历添加子节点
	args.getNodes=function(all,data){ 
		all=$(all).children(); 
		function hasTMP(obj){ 
			while (obj != undefined && obj != null && obj.tagName.toUpperCase() != 'BODY'){
				if (obj.tagName.toUpperCase() == 'TMP'){
					return true;
				}
				obj = obj.parentNode;
			}
			return false;
		}
		var new_node=[];
		all.each(function(i,item){ 
			//遍历所有节点，除去脚本和模板 
			if(item.tagName=="TMP"){
				args.tmp.push({
					name:$(item).attr("name"),
					html:$(item).html()
				});  
				$(item).remove();   
			}
			if(item.tagName!=undefined&&item.tagName!="SCRIPT"&&item.tagName!="TMP"){  
				//保存属性
				var attr=[];
				$.each(item.attributes, function() {    
				    if(this.specified) {
				    	attr.push({
							name:this.name,
							val:this.value
						});
				    }
				});
				//保存节点
				args.node.push({
					item:item,
					data:data,
					attr:attr,
					html:$(item).html(),
					outhtml:$(item).prop("outerHTML"),
					hasChild:($(item).children().length>0)?true:false,
					update:function(){
						args.render(this,this.data);
					}
				}); 
				new_node.push(args.node[args.node.length-1]);
			} 
		});
		
		for(var i in new_node){  
			args.render(new_node[i],data);
		}
	};
	
	//根据ID查找节点
	args.getNodeByID=function(id){
		for(var i in args.node){
			if(args.node[i].id!=undefined&&args.node[i].id==id){
				return args.node[i];
			}
		}
	};
	
	//简化获取工具
	window["wt"]=WheelTool;
	window["wd"]=args.data;
	window["wg"]=args.getNodeByID;
	
	//加载模块化工具 
	if(args.mode!=undefined&&args.mode.length>0){
		if(args.mode_src!=undefined)WheelMode.init(args.mode_src);
		WheelMode.load(args.mode,function(){
			//初始化
			if(args.onLoad!=undefined){  
				args.onLoad();
			}
			args.getNodes($("body"),args.data); 
			//准备完成
			if(args.onReady!=undefined){  
				args.onReady();
			}
		}); 
	}else{
		//初始化
		if(args.onLoad!=undefined){  
			args.onLoad();
		}
		args.getNodes($("body"),args.data); 
		//准备完成
		if(args.onReady!=undefined){  
			args.onReady();
		}
	}
	
}