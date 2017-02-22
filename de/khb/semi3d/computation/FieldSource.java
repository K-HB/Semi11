package de.khb.semi3d.computation;

import de.khb.semi3d.util.Vector;

public interface FieldSource {
	public void computeActualisation(FieldState fs);
	public void actualise();
	
	public Vector getBField(Vector p);
	public Vector getEField(Vector p);
	public double getEPotential(Vector p);
}
