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

package org.gcn.plinguacore.simulator;

import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.Pair;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.CellLikeConfiguration;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.membrane.MembraneStructure;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoEvolution;



/**
 * An abstract class for simulators
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

public abstract class CopyOfAbstractSimulator extends AbstractSimulator implements ISimulator {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8223592697775323152L;

	private Configuration stableCurrentConfig = null;


	
	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#setPsystem(org.gcn.plinguacore.util.psystem.Psystem)
	 */
	@Override
	public void setPsystem(Psystem psystem){
		super.setPsystem(psystem);
		stableCurrentConfig = initializePsystemConfiguration(psystem, stableCurrentConfig);
	}

	/**
	 * Creates a simulator which will simulate the P-system passed as argument
	 * 
	 * @param psystem
	 *            The P system to be simulated
	 */
	public CopyOfAbstractSimulator(Psystem psystem) {
		super(psystem);
		innerReset();
	}
	
	
	/**
	 * Resets the simulator to the initial configuration
	 */
	public void reset() {
		super.reset();
		innerReset();
	
	
	}
	

	
	


	
	
	

	/**
	 * Sets the current configuration of the simulator
	 * @param currentConfig the configuration to be set
	 */
	public void setCurrentConfig(Configuration currentConfig){
		super.setCurrentConfig(currentConfig);
		updateStableCurrentConfig();
	}
	
	private void updateStableCurrentConfig(){
		this.stableCurrentConfig =(Configuration)currentConfig.clone(); 
	}
	
	
	/**
	 * Makes one step of simulation and generate a new configuration
	 * 
	 * @return false if the current configuration is a halting one. True if a
	 *         new configuration was generated.
	 * @throws PlinguaCoreException
	 *             if a semantic error occurs 
	 */
	public final boolean step() throws PlinguaCoreException{		
		boolean stepTaken =  specificStep();
		/*After taking the step, we need to save the current configuration to avoid not having an stable configuration in case the step couldn't be performed*/
		updateStableCurrentConfig();
		return stepTaken;
	}
	/*
	 * QUERY: This set of methods allows Simulator instances to be stored and
	 * loaded
	 */







	private void innerReset(){
		stableCurrentConfig = (Configuration)getPsystem().getFirstConfiguration().clone();
	}
	
	

	/**
	 * Gets the current configuration
	 * 
	 * @return The current configuration
	 */
	public final Configuration getCurrentConfig() {
		return stableCurrentConfig;
	}


	
	
	
	
	
	
	
	protected abstract boolean specificStep() throws PlinguaCoreException; 


	

}
