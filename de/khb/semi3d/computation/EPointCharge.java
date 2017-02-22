package de.khb.semi3d.computation;

import de.khb.semi3d.util.Constants;
import de.khb.semi3d.util.Vector;

public class EPointCharge implements FieldSource {
	
	private final Vector point;
	private /*final*/ double charge;
	
	private int testI = 0;

	public EPointCharge(Vector point, double charge) {
		this.point = point;
		this.charge = charge;
	}

	@Override
	public void computeActualisation(FieldState fs) {
		//nothing
	}

	@Override
	public void actualise() {
		//nothing
	}

	@Override
	public Vector getEField(Vector p) {
		if(point.equals(p)) //sonst teilen durch 0
			return Vector.ZERO_VECTOR;
		
		Vector v = Vector.subtract(p, point);
		
		return v.scalarMultiply(charge/(4*Math.PI*Constants.EPSILON0*v.norm()*v.norm()*v.norm()));
	}

	/**
	 * 
	 */
	@Override
	public double getEPotential(Vector p) {
		if(point.equals(p)) //sonst teilen durch 0
			return 0.0;
		Vector v = Vector.subtract(p, point);
		return charge/(4*Math.PI*Constants.EPSILON0*v.norm());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(charge);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		EPointCharge other = (EPointCharge) obj;
		if (Double.doubleToLongBits(charge) != Double.doubleToLongBits(other.charge))
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
		return "EPointCharge [point=" + point + ", charge=" + charge + "]";
	}

	@Override
	public Vector getBField(Vector p) {
		//kein Einfluss auf BFeld
		return Vector.ZERO_VECTOR;
	}

}
