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

import java.util.Iterator;

import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.DecoratorCheckRule;


/**
 * This class tests if there's one or no object in the left hand of the rule, no
 * matter if, in case it exists, it's into an inner membrane, the outer multiset
 * or the inner multiset of the outer membrane
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

public class NoCooperation extends DecoratorCheckRule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6167535217122687596L;
	
	/**
	 * Creates a new NoCooperation instance, which checks only the restrictions defined on itself.
	 */
	public NoCooperation(){
		super();
	}
	
	/**
	 * Creates a new NoCooperation instance, which wraps cr as stated
	 * by Decorator pattern. Thus, it will be capable to test both the instance restrictions and cr restrictions 
	 * 
	 * @param cr
	 *            the CheckRule instance to be wrapped
	 */
	public NoCooperation(CheckRule cr) {
		super(cr);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean checkSpecificPart(IRule r) {
		int count = r.getLeftHandRule().getMultiSet().size()
				+ r.getLeftHandRule().getOuterRuleMembrane().getMultiSet()
						.size();

		if (count > 1)
			return false;

		Iterator<InnerRuleMembrane> it = r.getLeftHandRule()
				.getOuterRuleMembrane().getInnerRuleMembranes().iterator();
		while (it.hasNext()) {
			count += it.next().getMultiSet().size();
			if (count > 1)
				return false;
		}
		return true;

	}

	@Override
	protected String getSpecificCause() {
		return "Rules with cooperative left-hand-rule objects are not allowed";
	}

}
