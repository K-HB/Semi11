package schappi.felder2.test;


import java.util.HashSet;

import schappi.felder2.Point;
import schappi.felder2.Simulation;
import schappi.felder2.graphic.FieldSourceGraphic;
import schappi.felder2.graphic.PointChargeGraphic;
import schappi.felder2.ui.FieldLinesWindow;

public class Test2 {

	public static void main(String[] args) {
//		HashSet<FieldSourceGraphic> set = new HashSet<FieldSourceGraphic>();
//		//Quadrat
//		set.add(new PointChargeGraphic(0.25, 16, new Point(0.5,0.5), 1.0));
//		set.add(new PointChargeGraphic(0.25, 16, new Point(2.5,2.5), 1.0));
//		set.add(new PointChargeGraphic(0.25, 16, new Point(0.5,2.5), 1.0));
//		set.add(new PointChargeGraphic(0.25, 16, new Point(2.5,0.5), 1.0));
		
		//gleichseitiges Dreieck
//		set.add(new PointCharge(new Point(0.5,0.5), 1.0));
//		set.add(new PointCharge(new Point(2.5,0.5), 1.0));
//		set.add(new PointCharge(new Point(1.5,0.5 + Math.sqrt(3)), 1.0));
	
//		Simulation sim = new Simulation(4, set);
//		sim.simulateAll();
		
//		sim.simulateFieldLine(new Point(0.2,0.8), 1E-1);
//		sim.simulateFieldLine(new Point(0.3,1), 1E-1);
//		sim.simulateFieldLine(new Point(0.2,1.2), 1E-1);
		
		//sim.simulateAllFieldLines(1E-2);
		
		//sim.simulateAllEpls(5E6);
		//sim.simulateEpll(new Point(1.8,1.8), 1E0, 1E-1);
		//sim.simulateEpl(new Point(1.8,1.8), 1E-1);
		
		/*HashMap<Point,Vector> e = sim.eField;
		for(int y = 0; y < 4; y++){
			for(int x = 0; x < 4; x++){
				System.out.print(e.get(new Point(x,y)));
			}
			System.out.println();
		}*/
		
//		JFrame frame = new JFrame("Test");
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setSize(800, 800);
//		frame.add(new DrawFieldLines(sim.fieldLines,sim.epLines, sim.size, set));
//		frame.setVisible(true);
		
		new FieldLinesWindow(4, 800, 800);
		
	}

}
