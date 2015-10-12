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
 * This class provides the basic functionality for all kinds of messages, such
 * as information messages, warning messages and so on.
 * 
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public abstract class InputParserAbstractMsg implements InputParserMsg {

	private String message;

	/**
	 * Creates a new InputParserAbstractMsg instance which contains a string
	 * representing a message
	 * 
	 * @param message
	 *            the message to contain
	 */
	public InputParserAbstractMsg(String message) {
		if (message == null)
			throw new NullPointerException(
					"Message constructor argument shouldn't be null");
		this.message = message;
	}

	/**
	 * @see org.gcn.plinguacore.parser.input.messages.InputParserMsg#getMessage()
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getType() + ": " + getMessage();

	}

	/**
	 * @see org.gcn.plinguacore.parser.input.messages.InputParserMsg#getExtendedMsg()
	 */
	public String getExtendedMsg() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.gcn.plinguacore.parser.input.messages.InputParserMsg#getInterval()
	 */
	public MsgInterval getInterval() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.gcn.plinguacore.parser.input.messages.InputParserMsg#hasExtendedMsg()
	 */
	public boolean hasExtendedMsg() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see org.gcn.plinguacore.parser.input.messages.InputParserMsg#hasInterval()
	 */
	public boolean hasInterval() {
		// TODO Auto-generated method stub
		return false;
	}

}
