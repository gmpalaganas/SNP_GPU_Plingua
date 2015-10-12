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


package org.gcn.plinguacore.parser.input.messages;


/**
 * This interface defines all functionality to manage messages resulting of parsing streams which specify P-systems
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 *
 */
public interface InputParserMsg {

	/**
	 * Gets the contained message
	 * @return the contained message 
	 */
	public String getMessage();

	/**
	 * Gets the type of the contained message 
	 * @return the type of the contained message 
	 */
	public String getType();


	/**
	 * Returns a boolean which represents if the message has a specific interval, it is, a defined piece of code which the messages refers to
	 * @return true if the message has a specific interval, false otherwise
	 */
	public boolean hasInterval();
	
	/**
	 * Gets the message interval
	 * @return the message interval
	 */
	public MsgInterval getInterval();

	/**
	 * Gets a boolean which represents if the message has an extended info which complements it
	 * @return true if the message has a extended info, false otherwise.
	 */
	public boolean hasExtendedMsg();

	/**
	 * Gets the extension of the message
	 * @return the extension of the message
	 */
	
	public String getExtendedMsg();

	/**
	 * Gets the verbosity level of the message
	 * @return the verbosity level of the message
	 */
	public int getVerbosityLevel();
	
	

}
