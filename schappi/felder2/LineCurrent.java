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
	public Vector getBField(Point p) {
		if(p.equals(point))
			return null;
		//r = p - point
		Vector v = Vector.add(p, point.scalarMultiplication(-1d));
		v = v.orthogonal();
		v = v.scalarMultiplication(Mu0*current/(2*Math.PI*Math.pow(v.magnitude(),2)));
		return v;
	}

}
