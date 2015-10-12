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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;



public class HashInfiniteMultiSet<E>  extends HashMultiSet<E> implements InfiniteMultiSet<E>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6023263700543581160L;

	private Set<E> infiniteObjects;
	
	public HashInfiniteMultiSet() {
		super();
		infiniteObjects = new HashSet<E>();
	}

	
	public HashInfiniteMultiSet(Collection<? extends E> objects) {
		super();
		infiniteObjects=new HashSet<E>();
		addAll(objects);

	}
	

	@Override
	public boolean add(E object, long multiplicity) {
		// TODO Auto-generated method stub
		
		if (object == null)
			throw new NullPointerException();
		
		if (multiplicity < 0)
			throw new IllegalArgumentException("An object cannot have a negative multiplicity ("+multiplicity+")");
		
		if (multiplicity == 0)
			return false;
		
		if (infiniteObjects.contains(object))
			return true;
		
		return super.add(object,multiplicity);
		
	}

	@Override
	public boolean addAll(Collection<? extends E> objects, long multiplicity) {
		
		if (objects == null)
			throw new NullPointerException();
		
		if (multiplicity < 0)
			throw new IllegalArgumentException("An object cannot have a negative multiplicity ("+multiplicity+")");
		
		if (multiplicity == 0)
			return false;
		
		if (objects instanceof InfiniteMultiSet)
		{
			InfiniteMultiSet<? extends E>ims = (InfiniteMultiSet<? extends E>)objects;
			boolean b=	infiniteObjects.addAll(ims.objectsWithInfiniteMultiplicity());
			Iterator <? extends E> it= ims.entrySet().iterator();
			while (it.hasNext())
			{
				E obj = it.next();
				if (!infiniteObjects.contains(obj))
				{
					long c = ims.count(obj) * multiplicity;
					if (c<0)
						throw new IllegalStateException("Multiset overflow");
					b = super.add(obj,c)||b;
				}
			}
			return b;
		}
		else
			return super.addAll(objects,multiplicity);
		
	}

	@Override
	public long count(Object object) {
		// TODO Auto-generated method stub
		if (infiniteObjects.contains(object))
			return Long.MAX_VALUE;
		return super.count(object);
	}

	@Override
	public long countSubSets(Collection<?> objects) {
		// TODO Auto-generated method stub
		InfiniteMultiSet<?>aux = new HashInfiniteMultiSet<Object>(objects);
		aux.removeAll(infiniteObjects);
		if (!aux.objectsWithInfiniteMultiplicity().isEmpty())
			return 0;
		return super.countSubSets(aux);
	}

	@Override
	public Set<E> entrySet() {
		// TODO Auto-generated method stub
		return new UnionSet<E>(super.entrySet(),infiniteObjects);
	}

	@Override
	public long longSize() {
		// TODO Auto-generated method stub
		if (!infiniteObjects.isEmpty())
			return Long.MAX_VALUE;
		return super.longSize();
	}

	@Override
	public boolean remove(Object object, long multiplicity) {
		// TODO Auto-generated method stub
		if (infiniteObjects.contains(object))
			return false;
		return super.remove(object,multiplicity);
	}

	@Override
	public boolean subtraction(Collection<?> objects) {
		// TODO Auto-generated method stub
		return subtraction(objects,1);
	}

	@Override
	public boolean subtraction(Collection<?> objects, long multiplicity) {
		// TODO Auto-generated method stub
		InfiniteMultiSet<?>aux = new HashInfiniteMultiSet<Object>(objects);
		aux.removeAll(infiniteObjects);
		if (!aux.objectsWithInfiniteMultiplicity().isEmpty())
			return false;
		return super.subtraction(aux,multiplicity);
	}

	@Override
	public boolean add(E e) {
		// TODO Auto-generated method stub
		return add(e,1);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		// TODO Auto-generated method stub
		return addAll(c,1);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		infiniteObjects.clear();
		super.clear();
		
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return infiniteObjects.contains(o) || super.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		Iterator<?>it;
		if (c instanceof MultiSet<?>)
			it = ((MultiSet<?>)c).entrySet().iterator();
		else
			it = c.iterator();
		boolean b=true;
		while(it.hasNext() && b)
			b= contains(it.next());
		return b;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return infiniteObjects.isEmpty() && super.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		if (!infiniteObjects.isEmpty())
			throw new IllegalStateException("Multiset overflow");
		return super.iterator();
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return remove(o,1);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		Collection<?>aux;
		if (c instanceof MultiSet<?>)
			aux = (MultiSet<?>)c;
		else
			aux=c;
		
		boolean b1= infiniteObjects.removeAll(aux);
		boolean b2= super.removeAll(c);
		return b1||b2;
		
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		Collection<?>aux;
		if (c instanceof MultiSet<?>)
			aux = (MultiSet<?>)c;
		else
			aux=c;
		
		boolean b1= infiniteObjects.retainAll(aux);
		boolean b2= super.retainAll(c);
		return b1||b2;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		if (!infiniteObjects.isEmpty())
			return Integer.MAX_VALUE;
		return super.size();
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		if (!infiniteObjects.isEmpty())
			throw new IllegalStateException("Multiset overflow");
		return super.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		if (!infiniteObjects.isEmpty())
			throw new IllegalStateException("Multiset overflow");
		return super.toArray(a);
	}


	@Override
	public boolean addObjectWithInfiniteMultiplicity(E obj) {
		// TODO Auto-generated method stub
		return infiniteObjects.add(obj);
	}


	@Override
	public Set<E> objectsWithInfiniteMultiplicity() {
		// TODO Auto-generated method stub
		return new UnionSet<E>(infiniteObjects,new HashSet<E>());
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((infiniteObjects == null) ? 0 : infiniteObjects.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		HashInfiniteMultiSet other = (HashInfiniteMultiSet) obj;
		if (infiniteObjects == null) {
			if (other.infiniteObjects != null)
				return false;
		} else if (!infiniteObjects.equals(other.infiniteObjects))
			return false;
		return true;
	}


	


	@Override
	public String toString() {

		if (isEmpty())
			return "#";
		String str = "";
		Iterator<E> it = entrySet().iterator();
		while (it.hasNext()) {
			E e = it.next();
			str += e.toString();
			if (infiniteObjects.contains(e))
			{
				str+="*Inf";
			}
			else
			{
				long n = count(e);
				if (n > 1)
					str += "*" + n;
			}
			if (it.hasNext())
				str += ", ";
		}
		return str;
	}


	@Override
	public boolean setAllObjectsWithInfiniteMultiplicity() {
		// TODO Auto-generated method stub
		if (super.isEmpty())
			return false;
		
		infiniteObjects.addAll(super.entrySet());
		super.clear();
		
		return true;
	}

	

	
	



}
