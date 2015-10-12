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

package org.gcn.plinguacore.util.psystem.cellLike.membrane;

import org.gcn.plinguacore.util.psystem.Label;

/**
 * This class creates CellLikeMembrane instances, according to Simple Factory
 * idiom
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class CellLikeMembraneFactory {

	/**
	 * Based on Simple Factory idiom, creates a membrane which label is passed
	 * as a parameter
	 * 
	 * @param label
	 *            the label of the membrane to create
	 * @return the membrane recently created
	 */
	public static CellLikeMembrane getCellLikeMembrane(Label label) {
		return new CellLikeSkinMembrane(label);
	}

	/**
	 * Based on Simple Factory idiom, creates a membrane which parent and label
	 * are passed as parameters
	 * 
	 * @param label
	 *            the label of the membrane to create
	 * @param parentMembrane
	 *            the parent membrane of the membrane to create
	 * @return the membrane recently created
	 */
	public static CellLikeMembrane getCellLikeMembrane(Label label,
			CellLikeMembrane parentMembrane) {
		/* If there's no parent membrane, the membrane to create is the skin */
		if (parentMembrane == null)
			return new CellLikeSkinMembrane(label);
		/* Otherwise it's not the skin */
		return new CellLikeNoSkinMembrane(label, parentMembrane);
	}

}
