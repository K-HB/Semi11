package schappi.felder2;

public class Vector {
	public final double x;
	public final double y;
	
	public Vector(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public Vector normalize(){
		return this.scalarMultiplication(1/this.magnitude());
	}
	
	public Vector orthogonal(){
		return new Vector(this.y, -this.x);
	}
	
	public double magnitude(){
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	public Vector scalarMultiplication(double s){
		return new Vector(this.x*s, this.y*s);
	}
	
	/**
	 * 
	 * @param v1
	 * @param v2
	 * @return Summe; Wenn ein Argument null, null
	 */
	public static Vector add(Vector v1, Vector v2){
		return (v1 == null || v2 == null ? null : new Vector(v1.x + v2.x, v1.y + v2.y));
	}
	
	public Point toPoint(){
		return new Point(x,y);
	}
	
	@Override
	public String toString(){
		return "["+x+","+y+"]";
		
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
		Vector other = (Vector) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}
}
