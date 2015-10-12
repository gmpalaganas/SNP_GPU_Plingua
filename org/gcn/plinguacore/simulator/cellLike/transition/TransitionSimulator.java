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

import java.util.Iterator;

import org.gcn.plinguacore.simulator.cellLike.CellLikeSimulator;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
/**
 * A simulator for transition and symport/antiport P systems
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 *
 */
public final class TransitionSimulator extends CellLikeSimulator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5914531846669504917L;

	public TransitionSimulator(Psystem psystem) {
		super(psystem);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void microStepSelectRules(ChangeableMembrane m,ChangeableMembrane temp) {
		Iterator<IRule> it = getPsystem().getRules().iterator(temp.getLabel(),
				temp.getCharge());

		while (it.hasNext()) {
			IRule r = it.next();
			long count = r.countExecutions(temp);
			if (count > 0) {
				selectRule(r, m, count);
				removeLeftHandRuleObjects(temp, r, count);
			}
		}
	}

}
