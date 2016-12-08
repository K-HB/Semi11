package de.khb.maxwell.versuch2;

import java.util.HashSet;

import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import de.khb.maxwell.versuch2.test.ZeitStopper;

/**
 * 
 * @author Kilian <br/>
 * 
 * Simuliert die Felder <br/>
 * TODO: Strom und Ladungen berücksichtigen <br/>
 * TODO: ggf. mu0*epsilon0 mit 1/c^2 ersetzen <br/>
 * 
 * TODO: Fehler nicht auf Beträge angewendet, ändern!! <br/>
 * 
 * Es dauert schon bei GROESSE = 5 extrem lange, vielleicht doch kein so guter Ansatz
 *
 */
public class Simulator {
	
	public enum CoeffType {
		E_X, E_Y, E_Z, B_X, B_Y, B_Z, F_I, F_II, F_III_X, F_III_Y, F_III_Z, F_IV_X, F_IV_Y, F_IV_Z, NEGATIVE
	}

	private static double MU0 = 4*Math.PI*Math.pow(10d, -7d);
	private static double LIGHTSPEED = 299_792_458d;
	private static double EPSILON0 = 1/(MU0*Math.pow(LIGHTSPEED, 2));
	
	
	private static int GROESSE = 5;
	private static int COEFF_LENGTH = 14*GROESSE*GROESSE*GROESSE+1;
	private static double DELTA_X = 0.001;	//in m
	private static double DELTA_T = 0.1; //in s
	
	private static SimplexSolver solver = new SimplexSolver();
	
	private static HashSet<LinearConstraint> setEquationsI_II = genSetEquationsI_II();
	
	private double[][][][] letzteHyperebene = new double[GROESSE][GROESSE][GROESSE][6];
	private double letzteNegative;
	private double[][][][] aktuelleHyperebene = new double[GROESSE][GROESSE][GROESSE][6];
	private double aktuelleNegative;

	
	/** Nummerierung der Koeffizienten der Variablen:
	 * E_X[i][j][k] = 6*(i*GROESSE*GROESSE+j*GROESSE+k)
	 * E_Y[i][j][k] = 6*(i*GROESSE*GROESSE+j*GROESSE+k)+1
	 * E_Z[i][j][k] = 6*(i*GROESSE*GROESSE+j*GROESSE+k)+2
	 * B_X[i][j][k] = 6*(i*GROESSE*GROESSE+j*GROESSE+k)+3
	 * B_Y[i][j][k] = 6*(i*GROESSE*GROESSE+j*GROESSE+k)+4
	 * B_Z[i][j][k] = 6*(i*GROESSE*GROESSE+j*GROESSE+k)+5
	 * 
	 * Fehler[I][i][j][k] = 6*((GROESSE-1)*GROESSE*GROESSE+(GROESSE-1)*GROESSE+(GROESSE-1)+1) + 8*(i*GROESSE*GROESSE+j*GROESSE+k)
	 * 						= 6*GROESSE*GROESSE*GROESSE + 8*(i*GROESSE*GROESSE+j*GROESSE+k)
	 * Fehler[II][i][j][k] = 6*GROESSE*GROESSE*GROESSE + 8*(i*GROESSE*GROESSE+j*GROESSE+k)+1
	 * Fehler[III_X][i][j][k] = 6*GROESSE*GROESSE*GROESSE + 8*(i*GROESSE*GROESSE+j*GROESSE+k)+2
	 * Fehler[III_Y][i][j][k] = 6*GROESSE*GROESSE*GROESSE + 8*(i*GROESSE*GROESSE+j*GROESSE+k)+3
	 * Fehler[III_Z][i][j][k] = 6*GROESSE*GROESSE*GROESSE + 8*(i*GROESSE*GROESSE+j*GROESSE+k)+4
	 * Fehler[IV_X][i][j][k] = 6*GROESSE*GROESSE*GROESSE + 8*(i*GROESSE*GROESSE+j*GROESSE+k)+5
	 * Fehler[IV_Y][i][j][k] = 6*GROESSE*GROESSE*GROESSE + 8*(i*GROESSE*GROESSE+j*GROESSE+k)+6
	 * Fehler[IV_Z][i][j][k] = 6*GROESSE*GROESSE*GROESSE + 8*(i*GROESSE*GROESSE+j*GROESSE+k)+7
	 * 
	 * NEGATIVE = 14*GROESSE*GROESSE*GROESSE
	 */
	
