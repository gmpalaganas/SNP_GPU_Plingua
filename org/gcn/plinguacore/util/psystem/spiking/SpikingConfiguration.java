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

package org.gcn.plinguacore.util.psystem.spiking;

import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.spiking.membrane.SpikingMembraneStructure;



public class SpikingConfiguration extends Configuration {

	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -8550282887814471902L;

	protected SpikingConfiguration(Psystem psystem) {
		super(psystem);
		// TODO Auto-generated constructor stub
	}
	public MultiSet<String> getEnvironment() {
		// TODO Auto-generated method stub
		SpikingMembraneStructure structure =  (SpikingMembraneStructure)getMembraneStructure();
		return structure.getEnvironment();
	}

	@Override
	protected Configuration getNewConfiguration() {
		// TODO Auto-generated method stub
		return new SpikingConfiguration(getPsystem());
	}

	
}
