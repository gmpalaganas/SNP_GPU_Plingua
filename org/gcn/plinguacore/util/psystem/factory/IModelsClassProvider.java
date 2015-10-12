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


package org.gcn.plinguacore.util.psystem.factory;

import java.util.Iterator;

import org.gcn.plinguacore.util.PlinguaCoreException;



/**
 * An interface to provide AbstractPsystemFactory the names of the classes which represent abstract factories for models.
 * It's intended to be used only by AbstractPsystemFactory class, in order to hide the resources layer from the user.
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 */
public interface IModelsClassProvider extends IModelsInfo{
	
	/**
	 * Gets the name of the simulator class for the model name passed
	 * 
	 * @param model
	 *            the model which its simulator class name is required
	 * @return the simulator class name of the model passed
	 * @throws PlinguaCoreException
	 *             if there's no simulator class implemented for the model
	 *             passed
	 */
	public String getModelClassName(String model) throws PlinguaCoreException ;
	
	/**
	 * Gets the model name supported by the simulator (identified by its name)
	 * passed
	 * 
	 * @param className
	 *            the name of the class which model name is required
	 * @return the model name required
	 * @throws PlinguaCoreException
	 *             if there's not any model which the class passed as parameter
	 *             supports
	 */
	public String getModelId(String className) throws PlinguaCoreException;
	
	

}
