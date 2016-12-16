package de.khb.maxwell.versuch3;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * 
 * @author Kilian
 * 
 * Orts- und Zeitstetige Simulation mit Polynomen
 *
 */
public class Simulator {
	public static final int GRAD = 2;
	
	private static double MU0 = 4*Math.PI*Math.pow(10d, -7d);
	private static double LIGHTSPEED = 299_792_458d;
	
	/**
	 * 
	 * @param ladung0 [i][j][k] Koeffizienten bei t = 0
	 * @param strom0 [i][j][k][X/Y/Z] Koeffizienten bei t = 0
	 * @return [i][j][k][l][EX/EY/EZ/BX/BY/BZ/r/jX/jY/jZ]
	 */
	public static double[][][][][] simulate(double[][][] ladung0, double[][][][] strom0){
		/*
		 * Durchnummerierung der Koeff:
		 * i*10*(GRAD+1)^3+j*10*(GRAD+1)^2+k*10*(GRAD+1)^1+l*10+typ
		 */
		
		
		double[][] matrixArray = new double[12*((int) Math.pow(GRAD+1,4)) + 4*((int) Math.pow(GRAD+1,3))][10*((int) Math.pow(GRAD+1,4))];
		double[] rechteSeite = new double[12*((int) Math.pow(GRAD+1,4)) + 4*((int) Math.pow(GRAD+1,3))];
		//Gleichungen einfügen
		
		//Anfangsbedingungen:
		int zaehlerZeile = 0;
		for(int i = 0;i < GRAD+1;i++){
			for(int j = 0;j < GRAD+1;j++){
				for(int k = 0;k < GRAD+1;k++){
					//Ladung
					matrixArray[zaehlerZeile][grundnummer(i, j, k, 0)+6] = 1;
					rechteSeite[zaehlerZeile] = ladung0[i][j][k];
					zaehlerZeile++;
					
					//Strom
					matrixArray[zaehlerZeile][grundnummer(i, j, k, 0)+7] = 1;
					rechteSeite[zaehlerZeile] = strom0[i][j][k][0];
					zaehlerZeile++;
					matrixArray[zaehlerZeile][grundnummer(i, j, k, 0)+8] = 1;
					rechteSeite[zaehlerZeile] = strom0[i][j][k][1];
					zaehlerZeile++;
					matrixArray[zaehlerZeile][grundnummer(i, j, k, 0)+9] = 1;
					rechteSeite[zaehlerZeile] = strom0[i][j][k][2];
					zaehlerZeile++;
				}
			}
		}
		
		/* Maxwell I:
		 * div E = q
		 * dEX/dx + dEY/dy + dEZ/dz - q = 0
		 * (i+1)*aEX_(i+1),j,k,l + ...
		 */
		for(int i = 0;i < GRAD+1;i++){
			for(int j = 0;j < GRAD+1;j++){
				for(int k = 0;k < GRAD+1;k++){
					for(int l = 0;l < GRAD+1;l++){
						if(i<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i+1, j, k, l)+0] = i+1;
						if(j<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i, j+1, k, l)+1] = j+1;
						if(k<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i, j, k+1, l)+2] = k+1;
						
						matrixArray[zaehlerZeile][grundnummer(i, j, k, l)+6] = -1;
						
						rechteSeite[zaehlerZeile] = 0;
						
						zaehlerZeile++;
					}
				}
			}
		}
		
		/* Maxwell II:
		 * div B = 0
		 * dBX/dx + dBY/dy + dBZ/dz = 0
		 * (i+1)*aBX_(i+1),j,k,l + ...
		 */
		for(int i = 0;i < GRAD+1;i++){
			for(int j = 0;j < GRAD+1;j++){
				for(int k = 0;k < GRAD+1;k++){
					for(int l = 0;l < GRAD+1;l++){
						if(i<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i+1, j, k, l)+3+0] = i+1;
						if(j<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i, j+1, k, l)+3+1] = j+1;
						if(k<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i, j, k+1, l)+3+2] = k+1;
						
						rechteSeite[zaehlerZeile] = 0;
						
						zaehlerZeile++;
					}
				}
			}
		}
		
		/* Maxwell III:
		 * rot E = -dB/dt
		 * 
		 * X: dEZ/dy - dEy/dz + dBX/dt = 0
		 * ...
		 */
		for(int i = 0;i < GRAD+1;i++){
			for(int j = 0;j < GRAD+1;j++){
				for(int k = 0;k < GRAD+1;k++){
					for(int l = 0;l < GRAD+1;l++){
						//X: dEZ/dy - dEy/dz + dBX/dt = 0
						if(j<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i, j+1, k, l)+2] = j+1;
						if(k<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i, j, k+1, l)+1] = -(k+1);
						if(l<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i, j, k, l+1)+3+0] = l+1;
						
						rechteSeite[zaehlerZeile] = 0;
						zaehlerZeile++;
						
						//Y: dEx/dz - dEz/dx + dBy/dt = 0
						if(k<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i, j, k+1, l)+0] = k+1;
						if(i<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i+1, j, k, l)+2] = -(i+1);
						if(l<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i, j, k, l+1)+3+1] = l+1;
						
						rechteSeite[zaehlerZeile] = 0;
						zaehlerZeile++;
						
						//Z: dEy/dx - dEx/dy + dBz/dt = 0
						if(i<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i+1, j, k, l)+1] = i+1;
						if(j<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i, j+1, k, l)+0] = -(j+1);
						if(l<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i, j, k, l+1)+3+2] = l+1;
						
						rechteSeite[zaehlerZeile] = 0;
						zaehlerZeile++;
					}
				}
			}
		}
		
		
		/* Maxwell IV:
		 * rot B = 1/c^2 * dE/dt + mu0*j
		 * 
		 * X: dBZ/dy - dBy/dz - 1/c^2 * dEX/dt - mu0*jX = 0
		 * ...
		 */
		for(int i = 0;i < GRAD+1;i++){
			for(int j = 0;j < GRAD+1;j++){
				for(int k = 0;k < GRAD+1;k++){
					for(int l = 0;l < GRAD+1;l++){
						//X: dBZ/dy - dBy/dz - 1/c^2 * dEX/dt - mu0*jX = 0
						if(j<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i, j+1, k, l)+3+2] = j+1;
						if(k<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i, j, k+1, l)+3+1] = -(k+1);
						if(l<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i, j, k, l+1)+0] = -(l+1)/Math.pow(LIGHTSPEED, 2);
						matrixArray[zaehlerZeile][grundnummer(i, j, k, l)+7] = -MU0;
						
						rechteSeite[zaehlerZeile] = 0;
						zaehlerZeile++;
						
						//Y: dBx/dz - dBz/dx - 1/c^2 * dEy/dt - mu0*jY = 0
						if(k<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i, j, k+1, l)+3+0] = k+1;
						if(i<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i+1, j, k, l)+3+2] = -(i+1);
						if(l<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i, j, k, l+1)+1] = -(l+1)/Math.pow(LIGHTSPEED, 2);
						matrixArray[zaehlerZeile][grundnummer(i, j, k, l)+8] = -MU0;
						
						rechteSeite[zaehlerZeile] = 0;
						zaehlerZeile++;
						
						//Z: dBy/dx - dBx/dy - 1/c^2 * dEz/dt - mu0*jZ = 0
						if(i<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i+1, j, k, l)+3+1] = i+1;
						if(j<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i, j+1, k, l)+3+0] = -(j+1);
						if(l<GRAD)
							matrixArray[zaehlerZeile][grundnummer(i, j, k, l+1)+2] = -(l+1)/Math.pow(LIGHTSPEED, 2);;
						matrixArray[zaehlerZeile][grundnummer(i, j, k, l)+9] = -MU0;
							
						rechteSeite[zaehlerZeile] = 0;
						zaehlerZeile++;
					}
				}
			}
		}

		
		RealMatrix matrix = new Array2DRowRealMatrix(matrixArray);
		QRDecomposition qrDec = new QRDecomposition(matrix);
		DecompositionSolver solver = qrDec.getSolver();
		RealMatrix solution = solver.solve(new Array2DRowRealMatrix(rechteSeite));
		//RealMatrix solution = new Array2DRowRealMatrix(new double[10*((int) Math.pow(GRAD+1,4))]);
		
		double[][][][][] rueck = new double[GRAD+1][GRAD+1][GRAD+1][GRAD+1][10];
		//umrechnen:
		for(int i = 0;i < GRAD+1;i++){
			for(int j = 0;j < GRAD+1;j++){
				for(int k = 0;k < GRAD+1;k++){
					for(int l = 0;l < GRAD+1;l++){
						int grundnummer = grundnummer(i,j,k,l);
						for(int a = 0;a < 10;a++)
							rueck[i][j][k][l][a] = solution.getEntry(grundnummer+a, 0);
					}
				}
			}
		}
		
		return rueck;
	}
				
	private static int grundnummer(int i, int j, int k, int l){
		return (int) (i*10*Math.pow(GRAD+1,3)+j*10*Math.pow(GRAD+1,2)+k*10*Math.pow(GRAD+1,1)+l*10);
	}
}
