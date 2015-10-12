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
import java.io.InputStream;

/**
 * 
 * This class provides a wrapper for InputStream which implements IByteCounter
 * 
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class InputStreamWrapper extends InputStream implements IByteCounter {

	private InputStream stream;
	private ByteCounter byteCounter;

	/**
	 * @param stream
	 *            The wrapped InputStram
	 */
	public InputStreamWrapper(InputStream stream) {
		if (stream == null)
			throw new NullPointerException();
		this.stream = stream;
		byteCounter = new ByteCounter();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		// TODO Auto-generated method stub
		return stream.available();
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		stream.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		// TODO Auto-generated method stub
		stream.mark(readlimit);
	}

	@Override
	public boolean markSupported() {
		// TODO Auto-generated method stub
		return stream.markSupported();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		// TODO Auto-generated method stub

		int r = stream.read(b, off, len);
		byteCounter.count(b, r);
		return r;
	}

	@Override
	public int read(byte[] b) throws IOException {
		// TODO Auto-generated method stub

		int r = stream.read(b);
		byteCounter.count(b, r);
		return r;
	}

	@Override
	public synchronized void reset() throws IOException {
		// TODO Auto-generated method stub
		stream.reset();
	}

	@Override
	public long skip(long n) throws IOException {
		// TODO Auto-generated method stub

		return stream.skip(n);
	}

	@Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		int ch = stream.read();
		byteCounter.count(ch);
		return ch;
	}

	@Override
	public int countBytes(int line, int column) {
		// TODO Auto-generated method stub
		return byteCounter.countBytes(line, column);
	}

}
