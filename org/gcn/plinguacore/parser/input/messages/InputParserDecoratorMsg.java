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
 * This class provides the basic functionality for all InputParserDecoratorMsg instances which
 * wrap another instance, as specified by Decorator pattern
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

public abstract class InputParserDecoratorMsg implements InputParserMsg {

	protected InputParserMsg decorated;
	protected String decoratedMsg;

	/**
	 * Creates a new InputParserDecoratorMsg instance, which wraps decorated as stated
	 * by Decorator pattern. Thus, it will be capable to test both the instance restrictions and decorated restrictions 
	 * 
	 * @param decorated
	 *            the InputParserMsg instance to be wrapped
	 */
	public InputParserDecoratorMsg(InputParserMsg decorated) {
		if (decorated == null)
			throw new NullPointerException(
					"Decorated constructor argument shouldn't be null");

		this.decorated = decorated;
	}

	
	/**
	 * @see org.gcn.plinguacore.parser.input.messages.InputParserMsg#getMessage()
	 */
	@Override
	public String getMessage() {
		return decorated.getMessage();
	}

	/**
	 * @see org.gcn.plinguacore.parser.input.messages.InputParserMsg#getType()
	 */
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return decorated.getType();
	}

	
	/**
	 * @see org.gcn.plinguacore.parser.input.messages.InputParserMsg#getExtendedMsg()
	 */
	@Override
	public String getExtendedMsg() {
		return decorated.getExtendedMsg();
	}

	/**
	 * @see org.gcn.plinguacore.parser.input.messages.InputParserMsg#getInterval()
	 */
	@Override
	public MsgInterval getInterval() {
		return decorated.getInterval();
	}

	/**
	 * @see org.gcn.plinguacore.parser.input.messages.InputParserMsg#hasExtendedMsg()
	 */
	@Override
	public boolean hasExtendedMsg() {
		return decorated.hasExtendedMsg();
	}

	/**
	 * @see org.gcn.plinguacore.parser.input.messages.InputParserMsg#hasInterval()
	 */
	@Override
	public boolean hasInterval() {
		return decorated.hasInterval();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return decorated.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return decorated.equals(obj);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return decorated.toString() + " " + decoratedMsg;
	}

	/**
	 * @see org.gcn.plinguacore.parser.input.messages.InputParserMsg#getVerbosityLevel()
	 */
	public int getVerbosityLevel() {
		// TODO Auto-generated method stub
		return decorated.getVerbosityLevel();
	}

}
