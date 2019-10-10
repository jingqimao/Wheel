


//小工具
var $wheel_tool={
		
}

var $wheel=function($args){
	
	//是否显示wheel控制标签
	if($args.showTag==undefined)$args.showTag=false;
	
	//动态执行语句并返回结果
	$args.$runjs=function(js_str,data){
		var head="";
		if(data==undefined)data=null;
		var item={
			data:data,
			res:null
		}; 
		for(var i in data){
			if(i.indexOf(".")<0)head+="var "+i+"=item_.data."+i+";";
		}
		var js_str_t=js_str.trim();
		if(js_str_t.charAt(js_str_t.length-1)!=";"){
			js_str=head+"(typeof("+js_str+")!=\"undefined\")?item_.res=("+js_str+"):null;";
		}else{
			js_str=head+js_str+"item_.res=null;";
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
		callAnotherFunc(new Function("item_", js_str), item);
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
				obj=obj[$args.$runjs(m[0].replace("[","").replace("]",""),oobj)];
			}else{
				obj=obj[ss[i]];
			}
		}
		var m=ss[ss.length-1].match(regex);
		if(m!=null){
			obj=obj[ss[i].replace(m[0],"")];
			key=m[0].replace("[","").replace("]","");
			key=$args.$runjs(key,oobj);
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
	$args.$setObj=function(obj,key,fun){
		
		var ld=getLastData(obj,key);
		
		obj=ld.obj;
		key=ld.key;
		var bValue=ld.bValue;
		
		var bind_item=null;
		for(var i in $args.bind){
			if($args.bind[i].obj==obj&&$args.bind[i].key==key){
				bind_item=$args.bind[i];
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
								bind_item.funs[i].$fun(bValue,method,res);
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
										bind_item.funs[i].$fun(bValue,method,res);
									}
									return res;
								}
							});
							
							bValue.__proto__ = newArrProto;
						}
						
						for(var i in bind_item.funs){
							bind_item.funs[i].$fun(bValue);
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
	//互相绑定
	/*$args.bindObj=function(pa,ia,pb,ib){
		$args.$setObj(pa,ia,{pa:pa,ia:ia,pb:pb,ib:ib,
			$fun:function(){
				this.pb[this.ib]=this.pa[this.ia];
			}
		});
		$args.$setObj(pb,ib,{pa:pa,ia:ia,pb:pb,ib:ib,
			$fun:function(){
				this.pa[this.ia]=this.pb[this.ib];
			}
		});
	};*/
	
	//内置对象转换
	var $insWheel=["data","event","init","ready","showTag"];
	for(var i in $insWheel){
		if($args[$insWheel[i]]!=undefined){
			$args["$"+$insWheel[i]]=$args[$insWheel[i]];
			delete $args[$insWheel[i]];
		}
	}
	
	//绑定到this方便调用
	if($args.$data==undefined)$args.$data={};
	
	//隐藏事件集并绑定到this方便调用
	if($args.$event==undefined)$args.$event={};
	
	//所有视图节点
	$args.$node={};
	
	//绑定的数据
	$args.$bind=[];
	
	
	//扫描节点
	$args.$scanNode=function(no,parent,item,data,root){
		
		//获取源DOM节点
		if(item instanceof jQuery)item=item[0];
		
		
		//保存节点，剔除脚本
		if(item.tagName!=undefined&&item.tagName!="SCRIPT"){
			
			//截取特定属性和普通属性
			var tmp_attr=[":id",":if",":for",":none",":set"];
			var reg_e = /@(.+?)/g;
			var reg_a = /a:(.+?)/g;
			var reg_r = /r:(.+?)/g;
			
			var attr=[];
			var wh_attr=[];
			$.each(item.attributes, function() {    
			    if(this.specified) {
					if(tmp_attr.indexOf(this.name)>-1||reg_e.test(this.name)||reg_a.test(this.name)||reg_r.test(this.name)){
						wh_attr.push({
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
			
			//生成虚拟节点
			var node={
				no:parent!=null?parent.no+"-"+no:no,
				tag:item.tagName,
				item:item,
				parent:parent,
				data:data,
				attr:attr,
				wh_attr:wh_attr,
				hasWhTag:wh_attr.length>0?true:false,
				html:$(item).html().replace(/<!--[\s\S]*?-->/g,''),//剔除注释
				child:[],
				hasChild:($(item).children().length>0)?true:false,
				isForChild:data["$isForChild"]!=undefined?true:false,
				isInit:false,
				root:root!=undefined?root:$args,
				update:function(){
					$args.$render(this);
				}
				
			};
			//插入父节点子集
			if(parent!=null)parent.child.push(node);
			//插入公共节点集
			$args.$node[node.no]=node;
			//渲染节点
			$args.$render(node);
			
			return node;
		} 
	};
	
	
	//渲染文本
	$args.$renderTXT=function(val,dd){
		
		var reg_rp = /\{\{(.+?)\}\}/g; 
		var escape={
			//"&nbsp;":"",
			"&lt;":"<",
			"&gt;":">",
			"&amp;":"&",
			"&quot;":"\"",
			"&copy;":"©"
		};
		var m=val.match(reg_rp);  
		if(m!=null){
			for(var j in m){
				var s=m[j].replace(/\{\{/g,'').replace(/\}\}/g,'');
				if(s!=null){
					for(var i in escape){
						var reg_s=new RegExp(i,"g");
						s=s.replace(reg_s,escape[i]);
					}
				}
				var reg=m[j].replace(/\[/g,'\\\[').replace(/\]/g,'\\\]').replace(/\*/g,'\\\*').replace(/\+/g,'\\\+').replace(/\=/g,'\\\=').replace(/\?/g,'\\\?').replace(/\'/g,'\\\'').replace(/\"/g,'\\\"').replace(/\(/g,'\\\(').replace(/\)/g,'\\\)');   
				var v=$args.$runjs(s,dd);
				if(v!=null&&v.replace!=undefined){
					for(var i in escape){
						var reg_s=new RegExp(i,"g");
						v=v.replace(reg_s,escape[i]);
					}
				}
				val=val.replace(new RegExp(reg,"g"),v); 
			}
			return val;
		}
		return null;
	}
	
	//节点渲染
	$args.$render=function(node){
		
		//节点数据集
		var data=node.data;
		//清除，重新渲染
		node.item.innerHTML=node.html;
		//是否已经渲染子节点
		var isRenderChilden=false;
		
		//节点ID :id
		//模板属性 :if,:for,:set,:none
		//事件绑定 @(.+?)
		//属性渲染  a:(.+?)
		//属性懒加载 r:(.+?)
		var reg_e = /@(.+?)/g;
		var reg_a = /a:(.+?)/g;
		var reg_r = /r:(.+?)/g;
		
		//标签顺序
		function wh_attr_sort(name){
			if(name==":id")return 0;
			if(name==":none")return 1;
			if(name==":if")return 2;
			if(name==":for")return 3;
			if(name.indexOf(":a")>-1)return 4;
			if(name.indexOf(":r")>-1)return 5;
			if(name.indexOf(":e")>-1)return 6;
			if(name==":set")return 7;
		}
		//标签渲染排序
		node.wh_attr.sort(function(a,b){
			return wh_attr_sort(a.name)-wh_attr_sort(b.name);
		});
		
		//是否继续渲染
		var goRender=true;
		//所有绑定事件,第一次渲染解绑和后续渲染不解绑
		var binds=[];
		
		//标签渲染
		if(node.hasWhTag)for(var i in node.wh_attr){
			var name=node.wh_attr[i].name;
			var val=node.wh_attr[i].val;
			
			//是否隐藏标签
			if(!$args.showTag)$(node.item).removeAttr(name); 
			
			//标签 标记:id
			if(name==":id"){
				if(val!=undefined&&val!=""){
					node.id=val;
				}
			}
			
			//标签 忽略wh-none
			if(name==":none"){
				$(node.item).hide();
				goRender=false;
			}
			
			//标签 模板:if
			if(name==":if"){
				if(!$args.$runjs(val,data)){
					Wheel_JR.remove(node.item);
					goRender=false;
				}
			}
			
			//标签 模板:for
			if(name==":for"){
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
				
				var arr=$args.$runjs(val,data);
				
				if((arr.constructor!=Array)&&/^[0-9]*$/.test(arr)){
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
									$args.$scanNode(i,node,item,dd,node.root);
								}else{
									var val=$args.$renderTXT(item.nodeValue,dd);
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
									$args.$scanNode(i,node,item,dd,node.root);
								}else{
									var val=$args.$renderTXT(item.nodeValue,dd);
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
								var rs=$args.$scanNode(i,node,item,dd,node.root);
								node.forList[j][i]=rs;
							}else{
								var val=$args.$renderTXT(item.nodeValue,dd);
								if(val!=null)item.nodeValue=val;
								node.forList[j][i]=item;
							}
						});
						n++;
					}
				}
			}
			
			//标签 渲染属性 a:
			var m=name.match(reg_a);
			if(m!=null){
				if(!goRender)continue;
				var name=name.replace("a:",""); 
				var val=$args.$runjs(val,data);
				if(val!=null&&val!=='')$(node.item).attr(name,val);
			}
			
			//标签 监听事件 @
			var m=name.match(reg_e);
			if(m!=null){
				if(!goRender)continue;
				if(node.item.wh_es==undefined)node.item.$es=[];
				if(node.root.$event!=undefined&&node.root.$event[val]!=undefined&&typeof node.root.$event[val] === "function"){
					var e_name=name.replace("@","");
					if(e_name!="init"){//初始化事件
						if(binds.indexOf(e_name)<0){
							$(node.item).unbind(e_name);
							binds.push(e_name);
						}
						node.item.$es[e_name]=val;
						$(node.item).bind(e_name,function(e){
							node.root.$event[this.$es[e.type]](e,node,node.root);
						});
					}
				}
			}
			
			//标签 数据绑定节点更新wh-bind
			if(name==":set"){console.log(node)
				if(!node.hasInit)$args.$setObj(data,val,{$fun:function(v,arr_method,arr_res){
					if(node.isForChild){
						node.data[node.forData.ftx]=node.forData.arr[node.forData[node.forData.fkx]];
						node.update();
					}else{
						var isBindFor=false;
						for(var k in node.wh_attr){
							if(node.wh_attr[k].name==":for"&&node.forList!=undefined&&node.forList.length>0){
								if(arr_method=="push"){
									isBindFor=true;
									var fnx="n"
									var fkx=node.wh_attr[k].val.split(":")[2]||"key"; 
									var ftx=node.wh_attr[k].val.split(":")[1]||"item";
									var arr=$args.$runjs(node.wh_attr[k].val.split(":")[0],node.data);
									var n=arr_res-1;
									var j=n;
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
											var rs=$args.$scanNode(i,node,item,dd,node.root);
											node.forList[j][i]=rs;
										}else{
											var val=$args.$renderTXT(item.nodeValue,dd);
											if(val!=null)item.nodeValue=val;
											node.forList[j][i]=item;
										}
									});
								}
								if(arr_method=="splice"){
									isBindFor=true;
									
									console.log(v,arr_method,arr_res)
								}
							}
						}
						if(!isBindFor)node.update();
						//node.update();
					}
				}});
			}
		}
		
		//属性渲染
		if(goRender)for(var i in node.attr){
			var name=node.attr[i].name;
			var val=node.attr[i].val;
			
			val=$args.$renderTXT(val,data);
			if(val!=null)$(node.item).attr(node.attr[i].name,val);
		}
		
		if(!isRenderChilden&&goRender){
			//纯文本内容渲染
			if(!node.hasChild){
				if(node.html==undefined)return;
				
				var val=$args.$renderTXT(node.html,data);
				if(val!=null)$(node.item).html(val); 
				
			//子节点渲染
			}else if(!isRenderChilden){
				$args.$renderChilds(node,data);
			}
		}
		
		//首次渲染完成
		node.hasInit=true;
		
		//渲染完成事件
		/*if(node.wh_attr.indexOf("@init")>-1){
			node.root.event[node.initEvent[i]](null,node,node.root);
		}*/
		
	}
	
	
	//渲染子节点
	$args.$renderChilds=function(node,data){
		var childs=node.item.childNodes;
		childs.forEach(function(item,i){
			if(item.tagName!=undefined){
				$args.$scanNode(i,node,item,data,node.root);
			}else{
				var val=$args.$renderTXT(item.nodeValue,data);
				if(val!=null)item.nodeValue=val;
			}
		}); 
	};
	
	//简化调用
	window["wt"]=$wheel_tool;
	window["wd"]=$args.$data;
	window["we"]=$args.$event;
	
	//渲染开始
	$renderWheel();
	function $renderWheel(){
		
		//初始化
		if($args.$init!=undefined)$args.$init();
		
		//渲染
		$args.$scanNode(0,null,$("body"),$args.$data);
		
		
		//准备完成
		if($args.$ready!=undefined)$args.$ready();
	}
	
}
window["$$"]=$wheel;




