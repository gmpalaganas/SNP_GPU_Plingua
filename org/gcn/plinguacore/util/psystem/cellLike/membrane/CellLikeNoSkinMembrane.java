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

import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;


/**
 * This class represents cell-like membranes which are not skin membranes
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public final class CellLikeNoSkinMembrane extends CellLikeMembrane {
	private CellLikeSkinMembrane skinMembrane;
	private final static long serialVersionUID = 1L;
	private CellLikeMembrane parentMembrane;

	protected CellLikeNoSkinMembrane(Label label,
			CellLikeMembrane parentMembrane) {
		super(label);
		if (parentMembrane == null) {
			throw new NullPointerException(
					"The parent membrane shouldn't be null in a CellLikeNoSkinMembrane instance.");
		}
		/*
		 * We need to set this membrane parent as the parameter given, and
		 * report this new membrane to its parent
		 */
		this.parentMembrane = parentMembrane;
		List<CellLikeNoSkinMembrane> clm = parentMembrane.childMembranes;
		clm.add(this);
		/* Then, we need to set the skin membrane */
		if (parentMembrane.isSkinMembrane())
			skinMembrane = (CellLikeSkinMembrane) parentMembrane;
		else
			skinMembrane = ((CellLikeNoSkinMembrane) parentMembrane)
					.getSkinMembrane();
		/* Finally, the membrane id is set */
		setId(skinMembrane.getNextID());
		skinMembrane.getMembranes().put(getId(), this);

	}

	private CellLikeSkinMembrane getSkinMembrane() {
		return skinMembrane;
	}

	/**
	 * Gets the parent membrane
	 * 
	 * @return the parent membrane
	 */
	public CellLikeMembrane getParentMembrane() {
		return parentMembrane;
	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane#dissolve()
	 */
	@Override
	public void dissolve() throws UnsupportedOperationException {

		/* We update parent */
		parentMembrane.childMembranes.remove(this);
		parentMembrane.getMultiSet().addAll(getMultiSet());

		/* We copy children on parent */
		Iterator<CellLikeNoSkinMembrane> it = childMembranes.iterator();
		while (it.hasNext()) {
			CellLikeNoSkinMembrane childMembrane = it.next();
			childMembrane.parentMembrane = parentMembrane;
			parentMembrane.childMembranes.add(childMembrane);
		}

		/* We remove reference on parent */
		parentMembrane = null;
		
		/* We remove children list */
		childMembranes.clear();
		
		skinMembrane.getMembranes().remove(this);
	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane#divide()
	 */
	@Override
	public ChangeableMembrane divide() throws UnsupportedOperationException {

		return divideRec(this, parentMembrane);

	}

	protected static CellLikeMembrane divideRec(CellLikeMembrane membrane,
			CellLikeMembrane parentMembrane) {
		CellLikeMembrane m = new CellLikeNoSkinMembrane(membrane.getLabelObj(),
				parentMembrane);
		/*
		 * A new membrane with it's parent label, charge and children is created
		 * out of the division
		 */
		m.setCharge(membrane.getCharge());
		m.getMultiSet().addAll(membrane.getMultiSet());
		Iterator<CellLikeNoSkinMembrane> it = membrane.childMembranes
				.iterator();
		while (it.hasNext())
			divideRec(it.next(), m);
		return m;

	}

}
