package schappi.felder2;

import java.util.HashMap;
import java.util.Map;

public class PointCharge extends FieldSource {
	
	private static final double RADIUS = 0.25;
	private static final int NUMBER_FIELDLINES = 16;

	private final Point point;
	private final double charge;
	
	private Map<Point, Boolean> beginPointsFieldLines;
	
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
		return Vector.add(p, point.scalarMultiplication(-1d)).magnitude() < RADIUS;
	}

	@Override
	public Map<Point, Boolean> getBeginPointsFieldLines() {
		if(beginPointsFieldLines == null)
			beginPointsFieldLines = genBeginPointsFieldLines();
		return beginPointsFieldLines;
	}
	
	private Map<Point, Boolean> genBeginPointsFieldLines() {
		Map<Point, Boolean> map = new HashMap<>();
		for(int i = 0;i < NUMBER_FIELDLINES;i++){
			double angle = i*2*Math.PI/NUMBER_FIELDLINES;
			map.put(Vector.add(point, new Vector(Math.cos(angle), Math.sin(angle)).scalarMultiplication(RADIUS*1.05)).toPoint(), charge > 0);
		}
		return map;
	}

}
