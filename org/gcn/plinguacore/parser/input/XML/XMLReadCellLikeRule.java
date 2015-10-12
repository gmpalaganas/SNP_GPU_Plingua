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

import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.rule.AbstractRuleFactory;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.LeftHandRule;
import org.gcn.plinguacore.util.psystem.rule.RightHandRule;
import org.jdom.Element;


/**
 * This class provides the common functionality for all sort of rule readers.
 * It's the abstract class which provides a common interface for reading rules,
 * as stated in Strategy pattern
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
abstract class XMLReadCellLikeRule {

	protected LeftHandRule leftHandRule;
	protected RightHandRule rightHandRule;

	private XMLCellLikeInputParser cellLikeInputParser;

	protected abstract IRule readRule(Element o,AbstractRuleFactory factory)
			throws PlinguaCoreException;

	protected abstract LeftHandRule readLeftHandRule(Element leftHandElement)
			throws PlinguaCoreException;

	/**
	 * Creates a new XMLReadCellLikeRule instance
	 * 
	 * @param cellLikeInputParser
	 *            the input parser which will use the created instance
	 */
	public XMLReadCellLikeRule(XMLCellLikeInputParser cellLikeInputParser) {
		super();
		if (cellLikeInputParser == null)
			throw new NullPointerException(
					"cellLikeInputParser argument shouldn't be null");
		this.cellLikeInputParser = cellLikeInputParser;
	}

	protected void notifyAfterReading(String msg){
		cellLikeInputParser.notifyAfterReading(msg);
	}

	protected void notifyAfterReading(String msg, String value)
			{
		cellLikeInputParser.notifyAfterReading(msg, value);
	}

	protected void notifyBeforeReading(String msg){
		cellLikeInputParser.notifyBeforeReading(msg);
	}

	protected void requiredDeclarationMissed(String msg)
			throws PlinguaCoreException {
		cellLikeInputParser.requiredDeclarationMissed(msg);
	}

	protected void requiredDeclarationMissed(String msg, String appendix)
			throws PlinguaCoreException {
		cellLikeInputParser.requiredDeclarationMissed(msg, appendix);
	}

	protected void unexpectedElement(String msg) throws PlinguaCoreException {
		cellLikeInputParser.unexpectedElement(msg);
	}

	protected void unexpectedElement(String msg, String expected)
			throws PlinguaCoreException {
		cellLikeInputParser.unexpectedElement(msg, expected);
	}

	protected void unnecesaryAttribute(String attribute, String value) {
		cellLikeInputParser.unnecesaryAttribute(attribute, value);
	}

	protected byte parseCharge(String charge) throws PlinguaCoreException {
		return cellLikeInputParser.parseCharge(charge);

	}

	protected void doubleDeclaration(String msg) throws PlinguaCoreException {
		cellLikeInputParser.doubleDeclaration(msg);
	}

	protected void doubleDeclaration(String msg, String limit)
			throws PlinguaCoreException {
		cellLikeInputParser.doubleDeclaration(msg, limit);
	}

	protected void invalidValue(String msg, String value)
			throws PlinguaCoreException {
		cellLikeInputParser.invalidValue(msg, value);
	}

	protected int readMultiplicity(String mul) throws PlinguaCoreException {
		return cellLikeInputParser.readMultiplicity(mul);
	}

	protected void checkElementName(Element e, String expectedName)
			throws PlinguaCoreException {
		cellLikeInputParser.checkElementName(e, expectedName);

	}

	protected MultiSet<String> readSpecificMultiSet(Element m)
			throws PlinguaCoreException {
		return cellLikeInputParser.readSpecificMultiSet(m);
	}

	protected void unnecesaryEmptyCollection(String msg) {
		cellLikeInputParser.unnecesaryEmptyCollection(msg);
	}

}
