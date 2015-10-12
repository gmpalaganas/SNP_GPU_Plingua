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

import org.gcn.plinguacore.util.PlinguaCoreException;

/**
 * This class gives support for simulations which will be interrupted when
 * several steps have been taken or the timeout have elapsed
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

class StepLimitedThreadSimulator extends ThreadSimulator {

	private int steps;

	/**
	 * Creates a new instance to simulate simul until it ends or until
	 * maximumSteps steps have been taken
	 * 
	 * @param simul
	 *            the simulator which will be executed
	 * @param maximumSteps
	 *            the maximum number of steps for the simulator
	 */
	public StepLimitedThreadSimulator(AbstractSimulator simul, int maximumSteps) {
		super(simul);
		if (maximumSteps <= 0)
			throw new IllegalArgumentException(
					"Timeout argument shouldn't be 0 nor less");
		this.steps = maximumSteps;
		// TODO Auto-generated constructor stub
	}


	@Override
	protected void executeSimulator() throws PlinguaCoreException{
		simul.runSteps(steps);
	}

}
