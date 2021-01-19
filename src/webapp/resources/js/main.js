// Main.js

const _red = "#b90013";
const _yellow = "#fdb515";
const _error = "#e7880a";

mdc.autoInit();

var robotConnected = false;
function updateConnection() {
	var xhr = new XMLHttpRequest();
	xhr.timeout = 200;
	xhr.onreadystatechange = function () {
		if (this.readyState != 4) return;

		//Data Recieved
		if (this.status == 200) {
			var data = JSON.parse(this.responseText);
			robotConnected = true;
			document.querySelector("#robotName").innerText = data.name;
			document.querySelector("#robotConnectedIcon").src = "resources/images/connection.png";
		}
	};
	xhr.ontimeout = function () {
		robotConnected = false;
		document.querySelector("#robotName").innerText = "";
		document.querySelector("#robotConnectedIcon").src = "resources/images/no_connection.png";
	}
	//Send Request
	xhr.open("POST", "/robot/get", true);
	xhr.setRequestHeader('Content-Type', 'application/json');
	xhr.send("");
} 
setInterval(updateConnection, 250);

String.prototype.hashCode = function () {
	var hash = 0, i, chr;
	if (this.length === 0) return hash;
	for (i = 0; i < this.length; i++) {
		chr = this.charCodeAt(i);
		hash = ((hash << 5) - hash) + chr;
		hash |= 0;
	}
	return hash;
};