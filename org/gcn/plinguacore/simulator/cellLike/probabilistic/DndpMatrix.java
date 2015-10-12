package org.gcn.plinguacore.simulator.cellLike.probabilistic;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.List;

import java.util.Map;
import java.util.Set;



import org.gcn.plinguacore.util.Pair;
import org.gcn.plinguacore.util.ShuffleIterator;
import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeNoSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;


public class DndpMatrix {
	
	private Map<HashKey,Pair<Double,Double>>matrix;
	private Map<ProbabilisticLeftHandRule,Set<HashKey>>columns;
	private Map<Pair<String,Integer>,Double>rowSum;
	private Map<ProbabilisticLeftHandRule,Long>minimos;
	private Map<ProbabilisticLeftHandRule,List<IRule>>rulesByLeftHandRule;
	private Map<ProbabilisticLeftHandRule,Long>maximos;
	private List<ProbabilisticLeftHandRule>filterLhrList;
	private List<ProbabilisticLeftHandRule>noFilterLhrList;
	private Map<Integer,Byte>selectedCharges;
	private boolean emptyMatrix;
	
	public DndpMatrix(Psystem psystem)
	{
		initMatrix(psystem);
		
	}
	
	
	
	public Map<HashKey, Pair<Double, Double>> getMatrix() {
		return matrix;
	}
	public long getMinimunByLeftHandRule(ProbabilisticLeftHandRule lhr)
	{
		return minimos.get(lhr);
	}
	
	public long getMaximunByLeftHandRule(ProbabilisticLeftHandRule lhr)
	{
		return maximos.get(lhr);
	}
	
	public Iterator<ProbabilisticLeftHandRule>getFilterColumsIterator()
	{
		return new ShuffleIterator<ProbabilisticLeftHandRule>(filterLhrList);
	}
	private byte getRightCharge(ProbabilisticLeftHandRule lhr)
	{
		return rulesByLeftHandRule.get(lhr).get(0).getRightHandRule().getOuterRuleMembrane().getCharge();
	}
	public void init()
	{
		selectedCharges.clear();
		emptyMatrix=false;
	}

	public boolean isEmptyMatrix()
	{
		return emptyMatrix;
	}
	
	public void filterAndUpdateMatrix(Configuration tmpCnf)
	{
		minimos.clear();
		maximos.clear();
		rowSum.clear();
		filterLhrList.clear();
		emptyMatrix=true;
		
		Iterator<ProbabilisticLeftHandRule>columnIt = new ShuffleIterator<ProbabilisticLeftHandRule>(noFilterLhrList);
		
		while(columnIt.hasNext())
		{
			ProbabilisticLeftHandRule lhr = columnIt.next();
			long count = lhr.countExecutions(tmpCnf.getMembraneStructure().getMembrane(lhr.getMainMembraneId()));
			if (count>0)
			{
				
				Byte charge = selectedCharges.get(lhr.getMainMembraneId());
				
				if (charge==null || charge==getRightCharge(lhr))
				{
					maximos.put(lhr, count);
					filterLhrList.add(lhr);
					if (charge==null)
						selectedCharges.put(lhr.getMainMembraneId(),getRightCharge(lhr));
					Iterator<HashKey>itKey=columns.get(lhr).iterator();
					while (itKey.hasNext())
					{
						HashKey key = itKey.next();
						Pair<String,Integer>pair = new Pair<String,Integer>(key.getObject(),key.getId());
						double value = matrix.get(key).getFirst();
						if(rowSum.containsKey(pair))
							value=value+rowSum.get(pair);
						rowSum.put(pair, value);
					}
				}
			}
		}
		
		Iterator<ProbabilisticLeftHandRule>maximosIt=maximos.keySet().iterator();
		
		while (maximosIt.hasNext())
		{
			ProbabilisticLeftHandRule lhr = maximosIt.next();
			
				Iterator<HashKey>itKey=columns.get(lhr).iterator();
				while (itKey.hasNext())
				{
					HashKey key = itKey.next();
					Pair<String,Integer>pair = new Pair<String,Integer>(key.getObject(),key.getId());
					Pair<Double,Double>value = matrix.get(key);
					double sum=rowSum.get(pair);
					long multiplicity = tmpCnf.getMembraneStructure().getMembrane(key.getId()).getMultiSet().count(key.getObject());
					
						
					value.setSecond(multiplicity*value.getFirst()*value.getFirst()/sum);
					long floorValue=(long)Math.floor(value.getSecond());
					emptyMatrix = emptyMatrix && (floorValue==0);
					if (!minimos.containsKey(lhr))
						minimos.put(lhr,floorValue);
					else
					{
						double aux = minimos.get(lhr);
						if (value.getSecond()<aux)
							minimos.put(lhr,floorValue);
					}
				}
			
		}
		
		
	
		
		
		
	
		
	
	}


	


