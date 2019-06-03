/**
 * wheel前端框架核心
 * 
 * wheel更新
 * 0.获取节点
 * 获取view并保存节点和内容
 * 1.渲染节点
 * 遍历节点内容和数据绑定
 * 2.逻辑标签控制和监听
 * 遍历节点进行控制和监听
 * 3.模块插入
 * 动态引入自定义模块
 * 4.基础工具
 * 引入各种简化小工具
 * 5.基础模块
 * 引入自定义模块
 * 6.新增路由功能
 * 方便制作单页应用
 * 7.优化内存消耗
 * 渲染更迅速
 * 
 * 
 *
 *作者：陈海山
 *更新日期：2019/3/22
 *
 */

//渲染前隐藏所有节点
var WheelInitCss = document.createElement('style');  
WheelInitCss.type='text/css';
WheelInitCss.innerHTML='body>*{visibility: hidden;}';
document.getElementsByTagName('head')[0].appendChild(WheelInitCss);

/*var WheelInitLoading = $('<div class="WheelInitLoading" style="visibility:visible;"><img style="margin-top: 100px;" src="http://www.sucaijishi.com/uploadfile/2013/0527/20130527034917143.gif"/></div>');   
WheelInitLoading.css({
	"width": "100%",
	"height": "100%",
	"position": "fixed",
    "background": "#F6F6F6",
    "left": "0",
    "top": "0",
    "text-align":"center"
    
});*/


//兼容工具
NodeList.prototype.forEach = Array.prototype.forEach;
var Wheel_JR={
		isIE:function(){
			if(!!window.ActiveXObject || "ActiveXObject" in window){
				return true;
			}else{
				return	false;
			}
		},
		isIE11:function(){
			if((/Trident\/7\./).test(navigator.userAgent)){
				return true;
			}else{
				return false;
			}
		},
		remove:function(_element){
			if(this.isIE()||this.isIE11()){
				var _parentElement = _element.parentNode;
			    if(_parentElement){
			        _parentElement.removeChild(_element);
			    }
			}else{
				_element.remove();
			}
			
		}
}


