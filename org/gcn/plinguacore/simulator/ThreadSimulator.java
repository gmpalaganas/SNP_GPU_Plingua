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
 * This class gives support for simulations which will be interrupted when defined a timeout has elapsed
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 *
 */

import org.gcn.plinguacore.util.PlinguaCoreException;

class ThreadSimulator extends Thread {
	protected AbstractSimulator simul;

	public ThreadSimulator(AbstractSimulator simul) {
		super("Inner simulator thread");
		if (simul == null)
			throw new NullPointerException(
					"Simulator constructor argument shouldn't be null");
		this.simul = simul;
	}
	
	@Override
	public void run() {
		/*
		 * The simulation runs and when it has finished the ThreadSimulator
		 * instance reports the monitor
		 */
		try {
			/*Now, the simulation takes place in this thread*/
			simul.setThread(Thread.currentThread());
			executeSimulator();
		} catch (PlinguaCoreException e) {
			/*If an exception occurred, end*/
			tellMonitor();
			return;

		}
		tellMonitor();
	}
	
	protected void executeSimulator() throws PlinguaCoreException{
		simul.run();
	}

	/**
	 * Tells the Monitor Thread that the simulation has finished so that the
	 * Monitor Process can stop waiting
	 */
	protected synchronized void tellMonitor() {
		

		notifyAll();
		/*try {
			finalize();
		} catch (Throwable e) {*/
			// TODO Auto-generated catch block
		//}		
		
		/*Give in execution time, as we can't destroy the thread*/
		//Thread.yield();

	}

}
