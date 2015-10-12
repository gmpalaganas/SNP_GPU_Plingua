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
import java.util.Set;
import java.util.Collection;

/** 
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 *  An interface to define multisets
 * @param <E >
 *  The type of the objects in the multiset
 */
public interface MultiSet<E> extends Collection<E>, Serializable {

	/**
	 * @param object
	 * @return The multiplicity of the object
	 */
	public long count(Object object);

	/**
	 * counts the number of subsets
	 * 
	 * @param objects
	 * @return The number of subsets
	 */
	public long countSubSets(Collection<?> objects);

	/**
	 * Add an object to the multiset
	 * 
	 * @param object
	 * @param multiplicity
	 * @return True if the multiset has been changed
	 */
	public boolean add(E object, long multiplicity);

	/**
	 * Add several objects to the multiset
	 * 
	 * @param objects
	 * @param multiplicity
	 * @return True if the multiset has been changed
	 */
	public boolean addAll(Collection<? extends E> objects, long multiplicity);

	/**
	 * Remove several objects of the multiset
	 * 
	 * @param object
	 * @param multiplicity
	 * @return True if the multiset has been changed
	 */
	public boolean remove(Object object, long multiplicity);

	/**
	 * Subtraction between multisets
	 * 
	 * @param objects
	 *            a collection containing the objects to be subtracted
	 * @return true if the multiset has been changed
	 */

	public boolean subtraction(Collection<?> objects);

	/**
	 * subtracts the objects in objects a number of times equal to multiplicity
	 * 
	 * @param objects
	 *            the collection of objects to be subtracted
	 * @param multiplicity
	 *            the number of times the objects will be subtracted
	 * @return true if there was any subtraction
	 */
	public boolean subtraction(Collection<?> objects, long multiplicity);

	/**
	 * Get the set of objects without repetitions Changes in this set will
	 * affect the multiset
	 * 
	 * @return A Set<E> of objects
	 */
	public Set<E> entrySet();
	
	public long longSize();
	
	

}
