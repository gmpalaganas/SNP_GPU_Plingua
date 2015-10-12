package org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule;

import java.util.Iterator;
import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.DecoratorCheckRule;

public class MultiEnvironment extends DecoratorCheckRule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3856117125403597907L;
	private int error=0;
	public MultiEnvironment(CheckRule cr) {
		super(cr);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean checkSpecificPart(IRule r) {
		error=0;
		// TODO Auto-generated method stub
		if (!r.getLeftHandRule().getOuterRuleMembrane().getInnerRuleMembranes().isEmpty() ||
				!r.getRightHandRule().getOuterRuleMembrane().getInnerRuleMembranes().isEmpty())
			return checkEnvironmentRule(r);
		
	
		
		return true;
	}
	
	private boolean checkEnvironmentRule(IRule r)
	{
				
		if (r.getLeftHandRule().getOuterRuleMembrane().getCharge()!=0)
		{
			error=1;
			return false;
		}
		
		if (!r.getLeftHandRule().getOuterRuleMembrane().getLabelObj().equals(r.getRightHandRule().getOuterRuleMembrane().getLabelObj()))
		{
			error=2;
			return false;
		}
				
		if (r.getRightHandRule().getOuterRuleMembrane().getCharge()!=0)
		{
			error=3;
			return false;
		}
		
		if (!r.getLeftHandRule().getMultiSet().isEmpty())
		{
			error=4;
			return false;
		}
		
		if (!r.getRightHandRule().getMultiSet().isEmpty())
		{
			error=5;
			return false;
		}
		
		if (!r.getLeftHandRule().getOuterRuleMembrane().getMultiSet().isEmpty())
		{
			error=6;
			return false;
		}
		
		if (!r.getRightHandRule().getOuterRuleMembrane().getMultiSet().isEmpty())
		{
			error=7;
			return false;
		}
		
		if (r.getRightHandRule().getOuterRuleMembrane().getInnerRuleMembranes().isEmpty() ||
				r.getLeftHandRule().getOuterRuleMembrane().getInnerRuleMembranes().isEmpty())
		{
			error=8;
			return false;
		}
		
		Iterator<InnerRuleMembrane>it = r.getLeftHandRule().getOuterRuleMembrane().getInnerRuleMembranes().iterator();
		Label label=null;
		while(it.hasNext())
		{
			InnerRuleMembrane irm = it.next();
			if (irm.getCharge()!=0)
			{
				error=9;
				return false;
			}
			
			if (irm.getMultiSet().size()>1)
			{
				error=11;
				return false;
			}
			if (irm.getMultiSet().size()==1 && label!=null)
			{
				error=12;
				return false;
			}
			if (irm.getMultiSet().size()==1)
			{
				error=13;
				label=irm.getLabelObj();
			}
		}
		if (label==null)
		{
			error=14;
			return false;
		}
		
			
		it = r.getRightHandRule().getOuterRuleMembrane().getInnerRuleMembranes().iterator();
		
		while(it.hasNext())
		{
			InnerRuleMembrane irm = it.next();
			if (irm.getCharge()!=0)
			{
				error=15;
				return false;
			}
			
			if (irm.getMultiSet().size()>1)
			{
				error=17;
				return false;
			}
			
			if (irm.getMultiSet().size()==0 && !irm.getLabelObj().equals(label))
			{
				error=19;
				return false;
			}
			
			
			/* Chequeo de que e_i y e_j deben ser distintos
			if (irm.getLabelObj().equals(label) && !irm.getMultiSet().isEmpty())
			{
				error=18;
				return false;
			}*/
		}
		
		return true;
	}

	@Override
	protected String getSpecificCause() {
		// TODO Auto-generated method stub
		return "The rule doesn't match the probabilistic framework (error "+error+")";
	}

}
