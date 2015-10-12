package org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;

import org.gcn.plinguacore.simulator.cellLike.probabilistic.AbstractProbabilisticSimulator;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.CellLikeConfiguration;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;


public class DCBAProbabilisticSimulator extends AbstractProbabilisticSimulator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5683365879577783767L;
	private DynamicMatrix matrix;
	private long selectedRules=0;
	private boolean showRules=true;
	public static final boolean DEBUG=false;
	private boolean writeToFile=false;
	private int executionStep = 0;

	public DCBAProbabilisticSimulator(Psystem psystem) throws PlinguaCoreException{
		super(psystem);
		matrix = new DynamicMatrix(psystem);
	
		// TODO Auto-generated constructor stub
	}

	
	public DynamicMatrix getDynamicMatrix()
	{
		return matrix;
	}
	
	public StaticMatrix getStaticMatrix()
	{
		return matrix;
	}
	
	public void setWriteToFile(boolean w)
	{
		writeToFile = w;
	}
	
	public void microStepInit() {
		// TODO Auto-generated method stub
		
		super.microStepInit();
		
		executionStep++;
		
		if(writeToFile)
		{
			PrintStream sout = System.out;
			PrintStream serr = System.err;
					
			try 
			{

				String filename =  System.getProperty("user.dir") + "/steps/step-" + executionStep + ".txt";
				OutputStream output = new FileOutputStream(filename);
				PrintStream printOut = new PrintStream(output);
				
				System.setOut(printOut);
				System.setErr(printOut);     
			} 
			catch (Exception e)
			{
				System.setOut(sout);
				System.setErr(serr);
				writeToFile = false;
			}			
		}
				
	}
	
	
	public boolean isShowRules() {
		return showRules;
	}





	public void setShowRules(boolean showRules) {
		this.showRules = showRules;
	}





	@Override
	protected boolean hasSelectedRules() {
		// TODO Auto-generated method stub
	
		return selectedRules!=0;
	}





	@Override
	protected void microStepSelectRules() throws PlinguaCoreException{
		// TODO Auto-generated method stub
		CellLikeConfiguration cnf = (CellLikeConfiguration)currentConfig;
		selectedRules=0;
		
		for (String environment:matrix.getEnvironmentLabels().keySet())
		{
			if (DEBUG)
				System.out.println("### Environment: "+environment);
			
			//System.out.println("INIT "+matrix.getColumns().size());
			matrix.initData(environment);
			//System.out.println("FILTRO 1");
			matrix.filterColumns1(cnf, environment);
			//System.out.println("FILTRO 2");
			matrix.filterColumns2(cnf, environment);
			//System.out.println("CHEQUEO CONSISTENCIA");
			matrix.checkMutualConsistency(cnf, environment);
			//System.out.println("FILTRO 3");
			matrix.initFilterRows(cnf, environment);
			
			int k = 1;
			int A = 1;	/* accuracy */
			boolean allZeroes = matrix.getAllZeroes();
			
			while(k <= A && !allZeroes)
			{	
				//System.out.println("NORMALIZACION Y CALCULO DE MINIMOS");
				matrix.normalizeRowsAndCalculateMinimums(cnf, environment);
				allZeroes = matrix.getAllZeroes();
				
				if (DEBUG)
				{
					System.out.println("** Fase de selección. K="+(k)+":");
					System.out.println(matrix);
				}
				//System.out.println("BORRA PARTE IZQUIERDA");
				matrix.removeLeftHandRuleObjects(cnf,environment);								
				//matrix.filterColumns(cnf, environment);
				//System.out.println("FILTRO 2");
				matrix.filterColumns2(cnf, environment);
				//System.out.println("FILTRO FILAS");
				matrix.filterRows(cnf, environment);

				k++;				
			}
			
			matrix.setAllZeroes(false);
			//System.out.println("MAXIMALIDAD");
			matrix.maximality(cnf, environment);
			selectedRules+=matrix.getSelectedRules();
			if (DEBUG)
			{
				System.out.println("** Fase de maximalidad:");
				System.out.println(matrix);
				System.out.println("Selected Rules: "+selectedRules);
				
			}	
		
		
			
		}
		
	
	}





	




	@Override
	protected void microStepExecuteRules() {
		// TODO Auto-generated method stub
		CellLikeConfiguration cnf = (CellLikeConfiguration)currentConfig;
		for (String environment:matrix.getEnvironmentLabels().keySet())
			matrix.executeRules(cnf,environment,this);
		cnf.setNumber(cnf.getNumber()+1);
	}





	@Override
	protected void microStepSelectRules(ChangeableMembrane membrane,
			ChangeableMembrane tempMembrane) {
		// TODO Auto-generated method stub

	}

}
