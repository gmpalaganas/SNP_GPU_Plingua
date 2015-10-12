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

import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.gcn.plinguacore.simulator.cellLike.probabilistic.ProbabilisticPsystemFactory;
import org.gcn.plinguacore.util.psystem.ActivationSets;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.CellLikePsystem;
import org.gcn.plinguacore.util.psystem.checkPsystem.CheckPsystem;
import org.gcn.plinguacore.util.psystem.checkPsystem.DecoratorCheckPsystem;
import org.gcn.plinguacore.util.psystem.rule.IConstantRule;
import org.gcn.plinguacore.util.psystem.rule.IRule;


public class CompleteProbabilities extends DecoratorCheckPsystem implements CheckPsystem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8921870457272723574L;
	private List<IRule> causeRules=null;
	
	
	public CompleteProbabilities() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CompleteProbabilities(CheckPsystem cr) {
		super(cr);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected boolean checkSpecificPart(Psystem p) {
		// TODO Auto-generated method stub
		causeRules=null;
		if (!(p instanceof CellLikePsystem))
			return true;
		if (!ProbabilisticPsystemFactory.isCheckCompleteProbabilities())
			return true;
		Map<ActivationSets,List<IRule>>map = ProbabilisticPsystemFactory.getRulesByActivationSet((CellLikePsystem)p);
		Iterator<List<IRule>>it = map.values().iterator();
		while(it.hasNext())
		{
			List<IRule>list=it.next();
			float sum=0;
			Iterator<IRule>it1=list.iterator();
			while(it1.hasNext() && sum<=1)
			{
				IRule r = it1.next();
				float pr=((IConstantRule)r).getConstant();
				sum+=pr;
				
				
			}
			
			if (sum>1.0001 || sum<0.9999)
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
			return "The summming of probabilities of rules with the same left-hand side must be 1";
		else
		
			return "The probabilities of the following set of rules (with same left-hand side) are not summing 1:\n"+getCauseRules();
		
		
	}

}
