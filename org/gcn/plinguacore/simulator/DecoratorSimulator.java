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

import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.Psystem;



/**
 * This class gives support for all common functionality in simulator
 * decorators, according to Decorator pattern
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 */
public abstract class DecoratorSimulator implements ISimulator {

	
	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#setPsystem(org.gcn.plinguacore.util.psystem.Psystem)
	 */
	public void setPsystem(Psystem psystem){
		decorated.setPsystem(psystem);
	}
	/**
	 * Creates a new instance based on a decorated instance
	 * 
	 * @param decorated
	 *            the simulator to be decorated, as stated by decorator pattern
	 */
	public DecoratorSimulator(ISimulator decorated) {
		super();
		if (decorated == null)
			throw new NullPointerException(
					"Simulator constructor argument shouldn't be null");
		this.decorated = decorated;
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#cleanPreviousConfigurations()
	 */
	@Override
	public void cleanPreviousConfigurations() {
		decorated.cleanPreviousConfigurations();
		
	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#setCurrentConfig(org.gcn.plinguacore.util.psystem.Configuration)
	 */

	@Override
	public void setCurrentConfig(Configuration configuration) {
		// TODO Auto-generated method stub
		decorated.setCurrentConfig(configuration);
		
	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#getVerbosity()
	 */
	@Override
	public int getVerbosity() {
		// TODO Auto-generated method stub
		return decorated.getVerbosity();
	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#setVerbosity(int)
	 */
	@Override
	public void setVerbosity(int verbosity) {
		decorated.setVerbosity(verbosity);
		
	}




	protected static final long serialVersionUID = 1L;

	protected ISimulator decorated;

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#stepsBackAvailable()
	 */
	@Override
	public boolean stepsBackAvailable() {
		// TODO Auto-generated method stub
		return decorated.stepsBackAvailable();
	}




	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#runUntilTimeOut(int)
	 */
	@Override
	public final void runUntilTimeOut(long timeOut) {
		decorated.runUntilTimeOut(timeOut);
	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#run()
	 */
	@Override
	public final void run() throws PlinguaCoreException {
		decorated.run(/* outputStream */);
	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#runSteps(int)
	 */
	@Override
	public final void runSteps(int steps) throws PlinguaCoreException {
		decorated.runSteps(steps);
	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#alternateStep()
	 */
	@Override
	public boolean alternateStep()
			throws UnsupportedOperationException{
		return decorated.alternateStep();
	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#countAlternatives()
	 */
	@Override
	public int countAlternatives()
			throws UnsupportedOperationException{
		return decorated.countAlternatives();
	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#supportsStepBack()
	 */
	@Override
	public boolean supportsStepBack(){
		return decorated.supportsStepBack();
	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#supportsAlternateSteps()
	 */
	@Override
	public boolean supportsAlternateSteps(){
		return decorated.supportsAlternateSteps();
	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#getCurrentConfig()
	 */
	public final Configuration getCurrentConfig() {
		// TODO Auto-generated method stub
		return decorated.getCurrentConfig();
	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#getInfoChannel()
	 */
	@Override
	public final PrintStream getInfoChannel() {
		// TODO Auto-generated method stub
		return decorated.getInfoChannel();

	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#getTime()
	 */
	public final double getTime() {
		// TODO Auto-generated method stub
		return decorated.getTime();
	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#isTimed()
	 */
	public final boolean isTimed() {
		// TODO Auto-generated method stub
		return decorated.isTimed();
	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#reset()
	 */
	public void reset() {
		decorated.reset();
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#getPsystem()
	 */
	public Psystem getPsystem() {
		return decorated.getPsystem();
	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#setInfoChannel(java.io.PrintStream)
	 */
	@Override
	public void setInfoChannel(PrintStream infoChannel) {
		decorated.setInfoChannel(infoChannel);
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#setTimed(boolean)
	 */
	public void setTimed(boolean time) {
		decorated.setTimed(time);
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#step()
	 */
	public boolean step() throws PlinguaCoreException {
		// TODO Auto-generated method stub
		return decorated.step();
	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#runUntilTimeOutorSteps(int, int)
	 */
	@Override
	public void runUntilTimeOutorSteps(long timeOut, int steps) {
		decorated.runUntilTimeOutorSteps(timeOut, steps);

	}

	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#stepBack()
	 */
	@Override
	public boolean stepBack(){
		return decorated.stepBack();
	}
	
	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#stopThread()
	 */
	public void stopThread(){
		decorated.stopThread();
	}
	
	/**
	 * @see org.gcn.plinguacore.simulator.ISimulator#isFinished()
	 */
	public boolean isFinished(){
		return decorated.isFinished();
	}

}
