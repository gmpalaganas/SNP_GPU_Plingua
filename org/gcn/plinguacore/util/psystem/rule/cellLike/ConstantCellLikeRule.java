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

package org.gcn.plinguacore.util.psystem.rule.cellLike;

import org.gcn.plinguacore.util.psystem.rule.IConstantRule;
import org.gcn.plinguacore.util.psystem.rule.LeftHandRule;
import org.gcn.plinguacore.util.psystem.rule.RightHandRule;

/**
 * This class represents cell-like rules which hold a float constant with any
 * meaning
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
class ConstantCellLikeRule extends CellLikeRule implements IConstantRule {

	private static long ruleCounter=0;
	private static final long serialVersionUID = -8711259870774793439L;
	private float constant;
	
	/**
	 * Creates a new ConstantCellLikeRule instance.
	 * 
	 * @param dissolves
	 *            a boolean parameter which reflects if the rule will dissolve
	 *            the membrane
	 * @param leftHandRule
	 *            the left hand of the rule
	 * @param rightHandRule
	 *            the right hand of the rule
	 * @param constant
	 *            the constant which holds part of the rule information
	 */
	protected ConstantCellLikeRule(boolean dissolves, LeftHandRule leftHandRule,
			RightHandRule rightHandRule, float constant) {
		super(dissolves, leftHandRule, rightHandRule);
		if (constant < 0)
			throw new IllegalArgumentException(
					"Constant argument should be at least 0");
		this.constant = constant;
		
		// TODO Auto-generated constructor stub
	}

	/**
	 * Gets the constant of the rule
	 * 
	 * @return the constant of the rule
	 */
	@Override
	public float getConstant() {
		return constant;
	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.rule.cellLike.CellLikeRule#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + " :: " + constant;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Float.floatToIntBits(constant);
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConstantCellLikeRule other = (ConstantCellLikeRule) obj;
		if (Float.floatToIntBits(constant) != Float
				.floatToIntBits(other.constant))
			return false;
		
		return true;
	}

	


	

}
