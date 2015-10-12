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

/**
 * This class holds integer constants (flags) related to verbosity levels
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public abstract class VerbosityConstants {
	/**
	 * The verbosity level for simulators which are expected to report no messages
	 */
	public final static int NO_MESSAGES = 0;
	/**
	 * The verbosity level for simulators which are expected to report only error messages
	 */
	public final static int ERROR = 1;
	/**
	 * The verbosity level for simulators which are expected to report error and warning messages
	 */
	public final static int WARNINGS = 2;
	/**
	 * The verbosity level for simulators which are expected to report error, warning and basic info messages no messages
	 */
	public final static int GENERAL_INFO = 3;
	/**
	 * The verbosity level for simulators which are expected to report error, warning and basic and detailed info messages no messages
	 */
	public final static int DETAILED_INFO = 4;
	/**
	 * The highest level of verbosity
	 */
	public final static int MAXIMUM_VERBOSITY = 5;

}
