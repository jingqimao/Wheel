/**
 * JS模块化工具
 */

var WheelMode={
		src:"/view/def/js/mode/",
		css_src:"/view/def/css/mode/",
		mode:[],
		css:[],
		init:function(src){ 
			WheelMode.src=src;
		},
		load:function(modeName,call){ 
			modeName=modeName.split(","); 
			var mload={n:0,m:0,mm:[]}; 
			for(var i in modeName){
				if(WheelMode.mode[modeName[i]]==undefined){  
					var script=document.createElement("script"); 
					script.type="text/javascript";
					script.src=WheelMode.src+modeName[i]+".js";
					document.getElementsByTagName('head')[0].appendChild(script);
					
					mload.n++;
					mload.mm.push(modeName[i]);  
					script.onload=function(){ 
						mload.m++;
						if(mload.n==mload.m){
							for(var j in mload.mm){  
								if(WheelMode.css[mload.mm[j]]==undefined&&WheelMode.mode[mload.mm[j]].css!=undefined&&WheelMode.mode[mload.mm[j]].css==true){
									var link = document.createElement('link');  
							        link.type='text/css';
							        link.rel = 'stylesheet';
							        link.href = WheelMode.css_src+modeName[i]+".css";
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
}