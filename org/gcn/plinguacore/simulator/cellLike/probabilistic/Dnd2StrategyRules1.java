package org.gcn.plinguacore.simulator.cellLike.probabilistic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcn.plinguacore.util.Triple;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.rule.IConstantRule;
import org.gcn.plinguacore.util.psystem.rule.IRule;

public class Dnd2StrategyRules1 {
	
	
	private Map<String,Integer> ruleIndex;
	private Map<String,Integer> objectIndex;
	
	private double t1[][];
	private double t2[][];
	
	
	public Dnd2StrategyRules1(Psystem psystem)
	{
		ruleIndex = new LinkedHashMap<String,Integer>();
		objectIndex = new LinkedHashMap<String,Integer>();
		float c;
		int rIndex =0;
		int oIndex =0;
		Iterator<IRule> it=psystem.getRules().iterator();
		
		while(it.hasNext())
		{
			IRule r = it.next();
			c=0;
			if (r instanceof IConstantRule) 
			{
				c = ((IConstantRule) r).getConstant();
			}
			else
				c=1;
			
			if (c==1)
			{
				if (!ruleIndex.containsKey(r.toString()))
				{
					ruleIndex.put(r.toString(), rIndex);
					rIndex++;
					Iterator<String> it1=r.getLeftHandRule().getObjects().iterator();
					while(it1.hasNext())
					{
						String obj = it1.next();
						if (!objectIndex.containsKey(obj))
						{
							objectIndex.put(obj, oIndex);
							oIndex++;
						}
					}
				}
			}
		}
		
		t1 = new double[rIndex][oIndex];
		t2 = new double[rIndex][oIndex];
		
	}
	
	private static Set<Triple<String,String,Double>>lhrObjects(IRule r)
	{
		Set<Triple<String,String,Double>> set = new HashSet<Triple<String,String,Double>>();
		
		//r.getLeftHandRule().getMultiSet()
		
		return set;
	}
	
	

}
