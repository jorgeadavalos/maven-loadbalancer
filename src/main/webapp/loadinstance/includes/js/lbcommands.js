var comAssocJad = {
		loadBalancerURL: "",
		taskdone: false,
		key: window.location.protocol+"//"+window.location.host,
		networkTimeId: "networkBgnTime",
		application: "",

	loadedcmd: function() {
		var date = new Date(); var timestamp = date.getTime();
		var ndx1 = window.location.pathname.lastIndexOf("/");
		var pathname = window.location.pathname.substring(0,ndx1);
		var url = pathname+"/loadEndNetworkTime?networkEndTime="+timestamp+"&script="+window.location.pathname.substring(ndx1);
		url += "&instancename="+pathname
		var obj = new comAssocJad.AjaxObj(url);
		obj.ajaxFunc = function(objResp) {
			comAssocJad.taskdone = true;
			window.onclick = null;
		};
		comAssocJad.ajaxRequest(obj);
	},
	AJAXResponse(text) {
		if (text.indexOf(comAssocJad.networkTimeId) == -1) return;
		var div = document.createElement('div');
		div.innerHTML = text;
		var networkParms ="";
		for (var i=0;i<div.children.length;i++) {
			if (typeof div.children[i].name === 'undefined') continue;
			networkParms += "&"+div.children[i].name+"="+div.children[i].value;
		}
		var date = new Date(); var timestamp = date.getTime();
		networkParms += "&networkEndTime="+timestamp;
		var url = "/loadEndNetworkTime?networkEndTime="+timestamp+networkParms;
		var obj = new comAssocJad.AjaxObj(url);
		comAssocJad.ajaxRequest(obj);
	},
	bldData: function() {
    	if (!comAssocJad.taskdone) comAssocJad.loadedcmd();
	},
	AjaxObj: function(url) {
		this.ajaxURL     = url;
		this.ajaxError   = false;
		this.ajaxFunc    = null;
		this.ajaxmsg;
	},
	ajaxRequest(obj) {
		pageRequest = new XMLHttpRequest();
		if (window.ActiveXObject)  pageRequest = new ActiveXObject("Microsfot.XMLHTTP"); 

		pageRequest.onreadystatechange = function() {
			if (this.readyState == 4 ) {
				if (this.status != 200) obj.ajaxError = true;
				obj.ajaxmsg = this.responseText;
				if (obj.ajaxFunc != null)
					obj.ajaxFunc(obj);
			}
		};
		pageRequest.open('GET',obj.ajaxURL,true);
		pageRequest.send();
	},
}
window.onclick = function(ev) {
	comAssocJad.bldData();
}