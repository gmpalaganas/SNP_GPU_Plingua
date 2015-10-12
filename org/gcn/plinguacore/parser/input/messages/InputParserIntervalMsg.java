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

class InputParserIntervalMsg extends InputParserDecoratorMsg implements
		InputParserMsg {

	private MsgInterval interval;

	public InputParserIntervalMsg(InputParserMsg decorated, int beginLine,
			int beginColumn, int endLine, int endColumn,int beginPosition,int endPosition,IByteCounter byteCounter) {

		this(decorated, new MsgInterval(beginLine, beginColumn, endLine,
				endColumn,byteCounter));

	}

	public InputParserIntervalMsg(InputParserMsg decorated, MsgInterval interval) {
		super(decorated);
		if (interval == null)
			throw new NullPointerException(
					"Interval constructor argument shouldn't be null");

		this.interval = interval;
		decoratedMsg = interval.toString();
	}

	@Override
	public MsgInterval getInterval() {
		return interval;
	}

	@Override
	public boolean hasInterval() {
		// TODO Auto-generated method stub
		return true;
	}

}
