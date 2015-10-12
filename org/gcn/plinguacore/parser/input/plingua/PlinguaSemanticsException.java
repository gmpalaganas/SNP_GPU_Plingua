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

import org.gcn.plinguacore.parser.input.messages.InputParserMsg;
/**
 * This exception is thrown when semantics errors are encountered.  
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 */
public class PlinguaSemanticsException extends ParseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4750534933505868248L;
	private InputParserMsg error;

	public PlinguaSemanticsException(InputParserMsg error) {
		super();
		if (error == null)
			throw new NullPointerException(
					"error constructor argument shouldn't be null");
		this.error = error;
		// TODO Auto-generated constructor stub
	}

	public InputParserMsg getError() {
		return error;
	}

	@Override
	public String getMessage() {

		return error.toString();
	}

}
