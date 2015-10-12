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

import java.io.Serializable;
import java.nio.BufferOverflowException;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A multiset that uses HashMap
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 * @param <E>
 *            The type of the elements of the multiset
 */
public class HashMultiSet<E> implements MultiSet<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4265738367398769859L;

	private EntrySet entrySet = null;

	public HashMultiSet() {
		super();

		entrySet = new EntrySet();
	}

	/**
	 * Creates a HashMultiSet based on the objects in the collection passed as
	 * argument
	 * 
	 * @param objects
	 *            An initial Collection for the multiset
	 */
	public HashMultiSet(Collection<? extends E> objects) {
		super();
		entrySet = new EntrySet();
		addAll(objects);

	}

	/**
	 * 
	 * @see org.gcn.plinguacore.util.MultiSet#add(java.lang.Object, int)
	 */
	public boolean add(E object, long multiplicity) {

		/*
		 * If the object to add is null or is intended to be added with a
		 * multiplicity less than 0, it results in a exception
		 */
		if (object == null)
			throw new NullPointerException();
		if (multiplicity < 0)
			throw new IllegalArgumentException("An object cannot have a negative multiplicity ("+multiplicity+")");
		if (multiplicity == 0)
			/* No object can be added with 0 multiplicity */
			return false;
		/* Otherwise, the global size and the object count is increased */
		synchronized (entrySet.map) {
			long currentCount = count(object) + multiplicity;
			long newSize = entrySet.size+multiplicity;
			if (currentCount<0 || newSize<0)
				throw new IllegalStateException("Multiset overflow: "+object+" * "+multiplicity+ "(" + count(object)+ ", " + entrySet.size + ") -> (" + currentCount + ", " + newSize + ")");
			entrySet.map.put(object, currentCount);
			entrySet.size = newSize;
		}
		return true;

	}

	/**
	 * 
	 * @see org.gcn.plinguacore.util.MultiSet#addAll(java.util.Collection, int)
	 */

	public boolean addAll(Collection<? extends E> objects, long multiplicity) {
		if (objects == null)
			throw new NullPointerException();
		if (multiplicity < 0)
			throw new IllegalArgumentException("An object cannot have a negative multiplicity ("+multiplicity+")");
		if (multiplicity == 0)
			return false;
		boolean r = false;
		Iterator<? extends E> it;
		if (objects instanceof MultiSet) {
			it = ((MultiSet<? extends E>) objects).entrySet().iterator();
			while (it.hasNext()) {
				E obj = it.next();
				long c = ((MultiSet<? extends E>) objects).count(obj) * multiplicity;
			
				if (c<0)
					throw new IllegalStateException("Multiset overflow "+c+", "+((MultiSet<? extends E>) objects).count(obj)+", "+", "+obj+", "+multiplicity);
				add(obj, c);
			}
			r = true;
		} else {
			it = objects.iterator();
			while (it.hasNext())
				add(it.next(), multiplicity);
			r = true;
		}

		return r;
	}

	/**
	 * 
	 * @see org.gcn.plinguacore.util.MultiSet#count(java.lang.Object)
	 */
	public long count(Object object) {

		if (object == null)
			throw new NullPointerException();
		if (!contains(object))
			return 0;
		else
			return entrySet.map.get(object).longValue();
	}

	/**
	 * 
	 * @see org.gcn.plinguacore.util.MultiSet#countSubSets(java.util.Collection)
	 */
	public long countSubSets(Collection<?> objects) {
		long min = Long.MAX_VALUE;
		MultiSet<?> multiSet;
		if (objects == null)
			throw new NullPointerException();

		if (objects instanceof MultiSet)
			multiSet = (MultiSet<?>) objects;
		else
			multiSet = new HashMultiSet<Object>(objects);
		Iterator<?> it = multiSet.entrySet().iterator();
		
		while (it.hasNext()) {
			Object o = it.next();
			long m1 = count(o);
			long m2 = multiSet.count(o);
			long c = m1 / m2;
			if (c < min) {
				min = c;
				if (min == 0)
					break;
			}
		}

		return min;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.MultiSet#entrySet()
	 */
	public Set<E> entrySet() {
		return entrySet;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(E arg0) {
		return add(arg0, 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends E> arg0) {

		return addAll(arg0, 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#clear()
	 */
	public void clear() {
		entrySet.clear();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public  boolean contains(Object arg0) {
		return entrySet.contains(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public  boolean containsAll(Collection<?> arg0) {

		return entrySet.containsAll(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty() {

		return entrySet.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#iterator()
	 */
	public Iterator<E> iterator() {
		return new EntryIterator(this);
	}

	public boolean remove(Object arg0, long multiplicity) {
		if (arg0 == null)
			throw new NullPointerException();
		if (multiplicity < 0)
			throw new IllegalArgumentException("An object cannot have a negative multiplicity ("+multiplicity+")");

		if (multiplicity==0)
			return false;
		
		long n = count(arg0);
		if (n < multiplicity)
			return false;

		if (n == multiplicity)
			entrySet.remove(arg0);
		else {
			synchronized (entrySet) {
				E obj = (E) arg0;
				entrySet.map.put(obj, n - multiplicity);
				entrySet.size -= multiplicity;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object arg0) {
		return remove(arg0,1);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> arg0) {
				
		return entrySet.removeAll(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> arg0) {
				
		return entrySet.retainAll(arg0);
	}

	
	public int size() {
		return (int)entrySet.size;
	}
	@Override
	public long longSize() {
		return entrySet.size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray() {
		Object[] objs = new Object[(int)size()];
		Iterator it = iterator();
		int i = 0;
		while (it.hasNext()) {
			objs[i] = it.next();
			i++;
		}
		return objs;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#toArray(T[])
	 */
	public <T> T[] toArray(T[] arg0) {

		T[] aux;
		if (arg0 == null)
			throw new NullPointerException();
		if (arg0.length < entrySet.size())
			aux = (T[]) new Object[entrySet.size()];
		else {
			
			aux = arg0;
			if (arg0.length > entrySet.size()) {
				aux[entrySet.size()] = null;
			}
		}

		Iterator it = iterator();
		int i = 0;
		while (it.hasNext()) {

			Object obj = it.next();
			if (aux[i].getClass().isAssignableFrom(obj.getClass()))
				aux[i] = (T) obj;
			else
				throw new ArrayStoreException();
			i++;
		}
		return aux;
	}

	public boolean subtraction(Collection<?> objects) {
		return subtraction(objects, 1);

	}

	@Override
	public boolean subtraction(Collection<?> objects,long multiplicity) {

		if (objects == null)
			throw new NullPointerException();

		if (multiplicity < 0)
		{
			throw new IllegalArgumentException("An object cannot have a negative multiplicity ("+multiplicity+")");
		}
		if (multiplicity==0)
			return false;
		
		long c = countSubSets(objects);
		if (c < multiplicity)
			return false;
		MultiSet<?> m;
		if (objects instanceof MultiSet<?>)
			m = (MultiSet<?>) objects;
		else
			m = new HashMultiSet<Object>(objects);

		Iterator<?> it = m.entrySet().iterator();
		synchronized (entrySet) {
			while (it.hasNext()) {
				Object obj = it.next();
				long n = count(obj);
				long n1 = m.count(obj) * multiplicity;
				if (n == n1)
					entrySet.map.remove(obj);
				else
					entrySet.map.put((E) obj, n - n1);
				entrySet.size -= n1;
			}
		}
		return true;

	}

	private class EntryIterator implements Iterator<E>, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private Iterator<E> iterator = null;

		private int count = 0;

		private E obj = null;

		private MultiSet<E> m;

		public EntryIterator(MultiSet<E> m) {
			this.iterator = m.entrySet().iterator();
			this.m = m;
		}

		public boolean hasNext() {
			if (iterator.hasNext())
				return true;
			else if (obj == null)
				return false;
			else {
				long max = m.count(obj);
				if (count < max)
					return true;
				else
					return false;
			}
		}

		public E next() {
			if (!hasNext())
				throw new NoSuchElementException();

			if (obj == null || count >= m.count(obj)) {
				count = 0;
				obj = iterator.next();
			}
			count++;
			return obj;
		}

		public void remove() {
			throw new UnsupportedOperationException();

		}

	}

	@Override
	public String toString() {

		if (isEmpty())
			return "#";
		String str = "";
		Iterator<E> it = entrySet.iterator();
		while (it.hasNext()) {
			E e = it.next();
			str += e.toString();
			long n = count(e);
			if (n > 1)
				str += "*" + n;
			if (it.hasNext())
				str += ", ";
		}
		return str;
	}

	private class EntrySet implements Set<E>, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private Map<E, Long> map = null;

		private long size = 0;

		public EntrySet() {

			map = new LinkedHashMap<E, Long>();
			
		}

		public boolean add(E arg0) {
			throw new UnsupportedOperationException();
		}

		public boolean addAll(Collection<? extends E> arg0) {
			throw new UnsupportedOperationException();
		}

		public synchronized void clear() {
			map.clear();
			size = 0;
		}

		public boolean contains(Object arg0) {
			return map.containsKey(arg0);
		}

		public boolean containsAll(Collection<?> arg0) {
			if (arg0 instanceof MultiSet<?>)
				return map.keySet().containsAll(((MultiSet<?>) arg0).entrySet());
			
			return map.keySet().containsAll(arg0);
		}

		public boolean isEmpty() {
			return map.isEmpty();
		}

		public Iterator<E> iterator() {
			return map.keySet().iterator();
		}

		public synchronized boolean remove(Object arg0) {
			if (map.containsKey(arg0))
				size -= map.get(arg0).longValue();
			return map.remove(arg0) != null;
		}

		public synchronized boolean removeAll(Collection<?> arg0) {
			Iterator<?> it;
			boolean r = false;
			if (arg0 instanceof MultiSet)
				it = ((MultiSet<?>) arg0).entrySet().iterator();
			else
				it = arg0.iterator();
			while (it.hasNext())
			{
				boolean b=remove(it.next());
				r = b||r;
			}
			return r;
		}

		public synchronized boolean retainAll(Collection<?> arg0) {
			boolean r = false;
						
			Iterator<E> it = map.keySet().iterator();
			while (it.hasNext()) {
				E obj = it.next();
				long c = map.get(obj).longValue();
				if (!arg0.contains(obj)) {
					it.remove();
					size -= c;
					r = true;
				}
			}
			return r;
		}

		public int size() {
			return map.size();
		}

		public Object[] toArray() {
			return map.keySet().toArray();
		}

		public <T> T[] toArray(T[] arg0) {
			return map.keySet().toArray(arg0);
		}

		@Override
		public String toString() {

			return map.keySet().toString();
		}

		@Override
		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result
					+ ((map == null) ? 0 : map.keySet().hashCode());
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
			final EntrySet other = (EntrySet) obj;
			if (map == null) {
				if (other.map != null)
					return false;
			} else if (!map.keySet().equals(other.map.keySet()))
				return false;
			return true;
		}

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MultiSet))
			return false;
		final MultiSet<?> other = (MultiSet<?>) obj;

		if (size() != other.size())
			return false;

		if (size() > 0
				&& (countSubSets(other) != 1 || other.countSubSets(this) != 1))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((entrySet == null) ? 0 : entrySet.map.hashCode());
		return result;
	}

}
