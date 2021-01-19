/* Breakerbots Robotics Team 2019 */
package frc.team5104.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Scanner;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import frc.team5104.Constants;
import frc.team5104.util.console;
import frc.team5104.util.console.c;

/**
 * Hosts the BreakerBoard (WebApp) through the RoboRIO.
 * See Tuner.java for the tuner functionality.
 * @version 2.6
 */
public class Webapp {
	private static final int port = 5804; //has to be between 5800-5810 (5800,5801 for limelight)
	private static final double version = 2.6;
	private static final boolean isLocalHost = false;
	private static HttpServer server;

	@SuppressWarnings("resource")
	public static boolean run() {
		try {
			//Check Version
			try {
				Scanner scan = new Scanner(new File(getBaseUrl() + "version.txt"));
				if (!scan.hasNextLine() || !scan.nextLine().equals("BreakerBots WebApp Version " + version))
					throw new Exception();
				scan.close();
			} catch (Exception e) { 
				console.log(e);
				throw new Exception("Invalid Version... Redeploy WebApp"); 
			}
			
			//Setup Server
			server = HttpServer.create(new InetSocketAddress(port), 0);
			
			//Web App URLs	
			server.createContext("/", new PageRequestHandler("app.html")); 
			server.createContext("/resources/", new FilesRequestHandler());
			
			//Web App Requests
			server.createContext("/tuner", new TunerHandler());
			server.createContext("/plotter", new PlotterHandler());
			server.createContext("/robot", new RobotHandler());
			
			//Start Server
			server.setExecutor(null);
			server.start();
			
			//TODO GET LOCAL IP
			console.log("Hosting Web App at 10.51.4.2:" + server.getAddress().getPort());
			
			return true;
		} catch (Exception e) { 
			console.error(c.WEBAPP, e);
			return false;
		}
	}
	
	public static String getBaseUrl() {
		if (isLocalHost)
			return System.getProperty("user.dir") + "\\src\\webapp\\";
		else return "/home/lvuser/webapp/";
	}
	
	private static class RequestHandler implements HttpHandler {
		public String fileUrl;
		public String contentType;
		
		public RequestHandler(String fileUrl, String contentType) {
			this.fileUrl = fileUrl;
			this.contentType = contentType;
		}
		
		public void handle(HttpExchange t) throws IOException {
			readSendFile(t, fileUrl, contentType);
		}
		
		void readSendFile(HttpExchange t, String fileUrl, String contentType) throws IOException {
			String finalUrl = getBaseUrl() + fileUrl;
			
			//Header
			Headers h = t.getResponseHeaders();
			h.add("Content-Type", contentType);

			//File
			File file = new File(finalUrl);
			FileInputStream input = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(input);
			byte[] bytes = new byte[input.available()];
			bis.read(bytes, 0, bytes.length);
			input.close();

			//Send
			t.sendResponseHeaders(200, bytes.length);
			OutputStream os = t.getResponseBody();
			os.write(bytes, 0, bytes.length);
			os.close();
		}
	}
	
	private static class PageRequestHandler extends RequestHandler {
		public PageRequestHandler(String fileUrl) {
			super(fileUrl, "text/html");
		}
	}
	
	private static class FilesRequestHandler extends RequestHandler {
		public static enum fileHeaders {
			js("application/javascript"),
			css("text/css"),
			png("image/png");
			String type; fileHeaders(String type) { this.type = type; }
		}
		
		public FilesRequestHandler() {
			super(null, null);
		}
		
		public void handle(HttpExchange t) throws IOException {
			String url = t.getRequestURI().toString();
			String contentType = url.substring(url.indexOf('.') + 1);
			contentType = fileHeaders.valueOf(contentType).type;
			
			readSendFile(t, url, contentType);
		}
	}
	
	private static class TunerHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			t.getResponseHeaders().add("Content-Type", "application/json");
			
			String requestType = t.getRequestURI().toString().substring(7);
			
			//Get
			if (requestType.equals("get")) {
				//Send outputs
				String response = Tuner.getOutput();
				
				t.sendResponseHeaders(200, response.length());
	            OutputStream os = t.getResponseBody();
	            os.write(response.getBytes());
	            os.close();
			}
			
			//Set
			else if (requestType.equals("set")) {
				//Set inputs
				InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
            	BufferedReader br = new BufferedReader(isr);
            	String data = br.readLine();
            	
            	data = data.substring(1, data.length() - 1);
            	
            	String name = data.substring(data.indexOf("\"name\":\"")+8, data.indexOf(',')-1);
            	String value = data.substring(data.indexOf("\"value\":\"")+9, data.length()-1);
            	
            	Tuner.handleInput(name, value);
			}
		}
	}
	
	private static class PlotterHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			t.getResponseHeaders().add("Content-Type", "application/json");
			
			String requestType = t.getRequestURI().toString().substring(9);
			
			//Get
			if (requestType.equals("get")) {
				//Send outputs
				String response = Plotter.readBuffer();
				
				t.sendResponseHeaders(200, response.length());
	            OutputStream os = t.getResponseBody();
	            os.write(response.getBytes());
	            os.close();
			}
		}
	}
	
	private static class RobotHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			t.getResponseHeaders().add("Content-Type", "application/json");
			
			String requestType = t.getRequestURI().toString().substring(7);
			
			//Get
			if (requestType.equals("get")) {
				//Send outputs
				String response = "{\"name\":\"" + Constants.ROBOT_NAME + "\"}";
				
				t.sendResponseHeaders(200, response.length());
	            OutputStream os = t.getResponseBody();
	            os.write(response.getBytes());
	            os.close();
			}
		}
	}
}