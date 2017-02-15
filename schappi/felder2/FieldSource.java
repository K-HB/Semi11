package schappi.felder2;

public abstract class FieldSource {
	public abstract Vector getField(Point p);
	/**
	 * 
	 * @param p
	 * @param sw
	 * @return Vector in Richtung des nächsten Punktes auf einer Feldlinie mit Betrag der Feldstärke
	 */
	public abstract Vector getPolygonFieldLine(Point p, double sw);
}
