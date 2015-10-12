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

import java.util.ArrayList;
import java.util.List;

import org.gcn.plinguacore.util.InmutableMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;



/**
 * This class represents the outer, main membrane of each hand rule
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class OuterRuleMembrane extends Membrane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6694799090774398321L;
	private List<InnerRuleMembrane> innerMembranes;

	/**
	 * Creates a new OuterRuleMembrane instance with the label, charge and child
	 * membranes passed as parameter
	 * 
	 * @param label
	 *            the outer membrane label
	 * @param charge
	 *            the outer membrane charge
	 * @param innerMembranes
	 *            a list containing all child membranes involved in the rule
	 */

	public OuterRuleMembrane(Label label, byte charge,
			List<InnerRuleMembrane> innerMembranes) {
		this(label, charge, new InmutableMultiSet<String>(), innerMembranes);
	}

	/**
	 * Creates a new OuterRuleMembrane instance with the label, charge, inner
	 * multiset and child membranes passed as parameter
	 * 
	 * @param label
	 *            the outer membrane label
	 * @param charge
	 *            the outer membrane charge
	 * @param multiset
	 *            the inner multiset into the outer membrane
	 * @param innerMembranes
	 *            a list containing all child membranes involved in the rule
	 */

	public OuterRuleMembrane(Label label, byte charge,
			MultiSet<String> multiset, List<InnerRuleMembrane> innerMembranes) {
		super(label, charge, multiset);
		if (innerMembranes == null)
			throw new NullPointerException(
					"InnerRuleMembrane list shouldn't be null");
		this.innerMembranes = innerMembranes;
	}

	/**
	 * Creates a new OuterRuleMembrane instance with the label and charge passed
	 * as parameter
	 * 
	 * @param label
	 *            the outer membrane label
	 * @param charge
	 *            the outer membrane charge
	 */
	public OuterRuleMembrane(Label label, byte charge) {
		this(label, charge, new ArrayList<InnerRuleMembrane>());

		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates a new OuterRuleMembrane instance with the label, charge and inner
	 * multiset passed as parameter
	 * 
	 * @param label
	 *            the outer membrane label
	 * @param charge
	 *            the outer membrane charge
	 * @param multiset
	 *            the inner multiset into the outer membrane
	 */
	public OuterRuleMembrane(Label label, byte charge,
			MultiSet<String> multiset) {
		this(label, charge, multiset, new ArrayList<InnerRuleMembrane>());

		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates a new OuterRuleMembrane instance with the label and charge passed
	 * as parameter
	 * 
	 * @param label
	 *            the outer membrane label
	 */
	public OuterRuleMembrane(Label label) {
		super(label);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Gets all inner rule membranes involved in the rule
	 * 
	 * @return a list containing all inner rule membranes involved in the rule
	 */
	public List<InnerRuleMembrane> getInnerRuleMembranes() {
		return this.innerMembranes;
	}

	protected String printInnerMembranes() {
		String str = "";
		for (int i = 0; i < innerMembranes.size(); i++) {
			str += innerMembranes.get(i).toString();
			if (i < innerMembranes.size() - 1)
				str += " ";
		}
		return str;
	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.membrane.Membrane#toString()
	 */
	@Override
	public String toString() {
		String ch = Membrane.getChargeSymbol(getCharge());
		if (ch.equals("0"))
			ch = "";
		String str = ch + "[";
		if (getMultiSet().size() != 0)
			str += getMultiSet().toString();
		if (innerMembranes.size() > 0)
			str += " ";
		str += printInnerMembranes();
		str += "]'" + getLabelObj();
		return str;
	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.membrane.Membrane#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result
				+ ((innerMembranes == null) ? 0 : innerMembranes.hashCode());
		return result;
	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.membrane.Membrane#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final OuterRuleMembrane other = (OuterRuleMembrane) obj;
		if (innerMembranes == null) {
			if (other.innerMembranes != null)
				return false;
		} else if (!innerMembranes.equals(other.innerMembranes))
			return false;
		return true;
	}

}
