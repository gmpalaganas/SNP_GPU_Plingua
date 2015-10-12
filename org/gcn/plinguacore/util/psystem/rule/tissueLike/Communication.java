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

package org.gcn.plinguacore.util.psystem.rule.tissueLike;


import org.gcn.plinguacore.util.psystem.tissueLike.membrane.TissueLikeMembrane;

public class Communication {
	
	private TissueLikeMembrane firstMembrane;
	private TissueLikeMembrane secondMembrane;
	private DoubleCommunicationTissueLikeRule rule;
	private long executions;
	public Communication(TissueLikeMembrane firstMembrane,
			TissueLikeMembrane secondMembrane,
			DoubleCommunicationTissueLikeRule rule, long executions) {
		super();
		this.firstMembrane = firstMembrane;
		this.secondMembrane = secondMembrane;
		this.rule = rule;
		this.executions = executions;
	}
	public TissueLikeMembrane getFirstMembrane() {
		return firstMembrane;
	}
	public TissueLikeMembrane getSecondMembrane() {
		return secondMembrane;
	}
	public DoubleCommunicationTissueLikeRule getRule() {
		return rule;
	}
	public long getExecutions() {
		return executions;
	}
	
	
	
	
}
