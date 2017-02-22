package de.khb.semi3d.computation;

import de.khb.semi3d.util.Constants;
import de.khb.semi3d.util.Vector;

public class IntegrationBlock implements FieldSource {
	
	private final Vector point;
	private final double volume;
	private final double timeStep;
	
	//hängen nur von Volume ab //Auslagerung verringert Laufzeit evtl. ein klitzekleines bisschen
	private final double constantFactorB;
	private final double constantFactorE;
	
	private Vector tEField, tBField, tMinus1EField, tMinus1BField, tMinus2EField, tMinus2BField;
	private boolean toActualise = false;
	private Vector partialE = Vector.ZERO_VECTOR, partialB = Vector.ZERO_VECTOR; //->deutlich schneller
	
//	private Map<VectorPair, Vector> bufferedEField = new HashMap<>(); //Pufferung verlangsamt
//	private Map<VectorPair, Vector> bufferedBField = new HashMap<>();
	
	public IntegrationBlock(Vector point, double size, double timeStep) {
		this.point = point;
		this.volume = size*size*size;
		this.timeStep = timeStep;
		
		this.constantFactorB = Constants.MU0*Constants.EPSILON0*volume/(4*Math.PI);
		this.constantFactorE = -volume/(4*Math.PI);
	}

	@Override
	public void computeActualisation(FieldState fs) {
		tEField = fs.getEField(point);
		tBField = fs.getBField(point);
		toActualise = true;
	}

	@Override
	public void actualise() {
		if(!toActualise)
			return;
		
		tMinus2EField = tMinus1EField;
		tMinus2BField = tMinus1BField;
		
		tMinus1EField = tEField;
		tMinus1BField = tBField;
		
		tEField = null;
		tBField = null;
		
		if(tMinus1EField != null && tMinus2EField != null)
			partialE = Vector.subtract(tMinus1EField, tMinus2EField).scalarMultiply(1/timeStep);
		else
			partialE = Vector.ZERO_VECTOR;
		
		if(tMinus1BField != null && tMinus2BField != null)
			partialB = Vector.subtract(tMinus1BField, tMinus2BField).scalarMultiply(1/timeStep);
		else
			partialB = Vector.ZERO_VECTOR;
	}

	@Override
	public Vector getBField(Vector p) {
		/*
		 * Biot-Savart-Gesetz:
		 * Fleisch+Wikipedia: dB(p) = mu0/(4pi) j(p) x (point - p)/|(point - p)|^3 dV
		 * -> analog für E: dB(p) = mu0*epsilon0/(4pi) dE/dt x (point - p)/|(point - p)|^3 dV
		 */
		
//		VectorPair pair = new VectorPair(point, partialE); 
//		if(bufferedBField.containsKey(pair))
//			return bufferedBField.get(pair);
		
//		if(point.equals(p)) //Punkt ein bisschen nach oben schieben
////			r = new Vector(1E-3, 0, 0);//TODO TEST
//			return Vector.ZERO_VECTOR;
//		
//		Vector r = Vector.subtract(point, p);
//		
//		Vector ret = Vector.crossProduct(partialE, r)
//				.scalarMultiply(constantFactorB/(Math.pow(r.norm(), 3)));
//	
////		bufferedBField.put(pair, ret);
//		return ret;
		
		return field(p, partialE, constantFactorB);
	}
	
	private Vector field(Vector p, Vector partial, double constantFactor){
		if(point.equals(p))
			return Vector.ZERO_VECTOR;
		
//		Vector r = Vector.subtract(point, p);
//		Vector ret = Vector.crossProduct(partial, r)
//				.scalarMultiply(constantFactor/(Math.pow(r.norm(), 3)));
//		return ret;
		//mit weniger Fkt und Objekten schreiben: -> etwas schneller (kleines bisschen)
		//r=[point.x-p.x, point.y-p.y, point.z-p.z]
		//ret=constantFactor/(Math.pow(r.x*r.x + r.y*r.y + r.z*r.z, 1.5))
		//*[-r.y*partial.z + r.z*partial.y, -r.z*partial.x + r.x*partial.z, -r.x*partial.y + r.y*partial.x]
		double rx = point.x-p.x; //TODO: richtigrum (soll auf p zeigen) ???
		double ry = point.y-p.y;
		double rz = point.z-p.z;
		
		//double factor = constantFactor/(Math.pow(rx*rx + ry*ry + rz*rz, 1.5));
		double factor = constantFactor/(Math.sqrt(rx*rx + ry*ry + rz*rz)*Math.sqrt(rx*rx + ry*ry + rz*rz)*Math.sqrt(rx*rx + ry*ry + rz*rz));
		
		return new Vector(factor*(rz*partial.y-ry*partial.z), factor*(rx*partial.z-rz*partial.x), 
				factor*(ry*partial.x-rx*partial.y));
	}

	@Override
	public Vector getEField(Vector p) {
		// dE(p) = -1/(4pi) dB/dt x (point - p)/|(point - p)|^3 dV
		
//		VectorPair pair = new VectorPair(point, partialB);
//		if(bufferedEField.containsKey(pair))
//			return bufferedEField.get(pair);
		
//		if(point.equals(p)) //Punkt ein bisschen nach oben schieben
////			r = new Vector(1E-3, 0, 0);//TODO TEST auskommentieren scheint sinnvoll zu sein
//			return Vector.ZERO_VECTOR;
//		
//		Vector r = Vector.subtract(point, p);
//		
//		Vector ret = Vector.crossProduct(partialB, r)
//				.scalarMultiply(constantFactorE/(Math.pow(r.norm(), 3)));
//		
////		bufferedEField.put(pair, ret);
//		return ret;
		
		return field(p, partialB, constantFactorE);
	} 

	@Override
	public double getEPotential(Vector p) {
		// Nur Wirbelfeld entsteht
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((point == null) ? 0 : point.hashCode());
		long temp;
		temp = Double.doubleToLongBits(volume);
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
		IntegrationBlock other = (IntegrationBlock) obj;
		if (point == null) {
			if (other.point != null)
				return false;
		} else if (!point.equals(other.point))
			return false;
		if (Double.doubleToLongBits(volume) != Double.doubleToLongBits(other.volume))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "IntegrationBlock [point=" + point + ", volume=" + volume + ", timeStep=" + timeStep + ", tEField="
				+ tEField + ", tBField=" + tBField + ", tMinus1EField=" + tMinus1EField + ", tMinus1BField="
				+ tMinus1BField + ", tMinus2EField=" + tMinus2EField + ", tMinus2BField=" + tMinus2BField
				+ ", toActualise=" + toActualise + "]";
	}
}
