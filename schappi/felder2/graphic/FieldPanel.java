package schappi.felder2.graphic;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import schappi.felder2.Constants;
import schappi.felder2.Vector;

@SuppressWarnings("serial")
public class FieldPanel extends JPanel implements Constants{
	private final Vector[][] field;
	private final double distanceUnits;
	private final int size;
	private final double factorVector;
	
	public FieldPanel(Vector[][] field, double distanceUnits, int size){
		this.field = field;
		this.distanceUnits = distanceUnits;
		this.size = size;
		
//		double maxMagnitude = 0;
//		for(Vector[] a:field)
//			for(Vector v:a)
//				if(v != null && v.magnitude() > maxMagnitude)
//					maxMagnitude = v.magnitude();
//		
//		this.factorVector = 0.95*distanceUnits/maxMagnitude;
		
		//andere Variante: durchschnittlicher Vektor bekommt Länge distanceUnits/2
		//alle Vektoren die länger als sqrt(2)*distanceUnits sind werden übersprungen
		//Änderung Unnötig, da Klasse veraltet -> in DrawFieldLines übertragen
		
		double avMagnitude = 0;
		for(Vector[] a:field)
			for(Vector v:a)
				avMagnitude += v.magnitude();
		avMagnitude /= field.length*field[0].length;

		this.factorVector = 0.48*distanceUnits/avMagnitude;
		
		//TODO TEST
		System.out.printf("Ein Vektor der durchschnittlichen Länge %f hat eine Länge von %f Einheiten.", avMagnitude, factorVector*distanceUnits);
	}
	
	private void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
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
	
    public void paintComponent(Graphics g) {
    	int width = this.getWidth();
		int height = this.getHeight();
		double hppu = ((double) width)/(size-1);
		double vppu = ((double) height)/(size-1);
    	
    	for (int y = 0; y < field.length; y++){
    		for (int x = 0; x < field[0].length; x++){
    			Vector v = field[x][y];
    			if(v == null)
    				continue;
    			v = v.scalarMultiplication(factorVector);
    			if(v.magnitude() > SQRT2*distanceUnits)
    				continue;
    			drawArrow(g, (int) Math.round(distanceUnits*hppu+distanceUnits*hppu*x), (int) Math.round(distanceUnits*vppu+distanceUnits*vppu*y),
    					(int) Math.round(distanceUnits*hppu+distanceUnits*hppu*x + v.x*hppu), (int) Math.round(distanceUnits*vppu+distanceUnits*vppu*y - /*y-Achse falschrum*/ v.y*hppu));
    		}	
    	}	
	}
}
