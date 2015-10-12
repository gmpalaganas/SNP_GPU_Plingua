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

package org.gcn.plinguacore.simulator.cellLike.transition;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


import org.gcn.plinguacore.simulator.cellLike.CellLikeSimulator;
import org.gcn.plinguacore.util.psystem.Psystem;

import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.RulesSet;
import org.gcn.plinguacore.util.psystem.rule.IPriorityRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoDivision;


import org.gcn.plinguacore.util.RandomNumbersGenerator;



public final class NonDeterministicTransitionSimulator2 extends
		CellLikeSimulator {


	/**
	 * 
	 */
	private static final long serialVersionUID = 290211051490843577L;
	private static final Comparador comparador = new Comparador();

	protected static final CheckRule noDivision = new NoDivision();
	
	
	
	public NonDeterministicTransitionSimulator2(Psystem psystem) {
		super(psystem);
			
		
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void microStepSelectRules(ChangeableMembrane m,
			ChangeableMembrane temp) {
		// TODO Auto-generated method stub
	
		
		int n=2;
		List<IRule>aux = new ArrayList<IRule>();
	
		for (int i=n;i>0;i--)
		{
			Iterator<IRule> it = getPsystem().getRules().iterator(
					temp.getLabel(),
					temp.getLabelObj().getEnvironmentID(),
					temp.getCharge(),true);
			
			aux.clear();
		
			while (it.hasNext())
				aux.add(it.next());
			
			RulesSet.sortByPriority(aux);
			if (RandomNumbersGenerator.getInstance().nextInt(10)!=0)
			{
				Collections.sort(aux,comparador);
			}
			
			it = aux.iterator();
			
			while (it.hasNext()) {
				IRule r = it.next();
				
					
				    if (!r.dissolves() || r.dissolves() && i==1)
				    {
				    	long count = r.countExecutions(temp);
					if (!(r instanceof IPriorityRule) && count>0 && i!=1)
					{
						
						count = RandomNumbersGenerator.getInstance().nextLong(count);
						
					}
					
					if (count > 0) {
						selectRule(r, m, count);
						removeLeftHandRuleObjects(temp, r, count);
					}
				    }
					
				
			}
			
		
			
		}
	}
	
	static class Comparador implements Comparator<IRule>
	{

		@Override
		public int compare(IRule arg0, IRule arg1) {
			// TODO Auto-generated method stub
			if (arg0.dissolves() && arg1.dissolves())
			return 0;
			if (!arg0.dissolves() && !arg1.dissolves())
			return 0;
			if (arg0.dissolves() && !arg1.dissolves())
			return 1;
			return -1;
		}
		
	}

}
