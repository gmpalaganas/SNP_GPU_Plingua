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
 * This class reads dissolution rules to keep compatibility with the original
 * format
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
class ReadDissolutionRule extends ReadEvolutionRule {

	/**
	 * Creates a new instance which will be used by the argument passed
	 * 
	 * @param cellLikeInputParser
	 *            the XMLCellLikeInputParser instance which will use this
	 *            instance
	 */
	public ReadDissolutionRule(XMLCellLikeInputParser cellLikeInputParser) {
		super(cellLikeInputParser);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void checkConsistency() throws PlinguaCoreException {
		super.checkConsistency();
		dissolves = true;
	}

}
