/**
 * JS格式化工具
 */

var FTool={
		fileSize:function(value){
		    if(null==value||value==''){
		        return "0 Bytes"; 
		    }
		    var unitArr = new Array("Bytes","KB","MB","GB","TB","PB","EB","ZB","YB");
		    var index=0,
		        srcsize = parseFloat(value);
		    index=Math.floor(Math.log(srcsize)/Math.log(1024));
		    var size =srcsize/Math.pow(1024,index);
		    
		    size=size.toFixed(2);
		    return size+unitArr[index];
		},
		ago:function(time){
			var now = new Date().getTime();   
		    var dv = now - new Date(str.replace(/-/g,"/")).getTime();
		    if(dv<0)return;
		    var res=[{name:"刚刚",time:1},
		    		{name:"n分钟前",time:60},
		    		{name:"n小时前",time:60*60},
		    		{name:"n天前",time:60*60*24},
		    		{name:"n周前",time:60*60*24*7},
		    		{name:"半个月前",time:60*60*24*15},
		    		{name:"n个月前",time:60*60*24*30},
		    		{name:"半年前",time:60*60*24*182},
		    		{name:"n年前",time:60*60*24*365}];
		    
		    var n=0;
		    dv=dv/1000;
			for(var i=0;i<res.length;i++){
				if(i<res.length-1&&dv>=res[i].time&&dv<res[i+1].time){
					return res[i].name.replace("n",parseInt(dv/res[i].time));
				}else if(dv>=res[res.length-1].time){
					return res[res.length-1].name.replace("n",parseInt(dv/res[res.length-1].time));
				}
			}
		}
}