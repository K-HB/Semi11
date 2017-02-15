package schappi.felder2.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import schappi.felder2.Point;
import schappi.felder2.Simulation;
import schappi.felder2.Vector;
import schappi.felder2.graphic.BFieldSourceGraphic;
import schappi.felder2.graphic.EFieldSourceGraphic;
import schappi.felder2.graphic.LineCurrentGraphic;
import schappi.felder2.graphic.PointChargeGraphic;

public class FieldLinesWindow extends JFrame implements MouseListener, ActionListener, ChangeListener {
	
	public static final String WINDOW_TITLE = "Feldliniensimulation";
	
	private final int size;
	private int width;
	private int height;
	private double horizontalPixelsPerUnit;
	private double verticalPixelsPerUnit;
	
	private DrawFieldLines dfl;
	
	//--------------------------------------------
	// Toolbox
	private JFrame toolboxFrame;
	private JLabel radiusLabel, chargeLabel, FLLabel, EPLLabel, vectorsLabel, uGFLLabel, densityLabel;
	private JTextField radiusInput, chargeInput;
	private JCheckBox showFieldLines, showEPL, showVectors, useGridFieldLines;
	private JSlider slideDensity;
	private JRadioButton pointChargeRadio, lineCurrentRadio;
	
	public FieldLinesWindow (int size, int width, int height) {
		super(WINDOW_TITLE);
		
		this.size = size;
		this.width = width;
		this.height = height;
		
		initialize();
	}
	
