package de.khb.semi3d.computation;

import de.khb.semi3d.computation.TimeFunction.ConstantFunction;
import de.khb.semi3d.util.Constants;
import de.khb.semi3d.util.Vector;

public class InfiniteStraightFunctionCurrent implements FieldSource {
	
	private final Vector point;
	private final Vector direction; //Norm=1, x>0 || ((x=0 && y > 0) || (y = 0 && z>0)) -> eindeutig
	private final TimeFunction function;
	private final double currentFactor;
	
	private double nextCurrent, current;

	public InfiniteStraightFunctionCurrent(Vector point, Vector direction, TimeFunction function) {
		this.point = point;
		this.function = function;
		
		if(direction.norm() == 0.0) //wirklich 0, sonst sinnvoll
			throw new IllegalArgumentException("direction.norm() == 0");
		
		direction = direction.scalarMultiply(1/direction.norm());
		
		double factor = 1;
		
		if(direction.x < 0){
			direction = direction.scalarMultiply(-1);
			factor *= -1;
		}
		else if(direction.x == 0){
			if(direction.y < 0){
				direction = direction.scalarMultiply(-1);
				factor *= -1;
			}
			else if(direction.y == 0){
				if(direction.z < 0){
					direction = direction.scalarMultiply(-1);
					factor *= -1;
				}
				//else if(direction.getZ() == 0) -> Exception geworfen
			}
		}
		
		this.direction = direction;
		this.currentFactor = factor;
		
		this.current = factor*function.firstValue();
	}

	@Override
	public void computeActualisation(FieldState fs) {
		nextCurrent = currentFactor*function.nextValue();
	}

	@Override
	public void actualise() {
		current = nextCurrent;
	}

	@Override
	public Vector getBField(Vector p) {
		//r = Lotvektor auf Gerade
		//r = (p - point) - ((p - point) * direction)*direction
		// B(p) = mu0*I/(2pi*r^2)*(direction x r)
		
		if(point.equals(p))
			return Vector.ZERO_VECTOR;
		
		Vector v = Vector.subtract(p, point);
		
		Vector r = Vector.subtract(v, direction.scalarMultiply(Vector.dotProduct(v, direction)));
		
		if(r.equals(Vector.ZERO_VECTOR)) //Punkt ein bisschen nach oben schieben
//			r = new Vector(1E-3, 0, 0); //TODO TEST
			return Vector.ZERO_VECTOR;
		
		//return Vector.crossProduct(direction, r)
		//		.scalarMultiply(Constants.EPSILON0*current/(2*Math.PI*Math.pow(r.norm(), 2)));
		return Vector.crossProduct(direction, r)
					.scalarMultiply(Constants.EPSILON0*current/(2*Math.PI*r.norm()*r.norm())); //TODO: bringt das was?
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(currentFactor);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + ((function == null) ? 0 : function.hashCode());
		result = prime * result + ((point == null) ? 0 : point.hashCode());
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
		InfiniteStraightFunctionCurrent other = (InfiniteStraightFunctionCurrent) obj;
		if (Double.doubleToLongBits(currentFactor) != Double.doubleToLongBits(other.currentFactor))
			return false;
		if (direction == null) {
			if (other.direction != null)
				return false;
		} else if (!direction.equals(other.direction))
			return false;
		if (function == null) {
			if (other.function != null)
				return false;
		} else if (!function.equals(other.function))
			return false;
		if (point == null) {
			if (other.point != null)
				return false;
		} else if (!point.equals(other.point))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InfiniteStraightFunctionCurrent [point=" + point + ", direction=" + direction + ", function=" + function
				+ ", currentFactor=" + currentFactor + ", nextCurrent=" + nextCurrent + ", current=" + current + "]";
	}

	@Override
	public Vector getEField(Vector p) {
		//kein EFeld
		return Vector.ZERO_VECTOR;
	}

	@Override
	public double getEPotential(Vector p) {
		//kein EFeld
		return 0.0;
	}
}
