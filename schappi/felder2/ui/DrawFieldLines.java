package schappi.felder2.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import schappi.felder2.Point;
import schappi.felder2.graphic.FieldSourceGraphic;

public class DrawFieldLines extends JPanel {

	private static final long serialVersionUID = 1L;
	public Set<List<Point>> fieldLines;
	public Set<List<Point>> epLines;
	public Set<FieldSourceGraphic> sources;
	public int size;
	public boolean drawEP = false , drawFieldLines = false;
	
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
	}

}
