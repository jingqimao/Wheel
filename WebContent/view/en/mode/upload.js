
WheelMode.mode['upload']={

	file:function(url,call,fileName,isMore){
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
	img:function(url,bnt,box,fileName,isMore){
		var bb=$("#"+bnt); 
		var bbx=$("#"+box); 
		var inp=$('<input type="file" accept="image/*"/>');
		inp.hide();
		var r={
			bb:bb,
			bbx:bbx,
			isMore:isMore,
			getImg:function(){ 
				var imgs=[]; 
			  	var imglist=$(r.bbx).find("img");
			  	for(var i in imglist){   
					if((typeof imglist[i])=="object"&&imglist[i].src!=undefined){   
						var src=imglist[i].src.replace(/^http:\/\/[^/]+/, "");
						imgs.push(src);
					}
				}
			  	return imgs; 
			},
			loadImg:function(res){
				if (r.isMore) {
        			if(res.urls!=undefined){ 
        				var urls=res.urls;
        				for(var i in urls){     
        					bbx.append('<dd class="item_img" id="' + res.imgid + '"><div class="operate"><i class="toleft layui-icon" onclick="if($(this.parentNode.parentNode).prev()!=undefined){$(this.parentNode.parentNode).prev().before(this.parentNode.parentNode)}"></i><i class="toright layui-icon" onclick="if($(this.parentNode.parentNode).next()!=undefined){$(this.parentNode.parentNode).next().after(this.parentNode.parentNode)}"></i><i onclick="this.parentNode.parentNode.parentNode.removeChild(this.parentNode.parentNode)" class="close layui-icon"></i></div><img src="' + urls[i] + '" class="img" "><input type="hidden" name="dzd_img[]" value="' + urls[i] + '" /></dd>');
        				}
        			}
        		}else{
        			bbx.html('<dd class="item_img" id="' + res.imgid + '"><div class="operate"><i onclick="this.parentNode.parentNode.parentNode.removeChild(this.parentNode.parentNode)" class="close layui-icon"></i></div><img src="' + res.data.src + '" class="img" "><input type="hidden" name="dzd_img" value="' + res.data.src + '" /></dd>');
        		}
			},
			clear:function(){
				bbx.html("");
			}
		};
		
		if(isMore){
			inp.attr("multiple","multiple");   
		}
		bb.after(inp); 
		bb.click(function(){ 
			inp.click();
		}); 
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
		        		r.loadImg(res);
		        		/*if(isMore){ 
		        			for(var i in res.data.src){
		        				bbx.append('<img src="'+res.data.src[i]+'" />');
		        			}
		        		}else{
		        			bbx.html("");
			        		bbx.append('<img src="'+res.data.src+'" />');
		        		}*/
		        	}
		        }
			})
		});
		return r;
	}
}