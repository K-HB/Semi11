package schappi.felder2;


import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import schappi.felder2.graphic.BFieldSourceGraphic;
import schappi.felder2.graphic.EFieldSourceGraphic;

public class Simulation {
	public int 						size;
	public Set<EFieldSourceGraphic>	sources;
	public Set<BFieldSourceGraphic>	bSources;
	public HashMap<Point, Vector>	eField; 
	public HashMap<Point, Double>	ePotential; 
	public HashMap<Point, Vector>	bField; 
	public Set<List<Point>> 		eFieldLines;
	public Set<List<Point>> 		bFieldLines;
	public Set<List<Point>> 		epLines;
	
	private static final double TOLERANCE = 1E-32;
	
	public Simulation(int s, Set<EFieldSourceGraphic> sources, Set<BFieldSourceGraphic> bSources){
		size = s;
		this.sources = sources;
		this.bSources = bSources;
		eField = new HashMap<Point, Vector>();
		ePotential = new HashMap<Point, Double>();
		bField = new HashMap<Point, Vector>();
		eFieldLines = new HashSet<List<Point>>();
		bFieldLines = new HashSet<List<Point>>();
		epLines = new HashSet<List<Point>>();
	}
	

	public void simulateAll(){
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				simulate(new Point(x,y));
			}
		}
	}
	
	public void simulate(Point p){
		if(eField.containsKey(p)){
			//schon erledigt
		}else{
			//eField
			Vector currentF = new Vector(0,0);
			for(EFieldSourceGraphic s:sources){
				currentF = Vector.add(currentF, s.getFieldSource().getElField(p));
			}
			eField.put(p, currentF);
			
			//ePotential
			double currentP = 0;
			for(EFieldSourceGraphic s:sources){
				currentP += s.getFieldSource().getElPotential(p);
			}
			ePotential.put(p, currentP);
			
			//bField
			Vector currentBF = new Vector(0,0);
			for(BFieldSourceGraphic s:bSources){
				currentBF = Vector.add(currentBF, s.getFieldSource().getBField(p));
			}
			bField.put(p, currentBF);
		}
	}
	
	public void simulateFieldLine(Point p, double pixelPerNC, boolean posDirection, Map<Point, Vector> field, Set<List<Point>> lines){
		LinkedList<Point> list = new LinkedList<Point>();
		list.add(p);
		boolean done = false;
		
		Point last = p;
		Point beforeLast = null;
		
		while(!done){
			if(!field.containsKey(last)){
				simulate(list.get(list.size()-1));
			}
			if(field.get(last) == null || field.get(last).magnitude() < TOLERANCE){
				done = true;
				continue;
			}
			if(last.x > size || last.x < 0 || last.y > size || last.y < 0){
				done=true;
				continue;
			}
			if(last != p && Vector.add(last, p.scalarMultiplication(-1.0)).magnitude() < 1E-8){
				done = true;
				continue;
			}
			for(EFieldSourceGraphic s:sources){
				if(s.isNearby(last))
					done = true;
			}
			for(BFieldSourceGraphic s:bSources){
				if(s.isNearby(last))
					done = true;
			}
			if(done){
				continue;
			}
			Point next = Vector.add(last, field.get(last).normalize().scalarMultiplication((posDirection ? 1 : -1)*pixelPerNC)).toPoint(); 
			
			if(beforeLast != null && Vector.add(next, beforeLast.scalarMultiplication(-1.0)).magnitude() < 1E-2){
				list.removeLast();
				done = true;
				continue;
			}
			
			list.add(next);
			beforeLast = last;
			last = next;
		}
		lines.add(list);
	}
	
	/**
	 * 
	 * @param pixelPerNC
	 * @param grid ob mit Gitter oder mit Anfangspunkten der Sources
	 * @param density Anzahl der Gitterpunkte pro Reihe/Spalte (bei !grid egal)
	 */
	public void simulateAllFieldLines(double pixelPerNC, boolean grid, double density, Map<Point, Vector> field, Set<List<Point>> lines){
		if(grid){
			double diff = 1.0/density;
			for(double x = 0.0; x <= size-1; x+=diff){
				for(double y = 0.0; y <= size-1; y+=diff){
					simulateFieldLine(new Point (x,y), pixelPerNC, true, field, lines);
					simulateFieldLine(new Point (x,y), pixelPerNC, false, field, lines);
				}
			}
		}
		else{
			for(EFieldSourceGraphic s:sources){
				for(Point p:s.getBeginPointsFieldLines().keySet())
					simulateFieldLine(p, pixelPerNC, s.getBeginPointsFieldLines().get(p), field, lines);
			}
			for(BFieldSourceGraphic s:bSources){
				for(Point p:s.getBeginPointsFieldLines().keySet())
					simulateFieldLine(p, pixelPerNC, s.getBeginPointsFieldLines().get(p), field, lines);
			}
		}
	}
	
	public void simulateAllElFieldLines(double pixelPerNC, boolean grid, double density){
		simulateAllFieldLines(pixelPerNC, grid, density, eField, eFieldLines);
	}
	
	public void simulateAllBFieldLines(double pixelPerNC, boolean grid, double density){
		simulateAllFieldLines(pixelPerNC, grid, density, bField, bFieldLines);
	}
	
	private boolean simulateEplHelp(Point s, double sw){
		Point curr = s;
		LinkedList<Point> list = new LinkedList<Point>();
		while(true){
			list.add(curr);
			simulate(curr);
			
			Vector unitField = eField.get(curr).normalize();
			Vector plDir = unitField.orthogonal();
			Point newPoint = Vector.add(curr, plDir.scalarMultiplication(sw)).toPoint();
			//entlang des Feldes nach richtigem Potential suchen
			curr = findSamePotential(ePotential.get(curr), newPoint);
			
			if(curr.x < 0 || curr.x > size || curr.y < 0 || curr.y > size){
				epLines.add(list);
				return false;
			}
			if((curr.x > s.x-1E-2 && curr.x < s.x+1E-2) && (curr.y > s.y-1E-2 && curr.y < s.y+1E-2)){
				epLines.add(list);
				return true;
			}
		}
	}
	
	private Point findSamePotential(double pot, Point p){
		double sw = 1E-4;
		
		simulate(p);
		
		Vector uFieldP = eField.get(p).normalize();
		
		Point curr = p;
		
		boolean lastPotBigger = pot > ePotential.get(curr);
		
		while(true){
			simulate(curr);
			double potDiff = pot - ePotential.get(curr);
			if(Math.abs(potDiff) < TOLERANCE){
				return curr;
			}
			else if(potDiff > 0){
				//gegen die Feldrichtung wieder hoch
				curr = Vector.add(curr, uFieldP.scalarMultiplication(-sw)).toPoint();
				
				if(!lastPotBigger){
					sw = sw/2;
					lastPotBigger = true;
				}
			}
			else{ //potDiff < 0
				//mit der Feldrichtung wieder runter
				curr = Vector.add(curr, uFieldP.scalarMultiplication(sw)).toPoint();
				
				if(lastPotBigger){
					sw = sw/2;
					lastPotBigger = false;
				}
			}
			
		}
	}
	
	public void simulateEpl(Point s, double sw){
		if(!simulateEplHelp(s,sw))
			simulateEplHelp(s,-sw);
	}
	
	public void simulateAllEpls(double deltaPot){
		
		for(double x = 0; x <= size-1; x += 0.1){
			for(double y = 0; y <= size-1; y += 0.1){
				simulate(new Point(x, y));
				if(Math.abs(ePotential.get(new Point(x,y)) % deltaPot) < 5E3){
					System.err.println("triggered: " + new Point(x, y) + " " + ePotential.get(new Point(x,y)));
					simulateEpl(new Point(x,y), 1E-1);
				}
			}
		}
	}
}
