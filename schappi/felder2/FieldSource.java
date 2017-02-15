package schappi.felder2;

public abstract class FieldSource {
	public abstract Vector getField(Point p);
	/**
	 * 
	 * @param p
	 * @param sw
	 * @return Vector in Richtung des n�chsten Punktes auf einer Feldlinie mit Betrag der Feldst�rke
	 */
	public abstract Vector getPolygonFieldLine(Point p, double sw);
}
