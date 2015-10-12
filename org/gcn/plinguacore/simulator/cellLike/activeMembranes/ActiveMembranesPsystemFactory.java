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
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.CellLikePsystem;
import org.gcn.plinguacore.util.psystem.factory.AbstractPsystemFactory;
import org.gcn.plinguacore.util.psystem.factory.cellLike.AbstractCellLikePsystemFactory;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoConstant;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoCooperation;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoEmptyLeftMultiSet;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoGeneStrings;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoLeftExternalMultiSetWithDissolution;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoLeftInnerMembranes;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoMultipleProduction;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoPriority;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoRightInnerMembranes;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NotAllEmptyMultiSet;


/**
 * This class provides common functionality for Active Membrane P-system factories, providing its common simulator and those conditions which are common for all Active Membranes mode (via checkRule method)
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 *
 */
abstract class ActiveMembranesPsystemFactory extends AbstractCellLikePsystemFactory {

	/**
	 * @see org.gcn.plinguacore.util.psystem.factory.IPsystemFactory#getCreateSimulator()
	 */
	@Override
	public CreateSimulator getCreateSimulator() throws PlinguaCoreException {

		return new ActiveMembranesCreateSimulator(getModelName());
	}

	protected ActiveMembranesPsystemFactory() {
		super();
		
		checkRule = new NoEmptyLeftMultiSet(
				new NoLeftExternalMultiSetWithDissolution(						
								new NotAllEmptyMultiSet(
										new NoMultipleProduction(
												new NoLeftInnerMembranes(
														new NoCooperation(
																new NoRightInnerMembranes(
																		new NoPriority(
																				new NoGeneStrings(
																				new NoConstant())))))))));
		// TODO Auto-generated constructor stub
	}

	


}
