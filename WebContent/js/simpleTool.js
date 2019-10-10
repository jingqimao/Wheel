/**
 * JS简化工具
 */

var STool={
		
		//===================基本简化
		//同步POST连接服务
		get:function(url,data){
			var res=$.ajax({url: url,data:data,async: false,type:"POST"}).responseText;
			res=res.replace(/\\/g,"\\\\"); 
			if(res!=undefined&&STool.isJSON(res)){  
				return JSON.parse(res);  
			}else{
				return res; 
			}
		},
		//模板引擎<#=#>
		mode:function(arg){ 
			var tmp=arg.tmp;
			if(arg.tmp_sel!=undefined){ 
				tmp=$(arg.tmp_sel).html();
			}else if(typeof arg.tmp=="object"){   
				tmp=$(arg.tmp).html();
			}
			var data=arg.data;
			
			var body="var by=\"\";by+=\"";
			var dd = /<#=([\s\S]+?)#>/g;
			var bb = /<#([\s\S]+?)#>/g; 
			
			var escaper = /\\|'|\r|\n|\t|\u2028|\u2029|"/g;   
			var escapes = {
				    "'":      "'",
				    '\\':     '\\',
				    '\r':     'r',
				    '\n':     'n',
				    '\t':     't',
				    '\u2028': 'u2028',
				    '\u2029': 'u2029',
				    '"':	  '"'
				  }; 
			
			tmp=tmp.replace(/&lt;/g,"<").replace(/&gt;/g,">");    
			
			tmp=tmp.replace(escaper, function(match) { return '\\' + escapes[match]; });
			
			var ddx=tmp.match(dd);
			for(var i in ddx){ 
				var n=ddx[i].substring(3,ddx[i].length-2);          
				tmp=tmp.replace(ddx[i],"\";by+="+n+";by+=\"");         
			}
			
			var bbx=tmp.match(bb);
			for(var i in bbx){ 
				var n=bbx[i].substring(2,bbx[i].length-2);   
				tmp=tmp.replace(bbx[i],"\";"+n+"by+=\"");     
			}
			
			
			body+=tmp;
			body+="\";return by;"; 
			
			var ff=new Function("data",body);         
			
			return ff(data);
		},
		//MVC/MV渲染
		mvc:function(arg){
			arg.h=$(arg.view).html();
			render(arg); 
			function render(ax){
				var h=ax.h,v=ax.view,a=ax;
				var m=null; 
				if(a.get!=undefined){   
					m=STool.get(a.get.url,a.get.data);  
				}else{
					m=a.data; 
				}
				if(a.tmp!=undefined)m=a.tmp(m); 
				$(a.view).html(STool.mode({tmp:h,data:m}));
				if($(v).attr("m-hide")!=undefined)$(v).removeAttr("m-hide"); 
				if($(v).hasClass("m-hide")){
					$(v).removeClass("m-hide"); 
					if($(v).attr("class")=="")$(v).removeAttr("class"); 
				}
				if(a.res!=undefined)m=a.res(m);  
			}
			return {
				update:function(d){
					if(d!=undefined){
						arg.tmp=d.tmp||arg.tmp; 
						arg.res=d.res||arg.res; 
						if(d.get==undefined&&d.data!=undefined){   
							delete arg.get; 
						}
						arg.get=d.get||arg.get;
						arg.data=d.data||arg.data;
					} 
					render(arg);
					
				}
			}
		},
		//CVM/VM渲染
		cvm:function(arg){
			for(var i in arg.input){
				if($(arg.input[i]).attr("type")=="checkbox"){ 
					arg.mode[i]=$(arg.input[i]).is(":checked");
				}else if($(arg.input[i]).attr("type")=="radio"){
					if(arg.mode[$(arg.input[i]).attr("name")]==undefined){
						arg.mode[$(arg.input[i]).attr("name")]=$('input:radio[name="'+$(arg.input[i]).attr("name")+'"]:checked').val();
						if(arg.mode[$(arg.input[i]).attr("name")]==undefined){
							arg.mode[$(arg.input[i]).attr("name")]=null; 
						}
					}
				}else{
					arg.mode[i]=$(arg.input[i]).val();   
				}
				
				$(arg.input[i]).on('input propertychange', function(){
					var index="";
					for(var i in arg.input){
						if($(this)[0]==$(arg.input[i])[0]){
							index=i;
							break;
						}
					}
					
					var val="";
					if($(this).attr("type")=="checkbox"){  
						val=$(this).is(":checked");
					}else if($(this).attr("type")=="radio"){
						val=$('input:radio[name="'+$(this).attr("name")+'"]:checked').val()
					}else{
						val=$(this).val();
					}
					if(arg.tmp!=null)val=tmp(val);
					if($(this).attr("type")=="radio"){ 
						arg.mode[$(this).attr("name")]=val;
					}else{
						arg.mode[index]=val;
					}
					if(arg.res!=null)arg.res(arg.mode,{index:index,item:this,val:val});   
				})
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
		getTime:function(str){
			str=str.replace(/-/g,"/");
			return new Date(str).getTime();
		},
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
		
		
		
		
		
		
		//===================扩展简化
		//弹出上传输入
		upload:function(url,call,fileName,isMore){
			var inp=$('<input type="file" accept="image/*"/>');
			if(isMore!=undefined&&isMore){
				inp.attr("multiple","multiple");   
			}
			$("body").append(inp); 
			inp.click();
			inp.change(function(){  
				if(inp[0].files[0]==undefined)return; 
				var formData = new FormData(); 
				if(!isMore){
					var f=inp[0].files[0];
					formData.append(fileName,inp[0].files[0]);   
				}else{   
					for(var i in inp[0].files){ 
						if((typeof inp[0].files[i])=="object"){
							formData.append(fileName,inp[0].files[i]);
						}
					}
				}
				$.ajax({
			        type: "POST",
			        url: url , 
			        data: formData,
			        cache: false,
			        processData: false,
			 	    contentType: false, 
			        success:function(d){
			        	var res=JSON.parse(d);
			        	if(res.msg=="suc"){ 
			        		call(res);
			        	}
			        }
				})
				inp.remove();
			});
		},
		setpos:function(p,item,x,y){  
			$(item).css("position","relative");
			$(p).scroll(function(){ 
				if($(p).scrollTop()>y){ 
					$(item).css("top",($(p).scrollTop()-y)+"px");
				}else{
					$(item).css("top","0px");  
				}
				if($(p).scrollTop()>x){ 
					$(item).css("left",($(p).scrollLeft()-x)+"px");
				}else{
					$(item).css("left","0px");
				}
			});
		}
}

//初始化隐藏模板元素
var link = document.createElement('style');  
link.type='text/css';
link.innerHTML='@charset "UTF-8";[m-hide],.m-hide{visibility: hidden; !important;}'; 
document.getElementsByTagName('head')[0].appendChild(link);