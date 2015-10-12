package org.gcn.plinguacore.simulator.cellLike.probabilistic;


import java.util.Iterator;


import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.rule.IConstantRule;
import org.gcn.plinguacore.util.psystem.rule.IRule;



public class NoMaximalSim extends AbstractProbabilisticSimulator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3990469922898085357L;

	public NoMaximalSim(Psystem psystem) {
		super(psystem);
		// TODO Auto-generated constructor stub
	}

	
	
	
	@Override
	protected void microStepSelectRules(ChangeableMembrane m,
			ChangeableMembrane temp) {
		// TODO Auto-generated method stub
		Iterator<IRule> it = getPsystem().getRules().iterator(
				temp.getLabel(),
				temp.getLabelObj().getEnvironmentID(),
				temp.getCharge(),true);
		
		while(it.hasNext())
		{
			IRule r = it.next();
			long N = r.countExecutions(m);
			long selections=0;
			if (N>0)
			{
				if (r instanceof IConstantRule)
				{
					double p = ((IConstantRule)r).getConstant();
					selections = BinomialProbabilisticSimulator.binomial(N, p);
				}
				else
				{
					selections=N;
				}
			}
			if (selections>0)
			{
				selectRule(r,m,selections);
				removeLeftHandRuleObjects(temp,r,selections);
			}
			
		}
	}

	

}
