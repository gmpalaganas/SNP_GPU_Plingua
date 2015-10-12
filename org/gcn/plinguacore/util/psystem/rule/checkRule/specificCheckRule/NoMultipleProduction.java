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

import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.DecoratorCheckRule;


/**
 * This class tests if there's only one object in the right hand of the rule. It
 * also considers the following restrictions: In rules which allow membrane
 * division, there should be only one object in each outer membrane In rules
 * which allow membrane creation, there should be only a new object in the right
 * hand of the rule, inside a new membrane
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

public class NoMultipleProduction extends DecoratorCheckRule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2863456471230961641L;

	
	/**
	 * Creates a new NoMultipleProduction instance, which checks only the restrictions defined on itself.
	 */
	public NoMultipleProduction(){
		super();
	}
	/**
	 * Creates a new NoMultipleProduction instance, which wraps cr as stated
	 * by Decorator pattern. Thus, it will be capable to test both the instance restrictions and cr restrictions 
	 * 
	 * @param cr
	 *            the CheckRule instance to be wrapped
	 */
	public NoMultipleProduction(CheckRule cr) {
		super(cr);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean checkSpecificPart(IRule r) {

		int count = r.getRightHandRule().getMultiSet().size()
				+ r.getRightHandRule().getOuterRuleMembrane().getMultiSet()
						.size();
		if (!evolutionCheckRule.checkRule(r)) {
			if (count > 1)
				return false;

			Iterator<InnerRuleMembrane> it = r.getRightHandRule()
					.getOuterRuleMembrane().getInnerRuleMembranes().iterator();
			while (it.hasNext() && count <= 1)
				count += it.next().getMultiSet().size();
			if (count > 1)
				return false;
		}

		if (!noCreationCheckRule.checkRule(r)) {
			if (!r.getRightHandRule().getMultiSet().isEmpty())
				return false;
			if (!r.getRightHandRule().getOuterRuleMembrane().getMultiSet()
					.isEmpty())
				return false;
			if (r.getRightHandRule().getOuterRuleMembrane()
					.getInnerRuleMembranes().size() != 1)
				return false;
			if (r.getRightHandRule().getOuterRuleMembrane()
					.getInnerRuleMembranes().get(0).getMultiSet().size() > 1)
				return false;
		}

		if (!noDivisionCheckRule.checkRule(r)) {

			if (!r.getRightHandRule().getMultiSet().isEmpty())
				return false;

			count = r.getRightHandRule().getSecondOuterRuleMembrane()
					.getMultiSet().size();
			if (count > 1)
				return false;

			Iterator<InnerRuleMembrane> it = r.getRightHandRule()
					.getSecondOuterRuleMembrane().getInnerRuleMembranes()
					.iterator();
			while (it.hasNext() && count <= 1)
				count += it.next().getMultiSet().size();

			if (count > 1)
				return false;
		}
		return true;

	}

	@Override
	protected String getSpecificCause() {
		// TODO Auto-generated method stub
		return "Too many and/or inappropiate right-hand-rule objects and/or membranes";
	}

}
