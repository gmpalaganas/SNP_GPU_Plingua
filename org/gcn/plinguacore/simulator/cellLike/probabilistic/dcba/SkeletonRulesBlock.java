package org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcn.plinguacore.util.ExtendedLinkedHashSet;
import org.gcn.plinguacore.util.Triple;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeNoSkinMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;

public class SkeletonRulesBlock extends MatrixColumn implements Cloneable{
	
	private SkeletonLeftHandRule skeletonLeftHandRule;
	
	private byte rCharge; // this is alpha prime but also alpha prime is include in every SkeletonRightHandRule.mainMembraneCharge
	
	private List<SkeletonRightHandRule>skeletonRightHandRules=null;
	
	
	
	
	
	public SkeletonRulesBlock()
	{
		super();
		skeletonLeftHandRule=new SkeletonLeftHandRule();
		rCharge=0;
		
		// initially skeletonRightHandRules = null (empty block)
	}
	
	public SkeletonRulesBlock(IRule r)
	{
		this(new SkeletonLeftHandRule(r),r.getRightHandRule().getOuterRuleMembrane().getCharge());
		
			// this sets up rCharge but doesn't add the fist rule to the block
			// it's expected for the algorithm to do this other way.
	}
	
	public SkeletonRulesBlock(SkeletonLeftHandRule skeletonLeftHandRule,byte rCharge) {
		super();
		this.skeletonLeftHandRule = skeletonLeftHandRule;
		this.rCharge = rCharge;
		
	}
	public SkeletonLeftHandRule getSkeletonLeftHandRule() {
		return skeletonLeftHandRule;
	}
	
	
	public byte getrCharge() {
		return rCharge;
	}
	
	public void setrCharge(byte rCharge)
	{
		this.rCharge = rCharge;
	}

	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + rCharge;
		result = prime
				* result
				+ ((skeletonLeftHandRule == null) ? 0 : skeletonLeftHandRule
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SkeletonRulesBlock other = (SkeletonRulesBlock) obj;
		if (rCharge != other.rCharge)
			return false;
		if (skeletonLeftHandRule == null) {
			if (other.skeletonLeftHandRule != null)
				return false;
		} else if (!skeletonLeftHandRule.equals(other.skeletonLeftHandRule))
			return false;
		return true;
	}

	@Override
	protected Object clone()  {
		// TODO Auto-generated method stub
		return new SkeletonRulesBlock((SkeletonLeftHandRule)getSkeletonLeftHandRule().clone(),getrCharge());
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		
		return skeletonLeftHandRule.toString()+" "+" "+Membrane.getChargeSymbol(rCharge)+" "+getMin()+" "+skeletonRightHandRules;
	}

	@Override
	public Collection<Triple<String, String, Long>> getLeftHandRuleObjects(Map<String,String>parents) 
			{
		// TODO Auto-generated method stub
		List<Triple<String,String,Long>>l = new ArrayList<Triple<String,String,Long>>();
	
		for(String o:skeletonLeftHandRule.getMainMultiSet().entrySet())
			l.add(new Triple<String,String,Long>(o,skeletonLeftHandRule.getMainMembraneLabel(),skeletonLeftHandRule.getMainMultiSet().count(o)));
		
		for (String o:skeletonLeftHandRule.getParentMultiSet().entrySet())
			l.add(new Triple<String,String,Long>(o,parents.get(skeletonLeftHandRule.getMainMembraneLabel()),skeletonLeftHandRule.getParentMultiSet().count(o)));
		

//		possible error: line should be
//		l.add(new Triple<String,String,Long>(o,skeletonLeftHandRule.getMainMembraneLabel(),skeletonLeftHandRule.getParentMultiSet().count(o)));
//		ask why
		
		return l;
	
	}

	// retain the column if and only if the charge in the left side of the rule matches.
	
	@Override
	public boolean retainColumn(CellLikeSkinMembrane ms,
			Map<String, Integer> map, String environment) {
		// TODO Auto-generated method stub
		Membrane m=StaticMethods.getMembrane(skeletonLeftHandRule.getMainMembraneLabel(), environment, ms, map);
		return m.getCharge()==skeletonLeftHandRule.getMainMembraneCharge();
	
	}
	


	// ask info about this method.
	
	@Override
	public boolean removeLeftHandRuleObjects(CellLikeSkinMembrane ms,
			Map<String, Integer> map, String environment, long multiplicity) {
		// TODO Auto-generated method stub
		
		CellLikeNoSkinMembrane m = (CellLikeNoSkinMembrane)StaticMethods.getMembrane(skeletonLeftHandRule.getMainMembraneLabel(), environment, ms, map);
		if (m==null)
			return false;
		
		boolean b1=false,b2=false;
		if (!skeletonLeftHandRule.getMainMultiSet().isEmpty())
		{
				m.getMultiSet().subtraction(skeletonLeftHandRule.getMainMultiSet(), multiplicity);
				b1=m.getMultiSet().countSubSets(skeletonLeftHandRule.getMainMultiSet())>0;
		}
				
		if (!skeletonLeftHandRule.getParentMultiSet().isEmpty())
		{
			m.getParentMembrane().getMultiSet().subtraction(skeletonLeftHandRule.getParentMultiSet(),multiplicity);
			b2=m.getParentMembrane().getMultiSet().countSubSets(skeletonLeftHandRule.getParentMultiSet())>0;
		}
		return b1 || b2;
		
		
	}

	@Override
	public long countApplications(CellLikeSkinMembrane ms,
			Map<String, Integer> map, String environment) {
		// TODO Auto-generated method stub
		
		CellLikeNoSkinMembrane m = (CellLikeNoSkinMembrane)StaticMethods.getMembrane(skeletonLeftHandRule.getMainMembraneLabel(), environment, ms, map);
		if (m==null)
			return 0;
	
		if (!skeletonLeftHandRule.getMainMultiSet().isEmpty() &&
				skeletonLeftHandRule.getParentMultiSet().isEmpty())
		{
			return m.getMultiSet().countSubSets(skeletonLeftHandRule.getMainMultiSet());
		}
		else
		if (skeletonLeftHandRule.getMainMultiSet().isEmpty() &&
					!skeletonLeftHandRule.getParentMultiSet().isEmpty())
		{
			return 	m.getParentMembrane().getMultiSet().countSubSets(skeletonLeftHandRule.getParentMultiSet());
		}
		else
		if (!skeletonLeftHandRule.getMainMultiSet().isEmpty() &&
						!skeletonLeftHandRule.getParentMultiSet().isEmpty())
		{
			return Math.min(m.getMultiSet().countSubSets(skeletonLeftHandRule.getMainMultiSet()),
					m.getParentMembrane().getMultiSet().countSubSets(skeletonLeftHandRule.getParentMultiSet()));
		}
		else
			return 0;
		
		
		
	
	}

	@Override
	public List<SkeletonRightHandRule> getRightHandRules() {
		// TODO Auto-generated method stub
		if (skeletonRightHandRules==null)
			skeletonRightHandRules=new ArrayList<SkeletonRightHandRule>();
		return skeletonRightHandRules;
		
	}

	@Override
	public String getMainLabel() {
		// TODO Auto-generated method stub
		return skeletonLeftHandRule.getMainMembraneLabel();
	}

	@Override
	public String matrixColumnToString() {
		// TODO Auto-generated method stub
		return skeletonLeftHandRule.toString()+" ("+Membrane.getChargeSymbol(rCharge)+")";
	}

	@Override
	public boolean isSkeletonColumn() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnvironmentColumn() {
		// TODO Auto-generated method stub
		return false;
	}

	


	
	

}
