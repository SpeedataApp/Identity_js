Identity Plugin
=================

Identity Plugin for  PhoneGap/cordova

本插件试用与北京思必拓科技股份  带有二代证模块的PDA   
A full example to scan could be:
...

    	function callRead() {
		//alert(scan)
		try {
			// 设置参数 
			var jsonstr=["/dev/ttyMT1",0,94];
			//调用初始化模块函数
			identity.init(winInit,failInit,jsonstr);
			//调用读身份证函数
			identity.read(winRead, failRead);
			
		} catch (err) {
			alert("Error: " + err)
		}
	}
	var winRead = function(result) {
	    var v="";
		if (!result.cancelled) {
			for (var i = 0; i < result.tags.length; i++) {
				v += result.tags[i] + "\n"
			}
			alert(v)
		} else {
			alert("Leitura Cancelada.")
		}
		//读成功后 释放模块
		identity.release(winRead, failRead);
	};
	var failRead = function(error) {
		alert("read failed: " + error)
	};
	
	var winInit = function(result) {
	    alert("init success")
	};
	var failInit = function(error) {
		alert("init failed: " + error)
	};
                   
...
需拷贝base.dat license.lic 至/sdcard/wltlib目录下  参考depend_file.png
 读到的身份证照片  也保存在该目录下
 
 KT50  参数设置
 var jsonstr=["/dev/ttyMT1",0,93];
  KT45Q  参数设置
 var jsonstr=["/dev/ttyMT1",0,94];
