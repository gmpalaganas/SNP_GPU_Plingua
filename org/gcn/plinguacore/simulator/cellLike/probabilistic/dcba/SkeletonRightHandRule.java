package org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeNoSkinMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;

public class SkeletonRightHandRule extends SkeletonHandRule implements IRightHandRule,Cloneable{
	
	/* Probabilidad asociada a la regla según entorno */
	private Map<String,Float>probabilities;
	private Map<String,Long> ruleId;
	
	
	
	
	
	public SkeletonRightHandRule()
	{
		super();
		probabilities=new HashMap<String,Float>();
		ruleId = new HashMap<String,Long>();
	}
	
	@Override
	public Long getRuleId(String environment) {
		return ruleId.get(environment);
	}

	@Override
	public void setRuleId(String environment,Long ruleId) {
		this.ruleId.put(environment, ruleId);
	}


	public SkeletonRightHandRule(IRule r)
	{
		this (r.getRightHandRule().getOuterRuleMembrane().getCharge(),			// in fact, this is alpha prime
				r.getRightHandRule().getOuterRuleMembrane().getMultiSet(),
				r.getRightHandRule().getMultiSet());
		
	}
	

	

	@Override
	public void set(IRule r)
	{
		setMainMembraneCharge(r.getRightHandRule().getOuterRuleMembrane().getCharge());
		setMainMultiSet(r.getRightHandRule().getOuterRuleMembrane().getMultiSet());
		setParentMultiSet(r.getRightHandRule().getMultiSet());
	
	}
	
	

	public SkeletonRightHandRule(byte mainMembraneCharge,
			MultiSet<String> mainMultiSet, MultiSet<String> parentMultiSet) {
		super(mainMembraneCharge, mainMultiSet, parentMultiSet);
		probabilities=new HashMap<String,Float>();
		ruleId = new HashMap<String,Long>();
		// TODO Auto-generated constructor stub
	}
	public boolean checkEnvironment(String environment)
	{
		return probabilities.containsKey(environment);
	}
	
	public void setProbability(String environment,float probability)
	{
		probabilities.put(environment,probability);
	
	}
	
	
	
	@Override
	public Float getProbability(String environment)
	{
		// Si encontramos el entorno
		if (probabilities.containsKey(environment))
			return  probabilities.get(environment);
		// en caso contrario, buscamos las probabilidades por defecto
		if (probabilities.containsKey(""))
			return probabilities.get("");
		// no hay probabilidades para este entorno
		return 0F;
		
	}

	

	@Override
	protected Object clone()  {
		// TODO Auto-generated method stub
		SkeletonRightHandRule o = new SkeletonRightHandRule(getMainMembraneCharge(),getMainMultiSet(),getParentMultiSet());
		
		if (probabilities!=null)
			o.probabilities = new HashMap<String,Float>(probabilities);
		
		o.ruleId=new HashMap<String,Long>(ruleId);
		return o;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str=super.toString();
		
		if (!probabilities.isEmpty())
			str+=" "+probabilities.toString();
		return str;
		
	}

	@Override
	public void execute(CellLikeSkinMembrane ms, Map<String, Integer> map,
			String mainMembraneLabel, String environment, long multiplicity) {
		// TODO Auto-generated method stub
		CellLikeNoSkinMembrane m = (CellLikeNoSkinMembrane)StaticMethods.getMembrane(mainMembraneLabel, environment, ms, map);
	
		m.setCharge(getMainMembraneCharge());
		
		if (!getMainMultiSet().isEmpty())
			m.getMultiSet().addAll(getMainMultiSet(),multiplicity);
		
		if (!getParentMultiSet().isEmpty())
			m.getParentMembrane().getMultiSet().addAll(getParentMultiSet(),multiplicity);
	}

	@Override
	public String toString(String environment) {
		// TODO Auto-generated method stub
		String str=" --> ";
		str+=super.toString();
		str+=" :: "+getProbability(environment);
		
		return str;
	}

	
	

	

}
