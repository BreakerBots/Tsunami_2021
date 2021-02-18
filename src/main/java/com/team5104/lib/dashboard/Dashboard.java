package com.team5104.lib.dashboard;

import com.team5104.lib.Looper;
import com.team5104.lib.Looper.Crash;
import com.team5104.lib.Looper.Loop;
import com.team5104.lib.Looper.TimedLoop;
import com.team5104.lib.console;

public class Dashboard {
	private static Thread thread;
	private static DashboardServer server;

	public static void run() {
		Looper.registerLoop(new Loop("Dashboard-Read", () -> {
			server = new DashboardServer();
			server.run();
		}, 2));

		Looper.registerLoop(new TimedLoop("Dashboard-Send", () -> {

		},1, 500));

		Looper.registerLoop(new TimedLoop("Dashboard-Ping", () -> {
			server.sendPing();
		},1, 10000));
	}

	public static void close() {
		try {
			server.stop();
		} catch (Exception e) { Looper.logCrash(new Crash(e)); }
	}
}
