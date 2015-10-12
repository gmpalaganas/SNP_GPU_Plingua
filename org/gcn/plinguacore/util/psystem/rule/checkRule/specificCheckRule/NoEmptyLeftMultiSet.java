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
import java.util.ListIterator;

import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.DecoratorCheckRule;


/**
 * This class tests if the're is any object in the left hand of the rule, no
 * matter if it's in any inner membrane, in the outer membrane or in the outer
 * multiset
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class NoEmptyLeftMultiSet extends DecoratorCheckRule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2521796773673522199L;

	/**
	 * Creates a new NoEmptyLeftMultiSet instance, which checks only the restrictions defined on itself.
	 */
	public NoEmptyLeftMultiSet(){
		super();
	}
	
	/**
	 * Creates a new NoEmptyLeftMultiSet instance, which wraps cr as stated
	 * by Decorator pattern. Thus, it will be capable to test both the instance restrictions and cr restrictions 
	 * 
	 * @param cr
	 *            the CheckRule instance to be wrapped
	 */
	public NoEmptyLeftMultiSet(CheckRule cr) {
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
		/*
		 * This method checks if there's any object on the left side of the
		 * rule: We assume there's not any object on the left hand of the rule
		 * if it doesn't comply with none of this conditions
		 */

		// TODO Auto-generated method stub
		ListIterator<InnerRuleMembrane> iterator = r.getLeftHandRule()
				.getOuterRuleMembrane().getInnerRuleMembranes().listIterator();
		/* a. The left hand outer multiset is empty */
		while (iterator.hasNext()) {
			InnerRuleMembrane elem = iterator.next();
			if (!elem.getMultiSet().isEmpty())
				return true;
		}
		/*
		 * b. The left hand outer membrane inner multiset is empty c. The left
		 * hand outer multiset is empty
		 */
		return !r.getLeftHandRule().getMultiSet().isEmpty()
				|| !r.getLeftHandRule().getOuterRuleMembrane().getMultiSet()
						.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.psystem.rule.checkRule.DecoratorCheckRule#getSpecificCause()
	 */
	@Override
	protected String getSpecificCause() {
		// TODO Auto-generated method stub
		return "Rules with no objects in any defined MultiSet are not allowed";
	}

}
