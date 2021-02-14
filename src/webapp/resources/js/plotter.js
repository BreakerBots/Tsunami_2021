// Plotter.js

//Setup
let plotterElement = document.querySelector('#plotterWindow');
let plotterDesmos = Desmos.GraphingCalculator(plotterElement, {
	keypad: false,
	expressions: true,
	settingsMenu: false,
	expressionsTopbar: false,
	trace: true,
	expressionsCollapsed: true
});
let plotterRatioNow = plotterElement.clientWidth / plotterElement.clientHeight;
plotterDesmos.setMathBounds({
	left: -30 * (plotterRatioNow),
	right: 30 * (plotterRatioNow),
	bottom: -30 * (plotterRatioNow),
	top: 30 * (plotterRatioNow)
});

let plotterColorWrapper = document.querySelector("#plotterColorWrapper");

//Visuals
function plotterReset() {
	plotterDesmos.setBlank();
	plotterColorWrapper.innerHTML = "";
	document.querySelector("#plotter-input-points-wrapper").innerHTML = "";
}

function plotterAddPointToTable(x, y, color) {
	var exp;
	plotterDesmos.getExpressions().forEach(function (e) {
		if (e.id === ("table" + color.replace(/#/g, "-")))
			exp = e;
	});

	if (!exp) {
		plotterAddTable(x, y, color);
	}
	else {
		exp.columns[0].values.push(x);
		exp.columns[1].values.push(y);
		plotterDesmos.setExpression(exp);
	}
}

function plotterAddTable(x, y, color) {
	plotterDesmos.setExpression({
		id: "table" + color.replace(/#/g, "-"),
		type: 'table',
		columns: [
			{
				latex: 'x',
				values: [x],
				lines: true,
				points: false,
				color: color
			},
			{
				latex: 'y',
				values: [y],
				lines: true,
				points: false,
				color: color,
				hidden: false
			}
		]
	});
	plotterColorWrapper.innerHTML += `
	<div id="plotter-color-COLOR" class="plotter-color mdc-elevation--z2">
		<i onclick="plotterHideShowColor('COLOR', this)" oncontextmenu="plotterRemoveTable('COLOR', this)" style="background-color: COLOR" class="plotter-color-icon mdc-icon-toggle" data-mdc-auto-init="MDCIconToggle"></i>
	</div>
	`.replace(/COLOR/g, color);
}

function plotterRemoveTable(color, element) {
	plotterDesmos.getExpressions().forEach(function (e) {
		if (e.id === ("table" + color.replace(/#/g, "-")))
			plotterDesmos.removeExpression(e);
	});
	if (element.parentNode)
		element.parentNode.remove();
}

//Hide/Show Color
function plotterHideShowColor(color, element) {
	var exp;
	plotterDesmos.getExpressions().forEach(function (e) {
		if (e.id === ("table" + color.replace(/#/g, "-")))
			exp = e;
	});
	if (exp) {
		exp.columns[1].hidden = !exp.columns[1].hidden;
		plotterDesmos.setExpression(exp);
		if (exp.columns[1].hidden) {
			element.style.backgroundColor = "";
		}
		else {
			element.style.backgroundColor = color;
		}
	}
}

//Pause/Play
function handlePlotterPausePlay() {
	plotterRunning = !plotterRunning;
	document.querySelector("#plotter-pause-play").src = plotterRunning ? "resources/images/pause.png" : "resources/images/play.png";
}
window.addEventListener("keydown", handlePlotterKeyPressed, false);
function handlePlotterKeyPressed(evt) {
	if (location.hash == "#plotter") {
		if (evt.code == "Space") {
			handlePlotterPausePlay();
			evt.preventDefault();
		}
		else if (evt.key == "s" && evt.ctrlKey) {
			handleSavePlotter();
			evt.preventDefault();
		}
	}
}

//Save Plotter
function handleSavePlotter() {
	if (plotterRunning)
		handleTunerPausePlay();

	let csvContent = "data:text/csv;charset=utf-8,";

	var expressions = plotterDesmos.getExpressions();

	//remove empty tables
	for (var i = 0; i < expressions.length; i++) {
		if (!expressions[i].columns || expressions[i].columns[0].values.length < 1) {
			expressions.splice(i, 1);
			i--;
		}
	}

	var biggestTableSize = 0;
	expressions.forEach(function (exp) {
		csvContent += exp.id + "_x," + exp.id + "_y,";
		if (exp.columns[0].values.length > biggestTableSize)
			biggestTableSize = exp.columns[0].values.length;
	});
	csvContent = csvContent.slice(0, -1) + "\r\n";

	for (var i = 0; i < biggestTableSize; i++) {
		expressions.forEach(function (exp) {
			if (exp.columns[0].values.length > i)
				csvContent += exp.columns[0].values[i] + "," + exp.columns[1].values[i] + ",";
			else csvContent += ",,";
		});
		csvContent = csvContent.slice(0, -1) + "\r\n";
	}

	var encodedUri = encodeURI(csvContent);
	var link = document.createElement("a");
	link.setAttribute("href", encodedUri);
	link.setAttribute("download", "breakerboard_plotter_data_" + Math.round(Math.random() * 100000) + ".csv");
	document.body.appendChild(link);

	link.click();
}

//Plotter Inputs
const plotterInputElementUI = `
<div class="plotter-input-element plotter-input-coord mdc-text-field mdc-text-field--outlined"
	 data-mdc-auto-init="MDCTextField">
	<input class="mdc-text-field__input" type="number" step="0.01">
		<div class="mdc-notched-outline">
			<div class="mdc-notched-outline__leading"></div>
			<div class="mdc-notched-outline__notch">
				<label class="mdc-floating-label">X</label>
			</div>
			<div class="mdc-notched-outline__trailing"></div>
		</div>
</div>
<div class="plotter-input-element plotter-input-coord mdc-text-field mdc-text-field--outlined"
	 data-mdc-auto-init="MDCTextField">
	<input class="mdc-text-field__input" type="number" step="0.01">
		<div class="mdc-notched-outline">
			<div class="mdc-notched-outline__leading"></div>
			<div class="mdc-notched-outline__notch">
				<label class="mdc-floating-label">Y</label>
			</div>
			<div class="mdc-notched-outline__trailing"></div>
		</div>
</div>
<div class="plotter-input-element plotter-input-coord mdc-text-field mdc-text-field--outlined"
	 data-mdc-auto-init="MDCTextField">
	<input class="mdc-text-field__input" type="number" step="0.01">
		<div class="mdc-notched-outline">
			<div class="mdc-notched-outline__leading"></div>
			<div class="mdc-notched-outline__notch">
				<label class="mdc-floating-label">Angle</label>
			</div>
			<div class="mdc-notched-outline__trailing"></div>
		</div>
</div>
<div class="plotter-input-remove">
	<i onClick="removePlotterInputElement(this.parentElement.parentElement)" class="mdc-icon-toggle" data-mdc-auto-init="MDCIconToggle">
		<img src="resources/images/remove.png" style="width: 100%;"/>
	</i>
</div>`;
let plotterInputIDCounter = 0;
function addPlotterInputElement() {
	let trajectoryData = getPlotterTrajectoryData();
	let element = document.createElement("div");
	element.innerHTML = plotterInputElementUI;
	element.className = "plotter-input-element-parent";
	element.id = "plotter-input-element-parent-" + plotterInputIDCounter;
	document.querySelector("#plotter-input-points-wrapper").appendChild(element);
	mdc.autoInit(element);

	element.children[0].MDCTextField.input_.focus();
	setTimeout(function () {
		element.children[0].MDCTextField.input_.select();
	}, 10);

	//Update on Text Field Input
	element.children[0].oninput = (evt) => {
		let id = Number(evt.target.parentElement.parentElement.id.replace("plotter-input-element-parent-",""));
		plotterDesmos.setExpressions([
			{id: 'x_'+id, latex: 'x_'+id+' = ' + evt.target.parentNode.MDCTextField.value}
		]);
		plotterRequestSendTrajectory();
	};
	element.children[1].oninput = (evt) => {
		let id = Number(evt.target.parentElement.parentElement.id.replace("plotter-input-element-parent-",""));
		plotterDesmos.setExpressions([
			{id: 'y_'+id, latex: 'y_'+id+' = ' + evt.target.parentNode.MDCTextField.value}
		]);
		plotterRequestSendTrajectory();
	};
	element.children[2].oninput = () => {
		plotterRequestSendTrajectory();
	}

	//Enter Button
	for (let c = 0; c < 3; c++) {
		element.children[c].onkeypress = (evt) => {
			if (evt.key === "Enter")
				addPlotterInputElement();
		}
	}

	//Starting Position
	let startX = 0;
	let startY = 0;
	let startAngle = 0;
	if (trajectoryData.points.length > 0) {
		startX = Number(trajectoryData.points[trajectoryData.points.length - 1][0]) + 5;
		startY = Number(trajectoryData.points[trajectoryData.points.length - 1][1]) + 5;
		startAngle = Number(trajectoryData.points[trajectoryData.points.length - 1][2]);
	}
	element.children[2].MDCTextField.value = startAngle;

	//Desmos Point
	plotterDesmos.setExpressions([
		{id: 'x_'+plotterInputIDCounter, latex: 'x_'+plotterInputIDCounter+' = '+startX},
		{id: 'y_'+plotterInputIDCounter, latex: 'y_'+plotterInputIDCounter+' = '+startY},
		{id: 'point_'+plotterInputIDCounter, latex: '(x_'+plotterInputIDCounter+', y_'+plotterInputIDCounter+')'}
	]);
	plotterDesmos.HelperExpression({latex: 'x_'+plotterInputIDCounter+''}).observe('numericValue', function(type, el) {
		element.children[0].MDCTextField.value = el[type];
		plotterRequestSendTrajectory();
	});
	plotterDesmos.HelperExpression({latex: 'y_'+plotterInputIDCounter+''}).observe('numericValue', function(type, el) {
		element.children[1].MDCTextField.value = el[type];
		plotterRequestSendTrajectory();
	});

	plotterInputIDCounter++;
}
function removePlotterInputElement(element) {
	let id = Number(element.id.replace("plotter-input-element-parent-",""));
	element.remove();
	plotterDesmos.removeExpressions([
		{id: 'x_'+id},
		{id: 'y_'+id},
		{id: 'point_'+id}
	]);
}
function getPlotterTrajectoryData() {
	let trajectoryData = {
		reversed: plotterInputReversedEl.MDCTextField.value,
		points: []
	};

	let children = document.querySelector("#plotter-input-points-wrapper").children;
	for (let c = 0; c < children.length; c++) {
		trajectoryData.points.push([
			children[c].children[0].MDCTextField.value,
			children[c].children[1].MDCTextField.value,
			children[c].children[2].MDCTextField.value
		]);
	}
	return trajectoryData;
}
function savePlotterInput() {
	let trajectoryData = getPlotterTrajectoryData();
	let code = "run(<or>new</or> DriveTrajectoryAction(<or>" + trajectoryData.reversed + "</or><br>";
	for (let p = 0; p < trajectoryData.points.length; p++) {
		code += "&emsp;<or>new</or> Position(<bl>" +
			trajectoryData.points[p][0] + "</bl><or>,</or> <bl>" +
			trajectoryData.points[p][1] + "</bl><or>,</or> <bl>" +
			trajectoryData.points[p][2] + "</bl>)" +
			(p==trajectoryData.points.length-1?"":"<or>,</or>")+"<br>";
	}
	code += "));";
	document.querySelector(".mdc-dialog__content").innerHTML = code;
	document.querySelector(".mdc-dialog").MDCDialog.open();
}
let plotterInputReversedEl = document.querySelector("#plotter-input-reversed");
plotterInputReversedEl.MDCTextField.value = "false";
plotterInputReversedEl.oninput = () => { plotterRequestSendTrajectory(); }

//HTTP Requests
var plotterRunning = true;
function plotterUpdate() {
	if (plotterRunning && robotConnected) {
		var xhr = new XMLHttpRequest();
		xhr.timeout = 200;
		xhr.onreadystatechange = function () {
			if (this.readyState != 4) return;

			//Data Recieved
			if (this.status == 200) {
				var data = JSON.parse(this.responseText);
				data.forEach(function (point) {
					if (point.color == "RESET")
						plotterReset();
					else plotterAddPointToTable(point.x, point.y, point.color);
				});
			}
		};
		//Send Request
		xhr.open("POST", "/plotter/get", true);
		xhr.setRequestHeader('Content-Type', 'application/json');
		xhr.send("");

	}
}
let sendTrajectoryNow = false;
function plotterRequestSendTrajectory() {
	sendTrajectoryNow = true;
}
function plotterSendTrajectory() {
	if (sendTrajectoryNow === true) {
		sendTrajectoryNow = false;

		//Remove old trajectory
		if (document.querySelector("#plotter-color-red"))
			plotterRemoveTable('red', document.querySelector("#plotter-color-red").firstElementChild)

		//Send Request
		let xhr = new XMLHttpRequest();
		xhr.timeout = 200;
		xhr.open("POST", "/plotter/set", true);
		xhr.setRequestHeader('Content-Type', 'application/json');
		xhr.send(JSON.stringify(getPlotterTrajectoryData()));
	}
}
setInterval(plotterSendTrajectory, 250)
setInterval(plotterUpdate, 250);