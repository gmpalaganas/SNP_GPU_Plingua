package org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.cellLike.CellLikeConfiguration;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeNoSkinMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;

public class EnvironmentRightHandRule implements IRightHandRule,Cloneable{
	
	private Float probability;
	private long ruleId;

	private Map<String,MultiSet<String>>newObjects;
	
	public EnvironmentRightHandRule()
	{
		newObjects=new HashMap<String,MultiSet<String>>();
		
	}

	@Override
	public Long getRuleId(String environment) {
		return ruleId;
	}

	@Override
	public void setRuleId(String environment,Long ruleId) {
		this.ruleId = ruleId;
	}


	public void set(IRule r)
	{
		setProbability(StaticMethods.getProbability(r));
		setRuleId("",r.getRuleId());
		if (r.getRightHandRule().getOuterRuleMembrane().getInnerRuleMembranes().isEmpty())
		{
			if (!r.getRightHandRule().getOuterRuleMembrane().getMultiSet().isEmpty())
				newObjects.put(r.getRightHandRule().getOuterRuleMembrane().getLabel(), r.getRightHandRule().getOuterRuleMembrane().getMultiSet());
		}
		else
		for (InnerRuleMembrane irm:r.getRightHandRule().getOuterRuleMembrane().getInnerRuleMembranes())
		{
			if (!irm.getMultiSet().isEmpty())
				newObjects.put(irm.getLabel(), irm.getMultiSet());
		}
		
	}

	public void setProbability(float probability) {
		this.probability=probability;
	}

	public Map<String, MultiSet<String>> getNewObjects() {
		return newObjects;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str="";
		for (Entry<String,MultiSet<String>> entry:newObjects.entrySet())
		{
			str+="("+entry.getValue()+")'"+entry.getKey()+" ";
		}
		str+="::"+probability;
		return str;
	}

	@Override
	public Float getProbability(String environment) {
		// TODO Auto-generated method stub
		return probability;
	}


	@Override
	public void execute(CellLikeSkinMembrane ms, Map<String, Integer> map,String mainMembraneLabel,
			String environment, long multiplicity) {
		// TODO Auto-generated method stub
	
		for (Entry<String,MultiSet<String>>entry:newObjects.entrySet())
		{
			String e1=entry.getKey();
			MultiSet<String>ms1=entry.getValue();
			Membrane m = StaticMethods.getMembrane(e1, e1, ms, map);
			m.getMultiSet().addAll(ms1, multiplicity);
		}
	}


	@Override
	public String toString(String environment) {
		// TODO Auto-generated method stub
		String str=" -->";
		for (Entry<String,MultiSet<String>>entry:newObjects.entrySet())
		{
			str+=" ("+entry.getValue()+")'"+entry.getKey();
		}
		str+=" :: "+probability;
		return str;
	}


	@Override
	protected Object clone()  {
		// TODO Auto-generated method stub
		EnvironmentRightHandRule e=new EnvironmentRightHandRule();
		e.probability=new Float(this.probability);
		e.newObjects=new HashMap<String,MultiSet<String>>(this.newObjects);
		e.ruleId=ruleId;
		return e;
	}


	



	
	

}
