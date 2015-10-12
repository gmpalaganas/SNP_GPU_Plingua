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

package org.gcn.plinguacore.util.psystem.membrane;

import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.Label;

/**
 * This class provides methods for modifiable membranes
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public abstract class ChangeableMembrane extends Membrane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8308332619574555993L;

	
	
	public ChangeableMembrane(Label label, byte charge,
			MultiSet<String> multiSet) {
		super(label, charge, multiSet);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates a new ChangeableMembrane instance
	 * 
	 * @param label
	 *            the label of the ChangeableMembrane instance to create
	 * @param charge
	 *            the charge of the ChangeableMembrane instance to create
	 */
	public ChangeableMembrane(Label label, byte charge) {
		super(label, charge);
	}

	/**
	 * Creates a new ChangeableMembrane instance which charge is 0
	 * 
	 * @param label
	 *            the label of the ChangeableMembrane instance to create
	 */
	public ChangeableMembrane(Label label) {
		super(label);
	}

	/**
	 * Gets the membrane multiset
	 * 
	 * @return the ChangeableMembrane instance multiset
	 */
	@Override
	public MultiSet<String> getMultiSet() {

		return multiSet;
	}

	/**
	 * Dissolves the membrane
	 * 
	 * @throws UnsupportedOperationException
	 *             if dissolution is no supported
	 */
	public abstract void dissolve() throws UnsupportedOperationException;

	/**
	 * Divides the membrane and returns the newly obtained membrane
	 * 
	 * @return the ChangeableMembrane instance resulting out of the division
	 * @throws UnsupportedOperationException
	 *             if division is no supported
	 */
	public abstract ChangeableMembrane divide()
			throws UnsupportedOperationException;

	/**
	 * Sets the membrane charge
	 * 
	 * @param charge
	 *            the charge to be set
	 */
	public final synchronized void setCharge(byte charge) {
		this.charge = charge;
	}

	protected final void setId(int id)
	{
		this.id=id;
	}

	private int id = 0;

	/**
	 * Gets the ID of the membrane, a unique identifier for it
	 * 
	 * @return The automatically-generated ID for the membrane, the skin
	 *         membrane has ID 0
	 */
	public final int getId() {
		return id;
	}
}
