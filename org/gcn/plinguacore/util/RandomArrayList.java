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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public class RandomArrayList<E> extends ArrayList<E> implements Collection<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6028247731361474736L;

	private static Random rand=null;
	
	public RandomArrayList() {
		super();
		
		// TODO Auto-generated constructor stub
	}

	public RandomArrayList(Collection<? extends E> arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public RandomArrayList(int arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return new RandomIterator();
	}

	
	
	class RandomIterator implements Iterator<E>
	{
		private List<E> indexes;
		
		public RandomIterator()
		{
			indexes=new LinkedList<E>();
			if (rand==null)
				rand=new Random();
			for (int i=0;i<size();i++)
				indexes.add(i,get(i));
		}
		
		
		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return !indexes.isEmpty();
		}

		@Override
		public E next() {
			// TODO Auto-generated method stub
			if (!hasNext())
				throw new NoSuchElementException();
			
			int id = rand.nextInt(indexes.size());
			E obj = indexes.get(id);
			indexes.remove(id);
			return obj;
			
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}
		
	}

}
