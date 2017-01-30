package schappi.felder2;

public class PointCharge extends FieldSource {

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
	public Vector getElField(Point p) {
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
		if(p.equals(point))
			return Double.NaN;
		Vector v = Vector.add(p, point.scalarMultiplication(-1d));
		return charge/(4*Math.PI*Epsilon0*v.magnitude());
	}

	@Override
	public boolean isNearby(Point p) {
		return Vector.add(p, point.scalarMultiplication(-1d)).magnitude() < 0.25;
	}

}
