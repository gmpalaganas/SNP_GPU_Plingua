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

import org.gcn.plinguacore.util.InmutableMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;


/**
 * This class represents membranes which, in models which accept collaboration,
 * are the children membrane of an outer membrane involved in the rule
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public final class InnerRuleMembrane extends Membrane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4060305358392778861L;

	/**
	 * Creates an InnerRuleMembrane instance
	 * 
	 * @param label
	 *            the InnerRuleMembrane instance label
	 * @param charge
	 *            the InnerRuleMembrane instance charge
	 * @param multiset
	 *            the InnerRuleMembrane instance outer multiset
	 */

	public InnerRuleMembrane(Label label, byte charge,
			MultiSet<String> multiset) {
		super(label, charge, new InmutableMultiSet<String>(multiset));

	}

	/**
	 * Creates an InnerRuleMembrane instance
	 * 
	 * @param label
	 *            the InnerRuleMembrane instance label
	 * @param charge
	 *            the InnerRuleMembrane instance charge
	 */
	public InnerRuleMembrane(Label label, byte charge) {
		super(label, charge);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates an InnerRuleMembrane instance
	 * 
	 * @param label
	 *            the InnerRuleMembrane instance label
	 */
	public InnerRuleMembrane(Label label) {
		super(label);
		// TODO Auto-generated constructor stub
	}

}