	public Map<ProbabilisticLeftHandRule, Set<HashKey>> getColumns() {
		return columns;
	}

	public Iterator<IRule> getRulesByLeftHandRuleIterator(ProbabilisticLeftHandRule leftHandRule){
		return new ShuffleIterator<IRule>(rulesByLeftHandRule.get(leftHandRule));
	}
	
	private void groupRulesByLeftHandRule(ProbabilisticLeftHandRule leftHandRule, IRule rule) {
		// TODO Auto-generated method stub
		List<IRule> rulesList;
		if(!rulesByLeftHandRule.containsKey(leftHandRule))
		{
			rulesList = new ArrayList<IRule>();
			rulesByLeftHandRule.put(leftHandRule, rulesList);
		}
		else
			rulesList = rulesByLeftHandRule.get(leftHandRule);
		rulesList.add(rule);
		
	}



	private void initMatrix(Psystem psystem)
	{
		matrix = new HashMap<HashKey,Pair<Double,Double>>();
		columns = new HashMap<ProbabilisticLeftHandRule,Set<HashKey>>();
		rowSum= new HashMap<Pair<String,Integer>,Double>();
		minimos = new HashMap<ProbabilisticLeftHandRule,Long>();
		maximos = new HashMap<ProbabilisticLeftHandRule,Long>();
		rulesByLeftHandRule = new HashMap<ProbabilisticLeftHandRule,List<IRule>>();
		filterLhrList=new ArrayList<ProbabilisticLeftHandRule>();
		noFilterLhrList=new ArrayList<ProbabilisticLeftHandRule>();
		selectedCharges=new HashMap<Integer,Byte>();
		
		Iterator<? extends Membrane>itMembrane=psystem.getMembraneStructure().getAllMembranes().iterator();
		
		while(itMembrane.hasNext())
		{
			Membrane m = itMembrane.next();
			for (int charge=-1;charge<=1;charge++)
			{
				Iterator<IRule>itRule=psystem.getRules().iterator(m.getLabel(), m.getLabelObj().getEnvironmentID(), charge);	
				while(itRule.hasNext())
				{
					IRule rule=itRule.next();
					ProbabilisticLeftHandRule lhr=new ProbabilisticLeftHandRule(rule,(CellLikeMembrane)m);
					groupRulesByLeftHandRule(lhr,rule);
					
					Iterator<String>itObject=lhr.getMainMembraneMultiSet().entrySet().iterator();
					
					while(itObject.hasNext())
					{
						String object=itObject.next();
						HashKey key =new HashKey(lhr,object,lhr.getMainMembraneId());
						
						matrix.put(key, 
								new Pair<Double,Double>(new Double(1/(double)lhr.getMainMembraneMultiSet().count(object)),0.0));
						Set<HashKey>l = columns.get(lhr);
						if (l==null)
						{
							l = new LinkedHashSet<HashKey>();
							columns.put(lhr, l);
							noFilterLhrList.add(lhr);
						}
						l.add(key);
						
						
					}
					
					if (!((CellLikeMembrane)m).isSkinMembrane())
					{
						int parentId = ((CellLikeNoSkinMembrane)m).getParentMembrane().getId();
						itObject = lhr.getParentMembraneMultiSet().entrySet().iterator();
						while(itObject.hasNext())
						{
							String object = itObject.next();
							HashKey key = new HashKey(lhr,object,parentId);
							matrix.put(key,  new Pair<Double,Double>(new Double(1/(double)lhr.getParentMembraneMultiSet().count(object)),0.0));
							Set<HashKey>l = columns.get(lhr);
							if (l==null)
							{
								l = new LinkedHashSet<HashKey>();
								columns.put(lhr, l);
								noFilterLhrList.add(lhr);
							}
							l.add(key);
						}
					
					}
					
					
				}
			
			}
			
		}
	
	}



	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str="";
		Iterator <HashKey> it = matrix.keySet().iterator();
		while(it.hasNext())
		{
			HashKey key = it.next();
			str+=key.toString()+" = "+matrix.get(key);
			if (it.hasNext())
				str+="\n";
		}
		Iterator<ProbabilisticLeftHandRule>it1 = columns.keySet().iterator();
		if (it1.hasNext())
			str+="\n";
		while(it1.hasNext())
		{
			ProbabilisticLeftHandRule lhr = it1.next();
			str+=lhr.toString()+" = "+columns.get(lhr);
			if (it1.hasNext())
				str+="\n";
		}
		return str;
	}
	

}
