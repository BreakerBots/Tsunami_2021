//Tuner.js

//Tuner Setup
const outputRefreshRateMs = 50;
var chartMaxLengthMs = 10000;
var chartIndex = 0;
var chart, chartConfig;
function clearTuner() {
	document.querySelector("#tuner-container > #inputBox").innerHTML = "";
	clearChart();
}
function clearChart() {
	chartConfig = {
		type: 'line', data: { labels: [], datasets: [] },
		options: {
			responsive: true,
			scales: {
				xAxes: [{ display: true, scaleLabel: { display: true, labelString: 'Time' } }],
				yAxes: [{ display: true, scaleLabel: { display: true, labelString: 'Value' } }]
			},
			cubicInterpolationMode: 'monotone',
			elements: { point: { radius: 0 } },
			animation: { duration: 50 }
			//tooltips: {
			//	mode: 'nearest',
			//	intersect: false,
			//}
		}
	};
	chart = new Chart(document.querySelector("#tunerChart").getContext('2d'), chartConfig);
} clearChart();

//Chart Length MS
function initChartLengthMSTextField() {
	mdc.autoInit(document.querySelector("#outputBoxGraphLengthMs"));
	document.querySelector("#outputBoxGraphLengthMs").MDCTextField.value = chartMaxLengthMs / 1000;
} initChartLengthMSTextField();

//Tuner Outputs (Graph)
const tunerOutputColors = [
	'red', 'gold', 'green', 'blue', 'purple', 'orange', 'yellow', 'black', 'grey', 'pink',
	'AntiqueWhite', 'Aqua', 'Aquamarine', 'Chartreuse', 'Brown', 'CadetBlue', 'Chocolate',
	'Coral', 'CornflowerBlue', 'Crimson', 'Cyan', 'DarkBlue', 'DarkGreen', 'DarkKhaki', 'DarkOrchid',
	'DeepPink', 'IndianRed', 'HotPink', 'YellowGreen'
];
function addTunerOutput(name, value) {
	var nd = {
		label: name,
		data: [],
		fill: false
	};

	for (var i = 0; i < chartConfig.data.labels.length; i++) {
		nd.data.push(value);
	}

	chartConfig.data.datasets.push(nd);
	chartConfig.data.datasets[chartConfig.data.datasets.length - 1].exists = true;
	chart.update();
}

function updateTunerOutputs(outputs) {
	updateTunerValueOutputs(outputs);
	var rm = (chartConfig.data.labels.length - 1) - (chartMaxLengthMs / outputRefreshRateMs);
	chartConfig.data.labels.push(chartIndex.toFixed(2));
	for (var c = 0; c < rm; c++) { chartConfig.data.labels.shift(); }

	chartConfig.data.datasets.forEach(function (dataset, i) {
		dataset.exists = false;
	});

	for (var o = 0; o < Object.keys(outputs).length; o++) {
		var exists = false;
		if (!isNaN(Object.values(outputs)[o])) {
			chartConfig.data.datasets.forEach(function (dataset, i) {
				if (dataset.label === Object.keys(outputs)[o]) {
					dataset.data.push(Object.values(outputs)[o]);
					for (var c = 0; c < rm; c++) { dataset.data.shift(); }
					exists = true;
					dataset.exists = true;
				}
			});
		
			if (!exists) {
				addTunerOutput(Object.keys(outputs)[o], Object.values(outputs)[o]);
			}
		}
	}

	chartConfig.data.datasets.forEach(function (dataset, i) {
		if (!dataset.exists) {
			chartConfig.data.datasets.splice(i, 1);
		}
		else {
			dataset.backgroundColor = tunerOutputColors[i];
			dataset.borderColor = tunerOutputColors[i];
		}
	});

	chart.update();
}
const tunerValueOutputElementUI = `
<div id="tunerOutputValueID" disabled class="tuner-value-output-element mdc-text-field--disabled mdc-text-field mdc-text-field--outlined" data-mdc-auto-init="MDCTextField">
	<input class="mdc-text-field__input">
	<div class="mdc-notched-outline">
		<div class="mdc-notched-outline__leading"></div>
		<div class="mdc-notched-outline__notch">
			<label for="tf-outlined" class="mdc-floating-label">NAME</label>
		</div>
		<div class="mdc-notched-outline__trailing"></div>
	</div>
</div>
`;
function addTunerValueOutputElement(name, value) {
	var element = document.createElement("div");
	element.innerHTML = tunerValueOutputElementUI.replace(/NAME/g, name).replace(/ID/g, name.hashCode());
	element.className = "tuner-value-output-element-parent";
	document.querySelector("#tunerOutputValues").appendChild(element);
	mdc.autoInit(element);
	element.firstElementChild.MDCTextField.value = value;
}
function updateTunerValueOutputs(outputs) {
	[].forEach.call(document.querySelectorAll('.tuner-value-output-element-parent'), function (el) {
		el.classList.add("old");
	});

	for (var i = 0; i < Object.keys(outputs).length; i++) {
		var el = document.querySelector("#tunerOutputValue" + Object.keys(outputs)[i].hashCode());
		if (el != null) {
			el.MDCTextField.value = Object.values(outputs)[i];
			el.parentNode.classList.remove("old");
		}
		else {
			addTunerValueOutputElement(Object.keys(outputs)[i], Object.values(outputs)[i]);
		}
	}

	[].forEach.call(document.querySelectorAll('.tuner-value-output-element-parent'), function (el) {
		if (el.classList.contains("old")) {
			el.remove();
		}
	});
}


