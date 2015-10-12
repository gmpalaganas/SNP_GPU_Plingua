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


import java.util.ListIterator;
import java.util.Set;

import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.OuterRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.DecoratorCheckRule;

/**
 * This class tests if rule has no repeated labels, testing both the outer
 * membrane and the inner membranes (if they exist) in each hand separately
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public abstract class NoRepeatedLabels extends DecoratorCheckRule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8267221205826990188L;

	
	/**
	 * Creates a new NoRepeatedLabels instance, which checks only the restrictions defined on itself.
	 */
	public NoRepeatedLabels(){
		super();
	}
	
	/**
	 * Creates a new NoRepeatedLabels instance, which wraps cr as stated
	 * by Decorator pattern. Thus, it will be capable to test both the instance restrictions and cr restrictions 
	 * 
	 * @param cr
	 *            the CheckRule instance to be wrapped
	 */
	public NoRepeatedLabels(CheckRule cr) {
		super(cr);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * util.psystem.rule.checkRule.DecoratorCheckRule#checkSpecificPart(util
	 * .psystem.rule.Rule)
	 */

	protected final boolean checkHand(OuterRuleMembrane orm, Set<String> labels) {
		if (labels.contains(orm.getLabel()))
			return false;
		labels.add(orm.getLabel());
		ListIterator<InnerRuleMembrane> lirm = orm.getInnerRuleMembranes()
				.listIterator();
		while (lirm.hasNext()) {
			String label = lirm.next().getLabel();
			if (labels.contains(label))
				return false;
			labels.add(label);
		}
		return true;

	}

	@Override
	protected String getSpecificCause() {
		// TODO Auto-generated method stub
		return "Hand Rules with repeated labels aren't allowed";
	}

}
