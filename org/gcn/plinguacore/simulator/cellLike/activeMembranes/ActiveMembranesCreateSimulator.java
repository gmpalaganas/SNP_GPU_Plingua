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


package org.gcn.plinguacore.simulator.cellLike.activeMembranes;

import org.gcn.plinguacore.simulator.CreateSimulator;
import org.gcn.plinguacore.util.PlinguaCoreException;


/**
 * This class creates simulators which comply with Active Membrane model
 * conditions
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class ActiveMembranesCreateSimulator extends CreateSimulator {

	/**
	 * Creates a CreateSimulator object which will create simulators which
	 * comply with the given model conditions
	 * 
	 * @param model
	 *            the model of the simulators the CreateSimulator instance to
	 *            instantiate will create
	 * @throws PlinguaCoreException
	 */
	public ActiveMembranesCreateSimulator(String model)
			throws PlinguaCoreException {
		super(model);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getRoute() {
		// TODO Auto-generated method stub
		return "active_membranes";
	}

}
