package de.khb.semi3d.computation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.khb.semi3d.util.Vector;

public class FieldState {
	private Set<FieldSource> sources = new HashSet<>();
	
	/**
	 * 
	 * @param sourceCol eine Collection von Feldquellen
	 * @param size Seitenlänge des betrachteten Volumens in m
	 * @param blockNumber Anzahl der Integrationsblöcke pro Richtung
	 */
	public FieldState(Collection<? extends FieldSource> sourceCol, double size, double blockNumber, double timeStep){
		this.sources.addAll(sourceCol);
		
		double blockSize = size/blockNumber;
		for(int i = 0;i < blockNumber;i++){
			for(int ii = 0;ii < blockNumber;ii++){
				for(int iii = 0;iii < blockNumber;iii++){
					Vector point = new Vector(0.5+i, 0.5+ii, 0.5+iii).scalarMultiply(blockSize);
					this.sources.add(new IntegrationBlock(point, blockSize, timeStep));
				}
			}
		}
	}
	
	public Vector getEField(Vector p){
		//Idee: keine Vectoraddition //-> nicht wesentlich schneller
		//Vector v = Vector.ZERO_VECTOR;
		double x = 0.0, y = 0.0, z = 0.0;
		for(FieldSource s:sources){
			Vector v = s.getEField(p);
			x += v.x;
			y += v.y;
			z += v.z;
		}
		
		return new Vector(x, y, z);
	}
	public double getEPotential(Vector p){
		double d = 0;
		for(FieldSource s:sources){
			d += s.getEPotential(p);
		}
		return d;
	}
	public Vector getBField(Vector p){
		//Idee: keine Vectoraddition //-> nicht wesentlich schneller
		//Vector v = Vector.ZERO_VECTOR;
		double x = 0;
		double y = 0;
		double z = 0;
		
		for(FieldSource s:sources){
			//v = Vector.add(v, ((BFieldSource) s).getBField(p));
			Vector v = s.getBField(p);
			x += v.x;
			y += v.y;
			z += v.z;
		}
		return new Vector(x, y, z);
	}
	
	public void actualise(){
		for(FieldSource s:sources){
			s.computeActualisation(this);
		}		
		for(FieldSource s:sources){
			s.actualise();
		}
	}
}
