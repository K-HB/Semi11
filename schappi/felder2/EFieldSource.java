package schappi.felder2;

public abstract class EFieldSource implements Constants{
	public abstract Vector getElField(Point p);
	public abstract double getElPotential(Point p);
}
