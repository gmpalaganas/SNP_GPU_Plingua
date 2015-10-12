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
package org.gcn.plinguacore.parser.output.binary;


import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteOrder;

import org.gcn.plinguacore.parser.output.OutputParser;

import org.gcn.plinguacore.util.psystem.Psystem;

import org.gcn.plinguacore.util.ByteOrderDataOutputStream;
abstract class AbstractBinaryOutputParser extends OutputParser {

		
	private ByteOrderDataOutputStream stream = null;
	private Psystem psystem = null;

	
	public AbstractBinaryOutputParser()
	{
		
	}
	
	protected final void writeHeader(byte id) throws IOException {
		stream.writeByte(0xAF);
		stream.writeByte(0x12);
		stream.writeByte(0xFA);
		stream.writeByte(id);
	}
	
	
	

	protected final ByteOrderDataOutputStream getStream() {
		return stream;
	}

	private void setStream(ByteOrderDataOutputStream stream) {
		this.stream = stream;
	}

	protected final Psystem getPsystem() {
		return psystem;
	}

	private void setPsystem(Psystem psystem) {
		this.psystem = psystem;
	}

	@Override
	public final boolean parse(Psystem psystem, Writer stream) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
	
	
	
	@Override
	public final boolean parse(Psystem psystem, OutputStream stream) {
		// TODO Auto-generated method stub
	try {
			
			setPsystem(psystem);
			setStream(new ByteOrderDataOutputStream(stream,getByteOrder()));
			writeHeader(getFileId());
			writeFile();
			getStream().close();

		} catch (IOException e) {

			return false;
		}
		return true;

	}

	protected abstract void writeFile() throws IOException;
	protected abstract ByteOrder getByteOrder();
	protected abstract byte getFileId();


}
