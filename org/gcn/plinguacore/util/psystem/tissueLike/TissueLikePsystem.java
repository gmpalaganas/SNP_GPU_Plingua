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

package org.gcn.plinguacore.util.psystem.tissueLike;



import org.gcn.plinguacore.util.InfiniteMultiSet;
import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;

import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.membrane.MembraneStructure;
import org.gcn.plinguacore.util.psystem.tissueLike.membrane.TissueLikeMembraneStructure;


/**
 * This class represents P-systems belonging to tissue-like group
 * 
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class TissueLikePsystem extends Psystem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2290929626246752890L;


	
	@Override
	public void setMembraneStructure(MembraneStructure membraneStructure) {
		// TODO Auto-generated method stub
		if (membraneStructure instanceof CellLikeSkinMembrane)
		{
			super.setMembraneStructure(new TissueLikeMembraneStructure(membraneStructure,this));
		}
		else
		if (membraneStructure instanceof TissueLikeMembraneStructure)
			super.setMembraneStructure(membraneStructure);
		else
			throw new IllegalArgumentException("The membrane structure must be tissue-like");
	}

	@Override
	protected Configuration newConfigurationObject() {
		// TODO Auto-generated method stub
		return new TissueLikeConfiguration(this);
	}

	@Override
	protected void addInitialMultiSets(Membrane m) {
		// TODO Auto-generated method stub
		super.addInitialMultiSets(m);
		String envLabel = ((TissueLikeMembraneStructure)getMembraneStructure()).getEnvironmentLabel();
		if (m.getLabel().equals(envLabel))
			((InfiniteMultiSet<String>)m.getMultiSet()).setAllObjectsWithInfiniteMultiplicity();
		
	}

	
	
	
	
	
	

	

}
