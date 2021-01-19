// Tab.js

const tunerTab = document.querySelector("#tab-tuner");
const plotterTab = document.querySelector("#tab-plotter");
const tabBar = document.querySelector(".mdc-tab-bar");

function updateTab() {
	if (location.hash == "#tuner") {
		tunerTab.classList.add("active");
		plotterTab.classList.remove("active");
		tabBar.MDCTabBar.activateTab(0);
		mdc.autoInit();
	}
	else if (location.hash == "#plotter") {
		tunerTab.classList.remove("active");
		plotterTab.classList.add("active");
		tabBar.MDCTabBar.activateTab(1);
		mdc.autoInit();
	}
	else location.hash = "#tuner";
}

window.addEventListener("hashchange", updateTab);
updateTab();