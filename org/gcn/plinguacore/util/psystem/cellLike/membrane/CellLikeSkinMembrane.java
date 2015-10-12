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


import java.util.HashMap;
import java.util.Iterator;

import java.util.Map;

import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.membrane.MembraneStructure;


/**
 * This class represents cell-like skin membranes
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

public final class CellLikeSkinMembrane extends CellLikeMembrane implements MembraneStructure {
	private final static long serialVersionUID = 1L;
	private int nextId;
	private Map<Integer,CellLikeMembrane>membranes;
	
	protected CellLikeSkinMembrane(Label label) {
		super(label);
		setId(0);
		nextId = 1;
		membranes=new HashMap<Integer,CellLikeMembrane>();
		membranes.put(0,this);
	}

	
	
	protected final Map<Integer, CellLikeMembrane> getMembranes() {
		return membranes;
	}



	protected int getNextID() {
		/* We get the current next id and the next id is increased */
		int id = nextId;
		nextId++;
		return id;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone(){
		/*
		 * We need to clone both the label, the charge, the inner multiset and
		 * the children
		 */
		CellLikeSkinMembrane m = new CellLikeSkinMembrane(getLabelObj());
		m.setCharge(getCharge());
		m.getMultiSet().addAll(getMultiSet());
		Iterator<CellLikeNoSkinMembrane> it = childMembranes.iterator();
		while (it.hasNext())
			CellLikeNoSkinMembrane.divideRec(it.next(), m);
		return m;

	}

	@Override
	public Membrane getMembrane(int id) {
		// TODO Auto-generated method stub
		return membranes.get(id);
	}
	
	

}