//小工具
var WheelTool={
	//请求服务
	get:function(url,data,isJson,isHtml){
		//直接请求URL
		if(typeof url == "string"){
			if(data==undefined)data={};
			var jq_res=$.ajax({url:url,data:data,async:false,type:"POST"});
			var res=jq_res.responseText;
			if(jq_res.status!="200")res='{"code":'+jq_res.status+',"msg":"net error '+jq_res.status+'","data":"error"}';
			if(res!=undefined){
				if(isJson!=undefined&&isJson==false)return res;
				if(isHtml!=undefined&&isHtml==true)res=res.replace(/\r/g,"").replace(/\n/g,"<br>").replace(/\\/g,"\\\\");
				if(WheelTool.isJSON(res)){
					res=WheelTool.toJSON(res);
					if(res.code!=undefined&&res.code==-1&&res.msg!=undefined)console.error("后台报错："+res.msg);
					return res; 
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
					var jq_res=$.ajax({url:url.url,data:data,async:false,type:"POST"});
					var res=jq_res.responseText;
					if(jq_res.status!="200")res='{"code":'+jq_res.status+',"msg":"net error '+jq_res.status+'","data":"error"}';
					if(res!=undefined){
						if(isJson!=undefined&&isJson==false)return res;
						if(isHtml!=undefined&&isHtml==true)res=res.replace(/\r/g,"").replace(/\n/g,"<br>").replace(/\\/g,"\\\\");
						if(WheelTool.isJSON(res)){
							res=WheelTool.toJSON(res);
							if(res.code!=undefined&&res.code==-1&&res.msg!=undefined)console.error("后台报错："+res.msg);
							return res; 
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
	    }else{
	    	return false;
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
	//格式化时间
	fmTime:function(str,fm){
		if(typeof str == 'string'){
			str=str.replace(/-/g,"/");
			return format(new Date(str),fm);
		}else{
			return format(new Date(str),fm);
		}
		function format(time,format){
			var date = new Date(time);
			var year = date.getFullYear(),
				month = date.getMonth()+1,
				day = date.getDate(),
				hour = date.getHours(),
				min = date.getMinutes(),
				sec = date.getSeconds();
			var preArr = Array.apply(null,Array(10)).map(function(elem, index) {
				return '0'+index;
			});
			var newTime = format.replace(/yyyy/g,year)
								.replace(/MM/g,preArr[month]||month)
								.replace(/dd/g,preArr[day]||day)
								.replace(/HH/g,preArr[hour]||hour)
								.replace(/mm/g,preArr[min]||min)
								.replace(/ss/g,preArr[sec]||sec);

			return newTime;
		}
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
		if(para!=undefined){
			var h=0;
			for(var i in para){
				if(h==0){
					has++;
					url+="?";
				}
				if(h==1){
					has++;
					url+=i+"="+para[i];
				}else{
					url+="&"+i+"="+para[i];
				}
			}
		}
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
		function isClass(o){
            return Object.prototype.toString.call(o).slice(8,-1)
        }
		var result,oClass = isClass(obj);
	    if(oClass==='Object'){
	        result = {};
	    }else if(oClass==='Array'){
	        result = [];
	    }else{
	        return obj
	    }
	    if(oClass==='Object'){
	        for(var key in obj){
	            result[key]  = this.clone(obj[key]);
	        }
	    }else if(oClass==="Array"){
	        for(var i=0;i<obj.length;i++){
	             result.push(this.clone(obj[i]));
	        }
	    }
	
	    return result
	},
	//插入脚本
	joinJS:function(src,call){
		var script=document.createElement("script"); 
		script.type="text/javascript";
		script.src=src;
		document.getElementsByTagName('head')[0].appendChild(script);
		script.onload=script.onreadystatechange=function(){
			if(call!=undefined)call();
		}
	},
	//数组转字符串
	arr2str:function(arr,sp,sl,sr){
		var str="";
		var ssp=sp!=undefined?sp:"";
		var ssl=sl!=undefined?sl:"";
		var ssr=sr!=undefined?sr:"";
		for(var i in arr){
			if(str.length==0){
				str+=ssl+arr[i]+ssr;
			}else{
				str+=ssp+ssl+arr[i]+ssr;
			}
		}
		return str;
	},
	//纵轴滚动聚焦
	focus:function(item,speed){
		var sd=speed||800;
		var top=$(item).offset().top-50;
		if(top<0)top=0;
		$('html,body').animate({scrollTop:top}, sd);
	},
	//本地缓存
	cache:function(key,val){
		if(val!=undefined||val==null){
			if(val!=null){
				sessionStorage[key]=JSON.stringify(val);
			}else{
				delete sessionStorage[key];
			}
		}else{
			if(sessionStorage[key]!=undefined&&sessionStorage[key]!=null){
				return JSON.parse(sessionStorage[key]);
			}else{
				return null;
			}
		}
	},
	//文本转html
	txt2html:function(txt){
		return txt.replace(/\r\n/,"<br/>").replace(/\n/,"<br/>");
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
								if(WheelMode.mode[mload.mm[j]].html!=undefined){
									for(var k in WheelMode.mode[mload.mm[j]].html){  
										var content=wt.get(WheelMode.mode[mload.mm[j]].html[k]);
										content=content.replace(/\\n/g,"");
										WheelMode.mode[mload.mm[j]].html[k]=content;
									}
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
		},
		ready:function(){
			for(var i in WheelMode.mode){
				if(WheelMode.mode[i]!=undefined&&WheelMode.mode[i].onReady!=undefined&&typeof WheelMode.mode[i].onReady === "function"){
					WheelMode.mode[i].onReady();
				}
			}
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
	
	//所有视图节点
	args.node=[];
	//视图模板
	args.tmp=[];
	//绑定的数据
	args.bind=[];
	//路由节点
	args.routeNode=[];
	//资源懒加载节点
	args.wsrcNode=[];
	
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
		if(jsstr.indexOf(";")<0){
			jsstr=head+"(typeof("+jsstr+")!=\"undefined\")?item_.res=("+jsstr+"):null;";
		}else{
			jsstr=head+jsstr+"item_.res=null;";
		}
		function callAnotherFunc(fnFunction, vArgument) { 
			fnFunction(vArgument);
		}
		function checkStr(str){
			var regEn = /[`~!@#$%^&*()_+<>?:"{},.\/;'[\]]/im,regCn = /[·！#￥（——）：；“”‘、，|《。》？、【】[\]]/im;
			if(regEn.test(str) || regCn.test(str)) {
			    return false;
			}else{
				return true;
			}
		}
		callAnotherFunc(new Function("item_", jsstr), item);
		return item.res; 
	};
	
	//处理取值链，返回最后键值
	function getLastData(obj,key){
		
		var oobj=obj;
		
		var bValue=null;
		var regex = /\[(.+?)\]/g;
		
		var ss=key.split(".");
		for(var i=0;i<ss.length-1;i++){
			var m=ss[i].match(regex);
			if(m!=null){
				obj=obj[ss[i].replace(m[0],"")];
				obj=obj[args.runjs(m[0].replace("[","").replace("]",""),oobj)];
			}else{
				obj=obj[ss[i]];
			}
		}
		var m=ss[ss.length-1].match(regex);
		if(m!=null){
			obj=obj[ss[i].replace(m[0],"")];
			key=m[0].replace("[","").replace("]","");
			key=args.runjs(key,oobj);
		}else{
			key=ss[ss.length-1];
		}
		
		return {
			bValue:obj[key],
			obj:obj,
			key:key
		}
	}
	
	//对象属性修改事件绑定
	args.setObj=function(obj,key,fun){
		
		var ld=getLastData(obj,key);
		
		obj=ld.obj;
		key=ld.key;
		var bValue=ld.bValue;
		
		var bind_item=null;
		for(var i in args.bind){
			if(args.bind[i].obj==obj&&args.bind[i].key==key){
				bind_item=args.bind[i];
				break;
			}
		}
		//追加绑定事件
		if(bind_item==null){
			var funs=[];
			funs.push(fun);
			bind_item={
					init:true,
					obj:obj,
					key:key,
					funs:funs
				};
			args.bind.push(bind_item);
		}else{
			bind_item.funs.push(fun);
		}
		
		if(bind_item.init){
			bind_item.init=false;
			try {
				//监听数组变化
				if(bValue instanceof Array){
					const arrayProto = Array.prototype;
					const arrayMethods = Object.create(arrayProto);
					const newArrProto = [];
					['push','pop','shift','unshift','splice','sort','reverse'
					].forEach(function(method) {
						let original = arrayMethods[method];
						newArrProto[method] = function mutator() {
							var res=original.apply(this, arguments);
							for(var i in bind_item.funs){
								bind_item.funs[i](bValue,method,res);
							}
							return res;
						}
					});
					
					bValue.__proto__ = newArrProto;
				}
				//监听赋值变化
				var oj=Object.defineProperty(obj,key,{
					get:function(){
						return bValue;
					},
					set:function(newValue){
						bValue=newValue;
						for(var i in bind_item.funs){
							bind_item.funs[i](bValue);
						}
					},
					enumerable: true,
					configurable: true
				});
			} catch (e) {
				console.error("wh-bind报错:"+e);
			}
		}
	};
	
	var reg_rp = /\{\{(.+?)\}\}/g; 
	
	//渲染文本
	args.renderTXT=function(val,dd){
		var m=val.match(reg_rp);  
		if(m!=null){
			for(var j in m){
				var s=m[j].replace(/\{\{/g,'').replace(/\}\}/g,'');
				var reg=m[j].replace(/\[/g,'\\\[').replace(/\]/g,'\\\]').replace(/\*/g,'\\\*').replace(/\+/g,'\\\+').replace(/\=/g,'\\\=').replace(/\?/g,'\\\?').replace(/\'/g,'\\\'').replace(/\"/g,'\\\"').replace(/\(/g,'\\\(').replace(/\)/g,'\\\)');   
				val=val.replace(new RegExp(reg,"g"),args.runjs(s,dd)); 
			}
			return val;
		}
		return null;
	}
	
	//节点渲染
	args.render=function(node){
		
		if(node.item==null||node.item==undefined){
			console.error("Wheel渲染报错：item节点不存在");
			return;
		}
		//节点数据集
		var data=node.data;
		//清除，重新渲染
		node.item.innerHTML=node.html;
		//是否已经渲染子节点
		var isRenderChilden=false;
		
		var reg_a = /wh-a:(.+?)/g;
		var reg_e = /wh-e:(.+?)/g;
		var reg_r = /wh-r:/g;
		
		//渲染模板
		if(node.tag=="TMP"){
			var name=$(node.item).attr("set");
			if(name!=null&&args.tmp[name]!=undefined){
				var tmp_content=$($.parseHTML(args.tmp[name]));
				$(node.item).replaceWith(tmp_content);
				tmp_content.each(function(i,item){
					if(item.tagName!=undefined){
						args.getNode(i,node,item,data,node.root);
					}else{
						var val=args.renderTXT(item.nodeValue,data);
						if(val!=null)item.nodeValue=val;
					}
				});
			}
			return;
		}
		
		//标签顺序
		function wh_val(name){
			if(name=="wh-id")return 0;
			if(name=="wh-if"||name=="wh-if-rm")return 1;
			if(name=="wh-route")return 2;
			if(name=="wh-none")return 3;
			if(name.indexOf("wh-r")>-1)return 4;
			if(name=="wh-for")return 5;
			if(name.indexOf("wh-a")>-1)return 6;
			if(name.indexOf("wh-e")>-1)return 7;
			if(name=="wh-bind")return 8;
			if(name=="wh-listen")return 9;
			
			
		}
		//标签渲染排序
		node.wh.sort(function(a,b){
			return wh_val(a.name)-wh_val(b.name);
		});
		
		//是否继续渲染
		var goRender=true;
		//所有绑定事件,第一次渲染解绑和后续渲染不解绑
		var binds=[];
		
		//标签渲染
		if(node.hasWhTag)for(var i in node.wh){
			var name=node.wh[i].name;
			var val=node.wh[i].val;
			
			//是否隐藏标签
			if(!args.showTag)$(node.item).removeAttr(name); 
			
			if(name!="wh-none"&&name!="wh-route"&&name.indexOf("wh-r")<0&&(val==undefined||val=="")){
				console.error("Wheel标签错误："+name+"缺少参数",node.item);
				return;
			}
			
			//标签 标志wh-id
			if(name=="wh-id"){
				if(val!=undefined&&val!=""){
					node.id=val;
				}
			}
			//标签 控制wh-if
			if(name=="wh-if"){
				if(args.runjs(val,data)){
					$(node.item).show();
				}else{
					$(node.item).hide();
					node.item.innerHTML="";
					goRender=false;
				}
			}
			//标签 控制wh-if-rm
			if(name=="wh-if-rm"){
				if(!args.runjs(val,data)){
					Wheel_JR.remove(node.item);
					goRender=false;
				}
			}
			//标签 忽略wh-route
			if(name=="wh-route"){
				var has=false;
				for(var i in args.routeNode){
					if(args.routeNode[i].node==node){
						has=true;break;
					}
				}
				if(!has)args.routeNode.push({ 
					url:val,
					node:node
				});
			}
			//标签 忽略wh-none
			if(name=="wh-none"){
				$(node.item).hide();
				goRender=false;
			}
			//标签 懒加载
			var m=name.match(reg_r);
			if(m!=null){
				if(!goRender)continue;
				var name=name.replace("wh-r:","");
				if(val!=""){
					var _val=args.renderTXT(val,node.data);
					if(_val!=null)val=_val;
				}
				
				var s_node=null;
				if(name!=""){
					s_node={
						type:"inert",
						attr:name,
						val:val,
						isLoad:false,
						node:node
					};
				}else{
					$(node.item).css("opacity","0");
					isRenderChilden=true;
					s_node={
						type:"update",
						attr:name,
						val:val,
						isLoad:false,
						node:node
					};
				}
				var has=false;
				for(var i in args.wsrcNode){
					if(args.wsrcNode[i]==s_node){
						has=true;break;
					}
				}
				if(!has){
					args.wsrcNode.push(s_node);
					args.wsrc.rol(s_node);
				}
			}
			//标签 控制wh-for
			if(name=="wh-for"){
				if(!goRender)continue;
				var n=0;
				var fnx="n"
				var fkx="key"; 
				var ftx="item";
				isRenderChilden=true;
				
				
				if(val.indexOf(":")>-1){
					var ss=val.split(":");
					val=ss[0];
					if(ss[1]!=undefined)ftx=ss[1];
					if(ss[2]!=undefined)fkx=ss[2];
					if(ss[3]!=undefined)fnx=ss[3];
				}
				
				node.item.innerHTML="";
				
				var arr=args.runjs(val,data);
				if(/^[0-9]*$/.test(arr)){
					if(/^[0-9]*$/.test(ftx)){
						var start=parseInt(arr);
						var end=parseInt(ftx);
						for(var j=start;j<end;j++){
							var item=$($.parseHTML(node.html));
							var dd={};
							dd[fnx]=n;
							dd[fkx]=j;
							dd[ftx]=j; 
							dd["parent"]=data;
							
							$(node.item).append(item);
							item.each(function(i,item){
								if(item.tagName!=undefined){
									args.getNode(i,node,item,dd,node.root);
								}else{
									var val=args.renderTXT(item.nodeValue,dd);
									if(val!=null)item.nodeValue=val;
								}
							});
							n++;
						}
					}else{
						var num=parseInt(arr);
						for(var j=0;j<num;j++){
							var item=$($.parseHTML(node.html));
							var dd={};
							dd[fnx]=n;
							dd[fkx]=j;
							dd[ftx]=j; 
							dd["parent"]=data;
							
							$(node.item).append(item);
							item.each(function(i,item){
								if(item.tagName!=undefined){
									args.getNode(i,node,item,dd,node.root);
								}else{
									var val=args.renderTXT(item.nodeValue,dd);
									if(val!=null)item.nodeValue=val;
								}
							});
							n++;
						}
					}
				}else{
					node.forList=[];
					for(var j in arr){
						if(arr instanceof Array&&['push','pop','shift','unshift','splice','sort','reverse'].indexOf(j)>-1)continue;
						var item=$($.parseHTML(node.html));
						var dd={};
						dd[fnx]=n;
						dd[fkx]=j;
						dd[ftx]=arr[j]; 
						dd["parent"]=data;
						
						dd["_forData"]={};
						dd["_forData"].fnx=fnx;
						dd["_forData"].fkx=fkx;
						dd["_forData"].ftx=ftx;
						dd["_forData"][fkx]=j;
						dd["_forData"]["arr"]=arr;
						
						$(node.item).append(item);
						node.forList[j]=[];
						item.each(function(i,item){
							if(item.tagName!=undefined){
								var rs=args.getNode(i,node,item,dd,node.root);
								node.forList[j][i]=rs;
							}else{
								var val=args.renderTXT(item.nodeValue,dd);
								if(val!=null)item.nodeValue=val;
								node.forList[j][i]=item;
							}
						});
						n++;
					}
				}
			}
			//标签 插入属性wh-a:
			var m=name.match(reg_a);
			if(m!=null){
				if(!goRender)continue;
				var name=name.replace("wh-a:",""); 
				var val=args.runjs(val,data);
				if(val!=null&&val!=='')$(node.item).attr(name,val);
			}
			//标签 插入事件wh-e:
			var m=name.match(reg_e);
			if(m!=null){
				if(!goRender)continue;
				if(node.item.wh_es==undefined)node.item.wh_es=[];
				if(node.root.event!=undefined&&node.root.event[val]!=undefined&&typeof node.root.event[val] === "function"){
					var e_name=name.split(":")[1];
					if(e_name=="init"){//初始化事件
						node.initEvent.push(val);
					}else{//一般绑定事件
						if(binds.indexOf(e_name)<0){
							$(node.item).unbind(e_name);
							binds.push(e_name);
						}
						node.item.wh_es[e_name]=val;
						$(node.item).bind(e_name,function(e){
							node.root.event[this.wh_es[e.type]](e,node,node.root);
						});
					}
				}
			}
			//标签 数据绑定节点更新wh-bind
			if(name=="wh-bind"){
				if(!node.hasInit)args.setObj(data,val,function(v,arr_method,arr_res){
					if(node.isForChild){
						node.data[node.forData.ftx]=node.forData.arr[node.forData[node.forData.fkx]];
						node.update();
					}else{
						var isBindFor=false;
						for(var k in node.wh){
							if(node.wh[k].name=="wh-for"&&node.forList!=undefined&&node.forList.length>0){
								if(arr_method=="push"){
									isBindFor=true;
									var fnx="n"
									var fkx="key"; 
									var ftx="item";
									var arr=args.runjs(node.wh[k].val,node.data);
									var n=arr_res-1;
									var j=n-1;
									var item=$($.parseHTML(node.html));
									var dd={};
									dd[fnx]=n;
									dd[fkx]=j;
									dd[ftx]=arr[j]; 
									dd["parent"]=node.data;
									
									dd["_forData"]={};
									dd["_forData"].fnx=fnx;
									dd["_forData"].fkx=fkx;
									dd["_forData"].ftx=ftx;
									dd["_forData"][fkx]=j;
									dd["_forData"]["arr"]=arr;
									
									$(node.item).append(item);
									node.forList[j]=[];
									item.each(function(i,item){
										if(item.tagName!=undefined){
											var rs=args.getNode(i,node,item,dd,node.root);
											node.forList[j][i]=rs;
										}else{
											var val=args.renderTXT(item.nodeValue,dd);
											if(val!=null)item.nodeValue=val;
											node.forList[j][i]=item;
										}
									});
								}
							}
						}
						if(!isBindFor)node.update();
						//node.update();
					}
				});
			}
			//标签 控件绑定数据更新wh-listen
			if(name=="wh-listen"){
				if(!goRender)continue;
				if(val.indexOf(":")<0){
					//基础控件
					var ld=getLastData(data,val);
					node.item.wh_listen_data=ld.obj;
					node.item.wh_listen_val=ld.key;
					if(node.item.tagName=="INPUT"){
						if(node.item.type=="text"||node.item.type=="password"){
							$(node.item).val(node.item.wh_listen_data[node.item.wh_listen_val]);
							args.setObj(data,val,function(v){
								$(node.item).val(v);
							});
							if(binds.indexOf("keyup")<0){
								$(node.item).unbind("keyup");
								binds.push("keyup");
							}
							$(node.item).bind("keyup",function(e){
								this.wh_listen_data[this.wh_listen_val]=this.value;
							});
						}
						if(node.item.type=="radio"){
							var vl=args.renderTXT($(node.item).val(),data);
							var vv=args.runjs(val,data);
							if(vl==vv){
								$(node.item).attr('checked','true');
							}else{
								$(node.item).removeAttr('checked');
							}
							node.item.wh_listen_nval=val;
							args.setObj(data,val,function(v){
								if($(node.item).val()==args.runjs(node.item.wh_listen_nval,data)){
									$(node.item).attr('checked','true');
								}else{
									$(node.item).removeAttr('checked');
								}
							});
							if(binds.indexOf("change")<0){
								$(node.item).unbind("change");
								binds.push("change");
							}
							$(node.item).bind("change",function(e){
								this.wh_listen_data[this.wh_listen_val]=this.value;
							});
						}
						if(node.item.type=="checkbox"){
							var vl=args.renderTXT($(node.item).val(),data); 
							if(vl==null)vl=$(node.item).val();
							if(node.item.wh_listen_data[node.item.wh_listen_val].indexOf(vl)>-1){
								$(node.item).attr("checked",'true');
							}else{
								$(node.item).removeAttr('checked');
							}
							args.setObj(data,val,function(v){
								if(node.item.wh_listen_data[node.item.wh_listen_val].indexOf(vl)>-1){
									$(node.item).attr("checked",'true');
								}else{
									$(node.item).removeAttr('checked');
								}
							});
							if(binds.indexOf("change")<0){
								$(node.item).unbind("change");
								binds.push("change");
							}
							$(node.item).bind("change",function(e){
								if(this.wh_listen_data[this.wh_listen_val] instanceof Array){
									if($(this).is(':checked')){
										if(this.wh_listen_data[this.wh_listen_val].indexOf(this.value)==-1){
											this.wh_listen_data[this.wh_listen_val].push(this.value);
										}
									}else{
										for(var i=this.wh_listen_data[this.wh_listen_val].length-1;i>=0;i--){
											if(this.wh_listen_data[this.wh_listen_val][i]==this.value){
												this.wh_listen_data[this.wh_listen_val].splice(i,1);
											}
										}
									}
								}else if(typeof this.wh_listen_data[this.wh_listen_val]=="string"){
									if($(this).is(':checked')){
										if(this.wh_listen_data[this.wh_listen_val].indexOf(this.value)==-1){
											if(this.wh_listen_data[this.wh_listen_val].length>0)this.wh_listen_data[this.wh_listen_val]+=",";
											this.wh_listen_data[this.wh_listen_val]+=this.value;
										}
									}else{
										if(this.wh_listen_data[this.wh_listen_val].indexOf(this.value)==0){
											if(this.wh_listen_data[this.wh_listen_val].length==this.value.length){
												this.wh_listen_data[this.wh_listen_val]="";
											}else{
												this.wh_listen_data[this.wh_listen_val]=this.wh_listen_data[this.wh_listen_val].replace(this.value+",","");
											}
										}else{
											this.wh_listen_data[this.wh_listen_val]=this.wh_listen_data[this.wh_listen_val].replace(","+this.value,"");
										}
									}
								}
							});
						}
					}
					if(node.item.tagName=="TEXTAREA"){
						$(node.item).val(node.item.wh_listen_data[node.item.wh_listen_val]);
						args.setObj(data,val,function(v){
							$(node.item).val(v);
						});
						if(binds.indexOf("keyup")<0){
							$(node.item).unbind("keyup");
							binds.push("keyup");
						}
						$(node.item).bind("keyup",function(e){
							this.wh_listen_data[this.wh_listen_val]=this.value;
						});
					}
					if(node.item.tagName=="SELECT"){
						$(node.item).val(node.item.wh_listen_data[node.item.wh_listen_val]);
						args.setObj(data,val,function(v){
							$(node.item).val(v);
						});
						if(binds.indexOf("change")<0){
							$(node.item).unbind("change");
							binds.push("change");
						}
						$(node.item).bind("change",function(e){
							this.wh_listen_data[this.wh_listen_val]=this.value;
						});
					}
				}else{
					//自定义控件	val:(fun()==>{set:fun,get:fun,event:str})
					vx=val.split(":");
					var ld=getLastData(data,vx[0]);
					node.item.wh_listen_data=ld.obj;
					node.item.wh_listen_val=ld.key;
					node.item.wh_listen_node=node;
					
					if(node.root.event!=undefined&&node.root.event[vx[1]]!=undefined&&typeof node.root.event[vx[1]] === "function"){
						
						var fun=node.root.event[vx[1]]();
						node.item.wh_listen_fun=fun;
						
						if(fun!=undefined&&fun.set!=undefined&&fun.get!=undefined){
							fun.set(node,node.item.wh_listen_data[node.item.wh_listen_val]);
							args.setObj(data,vx[0],function(v){
								fun.set(node,v);
							});
							if(fun.event!=undefined){
								if(binds.indexOf(fun.event)<0){
									$(node.item).unbind(fun.event);
									binds.push(fun.event);
								}
								$(node.item).bind(fun.event,function(e){
									this.wh_listen_data[this.wh_listen_val]=this.wh_listen_fun.get(this.wh_listen_node);
								});
							}
						}else{
							console.error("Wheel自定义控件错误：定义格式不正确!"+fun);
						}
						
					}else{
						console.error("Wheel自定义控件错误：找不到"+vx[1]+"事件！");
					}
				}
			}
		}
		
		
		//属性渲染
		for(var i in node.attr){
			var name=node.attr[i].name;
			var val=node.attr[i].val;
			
			val=args.renderTXT(val,data);
			if(val!=null)$(node.item).attr(node.attr[i].name,val);
		}
		
		if(!isRenderChilden&&goRender){
			//纯文本内容渲染
			if(!node.hasChild){
				if(node.html==undefined)return;
				
				var val=args.renderTXT(node.html,data);
				if(val!=null)$(node.item).html(val); 
				
			//子节点渲染
			}else if(!isRenderChilden){
				args.renderChilds(node,data);
			}
		}
		
		//首次渲染完成
		node.hasInit=true;
	};
	//渲染子节点
	args.renderChilds=function(node,data){
		var childs=node.item.childNodes;
		childs.forEach(function(item,i){
			if(item.tagName!=undefined){
				args.getNode(i,node,item,data,node.root);
			}else{
				var val=args.renderTXT(item.nodeValue,data);
				if(val!=null)item.nodeValue=val;
			}
		}); 
	};
	//初始化节点
	args.getNode=function(no,parent,item,data,root){
		if(item==undefined){console.error("渲染节点报错：undefined");return;}
		if(item instanceof jQuery)item=item[0];
		
		//除去脚本和模板 
		if(item.tagName=="TMP"&&$(item).attr("name")!=null){
			args.tmp[$(item).attr("name")]=$(item).html();
			$(item).remove();
			return;
		}
		
		//保存节点
		if(item.tagName!=undefined&&item.tagName!="SCRIPT"){
			var reg_wh =new RegExp(/^wh-(.+?)$/);
			var attr=[];
			var wh=[];
			$.each(item.attributes, function() {    
			    if(this.specified) {
					if(reg_wh.test(this.name)){
						wh.push({
							name:this.name,
							val:this.value
						});
					}else{
						attr.push({
							name:this.name,
							val:this.value
						});
					}
			    	
			    }
			});
			var node={
				no:parent!=null?parent.no+"-"+no:no,
				tag:item.tagName,
				item:item,
				parent:parent,
				data:data,
				attr:attr,
				wh:wh,
				html:$(item).html().replace(/<!--[\s\S]*?-->/g,''),
				//outhtml:$(item).prop("outerHTML"),
				hasWhTag:wh.length>0?true:false,
				hasChild:($(item).children().length>0)?true:false,
				child:[],
				update:function(){
					args.render(this);
				},
				hasInit:false,
				root:root!=undefined?root:args,
				isForChild:data["_forData"]!=undefined?true:false,
				set:[],
				initEvent:[]
			};
			if(node.isForChild){
				node.forData=data["_forData"],
				delete data["_forData"];
			}
			if(parent!=null)parent.child.push(node);
			var has=false; 
			for(var i in args.node){
				if(args.node[i].no==node.no){
					has=true;
					args.node[i]=node;
					break;
				}
			}
			if(!has)args.node.push(node);   
			args.render(node);
			if(node.initEvent.length>0){//渲染完成事件
				for(var i in node.initEvent){
					node.root.event[node.initEvent[i]](null,node,node.root);
				}
			}
			return node;
		} 
	};
	
	//根据ID查找节点
	args.getNodeByID=function(id,node){
		if(node==undefined||node==null){
			for(var i in args.node){
				if(args.node[i].id!=undefined&&args.node[i].id==id){
					return args.node[i];
				}
			}
		}else{
			var res=null
			for(var i in node.child){
				if(node.child[i].id!=undefined&&node.child[i].id==id){
					res=node.child[i];
					break;
				}else if(node.child[i].hasChild){
					var re=args.getNodeByID(id,node.child[i]);
					if(re!=null)res=re;
				}
			}
			return res;
		}
	};
	
	//初始化路由
	args.initRoute=function(){
		//路由监听
		$(window).unbind('hashchange');
		$(window).bind('hashchange',function(){
			var url=location.href.split("#!")[1];
			var tmp=null;
			for(var i in args.app){
				if(args.app[i].url!=undefined&&url==args.app[i].url){
					if(args.app[i].controller!=undefined)args.app[i].controller();
					if(args.app[i].tmp!=undefined)tmp=args.tmp[args.app[i].tmp];
					break;
				}
			}
			for(var i in args.routeNode){
				if(args.routeNode[i].url==""||(args.routeNode[i].url!=""&&args.routeNode[i].url.indexOf(url)>-1)){
					if(args.routeNode[i].node!=undefined&&args.routeNode[i].node!=null){
						if(tmp!=null){
							var oldHtml=args.routeNode[i].node.html;
							args.routeNode[i].node.html=tmp;
							args.routeNode[i].node.update();
							args.routeNode[i].node.html=oldHtml;
						}else{
							args.routeNode[i].node.update();
						}
					}
				}
			}
	    });
		//初始化默认
		for(var i in args.routeNode){
			if(args.routeNode[i].node!=undefined&&args.routeNode[i].node!=null){
				var app=null;
				var tmp=null;
				if(args.routeNode[i].url!=""){
					for(var j in args.app){
						if(args.routeNode[i].url.indexOf(args.app[j].url)>-1){
							app=args.app[j];break;
						}
					}
				}else{
					app=args.app[0];
				}
				if(app.controller!=undefined)app.controller();
				if(app.tmp!=undefined)tmp=args.tmp[app.tmp];
				if(tmp!=null){
					var oldHtml=args.routeNode[i].node.html;
					args.routeNode[i].node.html=tmp;
					args.routeNode[i].node.update();
					args.routeNode[i].node.html=oldHtml;
				}else{
					args.routeNode[i].node.update();
				}
			}
		}
	};
	
	//滚动懒加载
	args.wsrc={
		init:function(){
			$(window).on('scroll',function(){  
				args.wsrc.rol();
		    });
		},
		rol:function(s_node){
			if(s_node!=undefined){
				var item=s_node.node.item;
				if (args.wsrc.checkShow($(item))&&!s_node.isLoad){
	            	args.wsrc.load(s_node);
	            }
			}else{
				for(var i in args.wsrcNode){
					var s_node=args.wsrcNode[i];
					var item=s_node.node.item;
					if(item!=undefined&&item!=null){
						if (args.wsrc.checkShow($(item))&&!s_node.isLoad){
			            	args.wsrc.load(s_node);
			            }
					}
		        };
			}
		},
		checkShow:function(item){
			var scrollTop = $(window).scrollTop(); 
	        var windowHeight = $(window).height();
	        var offsetTop = item.offset().top;

	        if (offsetTop < (scrollTop + windowHeight) && offsetTop > scrollTop) {
	            return true;
	        }
	        return false;
		},
		load:function(s_node){
			s_node.isLoad=true;
			if(s_node.type=="inert"){
				$(s_node.node.item).attr(s_node.attr,s_node.val);
			}
			if(s_node.type=="update"){
				$(s_node.node.item).css("opacity","");
				args.renderChilds(s_node.node,s_node.node.data);
			}
		}
	}
	
	//简化调用
	window["wt"]=WheelTool;
	window["wa"]=args;
	window["wd"]=args.data;
	window["wn"]=args.getNode;
	window["wg"]=args.getNodeByID;
	window["we"]=args.event;
	window["wnn"]=function(item,data,root){
		args.getNode(Math.random()*10,null,item,data,root);
	};
	window["wl"]=typeof(wl)!="undefined"?wl:{};
	
	//是否禁止渲染
	args.isStop=args.isStop!=undefined?args.isStop:false;
	
	//加载模块化工具 
	if(args.mode!=undefined&&args.mode.length>0){
		if(args.mode_src!=undefined)WheelMode.init(args.mode_src);
		WheelMode.load(args.mode,function(){
			initWheel();
		});
	}else{
		initWheel();
	}
	
	function initWheel(){
		//$("body").append(WheelInitLoading);
		//初始化
		if(args.onLoad!=undefined){  
			args.onLoad();
		}
		
		//渲染
		if(!args.isStop){
			
			args.getNode(0,null,$("body"),args.data);
			//WheelInitCss.remove();
			if(args.isLast==undefined||args.isLast==true)Wheel_JR.remove(WheelInitCss);
			args.initRoute();
			args.wsrc.init();
			
			//模块等待页面加载完成
			if(args.mode_src!=undefined){
				$(function(){
					WheelMode.ready();
				});
			}
		}
		
		//准备完成
		if(args.onReady!=undefined){  
			args.onReady();
		}
		//$(".WheelInitLoading").remove();
	}
}