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

package org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule;

import org.gcn.plinguacore.util.psystem.rule.IConstantRule;
import org.gcn.plinguacore.util.psystem.rule.IRule;

import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;

/* Comprueba si la constante de la regla esta entre 0 y 1 (es un ratio) */
/**
 * This class tests if the rule holds a float constant which is between 0 and 1
 * (the constant represents a probability)
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class Ratio extends Constant {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2198306913397498983L;

	
	/**
	 * Creates a new Ratio instance, which checks only the restrictions defined on itself.
	 */
	public Ratio(){
		super();
	}
	
	/**
	 * Creates a new Ratio instance, which wraps cr as stated
	 * by Decorator pattern. Thus, it will be capable to test both the instance restrictions and cr restrictions 
	 * 
	 * @param cr
	 *            the CheckRule instance to be wrapped
	 */
	public Ratio(CheckRule cr) {
		super(cr);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean checkSpecificPart(IRule r) {
		// TODO Auto-generated method stub
		return super.checkSpecificPart(r)
				&& (r instanceof IConstantRule)
				&& ((IConstantRule) r).getConstant() >= 0.0
				&& ((IConstantRule) r).getConstant() <= 1.0;
	}

	@Override
	protected String getSpecificCause() {
		// TODO Auto-generated method stub
		return "Ratio should be between 0.0 and 1.0 (both included)";
	}

}
