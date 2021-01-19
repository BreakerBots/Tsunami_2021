package frc.team5104.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import frc.team5104.util.CrashLogger;
import frc.team5104.util.CrashLogger.Crash;

/**
 * Handles all tuner inputs and outputs for the WebApp.
 * Either add the annotations @tunerOutput (graphing) and @tunerInput (inputs)
 * to methods and fields and plug in the class with Tuner.init(CLASS) or 
 * use the new methods "getTunerInput()" and "setTunerOutput()"
 * For WebApp version 2.6
 */
public class Tuner {
	private static ArrayList<TunerRunnable> runnables = new ArrayList<TunerRunnable>();
	
	//Getters and Setters
	public static String getTunerInput(String name, Object defaultValue) {
		boolean found = false;
		for (TunerRunnable runnable : runnables) {
			if (runnable.name.equals(name)) {
				found = true;
				return runnable.getValue();
			}
		}
		if (!found) {
			runnables.add(new NoTargetTunerRunnable(name, defaultValue.toString(), true));
		}
		return defaultValue.toString();
	}
	
	public static double getTunerInputDouble(String name, double defaultValue) {
		String value = getTunerInput(name, defaultValue);
		try {
			return Double.parseDouble(value);
		} catch(Exception e) { 
			return defaultValue;
		}
	}
	
	public static void setTunerOutput(String name, Object output) {
		boolean found = false;
		for (TunerRunnable runnable : runnables) {
			if (runnable.name.equals(name)) {
				found = true;
				runnable.setValue(output.toString());
			}
		}
		if (!found) {
			runnables.add(new NoTargetTunerRunnable(name, output.toString(), false));
		}
	}
	
	//Initialize the class
	public static void use(Class<?>... targets) {
		for (Class<?> target : targets) {
			for (Field field : target.getDeclaredFields()) {
		        field.setAccessible(true);
		        if (field.getAnnotation(tunerInput.class) != null || field.getAnnotation(tunerOutput.class) != null)
		        	runnables.add(new TunerRunnable(field));
		    }
			for (Method method : target.getDeclaredMethods()) {
		        method.setAccessible(true);
		        if (method.getAnnotation(tunerInput.class) != null || method.getAnnotation(tunerOutput.class) != null)
		        	runnables.add(new TunerRunnable(method));
		    }
		}
	}
	
	//Webapp Functions
	protected static String getOutput() {
		String str = "{\"outputs\":{";
		for (TunerRunnable runnable : runnables) {
			if (!runnable.isInput) {
				str += "\"" + runnable.name + "\"" + ": \"" + runnable.getValue() + "\",";
			}
		}
		if (str.charAt(str.length()-1) == ',')
			str = str.substring(0, str.length() - 1);
		str += "},\"inputs\":{";
		for (TunerRunnable runnable : runnables) {
			if (runnable.isInput) {
				str += "\"" + runnable.name + "\"" + ": \"" + runnable.getValue() + "\",";
			}
		}
		if (str.charAt(str.length()-1) == ',')
			str = str.substring(0, str.length() - 1);
		str += "}}";
		return str;
	}
	protected static void handleInput(String name, String data) {
		for (TunerRunnable runnable : runnables) {
			if (name.equals(runnable.name)) {
				runnable.setValue(data);
			}
		}
	}
	
	//Tuner Runnable
	protected static class TunerRunnable {
		private Field fieldTarget;
		private Method methodTarget;
		public String name;
		public boolean isInput;
		public TunerRunnable(Field target) { 
			fieldTarget = target;
			
			//Input
			if (fieldTarget.getAnnotation(tunerInput.class) != null) {
				isInput = true;
				name = fieldTarget.getAnnotation(tunerInput.class).name();
			}
			//Output
			else {
				isInput = false;
				name = fieldTarget.getAnnotation(tunerOutput.class).name();
			}
			
            if (name.equals(""))
            	name = fieldTarget.getName();
		}
		public TunerRunnable(Method target) { 
			this.methodTarget = target;
			
			//Output
			if (methodTarget.getAnnotation(tunerOutput.class) != null) {
				isInput = false;
				name = methodTarget.getAnnotation(tunerOutput.class).name();
			} else System.out.println("Method cannot be input into tuner.");
			
            if (name.equals(""))
            	name = methodTarget.getName();
		}
		public TunerRunnable() { }
		
		String getValue() {
			try {
				if (fieldTarget != null) {
					return fieldTarget.get(null).toString();
				}
				else if (methodTarget != null) {
					return methodTarget.invoke(null).toString();
				}
			} catch (Exception e) { CrashLogger.logCrash(new Crash("main", e)); }
			return null;
		}
		
		void setValue(String value) {
			try {
				if (isInput && fieldTarget != null) {
					String type = fieldTarget.getGenericType().getTypeName();
					if (value != null && !"".equals(value)) {
						if (type == "double") {
							double cv = Double.valueOf(value);
							fieldTarget.setDouble(null, cv);
						}
						else if (type == "int") {
							int cv = Integer.valueOf(value);
							fieldTarget.setInt(null, cv);
						}
						else if (type == "long") {
							long cv = Long.valueOf(value);
							fieldTarget.setLong(null, cv);
						}
						else if (type == "float") {
							float cv = Float.valueOf(value);
							fieldTarget.setFloat(null, cv);
						}
						else if (type == "boolean") {
							boolean cv = Boolean.valueOf(value);
							fieldTarget.setBoolean(null, cv);
						}
					}
				}
			} catch (Exception e) { }
		}
	}
	
	protected static class NoTargetTunerRunnable extends TunerRunnable {
		String storedValue;
		
		public NoTargetTunerRunnable(String name, String defaultValue, boolean isInput) {
			this.isInput = isInput;
			this.name = name;
			this.storedValue = defaultValue;
		}
		
		String getValue() {
			return storedValue;
		}
		
		void setValue(String value) {
			this.storedValue = value;
		}
	}
	
	//Annotations
	@Retention(RetentionPolicy.RUNTIME)
	/**
	 * An input into the control system (values being set from the tuner)
	 */
	public @interface tunerInput {
		String name() default "";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	/**
	 * An output from the control system (values being graphed in the tuner)
	 */
	public @interface tunerOutput {
		String name() default "";
	}
}
