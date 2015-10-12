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

import java.util.Comparator;



public class RuleComparator implements Comparator<IRule> {

	@Override
	public int compare(IRule arg0, IRule arg1) {
		// TODO Auto-generated method stub
		boolean b1 = arg0 instanceof IPriorityRule;
		boolean b2 = arg1 instanceof IPriorityRule;
		if (b1 && b2)
		{
			int p1 = ((IPriorityRule)arg0).getPriority();
			int p2 = ((IPriorityRule)arg1).getPriority();
			return p1-p2;
			
		}
		if (b1 && !b2)
			return -1;
		if (!b1 && b2)
			return 1;
		return 0;
	}

}
