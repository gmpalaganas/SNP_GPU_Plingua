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

package org.gcn.plinguacore.simulator.cellLike.transition;

import org.gcn.plinguacore.simulator.CreateSimulator;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.CellLikePsystem;
import org.gcn.plinguacore.util.psystem.factory.AbstractPsystemFactory;
import org.gcn.plinguacore.util.psystem.factory.cellLike.AbstractCellLikePsystemFactory;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.*;

public class EvolutionCommunicationPsystemFactory extends
		AbstractCellLikePsystemFactory {

	private static EvolutionCommunicationPsystemFactory singleton = null;

	protected EvolutionCommunicationPsystemFactory() {
		super();
		// TODO Auto-generated constructor stub

		checkRule = new NoCharges(new NoEmptyLeftMultiSet(
				new NoRightRepeatedLabels(
						new NoLeftRepeatedLabels(
								new NoLeftInnerMembranes(
										new NoRightInnerMembranes(
												new NoDissolution(
														new NoDivision(
																new NoCreation(
																		new NoConstant(
																				new NoPriority(
																						new NoGeneStrings())))))))))));
	}
	
	public static EvolutionCommunicationPsystemFactory getInstance()
	{
		if (singleton==null)
			singleton=new EvolutionCommunicationPsystemFactory();
		return singleton;
	}

	@Override
	public CreateSimulator getCreateSimulator() throws PlinguaCoreException {
		// TODO Auto-generated method stub
		return new TransitionCreateSimulator(getModelName());
	}

	
	

}
