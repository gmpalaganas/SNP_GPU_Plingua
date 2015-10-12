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

import org.gcn.plinguacore.util.psystem.rule.AbstractRuleFactory;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoDivision;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoLeftExternalMultiSetWithCreation;

/**
 * This class is intended to give support for P-systems in which only membrane
 * creation, evolution, dissolution and communication are allowed
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class CreationPsystemFactory extends ActiveMembranesPsystemFactory {

	private static CreationPsystemFactory singleton = null;

	private CreationPsystemFactory() {
		super();
		checkRule = new NoLeftExternalMultiSetWithCreation(new NoDivision(
				getCheckRule()));

	}

	/**
	 * Gets only instance of CreationPsystemFactory class, as stated in
	 * Singleton pattern
	 * 
	 * @return the only instance of CreationPsystemFactory class
	 */
	public static CreationPsystemFactory getInstance() {
		if (singleton == null)
			singleton = new CreationPsystemFactory();
		return singleton;
	}

	


}
