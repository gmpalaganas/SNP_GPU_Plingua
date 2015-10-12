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

/**
 * This class provides support for selecting and executing alternative rules
 * different from the selected one by default
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

public abstract class AlternativeStepsSupportedSimulator extends
		DecoratorSimulator {

	private static final long serialVersionUID = 4933648667083874575L;

	/**
	 * Creates a new AlternativeStepsSupportedSimulator which will use sim as
	 * decorated field, as stated in Singleton pattern
	 * 
	 * @param decorated
	 *            the decorated attribute this class will rely on
	 */
	public AlternativeStepsSupportedSimulator(ISimulator decorated) {
		super(decorated);
		// TODO Auto-generated constructor stub
	}


	/**
	 * In AlternativeStepsSupportedSimulator instances, this method always
	 * returns true
	 * 
	 * @see org.gcn.plinguacore.simulator.DecoratorSimulator#supportsAlternateSteps()
	 */
	@Override
	public final boolean supportsAlternateSteps() {
		return true;
	}

	/**
	 * Executes an alternate step and returns if the step has been properly
	 * executed
	 */
	@Override
	public abstract boolean alternateStep();

	/**
	 * If possible, counts the number of alternative steps available for the
	 * simulator
	 */
	@Override
	public abstract int countAlternatives()
			throws UnsupportedOperationException;



}
