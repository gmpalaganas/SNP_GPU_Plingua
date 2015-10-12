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

package org.gcn.plinguacore.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class UnionIterator<E> implements Iterator<E> {

	
	private Iterator<? extends E>it=null;
	private Iterator<? extends Collection<? extends E>>itCollections;
		
	public UnionIterator(Collection<? extends Collection<? extends E>>collections)
	{
		if (collections==null)
			throw new NullPointerException();
		itCollections=collections.iterator();
	}
	
	public UnionIterator(Collection<? extends E>cA,Collection<? extends E>cB)
	{
		List<Collection<? extends E>>l= new ArrayList<Collection<? extends E>>();
		if (cA!=null)
			l.add(cA);
		if (cB!=null)
			l.add(cB);
		itCollections=l.iterator();
	}
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		if (it==null)
		{
			if (!itCollections.hasNext())
				return false;
			it = itCollections.next().iterator();
		}
		while(!it.hasNext() && itCollections.hasNext())
			it = itCollections.next().iterator();
		return it.hasNext();
	}

	@Override
	public E next() {
		// TODO Auto-generated method stub
		if (!hasNext())
			throw new NoSuchElementException();
		return it.next();
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