	private void initialize(){
		dfl = new DrawFieldLines(null, null, null, size, new HashSet<EFieldSourceGraphic>(), new HashSet<BFieldSourceGraphic>());
		dfl.addMouseListener(this);
		
		horizontalPixelsPerUnit = width / (size - 1);
		verticalPixelsPerUnit = height / (size - 1);
		
		this.setSize(width, height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(dfl);
		this.setVisible(true);
		
		toolboxFrame = new JFrame(WINDOW_TITLE);
		toolboxFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		toolboxFrame.setLayout(new GridBagLayout());
		radiusLabel = new JLabel("Radius:");
		chargeLabel = new JLabel("Ladung:");
		FLLabel = new JLabel("Feldlinien anzeigen");
		uGFLLabel = new JLabel("Für Feldlinien Gittererzeugung nutzen");
		densityLabel = new JLabel("Feldlinendichte: ");
		EPLLabel = new JLabel("Äquipotenziallinien anzeigen");
		vectorsLabel = new JLabel("Feldvektoren anzeigen");
		pointChargeRadio = new JRadioButton("Punktladung");
		pointChargeRadio.setSelected(true);
		pointChargeRadio.addActionListener(this);
		lineCurrentRadio = new JRadioButton("Stromleitung");
		lineCurrentRadio.addActionListener(this);
		ButtonGroup groupRadio = new ButtonGroup();
		groupRadio.add(pointChargeRadio);
		groupRadio.add(lineCurrentRadio);
		radiusInput = new JTextField();
		radiusInput.setPreferredSize(new Dimension(100, radiusInput.getPreferredSize().height));
		chargeInput = new JTextField();
		chargeInput.setPreferredSize(new Dimension(100, chargeInput.getPreferredSize().height));
		showFieldLines = new JCheckBox();
		showFieldLines.addActionListener(this);
		showEPL = new JCheckBox();
		showEPL.addActionListener(this);
		showVectors = new JCheckBox();
		showVectors.addActionListener(this);
		useGridFieldLines = new JCheckBox();
		useGridFieldLines.addActionListener(this);
		useGridFieldLines.setEnabled(false);
		slideDensity = new JSlider(JSlider.HORIZONTAL, 0, 10, 2);
		slideDensity.addChangeListener(this);
		
		
		toolboxFrame.add(pointChargeRadio, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		toolboxFrame.add(lineCurrentRadio, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		
		toolboxFrame.add(radiusLabel, new GridBagConstraints(0, 1, 1, 1, 0.3, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		toolboxFrame.add(radiusInput, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		toolboxFrame.add(chargeLabel, new GridBagConstraints(0, 2, 1, 1, 0.3, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		toolboxFrame.add(chargeInput, new GridBagConstraints(1, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		
		toolboxFrame.add(showFieldLines, new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		toolboxFrame.add(FLLabel, new GridBagConstraints(1, 3, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		
		toolboxFrame.add(useGridFieldLines, new GridBagConstraints(0, 4, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		toolboxFrame.add(uGFLLabel, new GridBagConstraints(1, 4, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		
		toolboxFrame.add(slideDensity, new GridBagConstraints(0, 5, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		toolboxFrame.add(densityLabel, new GridBagConstraints(1, 5, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		
		toolboxFrame.add(showEPL, new GridBagConstraints(0, 6, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		toolboxFrame.add(EPLLabel, new GridBagConstraints(1, 6, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		
		toolboxFrame.add(showVectors, new GridBagConstraints(0, 7, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		toolboxFrame.add(vectorsLabel, new GridBagConstraints(1, 7, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		
		toolboxFrame.pack();
		toolboxFrame.setVisible(true);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		for(EFieldSourceGraphic s : dfl.sources){
			if(s.isColliding(new Point(x/horizontalPixelsPerUnit, y/verticalPixelsPerUnit))){
				dfl.sources.remove(s);
				simulateAllNeeded();
				return;
			}
		}
		for(BFieldSourceGraphic s : dfl.bSources){
			if(s.isColliding(new Point(x/horizontalPixelsPerUnit, y/verticalPixelsPerUnit))){
				dfl.bSources.remove(s);
				simulateAllNeeded();
				return;
			}
		}
		
		double radius;
		double charge;
		try{
			radius = Double.parseDouble(radiusInput.getText());
			charge = Double.parseDouble(chargeInput.getText());
		}
		catch(NumberFormatException ex){
			return;
		}
		
		if(pointChargeRadio.isSelected())
			dfl.sources.add(new PointChargeGraphic(radius, 16, new Point(x/horizontalPixelsPerUnit, y/verticalPixelsPerUnit), charge));
		else
			dfl.bSources.add(new LineCurrentGraphic(radius, new Point(x/horizontalPixelsPerUnit, y/verticalPixelsPerUnit), charge));
		simulateAllNeeded();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == showFieldLines){
			useGridFieldLines.setEnabled(showFieldLines.isSelected());
			simulateAllNeeded();
		}else if(e.getSource() == showVectors || e.getSource() == showEPL){
			simulateAllNeeded();
		}else if(e.getSource() == useGridFieldLines){
			slideDensity.setEnabled(useGridFieldLines.isSelected());
			simulateAllNeeded();
		}else if(e.getSource() == pointChargeRadio){
			chargeLabel.setText("Ladung: ");
		}
		else if(e.getSource() == lineCurrentRadio){
			chargeLabel.setText("Strom: ");
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		//slideDensity
		simulateAllNeeded();
	}

	private void simulateAllNeeded() {
		if(showFieldLines.isSelected()){ //sowohl E als auch B Linien
			dfl.drawElFieldLines = true;
			dfl.drawBFieldLines = true;
			Simulation sim = new Simulation(size, dfl.sources, dfl.bSources);
			sim.simulateAllElFieldLines(1E-2, useGridFieldLines.isSelected(), slideDensity.getValue());
			sim.simulateAllBFieldLines(1E-2, useGridFieldLines.isSelected(), slideDensity.getValue());
			dfl.eFieldLines = sim.eFieldLines;
			dfl.bFieldLines = sim.bFieldLines;
			//dfl.repaint();
		}else{
			dfl.drawElFieldLines = false;
			dfl.drawBFieldLines = false;
			//dfl.repaint();
		}
		if(showVectors.isSelected()){
			dfl.drawVectors = true;
			Simulation sim = new Simulation(size, dfl.sources, dfl.bSources);
			dfl.field = genFieldArray(sim, .25, size);
			dfl.distanceUnits = .25;
			//dfl.repaint();
		}else{
			dfl.drawVectors = false;
			//dfl.repaint();
		}
		if(showEPL.isSelected()){
			dfl.drawEP = true;
			Simulation sim = new Simulation(size, dfl.sources, dfl.bSources);
			sim.simulateAllEpls(5E7 /* TODO: Einstellbar */);
			dfl.epLines = sim.epLines;
			//dfl.repaint();
		}else{
			dfl.drawEP = false;
			//dfl.repaint();
		}
		
		//TODO: tiefer Sinn, dass mehrfach repaint()?
		dfl.repaint();
	}
	
	private static Vector[][] genFieldArray(Simulation sim, double distanceUnits, int size){
		int anz = (int) Math.floor((size-1)/distanceUnits)-1;
		Vector[][] field = new Vector[anz][anz];
		for(int i = 0; i < anz; i++){
			for(int ii = 0; ii < anz; ii++){
				Point p = new Point(distanceUnits+distanceUnits*i, distanceUnits+distanceUnits*ii);
				sim.simulate(p);
				field[i][ii] = sim.eField.get(p);
			}
		}
		return field;
	}

}
