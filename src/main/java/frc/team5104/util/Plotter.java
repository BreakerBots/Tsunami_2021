package frc.team5104.util;

import java.util.ArrayList;
import java.util.List;

import frc.team5104.auto.Position;

/**
 * Handles the plotting of points on the WebApp.
 * For WebApp version 2.6
 */
public class Plotter {
	public static class Color { 
		String value;
		public Color(String value) {
			this.value = value;
		}
		public static final Color 
		RED = new Color("red"), 
		BLUE = new Color("blue"), 
		GREEN = new Color("green"), 
		PURPLE = new Color("purple"), 
		ORANGE = new Color("orange"), 
		BLACK = new Color("black");
	}
	
	private volatile static List<PlotterPoint> buffer = new ArrayList<PlotterPoint>();
	
	public static void plotAll(List<Position> positions, Color color) {
		for (int i = 0; i < positions.size(); i++) {
			plot(
				positions.get(i).getXFeet(),
				positions.get(i).getYFeet(),
				color
			);
		}
	}
	
	public static void reset() {
		buffer.add(new PlotterPoint(0, 0, new Color("RESET")));
	}
	
	public static void plot(double x, double y, Color color) {
		buffer.add(new PlotterPoint(x, y, color));
	}
	
	protected static String readBuffer() {
		String data = "[";
		
		for (PlotterPoint point : buffer) {
			data += point.toString() + ",";
		}
		
		if (data.charAt(data.length()-1) == ',')
			data = data.substring(0, data.length() - 1);
		
		buffer.clear();
		
		return data + "]";
	}
	
	private static class PlotterPoint {
		private double x, y;
		private Color color;
		
		public PlotterPoint(double x, double y, Color color) {
			this.x = x;
			this.y = y;
			this.color = color;
		}
		
		public String toString() {
			return "{\"x\":" + x + ",\"y\":" + y + ",\"color\":\"" + color.value + "\"}";
		}
	}
}