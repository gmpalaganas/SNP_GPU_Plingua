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

import java.io.Serializable;

import org.gcn.plinguacore.simulator.CreateSimulator;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.checkPsystem.CheckPsystem;
import org.gcn.plinguacore.util.psystem.rule.AbstractRuleFactory;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;


/**
 * This interface is intended to be implemented by a P-system factory for each
 * supported model. Each factory should be able to create its own CheckRule
 * instances (which makes sure the p-system complies with the model conditions)
 * and its own CreateSimulator, which should be able to create simulators for
 * that model
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public interface IPsystemFactory{
	/**
	 * Creates a CreateSimulator instance able to create simulators for the
	 * specific model represented by this instance
	 * 
	 * @return the CreateSimulator instance able to create simulators for the
	 *         specific model represented by this instance
	 * @throws PlinguaCoreException
	 *             if any error occurred during the instantiation of
	 *             CreateSimulator
	 */
	public CreateSimulator getCreateSimulator() throws PlinguaCoreException;

	/**
	 * Gets a CheckRule object to check if the P-system complies with the model
	 * conditions, according to the specific model to give support
	 * 
	 * @return the CheckRule object able to check if the P-system complies with
	 *         the model conditions
	 */
	public CheckRule getCheckRule();
	
	public CheckPsystem getCheckPsystem();

	/**
	 * Gets the name of the specific model this class gives support
	 * 
	 * @return the the name of the specific model this class gives support
	 */
	public String getModelName();
	
	
	public Psystem createPsystem() throws PlinguaCoreException;

	AbstractRuleFactory getRuleFactory();
	
	
}
