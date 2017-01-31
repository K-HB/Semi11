package schappi.felder2.graphic;

import java.awt.Graphics2D;
import java.util.Map;

import schappi.felder2.FieldSource;
import schappi.felder2.Point;

public abstract class FieldSourceGraphic {
	
	public abstract boolean isNearby(Point p);
	public abstract Map<Point, Boolean>	getBeginPointsFieldLines();
	public abstract void paint(Graphics2D g, double horizontalPixelsPerUnit, double verticalPixelsPerUnit); 
	public abstract FieldSource getFieldSource();
}
