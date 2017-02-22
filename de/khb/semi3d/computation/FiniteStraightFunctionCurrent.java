package de.khb.semi3d.computation;

import de.khb.semi3d.computation.TimeFunction.ConstantFunction;
import de.khb.semi3d.util.Constants;
import de.khb.semi3d.util.Vector;

public class FiniteStraightFunctionCurrent implements FieldSource {

	private final Vector point1;
	private final TimeFunction function;
	private final int numberSection;
	
	private final Vector section;
	
	private double nextCurrent, current;

	public FiniteStraightFunctionCurrent(Vector point1,Vector point2, TimeFunction function, int numberSection) {
		this.point1 = point1;
		this.function = function;
		this.numberSection = numberSection;
		
		Vector direction = Vector.subtract(point2, point1);
		this.section = direction.scalarMultiply(1.0/numberSection);
		
		this.current = function.firstValue();
	}

	@Override
	public void computeActualisation(FieldState fs) {
		nextCurrent = function.nextValue();
	}

	@Override
	public void actualise() {
		current = nextCurrent;
	}

	@Override
	public Vector getBField(Vector p) {
		//Sonderbehandlung für Punkte auf Kabel unnötig, da ggf. section x v == [0|0|0]
		
		Vector ret = Vector.ZERO_VECTOR;
		for(int i=numberSection; i > 0;i--){
			Vector point = Vector.add(point1, section.scalarMultiply(i-0.5));
			Vector v = Vector.subtract(p, point);
			
			ret = Vector.add(ret, Vector.crossProduct(section, v).scalarMultiply(Constants.MU0*current/(4*Math.PI*v.norm()*v.norm()*v.norm())));
		}
		
		return ret;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((function == null) ? 0 : function.hashCode());
		result = prime * result + numberSection;
		result = prime * result + ((point1 == null) ? 0 : point1.hashCode());
		result = prime * result + ((section == null) ? 0 : section.hashCode());
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
		FiniteStraightFunctionCurrent other = (FiniteStraightFunctionCurrent) obj;
		if (function == null) {
			if (other.function != null)
				return false;
		} else if (!function.equals(other.function))
			return false;
		if (numberSection != other.numberSection)
			return false;
		if (point1 == null) {
			if (other.point1 != null)
				return false;
		} else if (!point1.equals(other.point1))
			return false;
		if (section == null) {
			if (other.section != null)
				return false;
		} else if (!section.equals(other.section))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FiniteStraightFunctionCurrent [point1=" + point1 + ", function=" + function + ", numberSection="
				+ numberSection + ", section=" + section + "]";
	}
}