//Tuner Inputs
const tunerInputElementUI = `
<div id="tunerInputElementID" class="tuner-input-element mdc-text-field mdc-text-field--outlined" data-mdc-auto-init="MDCTextField">
	<input class="mdc-text-field__input">
	<div class="mdc-notched-outline">
		<div class="mdc-notched-outline__leading"></div>
		<div class="mdc-notched-outline__notch">
			<label for="tf-outlined" class="mdc-floating-label">NAME</label>
		</div>
		<div class="mdc-notched-outline__trailing"></div>
	</div>
</div>
`;
function addTunerInputElement(name, value) {
	var element = document.createElement("div");
	element.innerHTML = tunerInputElementUI.replace(/NAME/g, name).replace(/ID/g, name.hashCode());
	element.className = "tuner-input-element-parent";
	document.querySelector("#tuner-container > #inputBox").appendChild(element);
	mdc.autoInit(element);
	element.firstElementChild.MDCTextField.value = value;
}
function updateTunerInputs(inputs) {
	[].forEach.call(document.querySelectorAll('.tuner-input-element-parent'), function (el) {
		el.classList.add("old");
	});
	
	for (var i = 0; i < Object.keys(inputs).length; i++) {
		var el = document.querySelector("#tunerInputElement" + Object.keys(inputs)[i].hashCode());
		if (el != null) {
			if (Object.values(inputs)[i] != el.MDCTextField.value) {
				tunerSet(Object.keys(inputs)[i], el.MDCTextField.value);
			}
			el.parentNode.classList.remove("old");
		}
		else {
			addTunerInputElement(Object.keys(inputs)[i], Object.values(inputs)[i]);
		}
	}

	[].forEach.call(document.querySelectorAll('.tuner-input-element-parent'), function (el) {
		if (el.classList.contains("old")) {
			el.remove();
		}
	});
}

//Pause/Play
function handleTunerPausePlay() {
	tunerRunning = !tunerRunning;
	document.querySelector("#tuner-pause-play").src = tunerRunning ? "resources/images/pause.png" : "resources/images/play.png";
}
window.addEventListener("keydown", handleTunerKeyPressed, false);
function handleTunerKeyPressed(evt) {
	if (location.hash == "#tuner") {
		if (evt.code == "Space") {
			handleTunerPausePlay();
			evt.preventDefault();
		}
		else if (evt.key == "s" && evt.ctrlKey) {
			handleSaveCSV();
			evt.preventDefault();
		}
	}
}

//HTTP Requests
var tunerRunning = true;
function tunerSet(name, value) {
	var xhr = new XMLHttpRequest();
	xhr.timeout = 200;

	//Send Request
	xhr.open("POST", "/tuner/set", true);
	xhr.setRequestHeader('Content-Type', 'application/json');
	xhr.send(JSON.stringify({
		name: name,
		value: value
	}));
}
function tunerUpdate() {
	if (tunerRunning && robotConnected) {
		chartIndex += outputRefreshRateMs / 1000.0;
		var xhr = new XMLHttpRequest();
		xhr.timeout = 200;
		xhr.onreadystatechange = function () {
			if (this.readyState != 4) return;

			//Data Recieved
			if (this.status == 200) {
				var data = JSON.parse(this.responseText);
				updateTunerOutputs(data.outputs);
				updateTunerInputs(data.inputs);
			}
		};
		//Send Request
		xhr.open("POST", "/tuner/get", true);
		xhr.setRequestHeader('Content-Type', 'application/json');
		xhr.send("");
	}
}
setInterval(tunerUpdate, outputRefreshRateMs);

//Save CSV
function handleSaveCSV() {
	if (tunerRunning)
		handleTunerPausePlay();

	let csvContent = "data:text/csv;charset=utf-8,";

	chartConfig.data.datasets.forEach(function (dataset) {
		csvContent += dataset.label + ",";
	});
	csvContent = csvContent.slice(0, -1) + "\r\n";

	for (var i = 0; i < chartConfig.data.labels.length; i++) {
		chartConfig.data.datasets.forEach(function (dataset) {
			csvContent += dataset.data[i] + ",";
		});
		csvContent = csvContent.slice(0, -1) + "\r\n";
	}

	var encodedUri = encodeURI(csvContent);
	var link = document.createElement("a");
	link.setAttribute("href", encodedUri);
	link.setAttribute("download", "breakerboard_tuner_data_" + Math.round(Math.random()*100000) + ".csv");
	document.body.appendChild(link);

	link.click();
}