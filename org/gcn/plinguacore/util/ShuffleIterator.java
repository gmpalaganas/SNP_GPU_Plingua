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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


public class ShuffleIterator<E> implements Iterator<E> {

	private List<E> data;
	private int swapIndex;

	public ShuffleIterator(List<E> data) {
		if (data == null)
			throw new NullPointerException();
		this.data = data;
	
		swapIndex = data.size() - 1;
	}

	@Override
	public boolean hasNext() {
		return swapIndex != -1;
	}

	@Override
	public E next() {

		if (!hasNext())
			throw new NoSuchElementException();

		E selectedElement;

		if (swapIndex == 0)
			selectedElement = data.get(0);
		else {
			int randomIndex = swapIndex != 1 ? RandomNumbersGenerator.getInstance().nextInt(swapIndex)
					: 0;
			selectedElement = data.get(randomIndex);
			data.set(randomIndex, data.get(swapIndex));
			data.set(swapIndex, selectedElement);

		}
		swapIndex--;
		return selectedElement;
	}

	@Override
	public void remove() {

		throw new UnsupportedOperationException();

	}
	
	

}
