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

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides an implementation of IByteCounter
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 *
 */
class ByteCounter implements IByteCounter{
	
	private List<Integer> bytesByLine;
	private int count=0;
	
	
	public ByteCounter()
	{
		bytesByLine = new ArrayList<Integer>();
		bytesByLine.add(0);
	}
	
	/* (non-Javadoc)
	 * @see org.gcn.plinguacore.parser.input.byteCounter.IByteCounter#countBytes(int, int)
	 */
	@Override
	public
	final int countBytes(int line,int column)
	{
		
		if (bytesByLine==null)
			return 0;
	
		if (line<1 || line>=bytesByLine.size())
			return 0;
	
		if (column<1)
			return 0;
	
		int offset = bytesByLine.get(line-1);
		return offset+column;
		
	}
	final public void count(char b[],int total)
	{
		for (int i=0;i<total;i++)
			count(b[i]);
	}
	
	final public void count(byte b[],int total)
	{
		for (int i=0;i<total;i++)
			count(b[i]);
	}
	
	final public void count(int ch)
	{
		count++;
		if (ch=='\n')
			bytesByLine.add(count);
	}

}
