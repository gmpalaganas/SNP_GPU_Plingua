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

import org.gcn.plinguacore.parser.input.byteCounter.IByteCounter;

/**
 * A class for representing messages interval, defined by both end and begin
 * column and line
 * 
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class MsgInterval {

	private int beginLine;
	private int beginColumn;
	private int endLine;
	private int endColumn;
	private int beginByte = 0;
	private int endByte = 0;

	/**
	 * Creates a new MsgInterval instance
	 * 
	 * @param beginLine
	 *            the line at which the message begins
	 * @param beginColumn
	 *            the column at which the message begins
	 * @param endLine
	 *            the line at which the message ends
	 * @param endColumn
	 *            the column at which the message ends
	 * @param IByteCounter
	 *            an IByteCounter to get the initial byte at which the message
	 *            begins and the final byte at which the message ends
	 */
	public MsgInterval(int beginLine, int beginColumn, int endLine,
			int endColumn, IByteCounter byteCounter) {
		super();
		this.beginLine = beginLine;
		this.beginColumn = beginColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
		if (byteCounter != null) {
			beginByte = byteCounter.countBytes(beginLine, beginColumn) - 1;
			endByte = byteCounter.countBytes(endLine, endColumn);
		}
	}

	/**
	 * @return the initial byte at which the message begins
	 */
	public int getBeginByte() {
		return beginByte;
	}

	/**
	 * @return  the final byte at which the message begins
	 */
	public int getEndByte() {
		return endByte;
	}

	/**
	 * Gets the column at which the message begins
	 * 
	 * @return the column at which the message begins
	 */
	public int getBeginColumn() {
		return beginColumn;
	}

	/**
	 * Gets the line at which the message begins
	 * 
	 * @return the line at which the message begins
	 */
	public int getBeginLine() {
		return beginLine;
	}

	/**
	 * Gets the column at which the message ends
	 * 
	 * @return the column at which the message ends
	 */
	public int getEndColumn() {
		return endColumn;
	}

	/**
	 * Gets the line at which the message ends
	 * 
	 * @return the line at which the message ends
	 */
	public int getEndLine() {
		return endLine;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + beginColumn;
		result = PRIME * result + beginLine;
		result = PRIME * result + endColumn;
		result = PRIME * result + endLine;
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final MsgInterval other = (MsgInterval) obj;
		if (beginColumn != other.beginColumn)
			return false;
		if (beginLine != other.beginLine)
			return false;
		if (endColumn != other.endColumn)
			return false;
		if (endLine != other.endLine)
			return false;

		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub

		String str = "at line " + beginLine;

		if (beginColumn > 0)
			str += " : " + beginColumn;

		if (endColumn > 0 && (beginColumn != endColumn || beginLine != endLine)) {
			str += "--";
			if (endLine != beginLine)
				str += endLine + " : ";
			str += endColumn;
		}
		/*
		 * if (beginByte!=0 || endByte!=0) str+=" ["+beginByte+"--"+endByte+"]";
		 */

		return str;
	}

}
