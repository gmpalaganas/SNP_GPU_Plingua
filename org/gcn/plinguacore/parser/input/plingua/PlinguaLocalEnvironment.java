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


package org.gcn.plinguacore.parser.input.plingua;

class PlinguaLocalEnvironment extends PlinguaEnvironment {

	private PlinguaGlobalEnvironment globalEnvironment;

	public PlinguaLocalEnvironment(PlinguaGlobalEnvironment globalEnvironment) {
		if (globalEnvironment == null)
			throw new NullPointerException(
					"globalEnvironment constructor argument shouldn't be null");
		this.globalEnvironment = globalEnvironment;
	}

	@Override
	protected Number getNumericVariable(String var, Token tokenInit,
			Token tokenEnd) throws ParseException {

		try {
			return super.getNumericVariable(var, tokenInit, tokenEnd);
		} catch (ParseException ex) {
			return globalEnvironment.getNumericVariable(var, tokenInit,
					tokenEnd);
		}
	}

	@Override
	protected String getStringVariable(String var, Token tokenInit,
			Token tokenEnd) throws ParseException {
		try {
			return super.getStringVariable(var, tokenInit, tokenEnd);
		} catch (ParseException ex) {
			return globalEnvironment
					.getStringVariable(var, tokenInit, tokenEnd);
		}
	}

}
