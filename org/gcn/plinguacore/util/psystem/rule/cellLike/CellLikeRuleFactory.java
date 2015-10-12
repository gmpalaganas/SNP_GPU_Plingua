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

package org.gcn.plinguacore.util.psystem.rule.cellLike;

import org.gcn.plinguacore.util.psystem.rule.AbstractRuleFactory;
import org.gcn.plinguacore.util.psystem.rule.IConstantRule;
import org.gcn.plinguacore.util.psystem.rule.IDoubleCommunicationRule;
import org.gcn.plinguacore.util.psystem.rule.IPriorityRule;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.IStochasticRule;
import org.gcn.plinguacore.util.psystem.rule.LeftHandRule;
import org.gcn.plinguacore.util.psystem.rule.RightHandRule;


public class CellLikeRuleFactory implements AbstractRuleFactory {

	@Override
	public IRule createBasicRule(boolean dissolves, LeftHandRule leftHandRule,
			RightHandRule rightHandRule) {
		// TODO Auto-generated method stub
		return new CellLikeRule(dissolves,leftHandRule,rightHandRule);
	}

	@Override
	public IConstantRule createConstantRule(boolean dissolves,
			LeftHandRule leftHandRule, RightHandRule rightHandRule,
			float constant) {
		// TODO Auto-generated method stub
		return new ConstantCellLikeRule(dissolves,leftHandRule,rightHandRule,constant);
	}

	@Override
	public IPriorityRule createPriorityRule(boolean dissolves,
			LeftHandRule leftHandRule, RightHandRule rightHandRule, int priority) {
		// TODO Auto-generated method stub
		return new PriorityCellLikeRule(dissolves,leftHandRule,rightHandRule,priority);
	}

	@Override
	public IStochasticRule createStochasticRule(boolean dissolves,
			LeftHandRule leftHandRule, RightHandRule rightHandRule,
			float constant) {
		// TODO Auto-generated method stub
		return new StochasticCellLikeRule(dissolves,leftHandRule,rightHandRule,constant);
	}

	@Override
	public IDoubleCommunicationRule createDoubleCommunicationRule(boolean dissolves,LeftHandRule leftHandRule,
			RightHandRule rightHandRule) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Double communication rules are not allowed");
	}

}
