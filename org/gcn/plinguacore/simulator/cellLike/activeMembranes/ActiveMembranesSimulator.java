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

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import org.gcn.plinguacore.simulator.cellLike.CellLikeSimulator;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;
/**
 * A simulator for active membrane P systems (with division or creation rules)
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 *
 */
public final class ActiveMembranesSimulator extends CellLikeSimulator {

	/**
	 * 
	 */

	public boolean isInitialConfig() {
		return initialConfig;
	}

	

	private boolean initialConfig;


	private static final long serialVersionUID = -7363771600223333693L;
	private Set<Integer> onlyEvolutionPermitted;

	public ActiveMembranesSimulator(Psystem psystem) {
		super(psystem);

		onlyEvolutionPermitted = new HashSet<Integer>();
		initialConfig = true;
	
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void microStepInit() {
		// TODO Auto-generated method stub
		super.microStepInit();
		onlyEvolutionPermitted.clear();
	}

	private boolean isOnlyEvolutionPermited(ChangeableMembrane m) {
		return onlyEvolutionPermitted.contains(new Integer(m.getId()));
	}

	private void setOnlyEvolutionPermitted(ChangeableMembrane m) {
		onlyEvolutionPermitted.add(m.getId());
	}

	@Override
	protected void microStepSelectRules(ChangeableMembrane m,ChangeableMembrane temp) {
		Iterator<IRule> it = getPsystem().getRules().iterator(temp.getLabel(),
				temp.getCharge());

		while (it.hasNext()) {
			IRule r = it.next();

			boolean isEvolutionRule = !noEvolution.checkRule(r);

			if (isEvolutionRule || !isOnlyEvolutionPermited(temp)) {

				long count = r.countExecutions(temp);

				if (count > 0) {

					if (!isEvolutionRule) {
						count = 1;
						setOnlyEvolutionPermitted(temp);
					}
					selectRule(r, m, count);
					removeLeftHandRuleObjects(temp, r, count);
				}
			}
		}
	}


	
	

	
	
	

}
