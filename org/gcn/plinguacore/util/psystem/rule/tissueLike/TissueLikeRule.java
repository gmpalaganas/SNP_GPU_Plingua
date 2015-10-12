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

import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.rule.LeftHandRule;
import org.gcn.plinguacore.util.psystem.rule.RightHandRule;
import org.gcn.plinguacore.util.psystem.rule.AbstractRule;

public class TissueLikeRule extends AbstractRule  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2311858756048522881L;

	protected TissueLikeRule(boolean dissolves, LeftHandRule leftHandRule,
			RightHandRule rightHandRule) {
		super(dissolves, leftHandRule, rightHandRule);
		// TODO Auto-generated constructor stub
	}
	
	

	@Override
	protected void checkState() {
		// TODO Auto-generated method stub
		super.checkState();
		if (dissolves())
			throw new IllegalArgumentException("Dissolution rules are not allowed");
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
			updateMembrane(membrane, getRightHandRule().getOuterRuleMembrane(),executions);
			updateMembrane(m1,getRightHandRule().getSecondOuterRuleMembrane(),executions);
			return true;
		}
		return false;
	}

}
