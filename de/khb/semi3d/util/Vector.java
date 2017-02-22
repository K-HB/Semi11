package de.khb.semi3d.util;

/**
 * Diese Klasse repräsentiert unveränderliche Vektoren im R3 in kartesischen Koordinaten.
 * @author Kilian
 *
 */
public final class Vector {
	
	public static final Vector ZERO_VECTOR = new Vector(0,0,0);
	
	public final double x,y,z;
	
	public Vector(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double norm(){
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}
	
	public Vector scalarMultiply(double d){
		return new Vector(d*x, d*y, d*z);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector other = (Vector) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}
	
	@Override
	public String toString(){
		return "[" + String.valueOf(x) + " | " + String.valueOf(y) + " | " + String.valueOf(z) + "]";
	}
	
	public static Vector add(Vector v1, Vector v2){
		return new Vector(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}
	
	public static Vector subtract(Vector v1, Vector v2){
		return new Vector(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
	}
	
	public static Vector crossProduct(Vector v1, Vector v2){
		return new Vector(v1.y * v2.z - v1.z * v2.y,
				v1.z * v2.x - v1.x * v2.z,
				v1.x * v2.y - v1.y * v2.x);
	}
	
	public static double dotProduct(Vector v1, Vector v2){
		return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;
	}
}