	private static int coeffNumber(int i, int j, int k, CoeffType type){
		switch(type){
		case E_X: return 6*(i*GROESSE*GROESSE+j*GROESSE+k);
		case E_Y: return 6*(i*GROESSE*GROESSE+j*GROESSE+k)+1;
		case E_Z: return 6*(i*GROESSE*GROESSE+j*GROESSE+k)+2;
		case B_X: return 6*(i*GROESSE*GROESSE+j*GROESSE+k)+3;
		case B_Y: return 6*(i*GROESSE*GROESSE+j*GROESSE+k)+4;
		case B_Z: return 6*(i*GROESSE*GROESSE+j*GROESSE+k)+5;
		
		case F_I: return 6*GROESSE*GROESSE*GROESSE + 8*(i*GROESSE*GROESSE+j*GROESSE+k);
		case F_II: return 6*GROESSE*GROESSE*GROESSE + 8*(i*GROESSE*GROESSE+j*GROESSE+k)+1;
		case F_III_X: return 6*GROESSE*GROESSE*GROESSE + 8*(i*GROESSE*GROESSE+j*GROESSE+k)+2;
		case F_III_Y: return 6*GROESSE*GROESSE*GROESSE + 8*(i*GROESSE*GROESSE+j*GROESSE+k)+3;
		case F_III_Z: return 6*GROESSE*GROESSE*GROESSE + 8*(i*GROESSE*GROESSE+j*GROESSE+k)+4;
		case F_IV_X: return 6*GROESSE*GROESSE*GROESSE + 8*(i*GROESSE*GROESSE+j*GROESSE+k)+5;
		case F_IV_Y: return 6*GROESSE*GROESSE*GROESSE + 8*(i*GROESSE*GROESSE+j*GROESSE+k)+6;
		case F_IV_Z: return 6*GROESSE*GROESSE*GROESSE + 8*(i*GROESSE*GROESSE+j*GROESSE+k)+7;
		
		case NEGATIVE: return 14*GROESSE*GROESSE*GROESSE;
		
		default: throw new IllegalArgumentException("Unknown type of coefficient.");
		}
	}
	
	//Randerscheinungsregler
	private static class BorderHelper{
		static int lower(int i){
			if(i==0)
				return 0;
			else
				return i-1;
		}
		static int higher(int i){
			if(i==GROESSE-1)
				return GROESSE-1;
			else
				return i+1;
		}
		static double step(int i){
			if(i==0 || i == GROESSE-1)
				return 1/DELTA_X;
			else
				return 2/DELTA_X;
		}
	}
	
