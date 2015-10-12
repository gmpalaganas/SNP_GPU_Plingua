package org.gcn.plinguacore.simulator.cellLike.probabilistic;



import java.util.Iterator;

import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.RandomNumbersGenerator;

import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeNoSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;

import org.gcn.plinguacore.util.psystem.rule.IConstantRule;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;

public class Dnd4ProbabilisticSimulator extends AbstractProbabilisticSimulator implements ISelectionLoopAlgorithm{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3375087211519864303L;

	private DndpMatrix matrix;
	
	
	private int maxIterations=1;
	



	public Dnd4ProbabilisticSimulator(Psystem psystem) {
		super(psystem);
	
	
		matrix = new DndpMatrix(psystem);
		// TODO Auto-generated constructor stub
	}

	
	
	@Override
	public int getMaxIterations() {
		return maxIterations;
	}



	@Override
	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}




	@Override
	protected void microStepInit() {
		// TODO Auto-generated method stub
		super.microStepInit();
		matrix.init();
		
		
	}
	
	
	@Override
	protected void executeRule(IRule r, ChangeableMembrane m,
			MultiSet<String> environment, long count) {
		// TODO Auto-generated method stub
		if (r.getLeftHandRule().getOuterRuleMembrane().getInnerRuleMembranes().isEmpty())
			super.executeRule(r, m, environment, count);
		else
		if (m instanceof CellLikeNoSkinMembrane)
			super.executeRule(r, ((CellLikeNoSkinMembrane)m).getParentMembrane(), environment, count);
		
		
	}



	private void removeLeftHandRuleObjects(CellLikeMembrane temp,ProbabilisticLeftHandRule lhr,long count)
	{
		if (!lhr.getParentMembraneMultiSet().isEmpty() && !temp.isSkinMembrane())
			((CellLikeNoSkinMembrane)temp).getParentMembrane().getMultiSet().subtraction(lhr.getParentMembraneMultiSet(),count);
		if (!lhr.getMainMembraneMultiSet().isEmpty())
			temp.getMultiSet().subtraction(lhr.getMainMembraneMultiSet(), count);
	}
	
	private void selectRulesByMultinomial(ProbabilisticLeftHandRule lhr,CellLikeMembrane m,CellLikeMembrane temp,long total)
	{
		
		if (total==0)
			return;
		
		long N=total;
		
		double d=1.0;
		Iterator<IRule>rulesIt = matrix.getRulesByLeftHandRuleIterator(lhr);
		while(rulesIt.hasNext())
		{
			IRule r = rulesIt.next();
			long max = lhr.countExecutions(temp);
			if (N>max)
				N=max;
			long n;
			if (N==0)
				n=0;
			else
			if (!(r instanceof IConstantRule))
				n=N;
			else
			{
				double p = ((IConstantRule)r).getConstant();
				if (p==0)
					n=0;
				else
				if (p==1)
					n=N;
				else
				{
					p=p/d;
					double q = 1-p;
					d = d*q;
					if (d<0) d=0;
					if (p>=1)
						n=N;
					else
						n = RandomNumbersGenerator.getInstance().nextLongBi(N,p);
				}
			}
			
			if (n>0)
			{
				selectRule(r,m,n);
				removeLeftHandRuleObjects(temp,lhr,n);
				long resto=N-n;
				if (resto<0) resto=0;
				N=resto;
			}
			
			
		}
		
	}
	
	private void selectRulesByMultinomial(Configuration cnf, Configuration tmpCnf,boolean maximally)
	{
	
		Iterator<ProbabilisticLeftHandRule>filterColumnsIt=matrix.getFilterColumsIterator();
		
		while(filterColumnsIt.hasNext())
		{
			ProbabilisticLeftHandRule lhr = filterColumnsIt.next();
			CellLikeMembrane temp=(CellLikeMembrane)tmpCnf.getMembraneStructure().getMembrane(lhr.getMainMembraneId());
			CellLikeMembrane m=(CellLikeMembrane)cnf.getMembraneStructure().getMembrane(lhr.getMainMembraneId());
			long total;
			if (maximally)
				total=lhr.countExecutions(temp);
			else
				total=matrix.getMinimunByLeftHandRule(lhr);
			
			selectRulesByMultinomial(lhr,m,temp,total);
		}
		
	}
	
	

	@Override
	protected void microStepSelectRules(Configuration cnf, Configuration tmpCnf) {
		// TODO Auto-generated method stub
		
			
		
		for(int k1=0;k1<maxIterations && !matrix.isEmptyMatrix();k1++)
		{
			
			matrix.filterAndUpdateMatrix(tmpCnf); 
			
			selectRulesByMultinomial(cnf,tmpCnf,false);
		}
		
	
		/**********************/
		/*FASE DE MAXIMALIDAD */
		/**********************/
		selectRulesByMultinomial(cnf,tmpCnf,true);
		
	
	}




	@Override
	protected void microStepSelectRules(ChangeableMembrane membrane,
			ChangeableMembrane tempMembrane) {
		// TODO Auto-generated method stub
		
	}




	
}
