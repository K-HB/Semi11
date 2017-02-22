package de.khb.semi3d.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JPanel;

import de.khb.semi3d.computation.FieldState;
import de.khb.semi3d.util.Constants;
import de.khb.semi3d.util.Vector;
import de.khb.semi3d.util.Vector2D;

/**
 * Dieses Panel zeigt die Pfeile eines magnetischen und eines elektrischen Feldes an.
 * Beides kann ausgeschaltet werden.
 * 
 * @author Kilian
 *
 */
public class ArrowPanel extends JPanel {
	
	public static enum PlaneOrientation{XY, XZ, YZ};
	
	private Map<Vector2D, Vector2D> eField;
	private Map<Vector2D, Vector2D> bField;
	private final double size; //von 0 bis size geht die Achse
	private final double eScale, bScale;
	
	public boolean showEField = true;
	public boolean showBField = true;
	
	public ArrowPanel(Map<Vector2D, Vector2D> eField, Map<Vector2D, Vector2D> bField, double size, double eScale, double bScale){
		this.eField = eField;
		this.bField = bField;
		this.size = size;
		this.eScale = eScale;
		this.bScale = bScale;
	}
	
	@Override
	protected void paintComponent(Graphics g){
		g = g.create();
		int width = this.getWidth();
		int height = this.getHeight();
		double hppu = ((double) width)/(size+1);
		double vppu = ((double) height)/(size+1); //am Rand jeweils 1/2 Einheit Platz
		
		g.translate((int) Math.round(hppu*0.5), (int) Math.round(vppu*0.5));
		
		//eField: blau
		if(showEField){
			g.setColor(Color.BLUE);
			for(Vector2D p:eField.keySet()){
				drawArrow(g, p, eField.get(p), hppu, vppu);
			}
			//Maﬂstab
			drawArrow(g, new Vector2D(0.5, size+0.2), new Vector2D(1, 0), hppu, vppu);
			g.drawString(eScale + " V/m", toKoor(1.75, hppu), toKoor(size+0.225, vppu));
		}
		
		//bField: rot
		if(showBField){
			g.setColor(Color.RED);
			for(Vector2D p:bField.keySet()){
				drawArrow(g, p, bField.get(p), hppu, vppu);
			}
			//Maﬂstab
			drawArrow(g, new Vector2D(0.5, size+0.4), new Vector2D(1, 0), hppu, vppu);
			g.drawString(bScale + " T", toKoor(1.75, hppu), toKoor(size+0.425, vppu));
		}		
	}
	
	private static int toKoor(double d, double ppu){
		return (int) Math.round(d*ppu);
	}
	
	private static void drawArrow(Graphics g, Vector2D p, Vector2D v, double hppu, double vppu){
		if(v.norm() < 1/hppu) //hppu rund gleich vppu
			g.fillOval(toKoor(p.x, hppu)-2, toKoor(p.y, vppu)-2, 4, 4);
		else{
			Vector2D p2 = Vector2D.add(p, v);
			drawArrow(g, toKoor(p.x, hppu), toKoor(p.y, vppu), toKoor(p2.x, hppu), toKoor(p2.y, vppu));
		}
	}
	
	private static void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
        Graphics2D g = (Graphics2D) g1.create();

        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx*dx + dy*dy);
        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g.transform(at);

        g.drawLine(0, 0, len, 0);
        g.fillPolygon(new int[] {len, len-4, len-4, len},
                      new int[] {0, -4, 4, 0}, 4);
    }
	
	public static ArrowPanel genPanel(FieldState fs, double size, int gridNumber, Vector translation, PlaneOrientation orientation){
		Map<Vector2D, Vector2D> eField = new HashMap<>();
		Map<Vector2D, Vector2D> bField = new HashMap<>();
		
		double gridGap = size/(gridNumber-1);
		
		double sumENorm = 0;
		double sumBNorm = 0;
		
		for(int i = 0;i < gridNumber;i++){
			for(int ii = 0;ii < gridNumber;ii++){
				Vector2D p2D = new Vector2D(i*gridGap, ii*gridGap);
				Vector p;
				switch(orientation){
				case XY: p = new Vector(p2D.x, p2D.y, 0); break;
				case XZ: p = new Vector(p2D.x, 0, p2D.y); break;
				case YZ: p = new Vector(0, p2D.x, p2D.y); break;
				default: throw new NullPointerException("orientation is null");
				}
				p = Vector.add(p, translation);
				
				Vector vE = fs.getEField(p);
				Vector2D vE2D = projectOnPlane(vE, orientation);
				sumENorm += vE2D.norm();
				eField.put(p2D, vE2D);
				
				Vector vB = fs.getBField(p);
				Vector2D vB2D = projectOnPlane(vB, orientation);
				sumBNorm += vB2D.norm();
				bField.put(p2D, vB2D);
			}
		}
		
		double averageENorm = sumENorm/eField.size();
		double averageBNorm = sumBNorm/bField.size();
		
		if(averageENorm < Constants.MIN_EFIELD)
			averageENorm = 0.0;
		
		if(averageBNorm < Constants.MIN_BFIELD)
			averageBNorm = 0.0;
				
		double eFactor = 0.48*gridGap/averageENorm;
		double bFactor = 0.48*gridGap/averageBNorm;
		
		if (sumENorm != 0) {
			for (Iterator<Vector2D> iter = eField.keySet().iterator(); iter.hasNext();) {
				Vector2D p = iter.next();
				
				if(averageENorm < Constants.MIN_EFIELD){
					eField.put(p, Vector2D.ZERO_VETCOR);
					continue;
				} //TODO: sinnvoller zusammen mit sumENorm != 0 ??
				
				Vector2D v = eField.get(p).scalarMultiply(eFactor);
				eField.put(p, v);
				if (v.norm() > gridGap)
					iter.remove();
			} 
		}
		if (sumBNorm != 0) {
			for (Iterator<Vector2D> iter = bField.keySet().iterator(); iter.hasNext();) {
				Vector2D p = iter.next();
				
				if(averageBNorm < Constants.MIN_BFIELD){
					bField.put(p, Vector2D.ZERO_VETCOR);
					continue;
				}
				
				Vector2D v = bField.get(p).scalarMultiply(bFactor);
				bField.put(p, v);
				if (v.norm() > gridGap)
					iter.remove();
			} 
		}
		
		return new ArrowPanel(eField, bField, size, 1/eFactor, 1/bFactor);
	}
	
	private static Vector2D projectOnPlane(Vector v, PlaneOrientation orientation){
		switch(orientation){
		case XY: return new Vector2D(v.x, v.y);
		case XZ: return new Vector2D(v.x, v.z);
		case YZ: return new Vector2D(v.y, v.z);
		default: throw new NullPointerException("orientation is null");
		}
	}
}
