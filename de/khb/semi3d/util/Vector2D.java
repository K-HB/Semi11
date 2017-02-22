package de.khb.semi3d.util;

public final class Vector2D {
	
	public static final Vector2D ZERO_VETCOR = new Vector2D(0.0, 0.0);
	
	public final double x;
	public final double y;
	
	public Vector2D(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public double norm(){
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	public Vector2D scalarMultiply(double d){
		return new Vector2D(d*x, d*y);
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
		Vector2D other = (Vector2D) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + x + "|" + y + "]";
	}
	
	public static Vector2D add(Vector2D v1, Vector2D v2){
		return new Vector2D(v1.x+v2.x, v1.y+v2.y);
	}
	
	public static Vector2D subtract(Vector2D v1, Vector2D v2){
		return new Vector2D(v1.x-v2.x, v1.y-v2.y);
	}
}
