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

/**
 * This class is intended to create XMLReadCellLikeRule instances, by following
 * the SimpleFactory idiom
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
abstract class ReadRuleFactory {
	/**
	 * Creates a XML cell-like rule reader according to the parameters passed
	 * 
	 * @param ruleName
	 *            the name of the type of rule which the resulting instance will
	 *            read
	 * @param inputParser
	 *            the InputParser instance which will use the rule reader to be
	 *            created
	 * @return a XMLReadCellLikeRule instance which reads the kind of rules
	 *         indicated by ruleName, according to the given parameters, as
	 *         specified in the Simple Factory idiom
	 */
	public static XMLReadCellLikeRule createReadRule(String ruleName,
			XMLCellLikeInputParser inputParser) {
		if (ruleName.equals("rule")) {
			return new ReadStandardRule(inputParser);
		}
		if (ruleName.equals("evolution_rule")) {
			return new ReadEvolutionRule(inputParser);
		}
		if (ruleName.equals("division_rule")) {
			return new ReadDivisionRule(inputParser);
		}
		if (ruleName.equals("dissolution_rule"))
			return new ReadDissolutionRule(inputParser);
		if (ruleName.equals("send_out_rule"))
			return new ReadSendOutRule(inputParser);
		if (ruleName.equals("send_in_rule"))
			return new ReadSendInRule(inputParser);
		throw new IllegalArgumentException(ruleName
				+ " rule type not recognized");
	}

}
