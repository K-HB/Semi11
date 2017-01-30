package schappi.felder2;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class Simulation {
	public int 						size;
	public HashSet<FieldSource>	sources;
	public HashMap<Point, Vector>	eField; 
	public HashMap<Point, Double>	ePotential; 
	public ArrayList<ArrayList<Point>> fieldLines;
	public ArrayList<ArrayList<Point>> epLines;
	
	private static final double TOLERANCE = 1E-32;
	
	public Simulation(int s, HashSet<FieldSource> sources){
		size = s;
		this.sources = sources;
		eField = new HashMap<Point, Vector>();
		ePotential = new HashMap<Point, Double>();
		fieldLines = new ArrayList<ArrayList<Point>>();
		epLines = new ArrayList<ArrayList<Point>>();
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
			for(FieldSource s:sources){
				currentF = Vector.add(currentF, s.getElField(p));
			}
			eField.put(p, currentF);
			
			
			//ePotential
			double currentP = 0;
			for(FieldSource s:sources){
				currentP += s.getElPotential(p);
			}
			ePotential.put(p, currentP);
		}
	}
	
	public void simulateFieldLine(Point p, double pixelPerNC){
		ArrayList<Point> list = new ArrayList<Point>();
		list.add(p);
		boolean done = false;
		while(!done){
			Point last = list.get(list.size()-1);
			if(!eField.containsKey(last)){
				simulate(list.get(list.size()-1));
			}
			if(eField.get(last) == null || eField.get(last).magnitude() < TOLERANCE){
				done = true;
				continue;
			}
			if(last.x > size || last.x < 0 || last.y > size || last.y < 0){
				done=true;
				continue;
			}
			for(FieldSource s:sources){
				if(s.isNearby(last))
					done = true;
			}
			if(done){
				continue;
			}
			Point next = Vector.add(list.get(list.size()-1), eField.get(list.get(list.size()-1)).normalize().scalarMultiplication(pixelPerNC)).toPoint(); 
			list.add(next);
		}
		fieldLines.add(list);
	}
	
	private boolean simulateEplHelp(Point s, double sw){
		Point curr = s;
		ArrayList<Point> list = new ArrayList<Point>();
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
		
		for(double x = 0; x <= size; x += 0.1){
			for(double y = 0; y <= size; y += 0.1){
				simulate(new Point(x, y));
				if(Math.abs(ePotential.get(new Point(x,y)) % deltaPot) < 5E3){
					System.err.println("triggered: " + new Point(x, y) + " " + ePotential.get(new Point(x,y)));
					simulateEpl(new Point(x,y), 1E-1);
				}
			}
		}
	}
	
}
