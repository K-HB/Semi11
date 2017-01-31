package schappi.felder2;

public abstract class FieldSource {
	
	public static final double Epsilon0 = 8.854E-12;

	public abstract Vector getElField(Point p);
	public abstract double getElPotential(Point p);

}
