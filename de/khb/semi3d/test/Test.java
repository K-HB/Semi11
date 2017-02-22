package de.khb.semi3d.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import de.khb.semi3d.computation.EPointCharge;
import de.khb.semi3d.computation.FieldSource;
import de.khb.semi3d.computation.FieldState;
import de.khb.semi3d.computation.FiniteStraightFunctionVoltage;
import de.khb.semi3d.computation.TimeFunction;
import de.khb.semi3d.graphics.ArrowPanel;
import de.khb.semi3d.util.Vector;

public class Test {

	public static void main(String[] args) {
		//TEST 1:
		//System.out.printf("Länge des INFITE_VECTOR: %b", Vector.INFINITE_VECTOR.norm());
		
		//TEST 2:
//		Map<Vector2D, Vector2D> eField = new HashMap<>();
//		Map<Vector2D, Vector2D> bField = new HashMap<>();
//		ArrowPanel panel = new ArrowPanel(eField, bField, 5);
		
		//TEST 3:
		double size = 9;
//		Set<FieldSource> sources = new HashSet<>();
////		sources.add(new EPointCharge(new Vector(size/2, size/2, size/3), 1));
//		sources.add(new InfiniteStraightFunctionCurrent(new Vector(size/2, size/2, size/3), 
//				new Vector(0,0,1), InfiniteStraightFunctionCurrent.getConstantCurrent(1)));
//		FieldState fs = new FieldState(sources, size, 3, 0.1); //Achtung blockNumber sehr klein
//		ArrowPanel panel = ArrowPanel.genPanel(fs, size/3, 15, new Vector(size/3, size/3, size/3), ArrowPanel.PlaneOrientation.XY);
		
		//TEST 4:
//		Map<Vector2D, Vector2D> eField = new HashMap<>();
//		Vector p1 = new Vector(1.5, 1.5, 7);
//		Vector p2 = new Vector(1.0, 1.0, 0);
//		Vector direction = new Vector(0,0,1.0);
//		Vector v = Vector.subtract(p2, p1);
//		Vector r = Vector.subtract(v, direction.scalarMultiply(Vector.dotProduct(v, direction)));
//		eField.put(new Vector2D(p1.x,p1.y), new Vector2D(r.x,r.y));
//		ArrowPanel panel = new ArrowPanel(eField, new HashMap<>(), 3);
		
		//TEST 5:
		double zPos = size*9/20;
		
		Set<FieldSource> sources = new HashSet<>();
		class MyFunction implements TimeFunction{
					double current = 10;
			
					@Override
					public double nextValue() {
						current -= 5;
						return current;
					}
					
					@Override
					public double firstValue() {
						return current;
					}
				};
			
//		sources.add(new InfiniteStraightFunctionCurrent(new Vector(size/2, size/2, size/3), 
//					new Vector(0,0,1), new MyFunction()));
//		sources.add(new InfiniteStraightFunctionCurrent(new Vector(size/2+1, size/2, size/3), 
//				new Vector(0,0,1), new MyFunction()));
		
		sources.add(new EPointCharge(new Vector(size/2, size/2, size/3), 1));
				
//		sources.add(new FiniteStraightFunctionCurrent(new Vector(size*5/12, size/2, size/2), new Vector(size*7/12, size/2, size/2),
//				new MyFunction(), 20));
		
		sources.add(new FiniteStraightFunctionVoltage(new Vector(size*5/12, size/2, size/2), new Vector(size*7/12, size/2, size/2),
				new TimeFunction.ConstantFunction(0), 10.0, 20));
		
		FieldState fs = new FieldState(sources, size, 19, 2);
		ArrowPanel panel = ArrowPanel.genPanel(fs, size/3, 13, new Vector(size/3, size/3, zPos), ArrowPanel.PlaneOrientation.XY);
		
		final JLabel label = new JLabel("0s");
		
		JFrame frame = new JFrame("Felderprogramm");
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.getContentPane().add(label, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 700);
		frame.setVisible(true);
		
		Timer timer = new Timer(5000, new ActionListener() {
			
			int time = 0;
			JPanel p = panel;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				time += 1;
				label.setText(time + "s");
				
				fs.actualise();
				ArrowPanel newPanel = ArrowPanel.genPanel(fs, size/3, 13, new Vector(size/3, size/3, zPos), ArrowPanel.PlaneOrientation.XY);
				frame.getContentPane().remove(p);
				frame.getContentPane().add(newPanel, BorderLayout.CENTER);
				p = newPanel;
				frame.revalidate();
				frame.repaint();
			}
		});
		timer.setRepeats(true);
		timer.start();
	}

}
