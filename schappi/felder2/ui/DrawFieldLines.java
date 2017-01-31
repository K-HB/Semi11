package schappi.felder2.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import schappi.felder2.Point;
import schappi.felder2.Vector;
import schappi.felder2.graphic.FieldSourceGraphic;

public class DrawFieldLines extends JPanel {

	private static final long serialVersionUID = 1L;
	public Set<List<Point>> fieldLines;
	public Set<List<Point>> epLines;
	public Set<FieldSourceGraphic> sources;
	public int size;
	public boolean drawEP = false , drawFieldLines = false, drawVectors=false;
	public Vector[][] field;
	public double distanceUnits;
	
	public DrawFieldLines(Set<List<Point>> fl, Set<List<Point>> epl, int s, Set<FieldSourceGraphic> sources){
		fieldLines = fl;
		epLines = epl;
		size = s;
		this.sources = sources;
	}
	
	public synchronized void paint(Graphics gr){
		Graphics2D g = (Graphics2D) gr;
		
		int width = this.getWidth();
		int height = this.getHeight();
		double hppu = ((double) width)/(size-1);
		double vppu = ((double) height)/(size-1);
		
		g.clearRect(0, 0, width, height);
		
		//draw Grid
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				g.drawRect((int) (x*hppu) -3, (int) (y*vppu) -3, 6, 6);
			}
		}
		
		//draw Sources
		for(FieldSourceGraphic fs: sources){
			fs.paint(g, hppu, vppu);
		}
		
		//draw Field Lines
		if(drawFieldLines){
			for(List<Point> l:fieldLines){
				Iterator<Point> iter = l.iterator();
				Point last = iter.next();
				while (iter.hasNext()){
					Point curr = iter.next();
					g.drawLine((int) (last.x*hppu), (int) (last.y*vppu), (int) (curr.x*hppu), (int) (curr.y*vppu));
					last = curr;
				}
			}
		}
		
		g.setColor(Color.RED);
		
		//draw EP Lines
		if(drawEP){
			for(List<Point> l:epLines){
				Iterator<Point> iter = l.iterator();
				Point last = iter.next();
				while (iter.hasNext()){
					Point curr = iter.next();
					g.drawLine((int) (last.x*hppu), (int) (last.y*vppu), (int) (curr.x*hppu), (int) (curr.y*vppu));
					last = curr;
				}
			}
		}
		
		Stroke dStroke = g.getStroke();
		g.setColor(Color.BLUE);
		
		//draw Vectors
		if(drawVectors) {
			double maxMagnitude = 0;
			for(Vector[] a:field)
				for(Vector v:a)
					if(v != null && v.magnitude() > maxMagnitude)
						maxMagnitude = v.magnitude();
			
			double factorVector = 0.95*distanceUnits/maxMagnitude/Math.sqrt(2);
			
			for (int y = 0; y < field.length; y++){
	    		for (int x = 0; x < field[0].length; x++){
	    			Vector v = field[x][y];
	    			if(v == null)
	    				continue;
	    			v = v.scalarMultiplication(factorVector);
	    			drawArrow(g, (int) Math.round(distanceUnits*hppu+distanceUnits*hppu*x), (int) Math.round(distanceUnits*vppu+distanceUnits*vppu*y),
	    					(int) Math.round(distanceUnits*hppu+distanceUnits*hppu*x + v.x*hppu), (int) Math.round(distanceUnits*vppu+distanceUnits*vppu*y + v.y*hppu));
	    		}	
	    	}
		}
		
		g.setStroke(dStroke);
	}
	
	private void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {
        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx*dx + dy*dy);
        //AffineTransform at = AffineTransform.getRotateInstance(0);//angle);//AffineTransform.getTranslateInstance(x1, y1);
        //at.concatenate(AffineTransform.getRotateInstance(angle));
        g.translate(x1, y1);
        g.rotate(angle);

        g.drawLine(0, 0, len, 0);
        g.fillPolygon(new int[] {len , len-4, len-4, len},
                      new int[] {0, 0-4, 0+4, 0}, 4);
        
        g.rotate(-angle);
        g.translate(-x1, -y1);
    }
	
	

}
