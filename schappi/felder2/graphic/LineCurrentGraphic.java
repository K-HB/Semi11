package schappi.felder2.graphic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;

import schappi.felder2.BFieldSource;
import schappi.felder2.LineCurrent;
import schappi.felder2.Point;
import schappi.felder2.Vector;

public class LineCurrentGraphic extends BFieldSourceGraphic {
	
	private double radius;
	
	private Point point;
	private double current;
	
	private LineCurrent fieldSource;
	
	public LineCurrentGraphic(double radius, Point p, double current) {
		this.radius = radius;
		this.point = p;
		this.current = current;
		this.fieldSource = new LineCurrent(p, current);
	}

	@Override
	public boolean isNearby(Point p) {
		return Vector.add(p, point.scalarMultiplication(-1d)).magnitude() < radius;
	}

	/**
	 * @return eine leere HashMap
	 */
	@Override
	public Map<Point, Boolean> getBeginPointsFieldLines() {
		return new HashMap<>();
	}

	@Override
	public void paint(Graphics2D g, double horizontalPixelsPerUnit, double verticalPixelsPerUnit) {
		g.setColor(Color.BLACK);
		Stroke defaultStroke = g.getStroke();
		
		//cross or point
		if(Math.signum(current)==1){
			//point
			g.fillOval((int)((this.point.x)*horizontalPixelsPerUnit-5), 
					(int)((this.point.y)*verticalPixelsPerUnit-5), 10, 10);
		}else if(current == 0){
			//nothing
		}else{
			//cross
			g.drawLine((int) ((this.point.x-this.radius*SIN45)*horizontalPixelsPerUnit), (int) ((this.point.y-this.radius*SIN45)*verticalPixelsPerUnit),
					(int) ((this.point.x+this.radius*SIN45)*horizontalPixelsPerUnit), (int) ((this.point.y+this.radius*SIN45)*verticalPixelsPerUnit));
			g.drawLine((int) ((this.point.x+this.radius*SIN45)*horizontalPixelsPerUnit), (int) ((this.point.y-this.radius*SIN45)*verticalPixelsPerUnit),
					(int) ((this.point.x-this.radius*SIN45)*horizontalPixelsPerUnit), (int) ((this.point.y+this.radius*SIN45)*verticalPixelsPerUnit));
		}
		
		g.setStroke(new BasicStroke(3));
		//stroke Ellipse
		g.drawOval((int)((this.point.x-this.radius)*horizontalPixelsPerUnit), 
				(int)((this.point.y-this.radius)*verticalPixelsPerUnit), 
				(int)(this.radius * 2 * horizontalPixelsPerUnit), 
				(int)(this.radius * 2 * verticalPixelsPerUnit));
		
		g.setStroke(defaultStroke);
	}

	@Override
	public BFieldSource getFieldSource() {
		return fieldSource;
	}

	@Override
	public boolean isColliding(Point p) {
		return Vector.add(p, point.scalarMultiplication(-1d)).magnitude() < radius;
	}

}
