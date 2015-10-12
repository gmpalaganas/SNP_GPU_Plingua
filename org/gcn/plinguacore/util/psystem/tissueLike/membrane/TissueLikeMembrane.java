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

package org.gcn.plinguacore.util.psystem.tissueLike.membrane;

import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;

public class TissueLikeMembrane extends ChangeableMembrane  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4331622171073550025L;
	private TissueLikeMembraneStructure structure;
	
	
		
	protected TissueLikeMembrane(String label,MultiSet<String> multiSet,TissueLikeMembraneStructure structure) {
		super(new Label(label), (byte)0, multiSet);
		initMembraneStructure(structure);
		
	}

	protected TissueLikeMembrane(String label,TissueLikeMembraneStructure structure) {
		super(new Label(label));
		initMembraneStructure(structure);
	
	}
	
	
	
	protected TissueLikeMembrane(Membrane membrane,TissueLikeMembraneStructure structure)
	{
		this(membrane.getLabel(),structure);
		if (membrane instanceof CellLikeMembrane)
		{
			CellLikeMembrane clm=(CellLikeMembrane)membrane;
			if (!clm.getChildMembranes().isEmpty())
				throw new IllegalArgumentException("Tissue-like membranes must be elemental membranes");
		}
		getMultiSet().addAll(membrane.getMultiSet());
	}
	
	private void initMembraneStructure(TissueLikeMembraneStructure structure)
	{
		if (structure==null)
			throw new NullPointerException("Null membrane structure");
		setId(structure.getNextId());
		this.structure=structure;
	}
	

	@Override
	public void dissolve() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();

	}
	@Override
	public ChangeableMembrane divide() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		if (getLabel().equals(structure.getEnvironmentLabel()))
			throw new UnsupportedOperationException("The environment cannot be divided");
		TissueLikeMembrane tlm= new TissueLikeMembrane(this,structure);
		structure.add(tlm);
		return tlm;

	}
	public TissueLikeMembraneStructure getStructure() {
		return structure;
	}


	
	

	
	

	

}
