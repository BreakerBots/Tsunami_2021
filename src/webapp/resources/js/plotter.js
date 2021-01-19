// Plotter.js

//Setup
var plotterDesmos = Desmos.GraphingCalculator(document.querySelector('#plotterWindow'), {
	keypad: false,
	expressions: true,
	settingsMenu: false,
	expressionsTopbar: false,
	trace: true,
	expressionsCollapsed: true
});
var plotterColorWrapper = document.querySelector("#plotterColorWrapper");

//Visuals
function plotterReset() {
	plotterDesmos.setBlank();
	plotterColorWrapper.innerHTML = "";
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
	<div class="plotter-color mdc-elevation--z2">
		<i onclick="plotterHideShowColor('COLOR', this)" oncontextmenu="plotterRemoveTable('COLOR', this)" style="background-color: COLOR" class="plotter-color-icon mdc-icon-toggle" data-mdc-auto-init="MDCIconToggle"></i>
	</div>
	`.replace(/COLOR/g, color);
}

function plotterRemoveTable(color, element) {
	plotterDesmos.getExpressions().forEach(function (e) {
		if (e.id === ("table" + color.replace(/#/g, "-")))
			plotterDesmos.removeExpression(e);
	});
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
		if (expressions[i].columns[0].values.length < 1) {
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
setInterval(plotterUpdate, 250);