(function() {
    var origOpen = XMLHttpRequest.prototype.open;
    XMLHttpRequest.prototype.open = function() {
        this.addEventListener('load', function() {
            comAssocJad.AJAXResponse(this.responseText);
        });
        origOpen.apply(this, arguments);
    };
})();
window.addEventListener("load", function(){
  	comAssocJad.bldData();
});
var comAssocJad = {
		loadBalancerURL: "",
		taskdone: false,
		key: window.location.protocol+"//"+window.location.host,
		networkTimeId: "networkBgnTime",
		application: "",
		URLEndTime: "",
		script: "",

	bldURLEndTime: function() {
		var date = new Date(); var timestamp = date.getTime();
		var ndx1 = window.location.pathname.lastIndexOf("/");
		var pathname = window.location.pathname;
		if(ndx1 > 0) pathname = window.location.pathname.substring(0,ndx1);
		comAssocJad.URLEndTime = pathname+"/loadEndNetworkTime?networkEndTime="+timestamp+"&instancename="+pathname;
		comAssocJad.script = "&script="+window.location.pathname.substring(ndx1);
	},
	loadedcmd: function() {
		comAssocJad.bldURLEndTime();
		var obj = new comAssocJad.AjaxObj(comAssocJad.URLEndTime+comAssocJad.script);
		obj.ajaxFunc = function(objResp) {
			comAssocJad.taskdone = true;
		};
		comAssocJad.ajaxRequest(obj);
	},
	AJAXResponse: function(text) {
		if (text.indexOf(comAssocJad.networkTimeId) == -1) return;
		var div = document.createElement('div');
		div.innerHTML = text;
		var networkParms ="";
		for (var i=0;i<div.children.length;i++) {
			if (typeof div.children[i].name === 'undefined') continue;
			networkParms += "&"+div.children[i].name+"="+div.children[i].value;
		}
		comAssocJad.bldURLEndTime();
		var obj = new comAssocJad.AjaxObj(comAssocJad.URLEndTime+networkParms);
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
