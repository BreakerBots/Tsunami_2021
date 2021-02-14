// Main.js

const _red = "#b90013";
const _yellow = "#fdb515";
const _error = "#e7880a";

mdc.autoInit();

//Robot Connection
var robotConnected = false;
var robotNameEl = document.querySelector("#robotName");
var enabledSwitchEl = document.querySelector("#enabledSwitch");
var robotConnectedIconEl = document.querySelector("#robotConnectedIcon");
function updateConnection() {
	var xhr = new XMLHttpRequest();
	xhr.timeout = 200;
	xhr.onreadystatechange = function () {
		if (this.readyState !== 4) return;

		//Data Recieved
		if (this.status === 200) {
			var data = JSON.parse(this.responseText);
			robotConnected = true;
			if (robotNameEl.innerText !== data.name) {
				robotNameEl.innerText = data.name;
				enabledSwitchEl.style.display = (data.sim === "true") ? "inline-block" : "none";
				enabledSwitchEl.MDCSwitch.checked = data.enabled === "true";
				robotConnectedIconEl.src = "resources/images/connection.png";
				document.querySelector("#plotterInput").style.display = data.plotterInput === "DISABLED" ? "none" : "block";
			}
		}
	};
	xhr.ontimeout = function () {
		robotConnected = false;
		robotNameEl.innerText = "Disconnected";
		enabledSwitchEl.style.display = "none";
		robotConnectedIconEl.src = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGAAAABgCAQAAABIkb+zAAADpElEQVR4AezXA5BkVxiG4WcY27Zt2yzEtu3ZQmzbtpNCbNspxLYz5h+t92bn7PS9s91V/X5lnmf4t2rFVK1atWoL21cFt7jfhL1UaMv4Q4hKJayoRYhKJaymTYjKJdwnRCUTGjxapoQq4UCVU5UwiVktalVLmdMUasqf0GgBGznAOe7zjhYxynr94mOve9SdrnCq3SyurrwIS2sW47R2zzvJ6urLhbBCBiFlLe61jYnKg9AqBrhWt9pcw/gmrJpMyN6vrrLq+CZ0iBL3tLXk3rxaEglr50AIz1lVbs3oIl1CJBM6RQ571LJKbkpnahdDl0rYMCdCuMt0Smh9P4iMpRC6cyL8YnMDaho3i4ylEjbPjRCuN7lxbCM/C1ESYRFrDt1atnSQU13nEV8OiPC11aWnSZ8QAyEkNK31NbnbryJ9+pymTkITuUckrPRju8FmbtMmkvesafTTrN4XYpAIMIkdPSYS95UljKXFfZ94gF1kfvfmRID5nes3kbAOG8nOBlqTfp2OMQWoy5XAhPb2reh33baS0cF6RT/7w5EagKGE23IlMLGTdSX8Qu9n9BwvxrpeV5vW6NW4IWcCc3u8X8KhMjpN/O/etITsalyZO4HNfZ/w/GTC+RqMrSIIU7s94fkJhJ+sr/8uKoDA5n5Jf3424UUzSOusQggzeDPt+dmEB00ovbuSCVNKbxJPJjw/k3CzWinVWsVZPhcikbCuZw2xsLQahh43TZBOOD3xV+0svwzgwFhPl/Chg5O+xzUu0iT3ZnBWximWTugQwg+ONplBb2LnaC/xzFtzKCH8YYh6g9hyPhHpSyCENy1kUGpwsh4hcie0O1ytgpvdWyJ9KYRRfo+eNa0CW8ZPoqQdacxWHoXwhYUV1AbahSic0GwtBbSXXjFOa/GRdwdE6LGz3GsSCev1iuOsYV6TAK5MJjSnX1FFELrcZRdTG710wnJDCX32gsEkdLvG7AAlEvrsDINJeM1CQC6EHRTccaP9ou6vBnIgFFr2x/+nzAZUGuE0IVyqBiqVcB5QuQRUCVVCUU1uW3d40xdatfvKW+60g8krg7CY+3TJPjbut1h5E+Z3t+hn91qgXAnrahYJa7ZBORIO1CsS1+PA8jww0nf8X+3PMRHDQBAEwQbsUKEZLAOjNQSpLnld1Q6Cad5HyOP9wF5CYC8hsJcQ2EsI7CUE9hICA8KXNxACA8IF5wmBAeGC84TAgPDhfBGTfnf7rbXWWmuttfYHUYTujbsXFxwAAAAASUVORK5CYII=";
	};
	//Send Request
	xhr.open("POST", "/robot/get", true);
	xhr.setRequestHeader('Content-Type', 'application/json');
	xhr.send("");
} 
setInterval(updateConnection, 250);

//Sim
enabledSwitchEl.MDCSwitch.listen('click', (evt) => {
	if (robotConnected)
		setRobotEnabled(enabledSwitchEl.MDCSwitch.checked);
});
function setRobotEnabled(enabled) {
	var xhr = new XMLHttpRequest();
	xhr.timeout = 500;
	xhr.open("POST", "/robot/set", true);
	xhr.setRequestHeader('Content-Type', 'application/json');
	xhr.send(JSON.stringify({ "enabled": enabled }));
}

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