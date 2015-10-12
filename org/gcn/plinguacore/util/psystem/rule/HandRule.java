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

import java.io.Serializable;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import org.gcn.plinguacore.util.InmutableMultiSet;
import org.gcn.plinguacore.util.MultiSet;



/**
 * This class provides common functionality for Left Hand Rules and Right Hand
 * Rules
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

public abstract class HandRule implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5525186671845193265L;
	private MultiSet<String> multiset;
	private OuterRuleMembrane outerMembrane;

	/**
	 * Creates a new instance by setting its fields with the parameters given
	 * 
	 * @param outerMembrane
	 *            the outer membrane of the hand
	 * @param multiset
	 *            the outer multiset of the hand
	 */
	public HandRule(OuterRuleMembrane outerMembrane, MultiSet<String> multiset) {
		super();
		if (outerMembrane == null)
			throw new NullPointerException(
					"OuterRuleMembrane parameter shouldn't be null");
		if (multiset == null)
			throw new NullPointerException(
					"Multiset parameter shouldn't be null");
		this.outerMembrane = outerMembrane;
		this.multiset = new InmutableMultiSet<String>(multiset);

	}

	/**
	 * Returns all different objects involved in the rule
	 * 
	 * @return a set with all different objects mentioned above
	 * */
	public Set<String> getObjects() {
		Set<String> set = new HashSet<String>();
		/*
		 * All objects involved in the rule consist of: all objects in the outer
		 * multiset
		 */

		set.addAll(multiset.entrySet());
		/* all objects in the inner multiset */
		set.addAll(outerMembrane.getMultiSet().entrySet());
		/* all objects into each inner membrane */
		ListIterator<InnerRuleMembrane> it = outerMembrane
				.getInnerRuleMembranes().listIterator();
		while (it.hasNext())
			set.addAll(it.next().getMultiSet().entrySet());
		return set;
	}

	/**
	 * Gets the outer multiset out of all membranes
	 * 
	 * @return the hand rule multiset
	 */
	public MultiSet<String> getMultiSet() {
		return multiset;
	}

	/**
	 * Gets the hand outer rule membrane
	 * 
	 * @return the hand outer rule membrane
	 */
	public OuterRuleMembrane getOuterRuleMembrane() {
		return outerMembrane;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String multisetString = "";
		if (!multiset.isEmpty())
			multisetString = multiset.toString();
		return multisetString + outerMembrane.toString();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((multiset == null) ? 0 : multiset.hashCode());
		result = PRIME * result
				+ ((outerMembrane == null) ? 0 : outerMembrane.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final HandRule other = (HandRule) obj;
		if (multiset == null) {
			if (other.multiset != null)
				return false;
		} else if (!multiset.equals(other.multiset))
			return false;
		if (outerMembrane == null) {
			if (other.outerMembrane != null)
				return false;
		} else if (!outerMembrane.equals(other.outerMembrane))
			return false;
		return true;
	}

}