	private static HashSet<LinearConstraint> genSetEquationsI_II() {
		HashSet<LinearConstraint> set = new HashSet<>();
		for(int i = 0;i < GROESSE;i++){
			for(int j = 0;j < GROESSE;j++){
				for(int k = 0;k < GROESSE;k++){
					double[] coeffI = new double[COEFF_LENGTH];
					
					/*div E = 0 bzw. Ladung
					 * 
					 * dE_x/dx + dE_y/dy + dE_z/dz
					 * = 1/DELTA_X * (E_X[i+1][j][k] - E_X[i-1][j][k]) +...
					 * = 1/DELTA_X * (E_X[i+1][j][k] - neg - E_X[i-1][j][k] + neg) +...
					 * = 1/DELTA_X * (E_X[i+1][j][k] - E_X[i-1][j][k]) +...
					 */
					coeffI[coeffNumber(BorderHelper.higher(i), j, k, CoeffType.E_X)] = BorderHelper.step(i);
					coeffI[coeffNumber(BorderHelper.lower(i), j, k, CoeffType.E_X)] = -BorderHelper.step(i);
					
					coeffI[coeffNumber(i, BorderHelper.higher(j), k, CoeffType.E_Y)] = BorderHelper.step(j);
					coeffI[coeffNumber(i, BorderHelper.lower(j), k, CoeffType.E_Y)] = -BorderHelper.step(j);
					
					coeffI[coeffNumber(i, j, BorderHelper.higher(k), CoeffType.E_Z)] = BorderHelper.step(k);
					coeffI[coeffNumber(i, j, BorderHelper.lower(k), CoeffType.E_Z)] = -BorderHelper.step(k);
					
					coeffI[coeffNumber(i, j, k, CoeffType.F_I)] = 1d;
					
					set.add(new LinearConstraint(coeffI, Relationship.EQ, 0d)); //TODO: Ladung statt 0d
					
					/*div B = 0
					 * 
					 * dB_x/dx + dB_y/dy + dB_z/dz
					 * = 1/DELTA_X * (B_X[i+1][j][k] - B_X[i-1][j][k]) +...
					 */
					coeffI[coeffNumber(BorderHelper.higher(i), j, k, CoeffType.B_X)] = BorderHelper.step(i);
					coeffI[coeffNumber(BorderHelper.lower(i), j, k, CoeffType.B_X)] = -BorderHelper.step(i);
					
					coeffI[coeffNumber(i, BorderHelper.higher(j), k, CoeffType.B_Y)] = BorderHelper.step(j);
					coeffI[coeffNumber(i, BorderHelper.lower(j), k, CoeffType.B_Y)] = -BorderHelper.step(j);
					
					coeffI[coeffNumber(i, j, BorderHelper.higher(k), CoeffType.B_Z)] = BorderHelper.step(k);
					coeffI[coeffNumber(i, j, BorderHelper.lower(k), CoeffType.B_Z)] = -BorderHelper.step(k);
					
					coeffI[coeffNumber(i, j, k, CoeffType.F_II)] = 1d;
					
					set.add(new LinearConstraint(coeffI, Relationship.EQ, 0d));
				}
			}
		}
		return set;
	}
	
	private static double rotateX(int i, int k, int j, boolean electric, double[][][][] hyperebene){
		/* x von rot E
		 * dE_Z/dy - dE_Y/dz
		 * (E_Z[i][j+1][k] - E_Z[i][j-1][k])*2/DELTA_X +...
		 */
		
		double E_Z_dy = (hyperebene[i][BorderHelper.higher(j)][k][(electric ? 0 : 3) + 2] - hyperebene[i][BorderHelper.lower(j)][k][(electric ? 0 : 3) + 2])*2/BorderHelper.step(j);
		double E_Y_dz = (hyperebene[i][j][BorderHelper.higher(k)][(electric ? 0 : 3) + 1] - hyperebene[i][j][BorderHelper.lower(k)][(electric ? 0 : 3) + 1])*2/BorderHelper.step(k);
		return E_Z_dy - E_Y_dz;
	}
	
	private static double rotateY(int i, int k, int j, boolean electric, double[][][][] hyperebene){
		/* y von rot E
		 * dE_X/dz - dE_Z/dx
		 */
		
		double E_Z_dx = (hyperebene[BorderHelper.higher(i)][j][k][(electric ? 0 : 3) + 2] - hyperebene[BorderHelper.lower(i)][j][k][(electric ? 0 : 3) + 2])*2/BorderHelper.step(i);
		double E_X_dz = (hyperebene[i][j][BorderHelper.higher(k)][(electric ? 0 : 3) + 0] - hyperebene[i][j][BorderHelper.lower(k)][(electric ? 0 : 3) + 0])*2/BorderHelper.step(k);
		return E_X_dz - E_Z_dx;
	}
	
	private static double rotateZ(int i, int k, int j, boolean electric, double[][][][] hyperebene){
		/* z von rot E
		 * dE_Y/dx - dE_X/dy
		 */
		
		double E_Y_dx = (hyperebene[BorderHelper.higher(i)][j][k][(electric ? 0 : 3) + 1] - hyperebene[BorderHelper.lower(i)][j][k][(electric ? 0 : 3) + 1])*2/BorderHelper.step(i);
		double E_X_dy = (hyperebene[i][BorderHelper.higher(j)][k][(electric ? 0 : 3) + 0] - hyperebene[i][BorderHelper.lower(j)][k][(electric ? 0 : 3) + 0])*2/BorderHelper.step(j);
		return E_Y_dx - E_X_dy;
	}
	
