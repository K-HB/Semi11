package schappi.felder2.ui;

import java.awt.GridBagLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import schappi.felder2.graphic.FieldSourceGraphic;

public class FieldLinesWindow extends JFrame {
	
	public static final String WINDOW_TITLE = "Feldliniensimulation";
	
	private final int size;
	private int width;
	private int height;
	private double horizontalPixelsPerUnit;
	private double verticalPixelsPerUnit;
	
	private FieldLinesMouseController mouse;
	
	private Set<FieldSourceGraphic> sources;
	
	private DrawFieldLines dfl;
	
	//--------------------------------------------
	// Toolbox
	private JFrame toolboxFrame;
	private JLabel radiusLabel, chargeLabel, FLLabel, EPLLabel, vectorsLabel;
	private JTextField radiusInput, chargeInput;
	private JCheckBox showFieldLines, showEPL, showVectors;
	
	public FieldLinesWindow (int size, int width, int height) {
		super(WINDOW_TITLE);
		
		this.size = size;
		this.width = width;
		this.height = height;
		
		initialize();
	}
	
	private void initialize(){
		sources = new HashSet<FieldSourceGraphic>();
		dfl = new DrawFieldLines(null, null, size, sources);
		mouse = new FieldLinesMouseController(width, height);
		dfl.addMouseListener(mouse);
		
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
		EPLLabel = new JLabel("Äquipotenziallinien anzeigen");
		vectorsLabel = new JLabel("Feldvektoren anzeigen");
		radiusInput = new JTextField();
		chargeInput = new JTextField();
		showFieldLines = new JCheckBox();
		showEPL = new JCheckBox();
		showVectors = new JCheckBox();
		
		
		toolboxFrame.pack();
		toolboxFrame.setVisible(true);
	}

}
