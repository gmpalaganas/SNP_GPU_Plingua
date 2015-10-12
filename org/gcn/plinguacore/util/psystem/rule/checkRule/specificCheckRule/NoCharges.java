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

package org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule;

import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.rule.HandRule;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.DecoratorCheckRule;

public class NoCharges extends DecoratorCheckRule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5023790016455964242L;

	
	
	public NoCharges() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NoCharges(CheckRule cr) {
		super(cr);
		// TODO Auto-generated constructor stub
	}
	
	private boolean thereIsCharges(HandRule hr)
	{
		if (hr.getOuterRuleMembrane().getCharge()!=0)
			return true;
		
		for (int i=0;i<hr.getOuterRuleMembrane().getInnerRuleMembranes().size();i++)
		{
			if (hr.getOuterRuleMembrane().getInnerRuleMembranes().get(i).getCharge()!=0)
				return true;
		}
		
		return false;
	}

	
	

	@Override
	protected boolean checkSpecificPart(IRule r) {
		// TODO Auto-generated method stub
		return !thereIsCharges(r.getLeftHandRule()) && !thereIsCharges(r.getRightHandRule());
	}

	@Override
	protected String getSpecificCause() {
		// TODO Auto-generated method stub
		return "No rules with electrical charges are allowed";
	}

}