	/**
	 * Berechnet den nächsten Zeitschritt
	 */
	private void update(){
		HashSet<LinearConstraint> set = new HashSet<>(setEquationsI_II);
		
		for(int i=0;i < GROESSE;i++){
			for(int j=0;j < GROESSE;j++){
				for(int k=0;k < GROESSE;k++){
					/* III: 
					 * rot E = -dB/dt
					 * rot E = -(B[t+1][i][j][k] - B[t-1][i][j][k])*2/DELTA_T
					 * 2/DELTA_T * B[t+1][i][j][k] = -rot E + B[t-1][i][j][k]*2/DELTA_T
					 * B[t+1][i][j][k] = -DELTA_T/2 * rot E + B[t-1][i][j][k]
					 * 
					 * B[t+1][i][j][k] - neg = -DELTA_T/2 * rot E + B[t-1][i][j][k]
					 */
					
					//E_X:
					double[] coeffIII_X = new double[COEFF_LENGTH];
					coeffIII_X[coeffNumber(i, j, k, CoeffType.B_X)] = 1d;
					coeffIII_X[coeffNumber(i, j, k, CoeffType.F_III_X)] = 1d;
					coeffIII_X[coeffNumber(i, j, k, CoeffType.NEGATIVE)] = -1d;
					set.add(new LinearConstraint(coeffIII_X, Relationship.EQ, -rotateX(i, k, j, true, aktuelleHyperebene) * DELTA_T/2 + letzteHyperebene[i][j][k][3] - letzteNegative));
				
					//E_Y:
					double[] coeffIII_Y = new double[COEFF_LENGTH];
					coeffIII_Y[coeffNumber(i, j, k, CoeffType.B_Y)] = 1d;
					coeffIII_Y[coeffNumber(i, j, k, CoeffType.F_III_Y)] = 1d;
					coeffIII_Y[coeffNumber(i, j, k, CoeffType.NEGATIVE)] = -1d;
					set.add(new LinearConstraint(coeffIII_Y, Relationship.EQ, -rotateY(i, k, j, true, aktuelleHyperebene) * DELTA_T/2 + letzteHyperebene[i][j][k][4] - letzteNegative));
					
					//E_Y:
					double[] coeffIII_Z = new double[COEFF_LENGTH];
					coeffIII_Z[coeffNumber(i, j, k, CoeffType.B_Z)] = 1d;
					coeffIII_Z[coeffNumber(i, j, k, CoeffType.F_III_Z)] = 1d;
					coeffIII_Z[coeffNumber(i, j, k, CoeffType.NEGATIVE)] = -1d;
					set.add(new LinearConstraint(coeffIII_Z, Relationship.EQ, -rotateZ(i, k, j, true, aktuelleHyperebene) * DELTA_T/2 + letzteHyperebene[i][j][k][5] - letzteNegative));
					
					
					/* IV: TODO Strom berücksichtigen
					 * rot B = mu0*epsilon0*dE/dt
					 * rot B = mu0*epsilon0*(E[t+1][i][j][k] - E[t-1][i][j][k])*2/DELTA_T
					 * 2/DELTA_T * mu0*epsilon0 * E[t+1][i][j][k] = rot B + E[t-1][i][j][k]*2/DELTA_T * mu0*epsilon0
					 * E[t+1][i][j][k] - neg = DELTA_T/(2*mu0*epsilon0) * rot B + E[t-1][i][j][k]
					 */
					
					//B_X:
					double[] coeffIV_X = new double[COEFF_LENGTH];
					coeffIV_X[coeffNumber(i, j, k, CoeffType.E_X)] = 1d;
					coeffIV_X[coeffNumber(i, j, k, CoeffType.F_IV_X)] = 1d;
					coeffIV_X[coeffNumber(i, j, k, CoeffType.NEGATIVE)] = -1d;
					set.add(new LinearConstraint(coeffIV_X, Relationship.EQ, rotateX(i, k, j, false, aktuelleHyperebene) * DELTA_T/(2*MU0*EPSILON0) + letzteHyperebene[i][j][k][0] - letzteNegative));
					
					//B_Y:
					double[] coeffIV_Y = new double[COEFF_LENGTH];
					coeffIV_Y[coeffNumber(i, j, k, CoeffType.E_Y)] = 1d;
					coeffIV_Y[coeffNumber(i, j, k, CoeffType.F_IV_Y)] = 1d;
					coeffIV_Y[coeffNumber(i, j, k, CoeffType.NEGATIVE)] = -1d;
					set.add(new LinearConstraint(coeffIV_Y, Relationship.EQ, rotateY(i, k, j, false, aktuelleHyperebene) * DELTA_T/(2*MU0*EPSILON0) + letzteHyperebene[i][j][k][1] - letzteNegative));
					
					//B_X:
					double[] coeffIV_Z = new double[COEFF_LENGTH];
					coeffIV_Z[coeffNumber(i, j, k, CoeffType.E_Z)] = 1d;
					coeffIV_Z[coeffNumber(i, j, k, CoeffType.F_IV_Z)] = 1d;
					coeffIV_Z[coeffNumber(i, j, k, CoeffType.NEGATIVE)] = -1d;
					set.add(new LinearConstraint(coeffIV_Z, Relationship.EQ, rotateZ(i, k, j, false, aktuelleHyperebene) * DELTA_T/(2*MU0*EPSILON0) + letzteHyperebene[i][j][k][2] - letzteNegative));
				}
			}
		}

		
		LinearConstraintSet lcSet = new LinearConstraintSet(set);
		
		double[] coeffFunc = new double[COEFF_LENGTH];
		for(int i = 6*GROESSE*GROESSE*GROESSE+1;i < 14*GROESSE*GROESSE*GROESSE;i++)
			coeffFunc[i] = 1d;
		LinearObjectiveFunction func = new LinearObjectiveFunction(coeffFunc, 0d);
		
		PointValuePair pair = solver.optimize(lcSet, func, GoalType.MINIMIZE, new NonNegativeConstraint(true));
		
		//TODO Test
		System.out.println(pair.getValue());
		
		letzteHyperebene = aktuelleHyperebene;
		letzteNegative = aktuelleNegative;
		aktuelleHyperebene = new double[GROESSE][GROESSE][GROESSE][6]; 
		for(int i = 0;i < GROESSE;i++){
			for(int j = 0;j < GROESSE;j++){
				for(int k = 0;k < GROESSE;k++){
					aktuelleHyperebene[i][j][k][0] = pair.getPoint()[coeffNumber(i, j, k, CoeffType.E_X)];
					aktuelleHyperebene[i][j][k][1] = pair.getPoint()[coeffNumber(i, j, k, CoeffType.E_Y)];
					aktuelleHyperebene[i][j][k][2] = pair.getPoint()[coeffNumber(i, j, k, CoeffType.E_Z)];
					aktuelleHyperebene[i][j][k][3] = pair.getPoint()[coeffNumber(i, j, k, CoeffType.B_X)];
					aktuelleHyperebene[i][j][k][4] = pair.getPoint()[coeffNumber(i, j, k, CoeffType.B_Y)];
					aktuelleHyperebene[i][j][k][5] = pair.getPoint()[coeffNumber(i, j, k, CoeffType.B_Z)];
				}
			}
		}
		aktuelleNegative = pair.getPoint()[coeffNumber(0, 0, 0, CoeffType.NEGATIVE)];
	}

	public static void main(String[] args) {
		//zu Beginn nur Nullen
		Simulator simulator = new Simulator();
		ZeitStopper.start();
		simulator.update();
		//simulator.update();
		ZeitStopper.stop();
		
		for(int i=0;i < GROESSE;i++){
			for(int j=0;j < GROESSE;j++){
				for(int k=0;k < GROESSE;k++){
					System.out.print(simulator.aktuelleHyperebene[i][j][k][0]);
					System.out.print("     ");
					System.out.print(simulator.aktuelleHyperebene[i][j][k][1]);
					System.out.print("     ");
					System.out.print(simulator.aktuelleHyperebene[i][j][k][2]);
					System.out.print("     ");
					System.out.print(simulator.aktuelleHyperebene[i][j][k][3]);
					System.out.print("     ");
					System.out.print(simulator.aktuelleHyperebene[i][j][k][4]);
					System.out.print("     ");
					System.out.println(simulator.aktuelleHyperebene[i][j][k][5]);
				}
				System.out.println();
			}
			System.out.println();
			System.out.println();
		}
		
	}	

}
