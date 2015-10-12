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
import java.util.List;
import java.util.Set;


public class UnionSet<E> implements Set<E> {

	
	private Set<E> sA,sB;
	
	public UnionSet(Set<E> sA, Set<E> sB)
	{
		if (sA==null || sB==null)
			throw new NullPointerException();
		
				
		this.sA=sA;
		this.sB=sB;
	}
	

	@Override
	public boolean add(E arg0) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		sA.clear();
		sB.clear();
		
	}

	@Override
	public boolean contains(Object arg0) {
		// TODO Auto-generated method stub
		return sA.contains(arg0) || sB.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		boolean b=true;
		Iterator<?>it = arg0.iterator();
		while(it.hasNext() && b)
		{
			Object obj = it.next();
			b = contains(obj);
		}
		return b;
		
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return sA.isEmpty() && sB.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return new UnionIterator<E>(sA,sB);
	}

	@Override
	public boolean remove(Object arg0) {
		// TODO Auto-generated method stub
		boolean b1= sA.remove(arg0);
		boolean b2= sB.remove(arg0);
		return b1||b2;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		boolean b1= sA.removeAll(arg0);
		boolean b2= sB.removeAll(arg0);
		
		return b1 || b2;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		boolean b1= sA.retainAll(arg0);
		boolean b2= sB.retainAll(arg0);
		
		return b1 || b2;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return sA.size()+sB.size();
	}
	


	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		List<E> l1 = new ArrayList<E>(sA);
		List<E> l2 = new ArrayList<E>(sB);
		l1.addAll(l2);
		return l1.toArray();
		
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		// TODO Auto-generated method stub
		List<E> l1 = new ArrayList<E>(sA);
		List<E> l2 = new ArrayList<E>(sB);
		l1.addAll(l2);
		return l1.toArray(arg0);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sA == null) ? 0 : sA.hashCode());
		result = prime * result + ((sB == null) ? 0 : sB.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UnionSet other = (UnionSet) obj;
		if (sA == null) {
			if (other.sA != null)
				return false;
		} else if (!sA.equals(other.sA))
			return false;
		if (sB == null) {
			if (other.sB != null)
				return false;
		} else if (!sB.equals(other.sB))
			return false;
		return true;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str="[";
		Iterator<E>it = iterator();
		while(it.hasNext())
		{
			str+=it.next().toString();
			if (it.hasNext())
				str+=", ";
		}
		str+="]";
		return str;
	}
	
	
	
	
	
	
	
}
