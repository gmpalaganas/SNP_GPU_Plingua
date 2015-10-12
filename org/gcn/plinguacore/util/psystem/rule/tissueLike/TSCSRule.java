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

package org.gcn.plinguacore.util.psystem.rule.tissueLike;

import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.rule.LeftHandRule;
import org.gcn.plinguacore.util.psystem.rule.RightHandRule;
import org.gcn.plinguacore.util.psystem.rule.AbstractRule;
import org.gcn.plinguacore.util.psystem.tissueLike.membrane.TissueLikeMembrane;

public class TSCSRule extends AbstractRule  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2311858756048522881L;

	protected TSCSRule(boolean separates, LeftHandRule leftHandRule,
			RightHandRule rightHandRule) {
		super(separates, leftHandRule, rightHandRule);
		// TODO Auto-generated constructor stub
	}
	
	

	@Override
	protected void checkState() {
		// TODO Auto-generated method stub
		super.checkState();
		if (dissolves())
			throw new IllegalArgumentException("Separation rules are not allowed");
	}



	@Override
	public long countExecutions(ChangeableMembrane membrane) {
		
		if (!checkLabel(membrane))
			return 0;
		
		return multiSetCount(getLeftHandRule().getOuterRuleMembrane().getMultiSet(), membrane.getMultiSet());
	}

	

	@Override
	protected boolean executeSafe(ChangeableMembrane membrane,
			MultiSet<String> environment, long executions) {
		// TODO Auto-generated method stub
		if (getRightHandRule().getSecondOuterRuleMembrane()!=null)
		{
			subtractMultiSet(this.getLeftHandRule().getOuterRuleMembrane().getMultiSet(), membrane.getMultiSet(), executions);
			
			
			
			ChangeableMembrane m1=membrane.divide();
			m1.getMultiSet().clear();
			MultiSet<String>ms1 = ((TissueLikeMembrane)membrane).getStructure().getPsystem().getFirstMultiSet();
			MultiSet<String>ms2 = ((TissueLikeMembrane)membrane).getStructure().getPsystem().getSecondMultiSet();
			
			
			MultiSet<String>newMs1=new HashMultiSet<String>();
			
			for (String obj:membrane.getMultiSet().entrySet())
			{
				if (ms1.contains(obj))
					newMs1.add(obj,membrane.getMultiSet().count(obj));
				else
				if (ms2.contains(obj))
					m1.getMultiSet().add(obj,membrane.getMultiSet().count(obj));
			}
			
			membrane.getMultiSet().clear();
			membrane.getMultiSet().addAll(newMs1);
			
			
			//updateMembrane(membrane, getRightHandRule().getOuterRuleMembrane(),executions);
			//updateMembrane(m1,getRightHandRule().getSecondOuterRuleMembrane(),executions);
			return true;
		}
		return false;
	}

}
