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

import java.io.Serializable;



import org.gcn.plinguacore.util.MultiSet;

import org.gcn.plinguacore.util.psystem.membrane.MembraneStructure;



/**
 * An abstract class for a Configuration
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public abstract class Configuration implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6642873032924795615L;

	private MembraneStructure membraneStructure;

	

	private int number = 0;

	private Psystem psystem;
	/**
	 * @param psystem
	 *            A P System
	 */
	protected Configuration(Psystem psystem) {
		if (psystem == null)
			throw new NullPointerException("Null P system");
		this.psystem = psystem;
		setMembraneStructure((MembraneStructure)psystem.getMembraneStructure().clone());
		
		
	}
	
	
	
	/**
	 * @return the P System for this configuration
	 */
	public Psystem getPsystem() {
		return psystem;
	}

	/**
	 * @return the number of configuration, the initial one is 0
	 */
	public int getNumber() {
		return number;
	}

	
	public void setNumber(int number) {
		this.number = number;
	}

	


	protected abstract Configuration getNewConfiguration();

	/**
	 * @see org.gcn.plinguacore.util.psystem.Configuration#clone()
	 */
	@Override
	public Object clone() 
	{
		Configuration c = getNewConfiguration();
		if (getMembraneStructure()!=null)
			c.setMembraneStructure((MembraneStructure)getMembraneStructure().clone());
		c.setNumber(getNumber()+1);
		return c;
	}

	@Override
	public String toString() {
		
		return membraneStructure.toString();
	}



	public MembraneStructure getMembraneStructure() {
		return membraneStructure;
	}



	private void setMembraneStructure(MembraneStructure membraneStructure) {
		this.membraneStructure = membraneStructure;
	}
	/**
	 * @return A Multiset<String> for the environment
	 */
	public abstract MultiSet<String>getEnvironment();

}
