package org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba;

import java.util.Map;


import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;

public interface IRightHandRule {
	
	public Float getProbability(String environment);
	public String toString(String environment);
	public void execute(CellLikeSkinMembrane ms,Map<String,Integer>map,String mainMembraneLabel,String environment,long multiplicity);
	public void setRuleId(String environment,Long ruleId);
	public Long getRuleId(String environment);
}
