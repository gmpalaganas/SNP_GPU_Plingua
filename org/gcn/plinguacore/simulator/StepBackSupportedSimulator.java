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

import java.util.Stack;

import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Configuration;



/**
 * This class is intended to be extended by classes which provide functionality
 * to take steps back, according to Decorator pattern
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class StepBackSupportedSimulator extends DecoratorSimulator {

	private Stack<Configuration> configurationStack;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6761190875799266639L;

	/**
	 * Creates a new StepBackSupportedSimulator instance which will wrap sim
	 * 
	 * @param sim
	 *            the decorated field according to Decorator pattern
	 */
	public StepBackSupportedSimulator(ISimulator sim) {
		super(sim);
		configurationStack= new Stack<Configuration>();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns true, because StepBackSupportedSimulator
	 * supports step back
	 * 
	 * @return true, because in this specific class, steps back are always
	 *         allowed
	 */
	@Override
	public final boolean supportsStepBack() {
		return true;
	}
	
	/**
	 * @see org.gcn.plinguacore.simulator.DecoratorSimulator#step()
	 */
	@Override
	public final boolean step() throws PlinguaCoreException{
		configurationStack.push(decorated.getCurrentConfig());
		return super.step();
	}


	/**
	 * @see org.gcn.plinguacore.simulator.DecoratorSimulator#stepsBackAvailable()
	 */
	@Override
	public boolean stepsBackAvailable(){
		return !configurationStack.isEmpty();
	}



	/**
	 * @see org.gcn.plinguacore.simulator.DecoratorSimulator#cleanPreviousConfigurations()
	 */
	@Override
	public void cleanPreviousConfigurations() {
		// TODO Auto-generated method stub
		configurationStack.clear();
	}

	/**
	 * @see org.gcn.plinguacore.simulator.DecoratorSimulator#stepBack()
	 */
	@Override
	public boolean stepBack(){
		if(configurationStack.isEmpty())
			throw new IllegalStateException("There's no previous configuration");
		decorated.setCurrentConfig(configurationStack.pop());
		return true;
		
	}

	/**
	 * @see org.gcn.plinguacore.simulator.DecoratorSimulator#reset()
	 */
	@Override
	public void reset() {
		cleanPreviousConfigurations();
		super.reset();
	}

	
	
}
