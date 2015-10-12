package org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba;


import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import org.gcn.plinguacore.util.ExtendedLinkedHashSet;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.Triple;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.CellLikePsystem;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.RulesSet;



public class StaticMatrix {
	
	private ExtendedLinkedHashSet<MatrixColumn>columns;	// columns	(each one a consistent rule block)
	private ExtendedLinkedHashSet<MatrixRow>rows;		// rows		(each one a pair (object, label) with 2 possibilities: (obj,membr) or (obj',e))		 
	private Map<MatrixKey,Long>staticMatrix;			// cell values, each one a pair (row,column) with the value contained within.
	private Map<String,String>environmentLabels;		// environment labels
	private CellLikePsystem ps;
	
	public StaticMatrix(Psystem ps) throws PlinguaCoreException
	{
		if (ps==null)
			throw new NullPointerException();
		if (!(ps instanceof CellLikePsystem))
			throw new IllegalArgumentException();
		this.ps = (CellLikePsystem)ps;
		
		initColumns();
		checkProbabilities();
	
		initRowsAndCells();
		ps.setRules(null);
		
		
	}
	private void checkProbabilities() throws PlinguaCoreException
	{
		for (String environment:StaticMethods.getEnvironments((CellLikeSkinMembrane)ps.getMembraneStructure()).values())
		{
			for(MatrixColumn column:columns)
			{
				if (column instanceof EnvironmentRulesBlock && 
						!((EnvironmentRulesBlock)column).getEnvironmentLeftHandRule().getEnvironment().equals(environment))
					continue;
								
				float sum=0;
				for (IRightHandRule rhr:column.getRightHandRules())
				{
					sum+=rhr.getProbability(environment);
					
				}
				if (sum>1.0001 || sum<0.9999)
				{
					throw new PlinguaCoreException("The probabilities of the Rules Block "+column+" are not summing 1 for environment "+environment);
				}
			}
		}
	}
	
	private void initRowsAndCells()
	{
		rows = new ExtendedLinkedHashSet<MatrixRow>();
		staticMatrix = new HashMap<MatrixKey,Long>();
		Map<String,String>parents = StaticMethods.getParents((CellLikeSkinMembrane)ps.getMembraneStructure());
	
		for (MatrixColumn column:columns)
		{
			
			// parents: for each membrane returns stores its parent membrane
			Collection<Triple<String,String,Long>>ternas=column.getLeftHandRuleObjects(parents);
			for (Triple<String,String,Long>t:ternas)
			{
				MatrixRow row = new MatrixRow(t.getFirst(),t.getSecond()); 
				rows.add(row); /* Al ser un SET se evitan repeticiones */
				MatrixKey key = new MatrixKey(row,column);
				
				// we are now storing k instead of 1/k so losing accuracy is reduced to minimum (also we change accordingly the rest of the algorithm in
				// DynamicJava to deal with k) 
				
				//staticMatrix.put(key,1/(double)t.getThird());
				staticMatrix.put(key,t.getThird());
			}
		}
	
	}
	
