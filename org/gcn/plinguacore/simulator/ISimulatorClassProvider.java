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
 * An interface to get information about class name providers for available simulators
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 *
 */
public interface ISimulatorClassProvider extends ISimulatorInfo{

	

	
	
	/**
	 * Returns an iterator on the simulators which support alternative step
	 * @return  an iterator on the simulators which support alternative step
	 */
	 
	public Class<?>[] getAlternativeStepSupportedSimulators();
	

	/**
	 * Returns the name of the class which implements the simulator required by id and which provides the extra functionality appointed by typeId
	 * @param id the id of the required simulator
	 * @param typeId the id of the type of simulator required
	 * @return the name of the class which implements the simulator
	 * @throws PlinguaCoreException if the simulator required doesn't exists
	 */
	public String idClassName(String id, String typeId)
	throws PlinguaCoreException;
}
