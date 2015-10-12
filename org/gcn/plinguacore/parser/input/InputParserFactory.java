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

package org.gcn.plinguacore.parser.input;

import org.gcn.plinguacore.parser.AbstractParserFactory;
import org.gcn.plinguacore.util.PlinguaCoreException;


/**
 * This class creates InputParser instances based on SimpleFactory idiom
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class InputParserFactory extends AbstractParserFactory {

	/**
	 * @see org.gcn.plinguacore.parser.AbstractParserFactory#getClassName(java.lang.String)
	 */
	@Override
	protected String getClassName(String formatName)
			throws PlinguaCoreException {
		
		return getParserClassProvider().getInputFormatClassName(formatName);
	}

	/**
	 * Creates a new InputParserFactory instance
	 */
	public InputParserFactory() {
		super();
		// TODO Auto-generated constructor stub
	}

}
