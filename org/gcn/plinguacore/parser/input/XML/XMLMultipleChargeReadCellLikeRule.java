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
import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.rule.OuterRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.RightHandRule;
import org.jdom.Element;


/**
 * This class provides the basic functionality for those original format rules
 * which may change the membrane charge
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
abstract class XMLMultipleChargeReadCellLikeRule extends
		XMLCompatibleReadCellLikeRule {

	protected byte rightOuterMembraneCharge;

	/**
	 * Creates a new instance which will be used by the XMLCellLikeInputParser
	 * passed as argument
	 * 
	 * @param cellLikeInputParser
	 *            the input parser which uses this instance
	 */
	public XMLMultipleChargeReadCellLikeRule(
			XMLCellLikeInputParser cellLikeInputParser) {
		super(cellLikeInputParser);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see parser.input.XML.XMLCompatibleCellLikeRule#buildRightHandRule()
	 */
	@Override
	protected RightHandRule buildRightHandRule() {
		// TODO Auto-generated method stub

		return new RightHandRule(new OuterRuleMembrane(new Label(label),
				rightOuterMembraneCharge, rightInnerMultiSet),
				rightOuterMultiSet);
	}

	/**
	 * @see parser.input.XML.XMLCompatibleCellLikeRule#readRightHandRule(org.jdom.Element)
	 */
	@Override
	protected void readRightHandRule(Element rightHandElement)
			throws PlinguaCoreException {
		notifyBeforeReading("Right Hand Rule");
		// TODO Auto-generated method stub
		/* The right hand rule should have only one object */
		String name = rightHandElement.getAttributeValue("object");
		rightOuterMembraneCharge = parseCharge(rightHandElement
				.getAttributeValue("charge"));
		addRightHandRuleObject(name, 1);
		notifyAfterReading("Right Hand Rule");

	}

}
