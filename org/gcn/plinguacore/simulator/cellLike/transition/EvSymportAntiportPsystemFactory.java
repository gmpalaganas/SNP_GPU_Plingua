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
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoConstant;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoCreation;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoDissolution;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoDivision;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoEmptyLeftMultiSet;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoEvolution;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoGeneStrings;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoLeftInnerMembranes;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoPriority;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoRightInnerMembranes;


/**
 * This class is intended to give support for P-systems in which only membrane
 * communication and evolution are allowed
 *
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 *
 *
 * 
 */
public class EvSymportAntiportPsystemFactory extends AbstractCellLikePsystemFactory {

	private static EvSymportAntiportPsystemFactory singleton = null;

	protected EvSymportAntiportPsystemFactory() {
		super();

		checkRule = new NoEmptyLeftMultiSet(new NoEvolution(
				new NoLeftInnerMembranes(new NoRightInnerMembranes(
						new NoDissolution(new NoDivision(new NoCreation(
								new NoConstant(new NoPriority(new NoGeneStrings(
										))))))))));
		// TODO Auto-generated constructor stub
	}

	/**
	 * Gets only instance of EvSymportAntiportPsystemFactory class, as stated in
	 * Singleton pattern
	 * 
	 * @return the only instance of EvSymportAntiportPsystemFactory class
	 */
	public static EvSymportAntiportPsystemFactory getInstance() {
		if (singleton == null)
			singleton = new EvSymportAntiportPsystemFactory();
		return singleton;
	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.factory.IPsystemFactory#getCreateSimulator()
	 */
	public CreateSimulator getCreateSimulator() throws PlinguaCoreException {
		return new TransitionCreateSimulator(getModelName());
	}

	
	

}
