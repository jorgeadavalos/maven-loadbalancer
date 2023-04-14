ajaxObjs = {};
function ajaxObj(name) {
	this.ajaxURL     = name;
	this.ajaxDestEl  = document.createElement("div");
	this.ajaxDone    = false;
	this.ajaxError   = false;
	this.ajaxFunc    = null;
	this.ajaxmsg;
}
function ajaxRequest(obj) {
	var pageRequest;
	if (window.XMLHttpRequest) pageRequest = new XMLHttpRequest();
	else if (window.ActiveXObject)  pageRequest = new ActiveXObject("Microsfot.XMLHTTP"); 
		 else
			return;
	
	var readyStateChange = function() {
		if (pageRequest.readyState == 4 ) {
			if (pageRequest.status != 200) obj.ajaxError = true;
			serverGetData(pageRequest.responseText,obj);
		}
	};
	pageRequest.onreadystatechange = readyStateChange;
	
	obj.ajaxDone  = false;
	pageRequest.open('POST',obj.ajaxURL,true);
	pageRequest.send();
}
function serverGetData(msg,obj) {
	obj.ajaxDone  = true;
	obj.ajaxDestEl.innerHTML = msg;
	obj.ajaxmsg = msg;
	if (obj.ajaxFunc != null)
		obj.ajaxFunc(obj);
}