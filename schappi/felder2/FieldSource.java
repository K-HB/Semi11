package schappi.felder2;

import java.util.Map;
import java.util.Set;

public abstract class FieldSource {
	
	public static final double Epsilon0 = 8.854E-12;

	public abstract Vector getElField(Point p);
	public abstract double getElPotential(Point p);
	public abstract boolean isNearby(Point p);
	
	/**
	 * 
	 * @return Map mit Anfangspunkten und boolean, ob in pos Richtung gelaufen werden soll
	 */
	public abstract Map<Point, Boolean> getBeginPointsFieldLines();
}
