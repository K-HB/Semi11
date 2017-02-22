package de.khb.semi3d.util;

public interface Constants {
	public static final double MU0 = 4*Math.PI*1E-7;
	public static final double LIGHTVELOCITY = 299_792_458;
	public static final double EPSILON0 = 1/(MU0*Math.pow(LIGHTVELOCITY, 2));
	
	public static final double ELEMENTARY_CHARGE = 1.6021766208*1E-19;
	public static final double ELECTRON_MASS = 9.10938356*1E-31;
	
	public static final double MIN_EFIELD = 1E-9;
	public static final double MIN_BFIELD = 1E-18;
}
