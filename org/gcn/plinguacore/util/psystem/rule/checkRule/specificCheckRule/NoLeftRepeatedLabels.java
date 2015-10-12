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


import java.util.HashSet;

import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;

/**
 * This class tests if the outer membrane in the left hand of the rule has no
 * repeated labels, testing both the outer membrane and the inner membranes (if
 * they exist)
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class NoLeftRepeatedLabels extends NoRepeatedLabels {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2814803775414901272L;

	
	
	/**
	 * Creates a new NoLeftRepeatedLabels instance, which checks only the restrictions defined on itself.
	 */
	public NoLeftRepeatedLabels(){
		super();
	}
	
	/**
	 * Creates a new NoLeftRepeatedLabels instance, which wraps cr as stated
	 * by Decorator pattern. Thus, it will be capable to test both the instance restrictions and cr restrictions 
	 * 
	 * @param cr
	 *            the CheckRule instance to be wrapped
	 */
	public NoLeftRepeatedLabels(CheckRule cr) {
		super(cr);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * util.psystem.rule.checkRule.DecoratorCheckRule#checkSpecificPart(util
	 * .psystem.rule.IRule)
	 */
	@Override
	protected boolean checkSpecificPart(IRule r) {
		// TODO Auto-generated method stub
		return super.checkHand(r.getLeftHandRule().getOuterRuleMembrane(),
				new HashSet<String>());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.psystem.rule.checkRule.DecoratorCheckRule#getSpecificCause()
	 */
	@Override
	protected String getSpecificCause() {
		// TODO Auto-generated method stub
		return "Left " + super.getSpecificCause();
	}

}
