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

package org.gcn.plinguacore.util.psystem.rule;

import java.util.Set;

import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;

public interface IRule {
	/**
	 * Counts the number of possible executions of the current rule on a
	 * membrane
	 * 
	 * @param membrane
	 *            the membrane where the executions will be counted
	 * @return the number of possible executions on the ChangeableMembrane
	 *         instance given
	 */
	public long countExecutions(ChangeableMembrane membrane);
	
	public boolean execute(ChangeableMembrane membrane,	MultiSet<String> environment);
	
	public boolean execute(ChangeableMembrane membrane,MultiSet<String> environment, long executions); 
	/**
	 * Gets if the rule can be applied to the given membrane
	 * 
	 * @param membrane
	 *            the ChangeableMembrane instance which the rule could be
	 *            applied to
	 * @return if the rule can be applied to the given ChangeableMembrane
	 *         instance
	 */
	public boolean isExecutable(ChangeableMembrane membrane);
	
	public boolean dissolves();
	
	public LeftHandRule getLeftHandRule();
	
	public RightHandRule getRightHandRule();
	
	public Set<String> getObjects();
	
	public boolean hasNewMembranes();
	
	public void setRuleId(long ruleId);
	public long getRuleId();
	
}
