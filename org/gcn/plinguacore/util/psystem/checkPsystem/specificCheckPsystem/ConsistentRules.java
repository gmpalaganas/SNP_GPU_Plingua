/* 
 * pLinguaCore: A JAVA library for Membrane Computing
 *              http://www.p-lingua.org
 *
 * Copyright (C) 2009  Research Group on Natural Computing
 *                     http://www.gcn.us.es
 *                      
 * This file is part of pLinguaCore.
 *
 * pLinguaCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pLinguaCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pLinguaCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gcn.plinguacore.util.psystem.checkPsystem.specificCheckPsystem;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.gcn.plinguacore.simulator.cellLike.probabilistic.ProbabilisticPsystemFactory;

import org.gcn.plinguacore.util.psystem.ActivationSets;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.CellLikePsystem;
import org.gcn.plinguacore.util.psystem.checkPsystem.CheckPsystem;
import org.gcn.plinguacore.util.psystem.checkPsystem.DecoratorCheckPsystem;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;


public class ConsistentRules extends DecoratorCheckPsystem {

	private List<IRule> causeRules=null;
	public ConsistentRules() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ConsistentRules(CheckPsystem cr) {
		super(cr);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3213988448397373493L;

	@Override
	protected boolean checkSpecificPart(Psystem p) {
		// TODO Auto-generated method stub
		causeRules=null;
		if (!(p instanceof CellLikePsystem))
			return true;
		if (!ProbabilisticPsystemFactory.isCheckConsistentRules())
			return true;
		Map<ActivationSets,List<IRule>>map = ProbabilisticPsystemFactory.getRulesByActivationSet((CellLikePsystem)p);
		
		Iterator<List<IRule>>it = map.values().iterator();
		while(it.hasNext())
		{
			boolean unconsistent=false;
			List<IRule>list=it.next();
			Map<String,Byte> map1 = new HashMap<String,Byte>();
			Iterator<IRule>it1=list.iterator();
			while(it1.hasNext() && !unconsistent)
			{
				IRule r = it1.next();
					
					
				String label=r.getRightHandRule().getOuterRuleMembrane().getLabel();
				byte ch =r.getRightHandRule().getOuterRuleMembrane().getCharge();
				if (map1.containsKey(label))
				{
					byte ch1=map1.get(label);
					if (ch1!=ch)
						unconsistent=true;
				}
				else
					map1.put(label,ch);
				Iterator<InnerRuleMembrane>it2 = r.getRightHandRule().getOuterRuleMembrane().getInnerRuleMembranes().iterator();
				while(it2.hasNext())
				{
					InnerRuleMembrane irm=it2.next();
					label=irm.getLabel();
					ch =irm.getCharge();
					if (map1.containsKey(label))
					{
						byte ch1=map1.get(label);
						if (ch1!=ch)
							unconsistent=true;
					}
					else
						map1.put(label,ch);
				
				}
			}
			if (unconsistent)
			{
				causeRules=list;
				return false;
			}
			
		}
		return true;
	}

	private String getCauseRules()
	{
		String str="";
		if (causeRules!=null)
		{
			Iterator<IRule>it=causeRules.iterator();
			while(it.hasNext())
			{
				IRule r = it.next();
				str+=r.toString();
				if (it.hasNext())
					str+="\n";
			}
		}
		return str;
	}
	@Override
	protected String getSpecificCause() {
		// TODO Auto-generated method stub
		if (causeRules==null)
			return "There are rules with unconsistent charges";
		else
		
			return "The following set of rules has unconsistent charges:\n"+getCauseRules();
		
	}

}