	private void addRhr(IRule r,SkeletonRulesBlock b)
	{
		
		SkeletonRightHandRule rhrResult=null;
		for (SkeletonRightHandRule rhr:b.getRightHandRules())
		{
			// Si RHR = RHR(R) Y ENTORNO NO PERTENECE A KEYS
			if(rhr.getMainMultiSet().equals(r.getRightHandRule().getOuterRuleMembrane().getMultiSet()) &&
					rhr.getParentMultiSet().equals(r.getRightHandRule().getMultiSet()) &&
					rhr.getRuleId(StaticMethods.getEnvironment(r))==null)
			{
				 rhrResult=rhr;
				 break;
			}
		}
		if (rhrResult==null)
		{
			rhrResult=new SkeletonRightHandRule(r);
			b.getRightHandRules().add(rhrResult);
		}
		rhrResult.setProbability(StaticMethods.getEnvironment(r), StaticMethods.getProbability(r));
		//System.out.println("HOLAAA "+StaticMethods.getEnvironment(r)+" "+ r.getRuleId());
		rhrResult.setRuleId(StaticMethods.getEnvironment(r), r.getRuleId());
		
		
	}
	
	
	private void initColumns()
	{
		RulesSet rulesSet = ps.getRules();
		environmentLabels = StaticMethods.getEnvironments((CellLikeSkinMembrane)ps.getMembraneStructure());
		columns = new ExtendedLinkedHashSet<MatrixColumn>();
		SkeletonRulesBlock b2,b1=new SkeletonRulesBlock();
		EnvironmentRulesBlock e2,e1 = new EnvironmentRulesBlock();
		EnvironmentRightHandRule erhr=new EnvironmentRightHandRule();
		
		Iterator<IRule> it=rulesSet.iterator();
		while(it.hasNext())
		{
			IRule r = it.next();
			if (StaticMethods.isSkeletonRule(r,environmentLabels))
			{
	
				b1.getSkeletonLeftHandRule().set(r);
				// modification to consider right side rule charge
				b1.setrCharge(r.getRightHandRule().getOuterRuleMembrane().getCharge());
				b2 = (SkeletonRulesBlock)columns.get(b1);
				if (b2==null)
				{
					b2 = (SkeletonRulesBlock)b1.clone();
					columns.add(b2);
				}
				
				addRhr(r,b2);
				
				
				
			}
			else
			{
				e1.getEnvironmentLeftHandRule().set(r);
				e2 = (EnvironmentRulesBlock)columns.get(e1);
				if (e2==null)
				{
					e2 = (EnvironmentRulesBlock)e1.clone();
					columns.add(e2);
				}
				erhr = new EnvironmentRightHandRule();
				erhr.set(r);
				e2.getEnvironmentRightHandRules().add(erhr);
				
			}
		}
		
	}
	
/*
	private void initColumns()
	{
		RulesSet rulesSet = ps.getRules();
		environmentLabels = StaticMethods.getEnvironments((CellLikeSkinMembrane)ps.getMembraneStructure());
		columns = new ExtendedLinkedHashSet<MatrixColumn>();
		SkeletonRulesBlock b2,b1=new SkeletonRulesBlock();
		SkeletonRightHandRule rhr2,rhr1 = new SkeletonRightHandRule();
		EnvironmentRulesBlock e2,e1 = new EnvironmentRulesBlock();
		EnvironmentRightHandRule erhr=new EnvironmentRightHandRule();
		
		Iterator<IRule> it=rulesSet.iterator();
		while(it.hasNext())
		{
			IRule r = it.next();
			if (StaticMethods.isSkeletonRule(r,environmentLabels))
			{
	
				b1.getSkeletonLeftHandRule().set(r);
				// modification to consider right side rule charge
				b1.setrCharge(r.getRightHandRule().getOuterRuleMembrane().getCharge());
				b2 = (SkeletonRulesBlock)columns.get(b1);
				if (b2==null)
				{
					b2 = (SkeletonRulesBlock)b1.clone();
					columns.add(b2);
				}
				
				// Create an empty rhr with r
				rhr1.set(r);
				
				// Search for rhr with r in the block
				rhr2=b2.getSkeletonRightHandRules().get(rhr1);
				
				// If it doesn't exist
				if (rhr2==null)
				{
					rhr2 = (SkeletonRightHandRule)rhr1.clone();
					b2.getSkeletonRightHandRules().add(rhr2);
				}
				else
				{
					// Si existe y el entorno está -> crear uno nuevo
				}
			
				// Add the prob
				rhr2.setProbability(StaticMethods.getEnvironment(r), StaticMethods.getProbability(r));
				rhr2.setRuleId(StaticMethods.getEnvironment(r), r.getRuleId());
			}
			else
			{
				e1.getEnvironmentLeftHandRule().set(r);
				e2 = (EnvironmentRulesBlock)columns.get(e1);
				if (e2==null)
				{
					e2 = (EnvironmentRulesBlock)e1.clone();
					columns.add(e2);
				}
				erhr = new EnvironmentRightHandRule();
				erhr.set(r);
				e2.getEnvironmentRightHandRules().add(erhr);
				
			}
		}
		
	}
	
	*/
	

	public ExtendedLinkedHashSet<MatrixRow> getRows() {
		return rows;
	}


	public Map<MatrixKey, Long> getStaticMatrix() {
		return staticMatrix;
	}


	public ExtendedLinkedHashSet<MatrixColumn> getColumns() {
		return columns;
	}

	
	

	public Map<String, String> getEnvironmentLabels() {
		return environmentLabels;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str = "Columns: ";
		str+=columns.toString()+"\n";
		str+="Rows: "+rows.toString()+"\n";
		str+="Cells: "+staticMatrix.toString();
		return str;
	}
	
	

}
