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

package org.gcn.plinguacore.parser.input.byteCounter;

/**
 * This interface provides a method to count the number of bytes from the
 * beginning of a file
 * 
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public interface IByteCounter {

	/**
	 * This method counts the number of bytes from the beginning of a file at
	 * position [line,column]
	 * 
	 * @param line
	 *            The initial line
	 * @param column
	 *            The initial column
	 * @return The number of bytes from the beginning of the file to position
	 *         [line,column] or 0 if the position [line,column] is invalid
	 */
	public int countBytes(int line, int column);

}
