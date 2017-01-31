package schappi.felder2.graphic;

import java.awt.Graphics2D;
import java.util.Map;

import schappi.felder2.Constants;
import schappi.felder2.EFieldSource;
import schappi.felder2.Point;

public abstract class EFieldSourceGraphic implements Constants{
	
	public abstract boolean isNearby(Point p);
	public abstract Map<Point, Boolean>	getBeginPointsFieldLines();
	public abstract void paint(Graphics2D g, double horizontalPixelsPerUnit, double verticalPixelsPerUnit); 
	public abstract EFieldSource getFieldSource();
	public abstract boolean isColliding(Point p);
}
