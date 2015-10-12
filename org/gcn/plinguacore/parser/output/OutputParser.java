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

package org.gcn.plinguacore.parser.output;

import java.io.OutputStream;
import java.io.Writer;

import org.gcn.plinguacore.parser.IParser;
import org.gcn.plinguacore.util.psystem.Psystem;


/**
 * An abstract class for an output parser
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public abstract class OutputParser implements IParser {

	/**
	 * Parses the P system to an OutputStream
	 * 
	 * @param psystem
	 *            a P-System to be parsed
	 * @param stream
	 *            an OutputStream
	 * @return True if it has success
	 */

	public abstract boolean parse(Psystem psystem, OutputStream stream);

	/**
	 * Parses the P system to an Writer instance
	 * 
	 * @param psystem
	 *            a P-System to be parsed
	 * @param stream
	 *            a Writer
	 * @return True if it has success
	 */
	public abstract boolean parse(Psystem psystem, Writer stream);
}
