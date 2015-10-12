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
import java.util.Iterator;
import java.util.Set;
import java.util.Collections;


/**
 * 
 * An inmutable multiset. All methods that attempts to change the inner state throws NotSupportedException
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 *
 * @param <E>
 */
public class InmutableMultiSet<E> implements MultiSet<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3690031173844421012L;
	private MultiSet<E> multiset;

	public InmutableMultiSet(){
		this(new HashMultiSet<E>());
	}
	
	public InmutableMultiSet(MultiSet<E> multiset) {
		if (multiset == null)
			throw new NullPointerException("Argument multiset can not be null");
		this.multiset = multiset;

	}

	public boolean add(E object, long multiplicity) {
		throw new UnsupportedOperationException();
	}
	

	public boolean addAll(Collection<? extends E> objects, long multiplicity) {
		throw new UnsupportedOperationException();
	}
	
	

	@Override
	public long longSize() {
		// TODO Auto-generated method stub
		return multiset.longSize();
	}

	public long count(Object object) {
		return multiset.count(object);
	}

	public long countSubSets(Collection<?> objects) {
		return multiset.countSubSets(objects);
	}

	public Set<E> entrySet() {
		return Collections.unmodifiableSet(multiset.entrySet());
	}

	public boolean remove(Object object, long multiplicity) {
		throw new UnsupportedOperationException();
	}

	public boolean subtraction(Collection<?> objects, long multiplicity) {
		throw new UnsupportedOperationException();
	}

	public boolean subtraction(Collection<?> objects) {
		throw new UnsupportedOperationException();
	}

	public boolean add(E arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends E> arg0) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();

	}

	public boolean contains(Object arg0) {
		return multiset.contains(arg0);
	}

	public boolean containsAll(Collection<?> arg0) {
		return multiset.containsAll(arg0);
	}

	public boolean isEmpty() {
		return multiset.isEmpty();
	}

	public Iterator<E> iterator() {
		return multiset.iterator();
	}

	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return multiset.size();
	}

	public Object[] toArray() {
		return multiset.toArray();
	}

	public <T> T[] toArray(T[] arg0) {
		return multiset.toArray(arg0);
	}

	@Override
	public String toString() {
		return multiset.toString();
	}

	@Override
	public boolean equals(Object arg0) {

		return multiset.equals(arg0);
	}

	@Override
	public int hashCode() {
		return multiset.hashCode();
	}

}
