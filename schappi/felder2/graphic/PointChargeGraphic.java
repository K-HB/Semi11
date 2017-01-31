package schappi.felder2.graphic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;

import schappi.felder2.FieldSource;
import schappi.felder2.Point;
import schappi.felder2.PointCharge;
import schappi.felder2.Vector;

public class PointChargeGraphic extends FieldSourceGraphic {
	
	private double radius;
	private int numberFieldlines;
	
	private Point point;
	private double charge;
	
	private PointCharge fieldSource;
	
	private Map<Point, Boolean> beginPointsFieldLines;
	
	public PointChargeGraphic(double radius, int numberFieldlines, Point p, double charge) {
		this.radius = radius;
		this.numberFieldlines = numberFieldlines;
		this.point = p;
		this.charge = charge;
		this.fieldSource = new PointCharge(this.point, this.charge);
	}

	@Override
	public boolean isNearby(Point p) {
		return Vector.add(p, point.scalarMultiplication(-1d)).magnitude() < radius;
	}

	@Override
	public Map<Point, Boolean> getBeginPointsFieldLines() {
		if(beginPointsFieldLines == null)
			beginPointsFieldLines = genBeginPointsFieldLines();
		return beginPointsFieldLines;
	}
	
	private Map<Point, Boolean> genBeginPointsFieldLines() {
		Map<Point, Boolean> map = new HashMap<>();
		for(int i = 0;i < numberFieldlines;i++){
			double angle = i*2*Math.PI/numberFieldlines;
			map.put(Vector.add(point, new Vector(Math.cos(angle), Math.sin(angle)).scalarMultiplication(radius*1.05)).toPoint(), charge > 0);
		}
		return map;
	}

	@Override
	public void paint(Graphics2D g, double horizontalPixelsPerUnit, double verticalPixelsPerUnit) {
		if(Math.signum(charge)==1){
			g.setColor(Color.RED);
		}else if(charge == 0){
			g.setColor(Color.GRAY);
		}else{
			g.setColor(Color.BLUE);
		}
		
		Stroke defaultStroke = g.getStroke();
		
		//draw Ellipse Bgr
		g.fillOval((int)((this.point.x-this.radius)*horizontalPixelsPerUnit), 
				(int)((this.point.y-this.radius)*verticalPixelsPerUnit), 
				(int)(this.radius * 2 * horizontalPixelsPerUnit), 
				(int)(this.radius * 2 * verticalPixelsPerUnit));
		
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(3));
		//stroke Ellipse
		g.drawOval((int)((this.point.x-this.radius)*horizontalPixelsPerUnit), 
				(int)((this.point.y-this.radius)*verticalPixelsPerUnit), 
				(int)(this.radius * 2 * horizontalPixelsPerUnit), 
				(int)(this.radius * 2 * verticalPixelsPerUnit));
		g.setStroke(defaultStroke);
	}

	@Override
	public FieldSource getFieldSource() {
		return fieldSource;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public int getNumberFieldlines() {
		return numberFieldlines;
	}

	public void setNumberFieldlines(int numberFieldlines) {
		this.numberFieldlines = numberFieldlines;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public double getCharge() {
		return charge;
	}

	public void setCharge(double charge) {
		this.charge = charge;
	}

	@Override
	public boolean isColliding(Point p) {
		return Math.sqrt(Math.pow(p.x - point.x, 2) + Math.pow(p.y - point.y, 2)) < radius;
	}
	
	

}
