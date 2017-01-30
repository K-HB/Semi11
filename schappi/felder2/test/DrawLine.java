package schappi.felder2.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import schappi.felder2.Point;

public class DrawLine extends JPanel {
	
	public ArrayList<ArrayList<Point>> fieldLines;
	public ArrayList<ArrayList<Point>> epLines;
	public int size;
	
	public DrawLine(ArrayList<ArrayList<Point>> fl, ArrayList<ArrayList<Point>> epl , int s){
		fieldLines = fl;
		epLines = epl;
		size = s;
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
		
		//draw Field Lines
		for(int i = 0; i < fieldLines.size(); i++){
			ArrayList<Point> l = fieldLines.get(i);
			Point last = l.get(0);
			for (int j = 1; j < l.size(); j++){
				Point curr = l.get(j);
				g.drawLine((int) (last.x*hppu), (int) (last.y*vppu), (int) (curr.x*hppu), (int) (curr.y*vppu));
				last = curr;
			}
		}
		
		g.setColor(Color.RED);
		
		//draw EP Lines
		for(int i = 0; i < epLines.size(); i++){
			ArrayList<Point> l = epLines.get(i);
			Point last = l.get(0);
			for (int j = 1; j < l.size(); j++){
				Point curr = l.get(j);
				//System.err.println(curr);
				g.drawLine((int) (last.x*hppu), (int) (last.y*vppu), (int) (curr.x*hppu), (int) (curr.y*vppu));
				last = curr;
			}
		}
	}

}
