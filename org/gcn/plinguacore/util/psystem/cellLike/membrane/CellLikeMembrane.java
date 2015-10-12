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


import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import java.util.Collections;

import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;

/**
 * A class for a membrane for cell-like P systems
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

public abstract class CellLikeMembrane extends ChangeableMembrane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6142935731109264452L;
	protected List<CellLikeNoSkinMembrane> childMembranes;



	/**
	 * @param label
	 *            A String
	 * @param parentMembrane
	 *            The parent membrane or null if this membrane will be the skin
	 *            one
	 */

	protected CellLikeMembrane(Label label) {
		super(label);
		childMembranes = new ArrayList<CellLikeNoSkinMembrane>();
	}

	protected CellLikeMembrane(Label label, byte charge) {
		super(label, charge);
		childMembranes = new ArrayList<CellLikeNoSkinMembrane>();
	}

	/**
	 * Gets if this membrane is the skin one
	 * 
	 * @return true if this membrane is the skin one, false otherwise
	 */
	public boolean isSkinMembrane() {
		return (this instanceof CellLikeSkinMembrane);
	}

	/**
	 * Gets a String with a representation of the membrane in P-Lingua format
	 * 
	 * @return a String with a representation of the membrane in P-Lingua format
	 */
	@Override
	public String toString() {
		String ch = Membrane.getChargeSymbol(getCharge());
		if (ch.equals("0"))
			ch = "";
		String str = ch + "[";
		for (int i = 0; i < childMembranes.size(); i++) {
			str += childMembranes.get(i).toString();
			if (i + 1 < childMembranes.size())
				str += ", ";
		}
		if (childMembranes.size() != 0 && getMultiSet().size() != 0)
			str += ", ";
		if (getMultiSet().size() != 0)
			str += getMultiSet().toString();

		str += "]'" + getLabelObj();
		return str;
	}

	/**
	 * Gets all child membranes
	 * 
	 * @return a collection containing all child membranes
	 */
	public Collection<CellLikeNoSkinMembrane> getChildMembranes() {
		return Collections.unmodifiableCollection(childMembranes);
	}

	/**
	 * Gets all membranes which this instance is an ancestor to
	 * 
	 * @return a collection containing all membranes which this instance is an
	 *         ancestor to
	 */
	public Collection<Membrane> getAllMembranes() {
		List<List<CellLikeMembrane>> l = new ArrayList<List<CellLikeMembrane>>();
		getAllMembranesRec(l, 0);
		List<Membrane> r = new ArrayList<Membrane>();
		for (int i = l.size() - 1; i >= 0; i--)
			r.addAll(l.get(i));
		return Collections.unmodifiableCollection(r);
	}

	private void getAllMembranesRec(
			List<List<CellLikeMembrane>> membranesByDepth, int depth) {
		if (depth == membranesByDepth.size())
			membranesByDepth.add(new ArrayList<CellLikeMembrane>());
		List<CellLikeMembrane> l = membranesByDepth.get(depth);
		//l.add(this);
		Iterator<CellLikeNoSkinMembrane> it = childMembranes.iterator();
		while (it.hasNext())
			((CellLikeMembrane) it.next()).getAllMembranesRec(membranesByDepth,
					depth + 1);
		l.add(this);
	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane#dissolve()
	 */
	@Override
	public void dissolve() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane#divide()
	 */
	@Override
	public ChangeableMembrane divide() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

}
