package schappi.felder2;

public class LineCurrent extends BFieldSource {

	private final Point point;
	private final double current;
		
	/**
	 * 
	 * @param p der Ort in Meter
	 * @param charge Ladung in Ampere
	 */
	public LineCurrent(Point p, double current) {
		this.point = p;
		this.current = current;
	}
	
	@Override
	public Vector getField(Point p) {
		if(p.equals(point))
			return null;
		//r = p - point
		Vector v = Vector.add(p, point.scalarMultiplication(-1d));
		v = v.orthogonal();
		v = v.scalarMultiplication(Mu0*current/(2*Math.PI*Math.pow(v.magnitude(),2)));
		return v;
	}
	
	@Override
	public Vector getPolygonFieldLine(Point p, double sw) {
		if(p.equals(point) || sw != sw)
			return null;
		if(sw == 0.0) //wirklich gleicheit, da sonst sinnvoller Wert
			return new Vector(0,0);
		//r = p - point
		Vector v = Vector.add(p, point.scalarMultiplication(-1d));
		double r = v.magnitude();
		
		if(r <= sw) //zu kleiner Kreis für sinnvolles Ergebnis
			return null;
			
		double sinDeltaAlpha = Math.abs(sw)/r*Math.sqrt(1-Math.pow(sw/r, 2));
		double cosDeltaAlpha = 1-Math.pow(sw/r, 2)/2;
		
		Vector ret = (sw > 0 ?
				new Vector(v.x - v.x*cosDeltaAlpha + v.y*sinDeltaAlpha, v.y - v.y*cosDeltaAlpha - v.x*sinDeltaAlpha)
				:new Vector(v.x - v.x*cosDeltaAlpha - v.y*sinDeltaAlpha, v.y - v.y*cosDeltaAlpha + v.x*sinDeltaAlpha))
				.normalize().scalarMultiplication((current > 0 ? 1 : -1) * -getField(p).magnitude());
		//Richtung je nach Stromrichtung

		return ret;
	}

}
