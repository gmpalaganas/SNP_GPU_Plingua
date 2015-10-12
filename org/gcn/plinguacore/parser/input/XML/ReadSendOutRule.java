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

import org.gcn.plinguacore.util.PlinguaCoreException;

/**
 * This class reads send-out rules to keep compatibility with the original
 * format
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
class ReadSendOutRule extends XMLMultipleChargeReadCellLikeRule {

	/**
	 * Creates a new instance which will be used by the argument passed
	 * 
	 * @param cellLikeInputParser
	 *            the XMLCellLikeInputParser instance which will use this
	 *            instance
	 */
	public ReadSendOutRule(XMLCellLikeInputParser cellLikeInputParser) {
		super(cellLikeInputParser);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void checkConsistency() throws PlinguaCoreException {
		/*
		 * In this case, the left hand rule inner multiset and the right hand
		 * rule outer multiset shouldn't be empty, as the object should go from
		 * the former to the later
		 */
		if (leftHandRule.getOuterRuleMembrane().getMultiSet().isEmpty())
			requiredDeclarationMissed("Object in Left Hand Rule Outer Membrane Multiset");
		if (rightHandRule.getMultiSet().isEmpty())
			requiredDeclarationMissed("Object in Right Hand Rule Multiset");

	}

	@Override
	protected void addRightHandRuleObject(String object, int multiplicity) {
		// TODO Auto-generated method stub
		rightOuterMultiSet.add(object, multiplicity);
	}

}
