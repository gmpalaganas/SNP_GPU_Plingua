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

package org.gcn.plinguacore.util.psystem.cellLike;



import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.Psystem;




/**
 * This class represents configurations in cell-like P-systems
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class CellLikeConfiguration extends Configuration{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5324665255482026462L;
	
	private MultiSet<String> environment;
	
	/**
	 * Creates a new cell-like configuration for a p-system passed as argument
	 * 
	 * @param psystem
	 *            the p-system which this instance represents a configuration to
	 */
	protected CellLikeConfiguration(Psystem psystem) {
		super(psystem);
		environment=new HashMultiSet<String>();
	}

	@Override
	protected Configuration getNewConfiguration() {
		// TODO Auto-generated method stub
		return new CellLikeConfiguration(getPsystem());
	}

	@Override
	public MultiSet<String> getEnvironment() {
		return environment;
	}

	
	
	

	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		CellLikeConfiguration c = (CellLikeConfiguration) super.clone();
		c.getEnvironment().addAll(getEnvironment());
		return c;
	}

	

	


}
