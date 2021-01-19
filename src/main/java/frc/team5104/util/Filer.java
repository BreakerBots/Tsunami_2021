package frc.team5104.util;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Filer {
	public static final String HOME_PATH = "/home/lvuser/";
	
	public static boolean fileExists(String fullPath) {
		File file = new File(fullPath);
		return file.exists();
	}
	
	public static String readFile(String fullPath) {
		try {
			Scanner scan = new Scanner(new File(fullPath));
			String returnString = "";
			while (scan.hasNextLine())
				returnString += scan.nextLine();
			scan.close();
			return returnString;
		} catch (Exception e) { }
		return "";
	}
	
	public static void createFile(String fullPath) {
		File file = new File(fullPath);
		try {
			file.createNewFile();
		} catch (IOException e) { }
	}
}
