package de.khb.semi3d.computation;

import de.khb.semi3d.util.Constants;
import de.khb.semi3d.util.Vector;

public class FiniteStraightFunctionVoltage implements FieldSource {

	private final Vector point1;
	private final TimeFunction function;
	private final int numberSection;
	private final double resistance;
	
	private final Vector section;
	
	private double nextVoltage, voltage;
	private double[] currents; //Strom von point1 zu point2 -> von [0] zu [numberSection]
	private double[] nextCurrents;
	private double[] charges; //Ladungen an den Stellen zwischen den Abschnitten + Enden
	private double[] nextCharges;
	private boolean toActualise;

	public FiniteStraightFunctionVoltage(Vector point1,Vector point2, TimeFunction voltageFunction, double resistance, int numberSection) {
		this.point1 = point1;
		this.function = voltageFunction;
		this.numberSection = numberSection;
		this.resistance = resistance;
		
		Vector direction = Vector.subtract(point2, point1);
		this.section = direction.scalarMultiply(1.0/numberSection);
		
		this.voltage = function.firstValue();
		
		this.currents = new double[numberSection];
		for(int i = 0; i < numberSection;i++){
			currents[i] = voltage/resistance;
		}
		this.charges = new double[numberSection+1]; //zu Beginn 0
		
		nextCurrents = new double[numberSection];
		nextCharges = new double[numberSection+1];
	}

	@Override
	public void computeActualisation(FieldState fs) {
		nextVoltage = function.nextValue();
		
		//nextCharges = charges + Ströme
		nextCharges[0] = charges[0] - currents[0];
		if(voltage != 0 && voltage*currents[0] >= 0 && voltage*nextCharges[0] < 0) //voltage und current[0] gleiche Vorzeichen -> gleich gerichtet ; voltage und nextCharges[0] versch. Vorzeichen
			nextCharges[0] = 0;
		
		nextCharges[numberSection] = charges[numberSection] + currents[numberSection-1];
		if(voltage != 0 && voltage*currents[numberSection-1] >= 0 && voltage*nextCharges[numberSection] > 0) //voltage und current[numberSection+1] gleiche Vorzeichen -> gleich gerichtet ; voltage und nextCharges[numberSection+1] gleiche Vorzeichen
			nextCharges[numberSection] = 0;
		
		for(int i = 1;i < numberSection-1;i++){
			nextCharges[i] = charges[i] + currents[i-1] - currents[i];
		}
		
		//nextCurrents = (voltage/numberSection + eField*section)*numberSection/resistance
		for(int i=0; i < numberSection;i++){
			Vector point = Vector.add(point1, section.scalarMultiply(i+0.5));
			nextCurrents[i] = (voltage/numberSection + Vector.dotProduct(fs.getEField(point), section))*numberSection/resistance;
			//TODO neue oder alte Spannug?
		}
		
		toActualise = true;
	}

	@Override
	public void actualise() {
		if(!toActualise)
			return;
		voltage = nextVoltage;
		currents = nextCurrents;
		charges = nextCharges;
		
		nextCurrents = new double[numberSection];
		nextCharges = new double[numberSection+1];
		
		toActualise = false;
	}

	@Override
	public Vector getBField(Vector p) {
		//Sonderbehandlung für Punkte auf Kabel unnötig, da ggf. section x v == [0|0|0]
		
		Vector ret = Vector.ZERO_VECTOR;
		for(int i=0; i < numberSection;i++){
			Vector point = Vector.add(point1, section.scalarMultiply(i+0.5));
			Vector v = Vector.subtract(p, point);
			
			ret = Vector.add(ret, Vector.crossProduct(section, v).scalarMultiply(Constants.MU0*currents[i]/(4*Math.PI*v.norm()*v.norm()*v.norm())));
		}
		
		return ret;
	}

	@Override
	public Vector getEField(Vector p) {
		//wenn irgendwo Ladungen -> wie Punktladungen
		Vector v = Vector.ZERO_VECTOR;
		
		for(int i=0; i <= numberSection;i++){
			Vector point = Vector.add(point1, section.scalarMultiply(i));
			
			if(point.equals(p)) //sonst teilen durch 0
				continue;
			
			Vector r = Vector.subtract(p, point);
			v = Vector.add(v, r.scalarMultiply(charges[i]/(4*Math.PI*Constants.EPSILON0*r.norm()*r.norm()*r.norm())));
		}
		
		return v;
	}

	@Override
	public double getEPotential(Vector p) {
		//wenn irgendwo Ladungen -> wie Punktladungen
		double d = 0.0;
			
		for(int i=0; i <= numberSection;i++){
			Vector point = Vector.add(point1, section.scalarMultiply(i));
			
			if(point.equals(p)) //sonst teilen durch 0
				continue;
			Vector r = Vector.subtract(p, point);
			d+= charges[i]/(4*Math.PI*Constants.EPSILON0*r.norm());
		}
		return d;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((function == null) ? 0 : function.hashCode());
		result = prime * result + numberSection;
		result = prime * result + ((point1 == null) ? 0 : point1.hashCode());
		long temp;
		temp = Double.doubleToLongBits(resistance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((section == null) ? 0 : section.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FiniteStraightFunctionVoltage other = (FiniteStraightFunctionVoltage) obj;
		if (function == null) {
			if (other.function != null)
				return false;
		} else if (!function.equals(other.function))
			return false;
		if (numberSection != other.numberSection)
			return false;
		if (point1 == null) {
			if (other.point1 != null)
				return false;
		} else if (!point1.equals(other.point1))
			return false;
		if (Double.doubleToLongBits(resistance) != Double.doubleToLongBits(other.resistance))
			return false;
		if (section == null) {
			if (other.section != null)
				return false;
		} else if (!section.equals(other.section))
			return false;
		return true;
	}
}
