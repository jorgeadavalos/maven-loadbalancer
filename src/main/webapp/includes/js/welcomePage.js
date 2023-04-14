document.onclick = function(ev) {
	if (ev.explicitOriginalTarget.tagName == "CANVAS") {
		var url = "graphBarDetail.xhtml"
		popUp(url,"popDetail");
	}
}
function showNav(navName) {
	var el = document.getElementById("navigationframe");
	if (el == null) {
		if (navName == null) navName = "mainNavigation.xhtml";
		var el2 = document.createElement("div"); 
		el2.id = "dynamicDIV";
		document.body.appendChild(el2);
		ajaxDestEl = el2;
		var obj = new ajaxObj(navName,el2);
		obj.ajaxFunc = function(objResp) {
			objResp.ajaxDestEl.innerHTML = objResp.ajaxmsg;
			fadeIn();			
		}
		ajaxRequest(obj);
	}
	$("nav").toggleClass("active-nav");
}
function fadeOut() {	
	if (document.getElementById('idFadeOut'))
		document.getElementById('idFadeOut').style.display = 'none';
	
	var el = document.getElementById("idFadeIn");
	el.style.display = 'block';
	el.style.left= "0px";
	$("nav").toggleClass("active-nav");
	return false;	
}
function fadeIn() {	
	if (document.getElementById('idFadeIn'))
		document.getElementById('idFadeIn').style.display = 'none';
	
	var el = document.getElementById("idFadeOut");
	el.style.display = 'block';
	el.style.left= "0px";
	$("nav").toggleClass("active-nav");
	return false;
}
function findElement(nam) {
	var namVar = document.getElementById(nam);
	if (namVar) return namVar;
	for (var i=0;i<document.forms.length;i++) {
		namVar = document.getElementById(document.forms[i].id+":"+nam);
		if (namVar) return namVar;
	}
	var elements = document.getElementsByTagName('*');
	if (elements) {
		for (var i = 0;i<elements.length;i++) {
			if (elements[i].id.indexOf(nam) != -1) {
				return elements[i];
			}
		}
	}
	return null;
}
function expand(parm,ndx) {
	var el = document.getElementById(parm);
	var plusImg = document.getElementById('plusImg'+ndx);
	var minusImg = document.getElementById('minusImg'+ndx);
	if (el.style.display == "block" || el.style.display == "inherit" ) {
		el.style.display = "none";
		minusImg.style.display = "none";
		plusImg.style.display = "inherit";
	} else {
		el.style.display = "block"; 
		minusImg.style.display = "inherit";
		plusImg.style.display = "none";
	}
}
function popUp(url,name) {
	genPopUp = window.open(url,name,"toolbar=no,location=no,scrollbars=yes,directories=no,status=no,menubar=no,resizable=yes,width=750,height=380");
	genPopUp.focus();
}
function openPage(pageName) {
	var genPopUp = window.open(pageName,"_parent");
 }
function downloadJar() {
	url = "docservlet?resource=resource&startWith=lbinstance&endWith=.jar";
	var mywind=window.open(url,'_blank','titlebar=no,status=no,menubar=no');
}
function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
	results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}
function setjsonitem() {
	var value = getParameterByName('jsonitem');
	var obj = JSON.parse(value);
	var fileDesc = document.getElementById("fileDesc");
	if (obj != null) fileDesc.innerText = obj.desc;
	var el = document.getElementById("jsonitem");
	if (el != null) el.value = value;
} 
blinkerTID	=  null;
fld 		= null;
function blinker(app) {
	if (fld == null) {
		if (blinkerTID != null) window.clearInterval(blinkerTID);
		blinkerTID = null;
		return;
	}
    fld.style.color = fld.style.color == "red" ? "blue" : "red";
}
function setMsg(msg,fldName) {
	fld = findElement(fldName); 
	if (fld == null) return false;
	fld.setText = msg;
	blinkerTID = setInterval('blinker("#")',500);
	return false;
}
function showModal(parm) {
	var modal = document.getElementById(parm);
    modal.style.display = "block";
}
function hideModal(parm) {
	var modal = document.getElementById(parm);
	modal.style.display = "none";
}
function bldMenu(parm,appList) {
	tblInstances();
	formatData();
	var parent = document.getElementById(parm);
	if (parent == null) return;
	
	appArray = appList.split(" ");
	for (var i=0;i<appArray.length;i++) {
		var elem = document.createElement('li');
		var elemA = document.createElement('a');
		elemA.className = "expand";
		elemA.href = "loadinstance/"+appArray[i];
		elemA.innerText = appArray[i];
		elem.appendChild(elemA);
		parent.insertBefore(elem, parent.childNodes[1]);
	}
}
function tblInstances() {
	if (document.getElementById("formname") == null) return;
	var elemId = document.getElementById("formname").value+":"+"tblInstances"
	var parent = document.getElementById(elemId);
	if (parent == null) return;
	for (var i=1; i<parent.rows.length;i++) {
		var text = parent.rows[i].cells[3].innerText;
		parent.rows[i].style.backgroundColor = "red";
		if (text.indexOf("instanceUp=true") != -1 && text.indexOf("serverUp=true") != -1) parent.rows[i].style.backgroundColor = "#88d976"; //"lightgreen";
		else if (text.indexOf("serverUp=true") != -1) parent.rows[i].style.backgroundColor = "yellow";
	}
}
function formatData() {
	var tables = document.getElementsByName("perfData");
	var giga = 1000000000;
	var tag = "XX";
	if (tables == null ) return;
	for (var i=0;i<tables.length;i++) {
		for (var j=0;j<tables[i].rows.length;j++) {
			var cells = tables[i].rows[j].cells;
			tag = "string";
			for (var k=0;k<cells.length;k++) {
				if (cells[k].tagName == "TH") {
					if (cells[k].innerText.indexOf("Size") != -1) tag = " G";
					else if (cells[k].innerText.indexOf("CpuTime") != -1) tag = " Ns";
					else if (cells[k].innerText.indexOf("CpuLoad") != -1) tag = " %";
					else if (cells[k].innerText.indexOf("Time") != -1) tag = " Ms";
					continue;
				}
				if (tag != "string") {
					if (tag == " Ms")
						cells[k].innerText = (cells[k].innerText/1.0).toFixed(2) + tag;
					else if (tag == " %")
						cells[k].innerText = (cells[k].innerText/100.0).toFixed(2) + tag;
					else cells[k].innerText = (cells[k].innerText/giga).toFixed(4) + tag;
				}
			}
		}
	}
}
function instanceData(instanceKey) {
	var formname = document.getElementById('formname').value;
	var url = "instanceData.xhtml?application="+document.getElementById(formname+':application').selectedOptions[0].value;
	url += "&instancekey="+instanceKey;
	window.open(url,"_parent");
 }
function instanceDataGraph(instanceKey) {
	var formname = document.getElementById('formname').value;
	var url = "graphBarRoot.xhtml?application="+document.getElementById(formname+':application').selectedOptions[0].value;
	url += "&instanceurl="+instanceKey;
	var obj = new ajaxObj("ajaxs/resetGraphBean.xhtml");
	obj.ajaxFunc = function(objResp) {
		popUp(url,"popRoot");
	}
	ajaxRequest(obj);
 }
