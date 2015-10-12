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

package org.gcn.plinguacore.util.psystem.rule;

import java.util.Set;

import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.rule.HandRule;


/**
 * This class represents right hand rules
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class RightHandRule extends HandRule {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4916985606491545835L;
	/**
	 * 
	 */

	private OuterRuleMembrane secondOuterMembrane;

	/**
	 * Creates a new {@link RightHandRule} instance with the outer membrane and
	 * the outer multiset passed as parameters. As there's no second outer rule
	 * membrane, this constructor doesn't support membrane division
	 * 
	 * @param outerMembrane
	 *            the outer membrane of the rule
	 * @param multiset
	 *            the outer multiset of the rule
	 */
	public RightHandRule(OuterRuleMembrane outerMembrane,
			MultiSet<String> multiset) {
		super(outerMembrane, multiset);
		this.secondOuterMembrane = null;
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates a new {@link RightHandRule} instance with the outer membrane, the
	 * outer membrane resulting from division and the outer multiset passed as
	 * parameters. This constructor is intended to be used to create rules
	 * instances which perform division
	 * 
	 * @param outerMembrane
	 *            the outer membrane of the rule
	 * @param multiset
	 *            the outer multiset of the rule
	 * @param secondOuterMembrane
	 *            the outer membrane resulting from membrane division
	 */
	public RightHandRule(OuterRuleMembrane outerMembrane,
			OuterRuleMembrane secondOuterMembrane, MultiSet<String> multiset) {
		super(outerMembrane, multiset);
		if (secondOuterMembrane == null)
			throw new NullPointerException(
					"In RightHandRule constructor, the second OuterRuleMembrane parameter shouldn't be null");
		this.secondOuterMembrane = secondOuterMembrane;
		// TODO Auto-generated constructor stub
	}

	/**
	 * Gets the second outer rule membrane if it exists. Otherwise, it returns
	 * null
	 * 
	 * @return returns the second outer membrane (it might be null)
	 */
	public OuterRuleMembrane getSecondOuterRuleMembrane() {
		return secondOuterMembrane;
	}

	private String removeLabel(String membrane) {
		int labelIndex = membrane.lastIndexOf("'");
		return membrane.substring(0, labelIndex);
	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.rule.HandRule#toString()
	 */
	@Override
	public String toString() {
		
		String str1 = removeLabel(getOuterRuleMembrane().toString()), str2 = "";
		if (!getMultiSet().isEmpty())
			str1 += getMultiSet().toString();
		if (secondOuterMembrane != null)
			str2 += removeLabel(secondOuterMembrane.toString());
		return str1 + str2;

	}
	
	public String toString(boolean removeLabels)
	{
		if (removeLabels)
			return toString();
		
		String str=super.toString();
		
		if (secondOuterMembrane != null)
			str += secondOuterMembrane.toString();
		
		return str;
	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.rule.HandRule#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME
				* result
				+ ((secondOuterMembrane == null) ? 0 : secondOuterMembrane
						.hashCode());
		return result;
	}

	/**
	 * Gets all objects involved in the rule including those in the second
	 * membrane
	 * 
	 * @return a set with all objects involved in the rule
	 */

	@Override
	public Set<String> getObjects() {
		Set<String> set = super.getObjects();
		if (secondOuterMembrane != null)
			set.addAll(secondOuterMembrane.getMultiSet().entrySet());
		return set;

	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.rule.HandRule#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final RightHandRule other = (RightHandRule) obj;
		if (secondOuterMembrane == null) {
			if (other.secondOuterMembrane != null)
				return false;
		} else if (!secondOuterMembrane.equals(other.secondOuterMembrane))
			return false;
		return true;
	}

}
