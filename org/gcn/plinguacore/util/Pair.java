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

/**
 * This class represents a pair, that is, a binary tuple of generic type E and T
 * (E,T)
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class Pair<E, T> implements Serializable,Comparable<Pair<E,T>> {

	private E first;
	private T second;

	/**
	 * @param first
	 * @param second
	 */
	public Pair(E first, T second) {
		super();
		this.first = first;
		this.second = second;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}

	public E getFirst() {
		return first;
	}

	public T getSecond() {
		return second;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "<"+first.toString()+","+second.toString()+">";
	}

	public void setFirst(E first) {
		this.first = first;
	}

	public void setSecond(T second) {
		this.second = second;
	}
	
	 public int compareTo( Pair<E,T> aThat ) {
		    // final int BEFORE = -1;
		    final int EQUAL = 0;
		    // final int AFTER = 1;

		    //this optimization is usually worthwhile, and can
		    //always be added
		    
		    if ( this == aThat ) return EQUAL;

		    int comparison;
		    
		    comparison = ((Comparable<E>) this.getFirst()).compareTo(aThat.getFirst());
		    
		    if ( comparison != EQUAL ) return comparison;
		    
		    comparison = ((Comparable<T>) this.getSecond()).compareTo(aThat.getSecond());
		    
		    if ( comparison != EQUAL ) return comparison;
		    
		    //all comparisons have yielded equality
		    //verify that compareTo is consistent with equals (optional)
		    
		    assert this.equals(aThat) : "compareTo inconsistent with equals.";

		    return EQUAL;
		  }


}
