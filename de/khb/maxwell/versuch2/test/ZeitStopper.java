package de.khb.maxwell.versuch2.test;

public class ZeitStopper {
	private static long time;
	
	public static void start(){
		time = System.currentTimeMillis();
	}
	
	public static void stop(){
		System.out.println(System.currentTimeMillis() - time);
	}
}
