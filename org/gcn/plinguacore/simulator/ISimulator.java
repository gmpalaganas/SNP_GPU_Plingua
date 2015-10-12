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
import java.io.Serializable;

import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.Psystem;


/**
 * This interface provides all common methods for all kind of simulator
 * 
 * IMPORTANT: Classes which implement this interface or extend both DecoratorSimulator and Simulator should have only fields which implement java.io.Serializable. Those fields which are not serializable should be transient. Otherwise, the simulator instances won't be able to be saved to object fields. 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

public interface ISimulator extends Serializable {

	/**
	 * Sets the simulator P-system
	 * 
	 * @param psystem the simulator p-system
	 */
	public void setPsystem(Psystem psystem);
	
	
	/**
	 * @return a PrintStream to write information about the simulation process
	 *         in real-time. By default it is System.out
	 */
	public PrintStream getInfoChannel();

	/**
	 * Runs the simulator until whether the time out or the steps have been
	 * reached
	 * 
	 * @param timeOut
	 *            the maximum time out allowed
	 * @param steps
	 *            the maximum steps allowed
	 */
	public void runUntilTimeOutorSteps(long timeOut, int steps);

	/**
	 * Returns the P-system simulated
	 * 
	 * @return The P-system simulated
	 */
	public Psystem getPsystem();

	/**
	 * Sets the simulator info channel
	 * 
	 * @param infoChannel
	 *            the info channel to be set
	 */
	public void setInfoChannel(PrintStream infoChannel);

	/**
	 * Runs the simulator until the time out has been reached
	 * 
	 * @param timeOut
	 *            the time out set as the maximum time out
	 */
	public void runUntilTimeOut(long timeOut);

	/**
	 * Runs the simulator until reaching a halting configuration
	 * 
	 * @throws PlinguaCoreException
	 *             if any exception occurred during the execution
	 */
	public void run() throws PlinguaCoreException;

	/**
	 * Takes a specified number of steps
	 * 
	 * @param steps
	 *            the steps to be taken
	 * @throws PlinguaCoreException
	 *             if any exception occurred during the execution
	 */
	public void runSteps(int steps) throws PlinguaCoreException;

	/**
	 * Takes one step of simulation and generate a new configuration
	 * 
	 * @return false if the current configuration is a halting one. True if a
	 *         new configuration was generated.
	 * @throws PlinguaCoreException
	 *             if a semantic error occurs
	 */
	public boolean step() throws PlinguaCoreException;

	/**
	 * Makes one alternate step of the last one.
	 * 
	 * @return false if there is not alternate configurations. True if a new
	 *         configuration was generated.
	 * @throws UnsupportedOperationException
	 *             if alternate steps are not supported
	 */
	public boolean alternateStep() throws UnsupportedOperationException;

	/**
	 * Counts the alternative steps which could be taken
	 * 
	 * @return The number of possible next configurations in the next level of
	 *         the non-deterministic tree
	 * @throws UnsupportedOperationException
	 *             if alternate steps are not supported
	 */
	public int countAlternatives() throws UnsupportedOperationException;

	/**
	 * Takes one step back
	 * 
	 * @return false if the current configuration is the initial one. True if a
	 *         new configuration was generated.
	 * @throws UnsupportedOperationException
	 *             if steps back are not supported
	 */
	public boolean stepBack() throws UnsupportedOperationException;

	/**
	 * Resets the simulator to the initial configuration
	 */
	public void reset();

	/**
	 * Reports if the simulator supports step back
	 * 
	 * @return if the simulator supports steps back
	 */
	public boolean supportsStepBack();

	/**
	 * Reports if the simulator supports alternate steps
	 * 
	 * @return if the simulator supports alternate steps
	 */
	public boolean supportsAlternateSteps();

	/**
	 * Gets the current configuration
	 * 
	 * @return The current configuration
	 */
	public Configuration getCurrentConfig();

	/**
	 * Gets the elapsed time since the beginning of the simulation
	 * 
	 * @return the elapsed time since the beginning of the simulation
	 */
	public double getTime();

	/**
	 * Gets if the elapsed time is being measured
	 * 
	 * @return if the elapsed time is being measured
	 */
	public boolean isTimed();

	/**
	 * Sets if the elapsed time is being measured or not
	 * 
	 * @param time
	 *            if the elapsed time is being measured
	 */
	public void setTimed(boolean time);

	/**
	 * Gets the verbosity level which the instances take into account when reporting messages.
	 * @return the verbosity level which the instances take into account when reporting messages.
	 */
	public int getVerbosity();

	/**
	 * Sets the verbosity level which the instances take into account when reporting messages.
	 * @param verbosity the verbosity level to set.
	 */
	public void setVerbosity(int verbosity);

	
	/**
	 * Returns a boolean representing if there are any step back which the simulator can go back to
	 * @return true if there are any step back which the simulator can go back to, false otherwise
	 */
	public boolean stepsBackAvailable();
	/**  
	 * Cleans the configurations previously reached
	 */
	public void cleanPreviousConfigurations();
	/**
	 * Sets the current configuration of the simulator
	 * @param configuration the configuration to be set
	 */
	public void setCurrentConfig(Configuration configuration);
	
	/**
	 * Stops the current thread where the run process takes place
	 */
	public void stopThread();
	   /**
	    * Returns if the simulator has finished
	   * @return true if the current configuration is a halting one.
	   */
	public boolean isFinished();
}
