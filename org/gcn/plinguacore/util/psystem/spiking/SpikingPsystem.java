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



import org.gcn.plinguacore.simulator.spiking.SpikingPsystemFactory;
import org.gcn.plinguacore.util.InfiniteMultiSet;
import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.Psystem;

import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.membrane.MembraneStructure;
import org.gcn.plinguacore.util.psystem.spiking.membrane.SpikingMembrane;
import org.gcn.plinguacore.util.psystem.spiking.membrane.SpikingMembraneStructure;
import org.gcn.plinguacore.util.psystem.spiking.SpikingConstants;


/**
 * This class represents P-systems belonging to spike-like group
 * 
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class SpikingPsystem extends Psystem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6533958106892732814L;
	

	
	
	public static SpikingPsystem buildPsystem()
	{
		SpikingMembraneStructure structure = new SpikingMembraneStructure("environment");
		return buildPsystem(structure);
	}
	
	public static SpikingPsystem buildPsystem(SpikingMembraneStructure structure)
	{
		SpikingPsystem ps = new SpikingPsystem();
		
		try{
		ps.setAbstractPsystemFactory(SpikingPsystemFactory.getInstance());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		ps.setMembraneStructure(structure);

		return ps;
	}
	
	@Override
	public void setMembraneStructure(MembraneStructure membraneStructure) {
		// TODO Auto-generated method stub
		if (membraneStructure instanceof SpikingMembraneStructure)
		{
			
			super.setMembraneStructure(membraneStructure);
		}
		else
			throw new IllegalArgumentException("The membrane structure must be spiking-like");
	}

	@Override
	protected Configuration newConfigurationObject() {
		// TODO Auto-generated method stub
		return new SpikingConfiguration(this);
	}

	@Override
	protected void addInitialMultiSets(Membrane m) {
		// TODO Auto-generated method stub
 
		long spikes = 0L;
		 
		SpikingMembrane s = (SpikingMembrane) m;
		
		if (getInitialMultiSets().containsKey(m.getLabel()))
			spikes = getInitialMultiSets().get(m.getLabel()).size();
		
		String object = SpikingConstants.spikeSymbol;
		
		if(spikes > 0 && getInitialMultiSets().entrySet().contains(SpikingConstants.antiSpikeSymbol))
			object = SpikingConstants.antiSpikeSymbol;
			
		s.addSpikes(object,spikes);
		
		String envLabel = ((SpikingMembraneStructure)getMembraneStructure()).getEnvironmentLabel();
		if (m.getLabel().equals(envLabel))
			((InfiniteMultiSet<String>)m.getMultiSet()).setAllObjectsWithInfiniteMultiplicity();
		
		addAlphabetObjects(m.getMultiSet());
		
	}

	
	
	
	
	
	

	

}
