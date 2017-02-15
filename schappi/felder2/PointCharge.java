package schappi.felder2;


public class PointCharge extends EFieldSource {
	
	private final Point point;
	private final double charge;
		
	/**
	 * 
	 * @param p der Ort in Meter
	 * @param charge Ladung in Coulomb
	 */
	public PointCharge(Point p, double charge) {
		this.point = p;
		this.charge = charge;
	}
	
	/**
	 * gibt beim Pkt selbst null zurück
	 */
	@Override
	public Vector getField(Point p) {
		if(p.equals(point))
			return null;
		//r = p - point
		Vector v = Vector.add(p, point.scalarMultiplication(-1d));
		return v.scalarMultiplication(charge/(4*Math.PI*Epsilon0*Math.pow(v.magnitude(),3)));
	}

	/**
	 * gibt beim Pkt selbst NaN zurück
	 */
	@Override
	public double getElPotential(Point p) {
		Vector v = Vector.add(p, point.scalarMultiplication(-1d));
		if(v.magnitude() < 0.1 /*TODO*/)
			return Double.NaN;
		return charge/(4*Math.PI*Epsilon0*v.magnitude());
	}

	@Override
	public Vector getPolygonFieldLine(Point p, double sw) {
		return getField(p).scalarMultiplication((sw < 0 ? -1.0 : 1.0)); //Bei Gerade keine weiteren Berechnungen
	}

	@Override
	public Vector getElPolygonPotential(Point p, double sw) {
		//Kreis; Norm wie Feld; Richtung je nach Ladung
		if(p.equals(point) || sw != sw)
			return null;
		if(sw == 0.0) //wirklich Gleicheit, da sonst sinnvoller Wert
			return new Vector(0,0);
		//r = p - point
		Vector v = Vector.add(p, point.scalarMultiplication(-1d));
		double r = v.magnitude();
		
		if(r <= sw) //zu kleiner Kreis f�r sinnvolles Ergebnis
			return null;
			
		double sinDeltaAlpha = Math.abs(sw)/r*Math.sqrt(1-Math.pow(sw/r, 2));
		double cosDeltaAlpha = 1-Math.pow(sw/r, 2)/2;
		
		Vector ret = (sw > 0 ?
				new Vector(v.x - v.x*cosDeltaAlpha + v.y*sinDeltaAlpha, v.y - v.y*cosDeltaAlpha - v.x*sinDeltaAlpha)
				:new Vector(v.x - v.x*cosDeltaAlpha - v.y*sinDeltaAlpha, v.y - v.y*cosDeltaAlpha + v.x*sinDeltaAlpha))
				.normalize().scalarMultiplication((charge > 0 ? 1 : -1) * -getField(p).magnitude());

		return ret;
	}

	

}
