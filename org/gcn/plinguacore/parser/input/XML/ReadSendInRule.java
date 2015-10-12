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

package org.gcn.plinguacore.parser.input.XML;

import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.rule.LeftHandRule;
import org.gcn.plinguacore.util.psystem.rule.OuterRuleMembrane;

/**
 * This class reads send-in rules to keep compatibility with the original format
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
class ReadSendInRule extends XMLMultipleChargeReadCellLikeRule {

	/**
	 * Creates a new instance which will be used by the argument passed
	 * 
	 * @param cellLikeInputParser
	 *            the XMLCellLikeInputParser instance which will use this
	 *            instance
	 */
	public ReadSendInRule(XMLCellLikeInputParser cellLikeInputParser) {
		super(cellLikeInputParser);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void checkConsistency() throws PlinguaCoreException {
		/*
		 * In this case, the left hand rule outer multiset and the right hand
		 * rule inner multiset shouldn't be empty, as the object should go from
		 * the former to the later
		 */
		if (leftHandRule.getMultiSet().isEmpty())
			requiredDeclarationMissed("Object in Left Hand Rule Multiset");
		if (rightHandRule.getOuterRuleMembrane().getMultiSet().isEmpty())
			requiredDeclarationMissed("Object in Right Hand Rule Outer Membrane Multiset");

	}

	/**
	 * @see parser.input.XML.XMLCompatibleCellLikeRule#constructLeftHandRule(java.lang.String,
	 *      byte, java.lang.String)
	 */
	@Override
	protected LeftHandRule constructLeftHandRule(String label, byte charge,
			String object) {
		// TODO Auto-generated method stub
		MultiSet<String> outerLeftMultiSet = new HashMultiSet<String>();
		outerLeftMultiSet.add(object);
		return new LeftHandRule(new OuterRuleMembrane(new Label(label), charge),
				outerLeftMultiSet);
	}

}
