package org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcn.plinguacore.util.ExtendedLinkedHashSet;
import org.gcn.plinguacore.util.Triple;
import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;

public class EnvironmentRulesBlock extends MatrixColumn implements Cloneable{
	
	private EnvironmentLeftHandRule environmentLeftHandRule;
	private List<EnvironmentRightHandRule> environmentRightHandRules=null;
	
	public EnvironmentRulesBlock()
	{
		environmentLeftHandRule = new EnvironmentLeftHandRule();
	}
	public EnvironmentRulesBlock(EnvironmentLeftHandRule environmentLeftHandRule)
	{
		this.environmentLeftHandRule=environmentLeftHandRule;
	}

	public EnvironmentLeftHandRule getEnvironmentLeftHandRule() {
		return environmentLeftHandRule;
	}
	

	public List<EnvironmentRightHandRule> getEnvironmentRightHandRules() {
		if (environmentRightHandRules==null)
			environmentRightHandRules= new ArrayList<EnvironmentRightHandRule>();
		return environmentRightHandRules;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((environmentLeftHandRule == null) ? 0
						: environmentLeftHandRule.hashCode());
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
		EnvironmentRulesBlock other = (EnvironmentRulesBlock) obj;
		if (environmentLeftHandRule == null) {
			if (other.environmentLeftHandRule != null)
				return false;
		} else if (!environmentLeftHandRule
				.equals(other.environmentLeftHandRule))
			return false;
		return true;
	}

	@Override
	protected Object clone() {
		// TODO Auto-generated method stub
		return new EnvironmentRulesBlock((EnvironmentLeftHandRule)environmentLeftHandRule.clone());
				
		
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return environmentLeftHandRule.toString()+" "+getMin()+" "+environmentRightHandRules;
	}
	@Override
	public Collection<Triple<String, String, Long>> getLeftHandRuleObjects(
			Map<String, String> parents) {
		// TODO Auto-generated method stub
		List<Triple<String,String,Long>>l = new ArrayList<Triple<String,String,Long>>();
		
		//l.add(new Triple<String,String,Long>(environmentLeftHandRule.getObject(),environmentLeftHandRule.getEnvironment(),1L));
		l.add(new Triple<String,String,Long>(environmentLeftHandRule.getObject(),StaticMethods.GENERIC_ENVIRONMENT,1L));
		return l;
	}
	
	// retain the column if and only if the column environment equals the given environment
	
	@Override
	public boolean retainColumn(CellLikeSkinMembrane ms,
			Map<String, Integer> map, String environment) {
		// TODO Auto-generated method stub
		return environmentLeftHandRule.getEnvironment().equals(environment);

	}
	@Override
	public boolean removeLeftHandRuleObjects(CellLikeSkinMembrane ms,
			Map<String, Integer> map, String environment,long multiplicity) {
		// TODO Auto-generated method stub
		Membrane m = StaticMethods.getMembrane(environmentLeftHandRule.getEnvironment(), environment, ms, map);
		if (m==null)
			return false;
		m.getMultiSet().remove(environmentLeftHandRule.getObject(),multiplicity);
		
		return m.getMultiSet().contains(environmentLeftHandRule.getObject());
	}
	@Override
	public long countApplications(CellLikeSkinMembrane ms,
			Map<String, Integer> map, String environment) {
		// TODO Auto-generated method stub
		Membrane m = StaticMethods.getMembrane(environmentLeftHandRule.getEnvironment(), environment, ms, map);
		if (m==null)
			return 0;
		return m.getMultiSet().count(environmentLeftHandRule.getObject());
	}
	@Override
	public List<? extends IRightHandRule> getRightHandRules() {
		// TODO Auto-generated method stub
		return environmentRightHandRules;
	}
	@Override
	public String getMainLabel() {
		// TODO Auto-generated method stub
		return environmentLeftHandRule.getEnvironment();
	}
	@Override
	public String matrixColumnToString() {
		// TODO Auto-generated method stub
		return environmentLeftHandRule.toString();
	}
	@Override
	public boolean isSkeletonColumn() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isEnvironmentColumn() {
		// TODO Auto-generated method stub
		return true;
	}
	
	
	
	

}
