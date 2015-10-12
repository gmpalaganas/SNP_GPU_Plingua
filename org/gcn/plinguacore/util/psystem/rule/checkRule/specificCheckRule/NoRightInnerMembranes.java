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

import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.DecoratorCheckRule;

/**
 * This class tests if the outer membrane in the right hand of the rule has no
 * inner membranes
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class NoRightInnerMembranes extends DecoratorCheckRule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2353523152452738877L;

	
	/**
	 * Creates a new NoRightInnerMembranes instance, which checks only the restrictions defined on itself.
	 */
	public NoRightInnerMembranes(){
		super();
	}
	/**
	 * Creates a new NoRightInnerMembranes instance, which wraps cr as stated
	 * by Decorator pattern. Thus, it will be capable to test both the instance restrictions and cr restrictions 
	 * 
	 * @param cr
	 *            the CheckRule instance to be wrapped
	 */
	public NoRightInnerMembranes(CheckRule cr) {
		super(cr);

		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean checkSpecificPart(IRule r) {

		return r.getRightHandRule().getOuterRuleMembrane()
				.getInnerRuleMembranes().isEmpty()
				&& (noDivisionCheckRule.checkRule(r) || r.getRightHandRule()
						.getSecondOuterRuleMembrane().getInnerRuleMembranes()
						.isEmpty());
	}

	@Override
	protected String getSpecificCause() {
		return "Rules with right-hand-rule inner membranes are not allowed";
	}

}
