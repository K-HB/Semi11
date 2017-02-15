package schappi.felder2;

public abstract class EFieldSource extends FieldSource implements Constants{
	public abstract double getElPotential(Point p);
	public abstract Vector getElPolygonPotential(Point p, double sw);
}
