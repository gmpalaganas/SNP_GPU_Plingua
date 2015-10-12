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

package org.gcn.plinguacore.util.psystem.rule.checkRule;

import java.io.Serializable;
import java.util.List;

import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.rule.IRule;



/**
 * This interface defines all the functionality to test if a rule complies with
 * all the conditions stated by a model
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

public interface CheckRule extends Serializable {
	/**
	 * Checks if the rule r fulfills the terms stated
	 * 
	 * @param r
	 *            the rule to be checked
	 * @return true if the rule fulfills the terms stated, false otherwise
	 */
	public boolean checkRule(IRule r);
	


	/**
	 * Gets the conditions which the rule didn't comply with
	 * 
	 * @return a list of String, each one reports one term that the rule didn't
	 *         comply with
	 */
	public List<String> getCauses();

	/**
	 * Gets a String which represent all the conditions not complied by the rule
	 * 
	 * @return a String which represent all the conditions not complied by the
	 *         rule
	 */
	public String getCausesString();

}
