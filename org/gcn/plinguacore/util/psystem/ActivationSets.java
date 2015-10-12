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

package org.gcn.plinguacore.util.psystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcn.plinguacore.util.Pair;

public class ActivationSets {
	
	//private Map<Pair<Integer,Byte>,List<String>>activationSets;
	private Map<Pair<Integer,Byte>,List<Pair<String,Long>>>activationSets;
	
	public ActivationSets()
	{
		//activationSets= new HashMap<Pair<Integer,Byte>,List<String>>();
		activationSets= new HashMap<Pair<Integer,Byte>,List<Pair<String,Long>>>();
	}
	
	//public void add(int id,int charge,Set<String> set)
	public void add(int id,int charge,Set<Pair<String,Long>> set)
	{
		//List<String>l = new ArrayList<String>(set);
		List<Pair<String,Long>>l = new ArrayList<Pair<String,Long>>(set);
		Collections.sort(l);
		activationSets.put(new Pair<Integer,Byte>(id,(byte)charge),l);
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activationSets == null) ? 0 : activationSets.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivationSets other = (ActivationSets) obj;
		if (activationSets == null) {
			if (other.activationSets != null)
				return false;
		} else if (!activationSets.equals(other.activationSets))
			return false;
		return true;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return activationSets.toString();
	}
	
	

}
