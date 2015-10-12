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

package org.gcn.plinguacore.util.psystem.checkPsystem;




import org.gcn.plinguacore.util.check.DecoratorCheck;
import org.gcn.plinguacore.util.psystem.Psystem;

public abstract class DecoratorCheckPsystem extends DecoratorCheck<Psystem> implements
		CheckPsystem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3886333432897639782L;

	public DecoratorCheckPsystem() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DecoratorCheckPsystem(CheckPsystem cr) {
		super(cr);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPsystem(Psystem p) {
		// TODO Auto-generated method stub
		return super.check(p);
	}

	
	

}
