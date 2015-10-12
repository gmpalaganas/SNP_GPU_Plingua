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

import java.io.IOException;
import java.io.StringReader;

/**
 * 
 * This class provides a wrapper for StringReader which implements IByteCounter
 * 
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class StringReaderWrapper extends StringReader implements IByteCounter {

	private ByteCounter byteCounter;

	/**
	 * @param reader
	 *            The wrapped StringReader
	 */
	public StringReaderWrapper(StringReader reader) {
		super(getString(reader));
		byteCounter = new ByteCounter();
	}

	@Override
	public int countBytes(int line, int column) {
		// TODO Auto-generated method stub
		return byteCounter.countBytes(line, column);
	}

	@Override
	public int read(char[] arg0, int arg1, int arg2) throws IOException {
		// TODO Auto-generated method stub

		int r = super.read(arg0, arg1, arg2);
		byteCounter.count(arg0, r);
		return r;
	}

	@Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		int ch = super.read();
		byteCounter.count(ch);
		return ch;
	}

	private static String getString(StringReader reader) {
		int ch;
		String str = "";
		do {

			try {
				ch = (char) reader.read();
				if (ch != -1)
					str += (char) ch;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ch = -1;
			}

		} while (ch != -1);
		return str;
	}

}
